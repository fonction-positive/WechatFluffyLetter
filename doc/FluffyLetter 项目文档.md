# **FluffyLetter 微信小程序项目四大文档合集**



------



## **1. 产品需求文档（PRD）**



```
# FluffyLetter 产品需求文档

## 1. 项目概述
- 名称：FluffyLetter
- 功能：展示手工制作手链，用户查看图片、介绍和详细信息，通过联系我们购买

## 2. 用户画像
- 国内外用户
- 需要中英文双语支持

## 3. 使用场景
- 微信小程序

## 4. 功能总览
- 首页：轮播图 + 推荐商品
- 分类页：搜索 + 分类商品
- 商品详情页：大图、名称、热卖状态、价格、折扣、简介、详细信息
- 收藏页（基于用户 openid 的云端收藏）
- 联系我们：官方微信客服购买流程
- 关于我们：品牌故事

## 5. 详细功能说明
### 首页
- 顶部导航栏：品牌商标/名字（点击跳转关于我们）、搜索、收藏、语言切换
- 内容块：轮播图 + 推荐商品卡片
- 悬浮按钮：联系我们

### 分类页
- 搜索框 + 分类切换 + 商品卡片展示

### 收藏功能
- 使用微信小程序 openid 识别用户，收藏数据存储在后端数据库，支持多设备同步
- 收藏入口：
  - 首页顶部导航栏收藏按钮（跳转收藏页）
  - 商品详情页收藏按钮（心形/星形图标，支持一键收藏/取消收藏）
- 收藏数据展示：
  - 收藏页展示当前用户已收藏的商品列表
  - 商品卡片和详情接口返回当前用户是否已收藏（is_favorited 字段）
- 收藏上限与行为：
  - 每个用户收藏数量可设置上限（例如 200 条，可在后端配置）
  - 再次点击收藏按钮即取消收藏
  - 取消收藏后，收藏页列表实时刷新/下次进入重新加载

## 6. 非功能需求
- 性能、兼容性、安全

## 7. 版本规划
- V1 / V2

## 8. 约束条件
- 遵守微信小程序规范
```



------





## **2. 技术方案设计文档**



```
# FluffyLetter 技术方案设计文档

## 技术栈
- 前端：微信小程序原生
- 后端：Java 17 + Spring Boot
- 数据库：MySQL 8.x
- ORM：JPA 或 MyBatis
- 构建工具：Maven
- 部署：Docker

## 架构设计
- 前后端分离
- RESTful JSON 接口
- 后端分层：Controller / Service / Repository / Entity / DTO

## 数据库选择
- MySQL 8.x，utf8mb4 编码，支持多语言

## 多语言实现
- product_i18n 表存储多语言文案
- 前端通过 lang 参数选择语言

## 用户身份与收藏实现
- 使用微信小程序登录能力获取用户 code，后端通过 code2Session 换取 openid
- 通过 openid 在 wechat_user 表中创建/查找用户，生成用户唯一 ID
- 后端基于用户 ID 管理收藏记录（favorite 表），并通过 token 或 session 维护登录态
- 小程序端在启动时完成登录，将后端返回的 userToken 存入本地，每次调用需要用户身份的接口在 Header 中携带

## 后端分层设计
- Controller：接收请求，返回 JSON
- Service：处理业务逻辑
- Repository：数据库操作
- Entity：映射表结构
- DTO：返回数据对象
```



------





## **3. 数据库设计文档**



