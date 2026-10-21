package com.cssd.infra.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CssdBillSeqMapper {
    @Select("""
            INSERT INTO cssd.cssd_bill_seq(prefix, biz_date, next_no)
            VALUES (#{prefix}, #{bizDate}, 1)
            ON CONFLICT (prefix, biz_date)
            DO UPDATE SET next_no = cssd.cssd_bill_seq.next_no + 1
            RETURNING next_no
            """)
    int nextNo(@Param("prefix") String prefix, @Param("bizDate") String bizDate);
}
