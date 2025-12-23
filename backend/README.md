# FluffyLetter Backend（Spring Boot / Java 17）

## 运行方式概览

- 本地运行（推荐开发调试）：MySQL 本机安装 + Java17 + Maven
- Docker 运行（推荐部署/联调）：`docker compose up -d --build` 一键启动 MySQL + 后端

---

## 一、配置说明

后端主要配置在 `src/main/resources/application.yml`：

- 数据库：`spring.datasource.*`
- 微信登录：`fluffy.wechat.appid` / `fluffy.wechat.secret`
- 用户 token：`fluffy.jwt.secret` / `fluffy.jwt.ttlSeconds`

### 1) 数据库

需要 MySQL 8.x（utf8mb4）。默认库名：`fluffyletter`。

### 2) 微信登录（openid）

收藏功能依赖用户 openid，所以 `POST /api/login/wechat` 会调用微信 `code2Session`。

- `fluffy.wechat.appid`：小程序 AppID
- `fluffy.wechat.secret`：小程序 AppSecret

### 3) JWT（userToken）

小程序端拿到的 `userToken` 是 JWT：

- `fluffy.jwt.secret`：至少 32 字符；生产环境必须使用强随机值
- `fluffy.jwt.ttlSeconds`：token 有效期（默认 30 天）

---

## 配置示例（可直接复制）

下面给出 3 份最常用的配置示例：

1) 本地开发：`application-dev.yml`（建议）
2) 直接编辑：`src/main/resources/application.yml`
3) Docker：`.env`（配合 `docker-compose.yml`）

### 1) MySQL 初始化示例（本地开发）

推荐创建独立用户，而不是一直用 root：

```sql
-- 1) 创建数据库
CREATE DATABASE IF NOT EXISTS fluffyletter
	DEFAULT CHARACTER SET utf8mb4
	COLLATE utf8mb4_unicode_ci;

-- 2) 创建本地开发用户（密码请自行替换）
CREATE USER IF NOT EXISTS 'fluffy'@'localhost' IDENTIFIED BY 'fluffy_dev_password';

-- 3) 授权
GRANT ALL PRIVILEGES ON fluffyletter.* TO 'fluffy'@'localhost';
FLUSH PRIVILEGES;
```

### 2) application.yml 示例（本地开发）

文件：`backend/src/main/resources/application.yml`

```yaml
server:
	port: 8080

spring:
	datasource:
		url: jdbc:mysql://localhost:3306/fluffyletter?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
		username: fluffy
		password: fluffy_dev_password
	jpa:
		hibernate:
			ddl-auto: update
		open-in-view: false
		properties:
			hibernate:
				format_sql: true

fluffy:
	wechat:
		# 必填：小程序 AppID / AppSecret
		appid: "wx_your_appid_here"
		secret: "wx_your_secret_here"
	jwt:
		# 必填：至少 32 字符；生产环境请替换为强随机值
		secret: "change_me_to_a_long_random_secret_key_32_chars_min"
		ttlSeconds: 2592000
	admin:
		bootstrap:
			# 可选：开发期启动时，如果 admin_user 表为空，则创建初始管理员。
			# 建议只在本地使用，并通过环境变量注入。
			username: ${ADMIN_BOOTSTRAP_USERNAME:}
			password: ${ADMIN_BOOTSTRAP_PASSWORD:}
			role: ${ADMIN_BOOTSTRAP_ROLE:superadmin}
```

### 3) application.yml 示例（用 root 直连，本地快速起）

如果你暂时不想建用户，用 root 也可以（仅开发期）：

```yaml
spring:
	datasource:
		url: jdbc:mysql://localhost:3306/fluffyletter?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
		username: root
		password: root
```

### 4) Docker 环境变量 .env 示例

文件：`backend/.env`（可从 `.env.example` 复制）

```dotenv
# MySQL
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=fluffyletter

# WeChat（必填）
FLUFFY_WECHAT_APPID=wx_your_appid_here
FLUFFY_WECHAT_SECRET=wx_your_secret_here

# JWT（必填）
FLUFFY_JWT_SECRET=change_me_to_a_long_random_secret_key_32_chars_min
```

---

## 二、本地运行（macOS / Linux）

### 0) 安装依赖

- Java 17
- Maven

如果你在 macOS 上没有 Maven，可用 Homebrew：

- `brew install maven`

（你当前终端提示 `mvn: command not found`，说明本机还没装 Maven。）

### 1) 启动 MySQL

创建数据库：