```
# FluffyLetter 数据库设计文档

## 数据表列表
- category: 商品分类
- product: 商品基础信息
- product_i18n: 商品多语言信息
- product_image: 商品图片
- admin_user: 后台管理员账号
- wechat_user: 小程序用户（基于 openid）
- favorite: 用户商品收藏关系

## category 表
| 字段 | 类型 | 说明 |
|---|---|---|
id | BIGINT | 主键
code | VARCHAR(50) | 分类唯一编码
name_zh | VARCHAR(100) | 中文名称
name_en | VARCHAR(100) | 英文名称
sort_order | INT | 排序
is_active | TINYINT | 是否启用
created_at | DATETIME | 创建时间
updated_at | DATETIME | 更新时间

## product 表
| 字段 | 类型 | 说明 |
|---|---|---|
id | BIGINT | 主键
category_id | BIGINT | 分类ID
price | DECIMAL(10,2) | 原价
discount_price | DECIMAL(10,2) | 折扣价
is_hot | TINYINT | 热卖状态
is_active | TINYINT | 上架状态
created_at | DATETIME | 创建时间
updated_at | DATETIME | 更新时间

## product_i18n 表
| 字段 | 类型 | 说明 |
|---|---|---|
id | BIGINT | 主键
product_id | BIGINT | 商品ID
lang | VARCHAR(10) | zh/en
name | VARCHAR(200) | 商品名称
brief | VARCHAR(500) | 简介
description | TEXT | 详细介绍

## product_image 表
| 字段 | 类型 | 说明 |
|---|---|---|
id | BIGINT | 主键
product_id | BIGINT | 商品ID
image_url | VARCHAR(500) | 图片地址
sort_order | INT | 图片排序
is_cover | TINYINT | 是否封面图

## admin_user 表
| 字段 | 类型 | 说明 |
|---|---|---|
id | BIGINT | 主键
username | VARCHAR(50) | 管理员用户名
password | VARCHAR(100) | 加密密码
role | VARCHAR(20) | admin / superadmin
created_at | DATETIME | 创建时间
updated_at | DATETIME | 更新时间

## wechat_user 表
| 字段 | 类型 | 说明 |
|---|---|---|
id | BIGINT | 主键
openid | VARCHAR(64) | 微信 openid，唯一标识小程序用户
nickname | VARCHAR(100) | 微信昵称（可选）
avatar_url | VARCHAR(500) | 微信头像（可选）
created_at | DATETIME | 创建时间
updated_at | DATETIME | 更新时间

## favorite 表
| 字段 | 类型 | 说明 |
|---|---|---|
id | BIGINT | 主键
user_id | BIGINT | wechat_user.id，标识哪个用户收藏
product_id | BIGINT | product.id，标识收藏的商品
created_at | DATETIME | 收藏时间

## 索引设计
- category.code 唯一索引
- product.category_id 索引
- product_i18n.product_id+lang 联合索引
- product_image.product_id 索引
- admin_user.username 唯一索引
- wechat_user.openid 唯一索引
- favorite.user_id+product_id 唯一索引（防止重复收藏）
- favorite.user_id 索引（按用户快速查询收藏列表）

## 建表 SQL 示例
```sql
CREATE TABLE category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) UNIQUE,
  name_zh VARCHAR(100),
  name_en VARCHAR(100),
  sort_order INT,
  is_active TINYINT DEFAULT 1,
  created_at DATETIME,
  updated_at DATETIME
);
---

