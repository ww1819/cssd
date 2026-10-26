# CSSD 追溯系统 · 本地开发部署手册

医院消毒供应中心（CSSD）追溯系统一期的本地启动说明。业务以 `docs/xdzs-requirements.md` 为准，工程约定以 `docs/reusable-engineering-conventions.md` 为准。

日常改代码用 **方式 B**（依赖容器 + 本机热更新）。整栈验收、给别人看演示用 **方式 A**（全部进容器）。

## 1. 环境要求

在项目目录 `cssd/` 下操作。Windows 用 PowerShell。

| 组件 | 版本 | 用途 |
|------|------|------|
| Docker Desktop（Compose V2） | 当前稳定版 | Postgres / Redis / RabbitMQ，方式 A 还负责前后端 |
| JDK | 17 | 方式 B 编译运行后端。`scripts/start-backend.ps1` 优先 `CSSD_JAVA_HOME`，否则 `D:\` / `C:\Program Files\Java\jdk-17` |
| Maven | 3.9+ | 方式 B。容器内构建使用 Maven 3.9.9 |
| Node.js | 20 | 方式 B 前端。容器构建镜像为 `node:20-alpine` |

检查：

```powershell
docker compose version
java -version
mvn -version
node -v
```

Docker Desktop 须处于运行状态。首次拉镜像和下载 Maven 依赖会比较慢。

## 2. 端口与账号

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端（方式 A） | http://localhost | Nginx，`/` 走静态页，`/api` 反代到后端 |
| 前端（方式 B） | http://localhost:5173 | Vite，`/api` 代理到 `http://localhost:8080` |
| 后端 | http://localhost:8080 | 健康检查 `GET /api/health` |
| PostgreSQL | `localhost:5432` | 库 / 用户 / 密码均为 `cssd` |
| Redis | `localhost:6379` | 仅 `docker` profile 连接 |
| RabbitMQ | `localhost:5672` | 仅 `docker` profile 连接 |
| RabbitMQ 管理台 | http://localhost:15672 | 用户 / 密码 `guest` / `guest` |

业务表在 schema `cssd`。Flyway 迁移历史写在 `public`。启动时自动执行 `V1`～`V4`（建表、索引、字典、演示主数据），无需手工导入 `docs/sql/pgsql/`。

演示账号密码都是 `123456`。库里先存 `PLAIN:123456`，应用第一次启动时由 `PasswordHashSeeder` 转成 BCrypt，之后用明文登录。

| 工号 | 姓名 | 角色 |
|------|------|------|
| ADMIN | 系统管理员 | 管理员 |
| NL001 | 王护士长 | 护士长 |
| OP001 | 李配包 | 操作员（配包） |
| OP002 | 张核包 | 操作员（核包，须与 OP001 不同人） |
| QC001 | 赵质检 | 质检员 |
| DL001 | 钱下送 | 下送 |
| CL001 | 孙护士 | 临床 |
| SG001 | 周医师 | 术者 |

一期 HIS、RFID、设备协议均为 Mock。登录后 JWT 有效 12 小时，密钥可用环境变量 `CSSD_JWT_SECRET` 覆盖（至少 32 字节）。

## 3. 方式 A：整栈容器

在 `cssd/` 目录：

```powershell
docker compose up --build
```

后台运行：

```powershell
docker compose up --build -d
docker compose ps
docker compose logs -f backend
```

浏览器打开 http://localhost 。健康检查应返回：

```json
{"code":0,"message":"ok","data":{"status":"UP"}}
```

```powershell
curl http://localhost:8080/api/health
```

`backend` 使用 `SPRING_PROFILES_ACTIVE=docker`，会连接 Redis 和 RabbitMQ（`cssd.rabbit.enabled=true`）。Postgres 健康后才启动后端；RabbitMQ ping 通过后才启动后端。

停止：

```powershell
docker compose stop
```

停掉并删除容器，保留数据库卷 `cssd_pg`：

```powershell
docker compose down
```

连数据一起清空（下次启动会重新跑 Flyway 和演示种子）：

```powershell
docker compose down -v
```

只重建某一个服务：

```powershell
docker compose up --build -d backend
docker compose up --build -d frontend
```

改 Java 或 Vue 源码后，方式 A 必须重新 build 对应服务才能看到效果。

## 4. 方式 B：本地热更新（推荐日常开发）

默认 profile **不连接** Redis 和 RabbitMQ（`application.yml` 排除了二者的自动配置）。本机开发只需要 PostgreSQL。

### 4.1 只起数据库

```powershell
cd cssd
docker compose up -d postgres
docker compose ps
```

确认 `pg_isready` 已通过后再起后端。本机若已占用 5432，先停掉本机 Postgres，或改 `docker-compose.yml` 的端口映射（例如 `"5433:5432"`），同时设置 `DB_PORT=5433`。

需要联调消息或缓存时再起另外两个，并改用 `docker` profile（见第 5 节）：

```powershell
docker compose up -d postgres redis rabbitmq
```

### 4.2 启动后端

JDK 装在脚本假定路径时：

```powershell
cd cssd
.\scripts\start-backend.ps1
```

脚本会自动选用可用的 JDK 17（`CSSD_JAVA_HOME` → `D:\Program Files\Java\jdk-17` → `C:\Program Files\Java\jdk-17`）并执行：

```powershell
mvn -DskipTests install -pl cssd-app -am
mvn -DskipTests spring-boot:run -pl cssd-app
```

JDK 在其他目录时：

```powershell
$env:CSSD_JAVA_HOME = 'D:\path\to\jdk-17'
.\scripts\start-backend.ps1
```

