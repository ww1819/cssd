package com.cssd.sys;

import com.cssd.infra.mapper.CssdBillSeqMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class BillNoService {
    private static final DateTimeFormatter DAY = DateTimeFormatter.BASIC_ISO_DATE;
    private final CssdBillSeqMapper seqMapper;

    public BillNoService(CssdBillSeqMapper seqMapper) {
        this.seqMapper = seqMapper;
    }

    @Transactional
    public String next(String prefix) {
        String day = LocalDate.now().format(DAY);
        int n = seqMapper.nextNo(prefix, day);
        return prefix + day + String.format("%04d", n);
    }
}
