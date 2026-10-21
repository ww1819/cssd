package com.cssd.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.common.ApiResult;
import com.cssd.common.IdUtils;
import com.cssd.infra.entity.CssdDict;
import com.cssd.infra.entity.CssdSysConfig;
import com.cssd.infra.mapper.CssdDictMapper;
import com.cssd.infra.mapper.CssdSysConfigMapper;
import com.cssd.security.LoginUser;
import com.cssd.security.SecurityUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sys")
public class DictConfigController {
    private final CssdDictMapper dictMapper;
    private final CssdSysConfigMapper configMapper;

    public DictConfigController(CssdDictMapper dictMapper, CssdSysConfigMapper configMapper) {
        this.dictMapper = dictMapper;
        this.configMapper = configMapper;
    }

    @GetMapping("/dicts")
    public ApiResult<Map<String, List<CssdDict>>> dicts(@RequestParam(required = false) String types) {
        LambdaQueryWrapper<CssdDict> q = new LambdaQueryWrapper<CssdDict>().eq(CssdDict::getIsActive, 1)
                .orderByAsc(CssdDict::getSortNo);
        if (types != null && !types.isBlank()) {
            q.in(CssdDict::getDictType, List.of(types.split(",")));
        }
        Map<String, List<CssdDict>> grouped = dictMapper.selectList(q).stream()
                .collect(Collectors.groupingBy(CssdDict::getDictType));
        return ApiResult.ok(grouped);
    }

    @GetMapping("/configs")
    public ApiResult<Map<String, String>> configs() {
        Map<String, String> map = new HashMap<>();
        for (CssdSysConfig c : configMapper.selectList(null)) {
            map.put(c.getConfigKey(), c.getConfigValue());
        }
        return ApiResult.ok(map);
    }

    @PutMapping("/configs")
    public ApiResult<Void> updateConfig(@RequestBody Map<String, String> body) {
        LoginUser user = SecurityUtils.requireUser();
        if (!"admin".equals(user.getRole()) && !"nurse_lead".equals(user.getRole())) {
            throw new com.cssd.common.BizException(403, "仅管理员/护士长可改参数");
        }
        String key = body.get("configKey");
        String value = body.get("configValue");
        CssdSysConfig row = configMapper.selectOne(new LambdaQueryWrapper<CssdSysConfig>()
                .eq(CssdSysConfig::getConfigKey, key));
        if (row == null) {
            row = new CssdSysConfig();
            row.setId(IdUtils.uuid7());
            row.setConfigKey(key);
            row.setConfigValue(value);
            configMapper.insert(row);
        } else {
            row.setConfigValue(value);
            configMapper.updateById(row);
        }
        return ApiResult.ok();
    }

    public String config(String key, String def) {
        CssdSysConfig row = configMapper.selectOne(new LambdaQueryWrapper<CssdSysConfig>()
                .eq(CssdSysConfig::getConfigKey, key));
        return row == null || row.getConfigValue() == null ? def : row.getConfigValue();
    }

    public boolean waitBio() {
        return !"0".equals(config("ster.release.wait_bio", "1"));
    }
}
