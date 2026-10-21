# 医院消毒供应中心（CSSD）追溯系统 · 一期

开发唯一依据：`docs/xdzs-requirements.md`（第 1 章 > 第 10 章 > 第 7 章）。  
工程约定：`docs/reusable-engineering-conventions.md`。  
库表：`docs/sql/pgsql/V1__cssd_tables.sql`、`V2__cssd_indexes.sql`。

## 技术栈

- Java 17 / Spring Boot 3.3 模块化单体（`backend/cssd-common` + `backend/cssd-app`）
- Vue 3 + Vite + Element Plus
- PostgreSQL 16（schema `cssd`）/ Redis / RabbitMQ
- 设备协议：SPI + Mock（TCP / Serial / OPC UA 占位，装锅不接实时参数）
- HIS：Mock 患者 + 手工住院号 + 幂等流水
- 一期不做 PDA、真机协议、按年分区、Spring Cloud 拆服务

## 启动

```bash
docker compose up --build
```

浏览器打开 http://localhost  后端 http://localhost:8080/api/health

演示账号（密码均为 `123456`）：

| 工号 | 角色 |
|------|------|
| ADMIN | 管理员 |
| NL001 | 护士长 |
| OP001 | 操作员（配包） |
| OP002 | 操作员（核包，须与 OP001 不同人） |
| QC001 | 质检员 |
| DL001 | 下送 |
| CL001 | 临床 |
| SG001 | 术者 |

工位大厅可点「一键演示闭环」，再在「追溯查询」输入返回的条码。

本地开发：先起 Postgres/Redis/Rabbit，再 `mvn -f backend/pom.xml spring-boot:run -pl cssd-app -am`，前端 `npm install && npm run dev`（Vite 代理 `/api` → 8080）。