IDE 运行主类 `com.cssd.CssdApplication`，工作目录可用 `cssd/backend`，JDK 17。默认连 `localhost:5432`，用户密码 `cssd` / `cssd`。

看到 Flyway 迁移成功，以及 `encoded N demo passwords`（仅首次），再访问：

```powershell
curl http://localhost:8080/api/health
```

`spring-boot:run` 会在改 class 后尝试热重启；改依赖或资源文件后重新执行上述 Maven 命令。

### 4.3 启动前端

另开一个终端：

```powershell
cd cssd\frontend
npm install
npm run dev
```

浏览器打开 http://localhost:5173 。Vite 把 `/api` 转到 `http://localhost:8080`，前端改完即刷新。路由是 hash 模式，登录页为 http://localhost:5173/#/login 。

## 5. 配置

后端配置在 `backend/cssd-app/src/main/resources/`：

| 文件 | 作用 |
|------|------|
| `application.yml` | 默认。数据源、JWT、Flyway；排除 Redis / Rabbit 自动配置 |
| `application-local.yml` | 与默认相同，显式关闭 Rabbit |
| `application-docker.yml` | 打开 Rabbit，并取消上述自动配置排除 |

环境变量（未设置时用括号内默认值）：

| 变量 | 默认 | 说明 |
|------|------|------|
| `DB_HOST` | `localhost` | 容器内由 Compose 设为 `postgres` |
| `DB_PORT` | `5432` | |
| `DB_NAME` | `cssd` | |
| `DB_USER` / `DB_PASSWORD` | `cssd` / `cssd` | |
| `REDIS_HOST` / `REDIS_PORT` | `localhost` / `6379` | 仅 docker profile |
| `RABBIT_HOST` / `RABBIT_PORT` | `localhost` / `5672` | 仅 docker profile |
| `RABBIT_USER` / `RABBIT_PASSWORD` | `guest` / `guest` | |
| `CSSD_JWT_SECRET` | 开发用内置密钥 | 本地可保持默认 |
| `SPRING_PROFILES_ACTIVE` | （空，即默认） | 整栈容器为 `docker` |

在本机同时连 Redis 和 RabbitMQ（三个容器都已映射到 localhost）：

```powershell
$env:SPRING_PROFILES_ACTIVE = 'docker'
mvn -DskipTests spring-boot:run -pl cssd-app -am
```

此时主机名保持默认 `localhost`，不要设成 Compose 里的服务名 `postgres` / `redis` / `rabbitmq`。

## 6. 验收

1. `GET http://localhost:8080/api/health` 返回 `code = 0`、`status = UP`。
2. 打开前端，用工号 `ADMIN`、密码 `123456` 登录，进入工位大厅。
3. 点「一键演示闭环」。成功后页面给出条码。
4. 打开「追溯查询」，输入该条码，能看到回收、清洗、灭菌、发放链路。
5. 退出后换 `OP001` 登录，确认能进入配包相关页面。

接口抽查（先登录拿 token）：

```powershell
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"staffNo\":\"ADMIN\",\"password\":\"123456\"}"
```

返回体里的 `data.token` 即为 JWT。业务接口带 `Authorization: Bearer <token>`。未登录访问 `/api/**` 返回 HTTP 401，正文 `{"code":401,"message":"未登录"}`。

菜单：工位大厅、回收、清洗、配包/核包、灭菌/监测/放行、发放/入库、科室申领与使用、寄消/外来/一次性、追溯查询、告警与召回、主数据。

## 7. 常见问题

**后端启动报连不上数据库**  
确认 `docker compose ps` 里 postgres 为 healthy，且本机 5432 没有另一套 Postgres。账号必须是 `cssd` / `cssd`，库名 `cssd`。

**Flyway 校验失败或迁移中断**  
开发库可以直接清卷重来：`docker compose down -v`，再 `docker compose up -d postgres`，然后重启后端。不要在已失败的版本上改已执行过的 `V1`～`V4` 脚本内容后指望自动重跑；改已发布脚本前先清库。

**登录提示密码错误，但确认是 123456**  
看后端日志是否有 `encoded N demo passwords`。若种子被手工改成非 BCrypt 且不以 `PLAIN:` 开头，启动器不会重写密码。清卷后重启可恢复演示账号。

**方式 A 页面打不开或 `/api` 502**  
`docker compose logs backend` 看 Flyway 和端口。前端容器依赖后端进程已监听 8080，后端还在下载依赖时 Nginx 会暂时失败，等后端日志出现启动完成后再刷新。

**80 或 8080 被占用**  
改 `docker-compose.yml` 左侧宿主机端口，例如前端 `"8088:80"`、后端 `"18080:8080"`。方式 B 的 Vite 端口在 `frontend/vite.config.js` 的 `server.port`（默认 5173），代理目标保持 `http://localhost:8080`，若后端改了宿主机端口，这里要一起改。

**`start-backend.ps1` 找不到 java**  
设置 `$env:CSSD_JAVA_HOME` 指向本机 JDK 17 根目录后再跑脚本；不要改用 JDK 8。Maven 若仍指向 JDK 8，会出现「无效的目标发行版: 17」。

**改了后端代码，浏览器仍是旧接口**  
方式 B 重启 `spring-boot:run`。方式 A 执行 `docker compose up --build -d backend`。前端静态资源同理，方式 A 需重建 `frontend`。

**想看队列**  
整栈或本机 `docker` profile 下打开 http://localhost:15672 ，`guest` / `guest`。默认 profile 不向 RabbitMQ 发消息。
