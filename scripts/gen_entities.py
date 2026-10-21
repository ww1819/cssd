# -*- coding: utf-8 -*-
"""Generate MyBatis-Plus entities + mappers from V1 SQL."""
import re
from pathlib import Path

SQL = Path(r"E:\workspace\cssd\docs\sql\pgsql\V1__cssd_tables.sql").read_text(encoding="utf-8")
ENT_DIR = Path(r"E:\workspace\cssd\backend\cssd-app\src\main\java\com\cssd\infra\entity")
MAP_DIR = Path(r"E:\workspace\cssd\backend\cssd-app\src\main\java\com\cssd\infra\mapper")
ENT_DIR.mkdir(parents=True, exist_ok=True)
MAP_DIR.mkdir(parents=True, exist_ok=True)

TYPE_MAP = {
    "varchar": "String",
    "integer": "Integer",
    "smallint": "Integer",
    "date": "java.time.LocalDate",
    "timestamptz": "java.time.OffsetDateTime",
    "jsonb": "String",
    "numeric": "java.math.BigDecimal",
}

SKIP_AUDIT = {
    "id", "created_at", "created_by", "created_by_no", "created_by_name",
    "updated_at", "updated_by", "updated_by_no", "updated_by_name",
    "is_deleted", "deleted_at", "deleted_by", "deleted_by_no", "deleted_by_name",
    "create_channel", "update_channel", "delete_channel",
}


def camel(s: str) -> str:
    parts = s.split("_")
    return parts[0] + "".join(p.title() for p in parts[1:])


def pascal(s: str) -> str:
    return "".join(p.title() for p in s.split("_"))


tables = re.findall(
    r"CREATE TABLE cssd\.(\w+)\s*\((.*?)CONSTRAINT",
    SQL,
    flags=re.S,
)

class_names = []
for table, body in tables:
    cls = pascal(table)
    class_names.append((table, cls))
    fields = []
    imports = set()
    for line in body.splitlines():
        line = line.strip().rstrip(",")
        if not line or line.startswith("CONSTRAINT") or line.startswith("--"):
            continue
        m = re.match(r"(\w+)\s+(\w+)", line)
        if not m:
            continue
        col, raw_type = m.group(1), m.group(2)
        if col in SKIP_AUDIT:
            continue
        jtype = TYPE_MAP.get(raw_type, "String")
        if jtype.startswith("java."):
            imports.add(jtype)
            jtype = jtype.split(".")[-1]
        fields.append((col, jtype))

    extra_imp = "".join(f"import {i};\n" for i in sorted(imports))
    field_lines = "\n".join(f"    private {t} {camel(c)};" for c, t in fields)
    src = (
        "package com.cssd.infra.entity;\n\n"
        "import com.baomidou.mybatisplus.annotation.TableName;\n"
        f"{extra_imp}"
        "import lombok.Data;\n"
        "import lombok.EqualsAndHashCode;\n\n"
        "@Data\n"
        "@EqualsAndHashCode(callSuper = true)\n"
        f'@TableName(value = "{table}", schema = "cssd")\n'
        f"public class {cls} extends BaseEntity {{\n"
        f"{field_lines}\n"
        "}\n"
    )
    (ENT_DIR / f"{cls}.java").write_text(src, encoding="utf-8")
    mapper = (
        "package com.cssd.infra.mapper;\n\n"
        "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n"
        f"import com.cssd.infra.entity.{cls};\n"
        "import org.apache.ibatis.annotations.Mapper;\n\n"
        "@Mapper\n"
        f"public interface {cls}Mapper extends BaseMapper<{cls}> {{\n"
        "}\n"
    )
    (MAP_DIR / f"{cls}Mapper.java").write_text(mapper, encoding="utf-8")

print(f"generated {len(class_names)} entities")
for t, c in class_names:
    print(t, c)