## 4. 后端接口与设计文档
```markdown
# FluffyLetter 后端接口与设计文档

## 接口约定
- 接口前缀：/api
- 数据格式：JSON
- 多语言支持：lang 参数 (zh/en)
- 用户身份：小程序端通过登录获取 userToken，后端基于 openid 识别用户

## 商品分类接口
GET /api/categories?lang=zh
返回示例：
[
  {"id":1,"name":"手链"},
  {"id":2,"name":"饰品"}
]

## 商品列表接口
GET /api/products?category_id=1&lang=en&page=1&size=10
返回示例：
[
  {
    "id":1,
    "name":"Fluffy Bracelet",
    "brief":"Handmade bracelet",
    "price":199,
    "discount_price":159,
    "is_hot":1,
    "cover_image":"https://cdn.example.com/1.jpg"
  }
]

## 商品详情接口
GET /api/products/{id}?lang=en
返回示例：
{
  "id":1,
  "name":"Fluffy Bracelet",
  "brief":"Handmade bracelet",
  "description":"Each piece is handmade...",
  "price":199,
  "discount_price":159,
  "is_hot":1,
  "is_favorited":true,
  "images":["https://cdn.example.com/1_1.jpg","https://cdn.example.com/1_2.jpg"]
}

## 收藏接口（用户端）

所有收藏相关接口需要携带用户身份标识（例如 Header: Authorization: Bearer {userToken}），后端根据 token 解析出对应 openid / user_id。

### 新增收藏
POST /api/favorites/{productId}
返回示例：
{
  "success": true
}

### 取消收藏
DELETE /api/favorites/{productId}
返回示例：
{
  "success": true
}

### 获取收藏列表
GET /api/favorites?lang=en&page=1&size=20
返回示例：
[
  {
    "id":1,
    "name":"Fluffy Bracelet",
    "brief":"Handmade bracelet",
    "price":199,
    "discount_price":159,
    "is_hot":1,
    "cover_image":"https://cdn.example.com/1.jpg",
    "is_favorited":true
  }
]

## 后端分层
- Controller: 接收请求并返回JSON
- Service: 处理业务逻辑
- Repository: 数据库操作
- Entity: 数据库表映射
- DTO: 接口数据对象

## Controller 示例
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
  @Autowired
  private ProductService productService;

  @GetMapping("")
  public List<ProductDTO> getProducts(@RequestParam(required=false) Long categoryId,
                                     @RequestParam(defaultValue="zh") String lang,
                                     @RequestParam(defaultValue="1") int page,
                                     @RequestParam(defaultValue="10") int size) {
      return productService.getProductList(categoryId, lang, page, size);
  }

  @GetMapping("/{id}")
  public ProductDTO getProductDetail(@PathVariable Long id,
                                     @RequestParam(defaultValue="zh") String lang) {
      return productService.getProductDetail(id, lang);
  }
}
```



好的，我们现在来写 **FluffyLetter 后端管理系统设计文档**，详细覆盖功能模块、页面结构、接口、权限和数据流，保证管理员可以管理商品、分类、图片和联系我们信息。



## **4.后端管理系统设计文档**

