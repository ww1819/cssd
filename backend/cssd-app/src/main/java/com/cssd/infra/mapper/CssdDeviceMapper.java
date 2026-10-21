package com.cssd.infra.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cssd.infra.entity.CssdDevice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CssdDeviceMapper extends BaseMapper<CssdDevice> {
}