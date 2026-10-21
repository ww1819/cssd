package com.cssd.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cssd.infra.entity.CssdAuthAccount;
import com.cssd.infra.mapper.CssdAuthAccountMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PasswordHashSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(PasswordHashSeeder.class);
    private final CssdAuthAccountMapper accountMapper;
    private final PasswordEncoder encoder;

    public PasswordHashSeeder(CssdAuthAccountMapper accountMapper, PasswordEncoder encoder) {
        this.accountMapper = accountMapper;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<CssdAuthAccount> list = accountMapper.selectList(new LambdaQueryWrapper<CssdAuthAccount>()
                .likeRight(CssdAuthAccount::getPasswordHash, "PLAIN:"));
        for (CssdAuthAccount acc : list) {
            String plain = acc.getPasswordHash().substring("PLAIN:".length());
            acc.setPasswordHash(encoder.encode(plain));
            accountMapper.updateById(acc);
        }
        if (!list.isEmpty()) {
            log.info("encoded {} demo passwords", list.size());
        }
    }
}