```
# FluffyLetter 后端管理系统设计文档

## 1. 系统概述
后台管理系统用于管理员管理 FluffyLetter 小程序的商品、分类、图片及联系我们信息，支持多语言数据管理，提供增删改查操作。同时可查看商品的收藏统计数据（收藏数量），辅助运营决策。系统和小程序前端共享同一个后端数据库，但接口需增加管理员权限验证。

---

## 2. 功能模块

### 2.1 登录与权限
- 管理员登录：用户名 + 密码
- 角色权限：
  - superadmin：所有功能
  - admin：商品、分类、图片管理
- JWT 或 Session 验证
- 登录后获取 token，用于所有后台接口验证

### 2.2 商品管理
- 商品列表：表格显示 ID、名称、分类、价格、折扣、热卖状态、上架状态、收藏数量、操作
- 搜索：按名称或分类搜索
- 新增商品：
  - 名称（中文/英文）
  - 简介（中文/英文）
  - 详细描述（中文/英文）
  - 价格、折扣
  - 分类选择
  - 热卖、上架状态
  - 图片上传（多图，可排序、选择封面）
- 编辑商品：修改任意字段
- 删除商品：逻辑删除（is_active=0）

### 2.3 分类管理
- 分类列表：ID、中文/英文名称、排序、状态、操作
- 新增/编辑分类：支持中英文
- 删除分类：逻辑删除（is_active=0）

### 2.4 图片管理
- 列表展示商品图片
- 上传图片、删除图片
- 排序图片、设置封面

### 2.5 联系我们信息管理
- 微信号修改
- 二维码上传
- 仅限管理员操作

### 2.6 收藏统计（可选）
- 查看按商品聚合的收藏数量（例如在商品列表中展示 favorite_count 字段）
- 支持按收藏数量排序，帮助运营挑选热门商品

---

## 3. 页面结构

### 3.1 登录页
- 输入框：用户名、密码
- 登录按钮
- 提示登录失败信息

### 3.2 仪表盘
- 快捷入口：商品管理、分类管理
- 统计信息：商品总数、热卖商品数

### 3.3 商品管理页
- 列表页：表格展示商品信息
- 搜索栏：按名称或分类
- 新增/编辑弹窗：表单输入商品信息、上传图片
- 操作按钮：编辑 / 删除

### 3.4 分类管理页
- 列表页：表格展示分类信息
- 新增/编辑弹窗：输入中文/英文名称、排序、状态
- 操作按钮：编辑 / 删除

### 3.5 图片管理页
- 列表页：显示商品图片
- 上传 / 删除 / 排序 / 设置封面按钮

### 3.6 联系我们页
- 微信号输入框
- 二维码上传组件

---

## 4. 后端接口设计（Admin API）

### 4.1 登录接口
- POST /admin/login
- 参数：username, password
- 返回：token, 用户信息

### 4.2 商品管理接口
- GET /admin/products?category_id=&search=&page=&size=
- POST /admin/products
- PUT /admin/products/{id}
- DELETE /admin/products/{id}

### 4.3 分类管理接口
- GET /admin/categories
- POST /admin/categories
- PUT /admin/categories/{id}
- DELETE /admin/categories/{id}

### 4.4 图片管理接口
- GET /admin/product-images?product_id=
- POST /admin/product-images
- PUT /admin/product-images/{id} （排序 / 设置封面）
- DELETE /admin/product-images/{id}

### 4.5 联系我们信息接口
- GET /admin/contact
- PUT /admin/contact

---

## 5. 数据流设计

管理员操作页面 → 发送请求（带 token） → Controller

↓

权限验证（JWT/Session）

↓

Service 层处理业务逻辑

↓

Repository 层操作数据库

↓

返回 JSON 数据给前端

---

## 6. 安全设计
- HTTPS 访问
- JWT / Session 验证
- 管理员角色权限控制
- 上传文件限制（大小、类型）
- 日志记录：操作日志、登录日志

---

## 7. 多语言支持
- 商品、分类、简介、详细信息均可按 lang 参数处理
- 前端提供语言切换按钮
- 后端接口返回对应语言数据

---

## 8. 系统约束
- 与小程序后端共享数据库
- 接口遵守 RESTful 规范
- 可扩展权限管理和功能模块
```





好，我们接下来写 **FluffyLetter 前端小程序设计文档**，详细说明页面结构、组件设计、交互流程和接口对接，保证前端开发能直接落地。

## 5.**前端设计文档**

UI风格参考苹果圆润UI风格