```sql
CREATE DATABASE fluffyletter DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后在 `src/main/resources/application.yml` 里配置本机数据库连接。

### 2) 启动后端

在本目录执行：

- `mvn spring-boot:run`

默认端口：`8080`

---

## 三、Docker 运行（推荐部署/联调）

本目录已提供：

- `Dockerfile`
- `docker-compose.yml`
- `docker-compose.prod.yml`（更适合服务器：MySQL 不对外暴露、自动重启、健康检查）
- `.env.example`

### 1) 准备 .env

复制一份并填写：

- `cp .env.example .env`

至少需要填写：

- `FLUFFY_WECHAT_APPID`
- `FLUFFY_WECHAT_SECRET`
- `FLUFFY_JWT_SECRET`

### 2) 一键启动

在本目录执行：

- `docker compose up -d --build`

启动后：

- MySQL：`localhost:3306`
- 后端：`http://localhost:8080`

---

## 三（生产部署）在服务器上部署（Docker Compose）

下面以 Linux 服务器为例（已安装 Docker + Docker Compose v2）。

### 1) 复制代码到服务器

把整个 `backend/` 目录上传到服务器（或在服务器上 git clone）。

建议目录：

- `/opt/fluffyletter/backend`

### 2) 准备 .env（生产必填）

在服务器的 `backend/` 目录：

- `cp .env.example .env`

至少要改这些：

- `MYSQL_ROOT_PASSWORD`：强密码
- `FLUFFY_WECHAT_APPID` / `FLUFFY_WECHAT_SECRET`
- `FLUFFY_JWT_SECRET`：至少 32 位强随机字符串

可选：首次启动自动创建管理员（仅当 admin_user 表为空时生效）：

- `ADMIN_BOOTSTRAP_USERNAME`
- `ADMIN_BOOTSTRAP_PASSWORD`

### 3) 启动（生产 compose 文件）

在服务器 `backend/` 目录执行：

- `docker compose -f docker-compose.prod.yml up -d --build`

查看日志：

- `docker compose -f docker-compose.prod.yml logs -f backend`

### 4) 访问验证

- 后端：`http://<服务器IP>:${BACKEND_PORT:-8080}`
- 管理页面：`http://<服务器IP>:${BACKEND_PORT:-8080}/admin.html`

### 5) 重要说明（小程序上线必看）

- 真机/线上环境通常需要 HTTPS 域名，并在微信小程序后台配置“服务器域名”。
- `docker-compose.prod.yml` 默认把后端端口对外暴露（`BACKEND_PORT`），如你要走 Nginx 反代/HTTPS，建议只在防火墙层面开放 80/443。

---

## 四、接口联调说明

### 1) 登录换 token

- `POST /api/login/wechat`
- Body：`{"code":"<wx.login 返回的 code>"}`
- Response：`{"userToken":"...","openid":"..."}`

### 2) 收藏接口认证方式

小程序请求时 Header 携带：

- `Authorization: Bearer <userToken>`

---

## 五、已实现接口（最小可用）

- `POST /api/login/wechat`：小程序传 `code`，后端调用微信 code2Session 换取 openid，并返回 `userToken`
- `POST /api/favorites/{productId}`：收藏
- `DELETE /api/favorites/{productId}`：取消收藏
- `GET /api/favorites`：获取收藏（默认返回商品卡片列表；可选 `idsOnly=true` 返回 productId 列表）

### 管理端（Admin）

- `POST /admin/login`：管理员登录，返回 `token`
- `GET/POST/PUT/DELETE /admin/categories`：分类管理（DELETE 为停用）
- `GET/POST/PUT/DELETE /admin/products`：商品管理（DELETE 为停用）
- `GET/PUT /admin/contact`：联系信息管理

管理页面（静态）：打开 `http://localhost:8080/admin.html`

## 认证方式

小程序请求时在 Header 携带：

- `Authorization: Bearer <userToken>`

---

## 六、常见问题（FAQ）

### 1) 微信登录报错 wechat appid/secret not configured

说明你还没在 `application.yml`（或 docker 环境变量）配置 `fluffy.wechat.appid/secret`。

### 2) 数据表怎么创建？

当前 `spring.jpa.hibernate.ddl-auto=update`，启动时会自动建表/更新字段（适合开发期）。生产建议改为手工迁移（Flyway/Liquibase）。

### 3) 启动时报错 Public Key Retrieval is not allowed

这是 MySQL 8 常见连接问题（认证插件导致）。本项目本地开发建议在 JDBC URL 上增加：

- `allowPublicKeyRetrieval=true`

例如：

- `jdbc:mysql://localhost:3306/fluffyletter?...&useSSL=false&allowPublicKeyRetrieval=true&...`