```
# FluffyLetter 前端小程序设计文档

## 1. 页面概述
小程序页面：
1. 首页
2. 分类页
3. 商品详情页
4. 收藏页
5. 联系我们页
6. 关于我们页
7. 设置/语言切换（导航栏）

---

## 2. 页面结构与组件

### 2.1 首页
- 顶部导航栏
  - 左侧：品牌 Logo + 名称（点击跳转关于我们）
  - 右侧：搜索按钮、收藏按钮、语言切换按钮
- 轮播图组件
  - 图片自动轮播 + 点击跳转商品
- 推荐商品区
  - 方形圆角卡片：缩略图、商品名称、价格、折扣、热卖标识
  - 点击卡片跳转商品详情页
- 悬浮联系我们按钮
  - 点击跳转联系我们页

### 2.2 分类页
- 顶部搜索栏
- 分类选择条
- 商品列表
  - 方形圆角卡片：缩略图、名称、价格、折扣
  - 点击跳转商品详情页
- 分页/滚动加载

### 2.3 商品详情页
- 商品轮播图
- 商品基本信息：名称、简介、价格、折扣、热卖标识
- 商品详细信息：多语言描述
- 收藏按钮（基于当前用户 openid 的云端收藏状态）
- 联系我们按钮

### 2.4 收藏页（暂定）
### 2.4 收藏页
- 从后端拉取当前用户收藏的商品列表（基于 openid / userToken）
- 列表项使用与首页/分类页相同的商品卡片样式
- 支持下拉刷新 / 进入页面自动刷新收藏数据
- 点击商品卡片可跳转详情页
- 在收藏页中支持直接取消收藏（取消后列表中移除）

### 2.5 联系我们页
- 显示官方微信号
- 显示二维码
- 点击按钮可复制微信号或保存二维码

### 2.6 关于我们页
- 品牌故事文字
- 图片展示（可选）

---

## 3. 交互设计

- 点击商品卡片 → 跳转详情页
- 点击分类 → 筛选商品
- 搜索框输入 → 返回搜索结果
- 语言切换按钮 → 页面刷新显示对应语言数据
- 收藏按钮 → 根据当前状态在后端新增/删除收藏，并更新本地 UI 状态
- 收藏页 → 自动加载当前用户收藏列表，滑动/点击取消收藏后刷新列表
- 悬浮联系我们按钮 → 跳转联系我们页

---

## 4. 数据与接口对接

### 4.1 获取分类列表

GET /api/categories?lang=zh


### 4.2 获取商品列表

GET /api/products?category_id={id}&lang=zh&page=1&size=10

### 4.3 获取商品详情

GET /api/products/{id}?lang=zh

### 4.4 收藏商品（可选功能）
### 4.4 收藏商品（后端收藏功能）
- 小程序端通过 wx.login 获取 code，调用后端登录接口换取 userToken（内部绑定 openid）
- 收藏商品：调用 POST /api/favorites/{productId}
- 取消收藏：调用 DELETE /api/favorites/{productId}
- 获取收藏列表：调用 GET /api/favorites?lang=zh&page=1&size=20

### 4.5 联系我们信息


GET /api/contact

### 4.6 用户登录与 openid 同步
- 小程序启动时调用 wx.login 获取 code
- 调用后端登录/注册接口（例如 POST /api/login/wechat），后端通过 code2Session 换取 openid
- 后端根据 openid 在 wechat_user 表中创建或查找用户，并返回 userToken
- 小程序将 userToken 缓存到本地（storage），后续调用收藏等需要用户身份的接口时在 Header 中携带

---

## 5. 多语言设计
- 页面加载时从全局变量获取当前语言 `lang`
- 所有接口请求带上 `lang` 参数
- 后端返回对应语言的商品名称、简介、描述
- 文案本地化：
  - 固定按钮/标题文字通过本地多语言文件管理（zh.js / en.js）

---

## 6. 页面状态管理
- 全局 Store 管理：
  - 用户收藏列表（从后端拉取并缓存）
  - 当前语言
  - 分类选择状态
- 页面生命周期：
  - onLoad：请求数据
  - onPullDownRefresh：刷新数据
  - onReachBottom：分页加载更多商品
  - 小程序冷启动时：检测本地是否已存在 userToken，如无则发起登录流程，确保收藏功能可用


## 8. 安全与性能
- 请求数据带 token（如果有登录功能）
- 图片大小压缩，懒加载
- 接口异常处理：显示错误提示
- 支持 iOS / Android 微信版本兼容

---

## 9. 页面导航结构


首页

├── 分类页

├── 商品详情页

├── 收藏页

├── 联系我们页

└── 关于我们页

---

## 10. 总结
- 所有页面均支持多语言显示
- 数据通过 RESTful 接口获取
- 页面组件统一风格：圆角卡片、悬浮按钮、轮播图
```







## **Service 示例**



```
@Service
public class ProductService {
  @Autowired
  private ProductRepository productRepo;
  @Autowired
  private ProductI18nRepository i18nRepo;
  @Autowired
  private ProductImageRepository imageRepo;

  public List<ProductDTO> getProductList(Long categoryId, String lang, int page, int size) { ... }
  public ProductDTO getProductDetail(Long id, String lang) { ... }
}

```