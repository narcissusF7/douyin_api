# 抖音（抖店）开放平台 Java 集成脚手架

基于 **Spring Boot 2.7 + Java 8 + Maven**（可在 JDK 8 环境直接编译运行），封装抖店开放平台
（openapi-fxg.jinritemai.com）核心能力，覆盖 **店铺 / 商品 / 订单 / 素材 / 经营数据（抖店罗盘）**
五大类接口，包含统一的 MD5 签名、OAuth 授权码换 token、token 自动刷新与缓存，
以及**对外接口 API-Key 鉴权**、**消息推送订阅（webhook）** 与**单元测试**。

> ⚠️ 接口 `method` 名称与响应字段以 [抖店开放平台官方文档](https://op.jinritemai.com) 为准。
> 本脚手架已按公开文档核实网关、公共参数与签名算法；少量 method（尤其罗盘 compass.*）为常用命名，
> 接入时请对照官方文档确认方法名与字段。

---

## 1. 工程结构（按功能组织 + 分层）

```
com.dsh.douyin
├── config/                # 配置绑定与 HTTP 客户端（启动时校验凭证、失败快速）
├── common/
│   ├── error/             # 类型化异常 + 全局异常处理（不向客户端暴露堆栈）
│   ├── model/             # ApiResult（平台响应信封）、Result（控制器统一返回）
│   ├── util/              # SignUtil（MD5 签名）、DateTimeUtils、JsonUtils
│   └── log/               # 请求链路 traceId、出站 HTTP 日志（不打印密钥）
├── core/                  # DouyinApiClient（签名调用）、AuthService、AccessTokenManager
│   └── model/             # TokenResult / TokenEntry
├── module/                # 业务模块，每个模块 = Api 封装 + DTO
│   ├── shop/              # shop.shopList
│   ├── product/           # product.listV2 / product.getDetail
│   ├── order/             # order.searchList / order.detail
│   ├── material/          # material.searchMaterial
│   └── data/             # compass.* 经营数据（返回原始 JSON）
├── security/              # API-Key 鉴权拦截器 + Web 注册
├── message/               # 消息推送：签名校验 / 事件模型 / 分发 / 接收端点
└── controller/            # 演示用 REST 端点
```

---

## 2. 配置

复制以下环境变量（**切勿把真实密钥提交进代码库**）：

```bash
export DOUYIN_APP_KEY=你的app_key
export DOUYIN_APP_SECRET=你的app_secret
export DOUYIN_REDIRECT_URI=https://你的域名/api/douyin/auth/callback
export DOUYIN_SCOPE=                      # 按需填写授权 scope，逗号分隔
export DOUYIN_API_KEY=你的接口密钥          # 对外接口 API-Key 鉴权（生产必配）
export DOUYIN_MESSAGE_TOKEN=你的消息推送token  # 开放平台「消息订阅」后台配置的 token
export DOUYIN_MOCK_ENABLED=false              # 本地 Mock 模式（演示/联调用，生产务必 false）
```

或在 `application.yml` 的 `douyin.*` 下直接填写（仅本地调试用，加入 .gitignore）。

---

## 3. 签名算法（已内置，无需手写）

1. 除 `sign` 外所有参数（公共参数 + `param_json` 业务 JSON，空值跳过）按 **key 的 ASCII 升序** 排列；
2. 依次拼接为 `key + value`（不 URL encode）；
3. 首尾包裹 `app_secret`：`app_secret + 拼接串 + app_secret`；
4. 对结果做 **MD5**，再转 **大写**。

公共参数：`method / app_key / access_token / param_json / timestamp / v=2 / sign / sign_method(md5)`，
其中 `timestamp` 格式为 `yyyy-MM-dd HH:mm:ss`（GMT+8）。

---

## 4. 授权流程（第三方应用）

1. 引导商家访问授权页（由后端生成）：`GET /api/douyin/auth/url`，返回类似
   `https://openweb.jinritemai.com/doudian/shop/authorize?client_key=...&response_type=code&scope=...&state=...&redirect_uri=...`
2. 商家授权后，平台携带 `code` 跳回 `redirect_uri`（`/api/douyin/auth/callback?code=xxx`）；
3. 回调中用 `code` 调 `token.create` 换取 `access_token` 与 `refresh_token`，按 `shop_id` 缓存；
4. 之后调用业务接口时，`AccessTokenManager` 自动附加 `access_token`，临近过期用 `refresh_token` 自动刷新。

---

## 5. 对外接口 API-Key 鉴权

演示控制器下的 `/api/douyin/**` 接口（除 `auth/**` 与 `push/**`）默认开启 API-Key 鉴权，
要求请求头携带与配置一致的密钥（默认头名 `X-API-Key`）：

```
GET /api/douyin/shops?shopId=xxx
Header: X-API-Key: 你的接口密钥
```

- `douyin.api.security.enabled=false` 可整体关闭；
- 开启但未配置 `api-key` 时打印 WARN 并**放行**（仅本地联调，生产务必配置 `DOUYIN_API_KEY`）；
- 缺/错密钥返回 `401 {"code":401,"message":"未授权：缺少或错误的 API-Key"}`。

---

## 6. 消息推送订阅（webhook）

对应开放平台「消息订阅 / 消息推送」能力，接收订单、库存、退款等实时事件：

- `GET /api/douyin/push?signature=&timestamp=&nonce=&echostr=` —— 平台配置推送 URL 时的签名校验，
  通过则原样回显 `echostr`；
- `POST /api/douyin/push?signature=&timestamp=&nonce=` —— 业务事件推送，先校验签名再分发处理；
- `GET /api/douyin/messages` —— 查看最近收到的推送事件（联调查看，受 API-Key 鉴权保护）。

签名校验采用微信公众号同款算法：`sha1(排序(token, timestamp, nonce) 拼接)`（详见
`message/PushSignatureVerifier`，如平台采用不同密钥/拼接方式仅改该方法实现即可）。
事件由 `MessageHandler` 策略分发，新增处理器只需实现接口并注册为 Spring Bean，
默认 `LoggingMessageHandler` 会记录全部事件（生产请替换为落库 / 发往消息队列）。

---

## 7. 调用示例

```bash
# 0) 设置接口密钥（演示用，生产请走环境变量）
export DOUYIN_API_KEY=demo-key

# 1) 获取授权页 URL
curl -H "X-API-Key: demo-key" http://localhost:8080/api/douyin/auth/url

# 2) 授权回调（实际由平台跳转，此处演示带 code 调用）
curl -H "X-API-Key: demo-key" "http://localhost:8080/api/douyin/auth/callback?code=商家的授权码"

# 3) 店铺列表
curl -H "X-API-Key: demo-key" "http://localhost:8080/api/douyin/shops?shopId=店铺ID"

# 4) 商品列表（第 1 页，每页 20）
curl -H "X-API-Key: demo-key" "http://localhost:8080/api/douyin/products?shopId=店铺ID&page=0&size=20"

# 5) 商品详情
curl -H "X-API-Key: demo-key" "http://localhost:8080/api/douyin/products/123456?shopId=店铺ID"

# 6) 订单列表
curl -H "X-API-Key: demo-key" "http://localhost:8080/api/douyin/orders?shopId=店铺ID&orderState=1"

# 7) 素材列表（0=图片 1=视频）
curl -H "X-API-Key: demo-key" "http://localhost:8080/api/douyin/materials?shopId=店铺ID&materialType=0"

# 8) 经营数据-店铺核心指标
curl -H "X-API-Key: demo-key" "http://localhost:8080/api/douyin/data/daily-summary?shopId=店铺ID&startTime=2026-01-01 00:00:00&endTime=2026-01-31 23:59:59"

# 9) 模拟平台推送签名校验（GET 回显 echostr）
curl "http://localhost:8080/api/douyin/push?signature=xxx&timestamp=1234567890&nonce=abc&echostr=HELLO"
```

也可在代码中直接注入各 `*Api` 使用，例如：

```java
@Autowired private ProductApi productApi;

ProductListResult result = productApi.listProducts(shopId, query);
```

---

## 8. 运行

```bash
mvn clean package && java -jar target/douyin-open-api.jar
# 或开发模式
mvn spring-boot:run
```

---

## 9. 本地 Mock 模式（无需真实凭证即可演示）

个人认证 / 无 `app_key` 时，开启 Mock 后所有抖店网关调用改为返回**仿真数据**，
可直接在 Swagger 与前端测试页完整演示「授权 → 店铺 / 商品 / 订单 / 素材 / 经营数据」全链路，无需企业认证或真实店铺。

- 开启方式（任选其一）：
  - 环境变量：`export DOUYIN_MOCK_ENABLED=true`
  - 或修改 `application.yml` 的 `douyin.mock.enabled: true`（注意为嵌套结构）
  - 或启动参数：`java -jar xxx.jar --douyin.mock.enabled=true`
- 行为：
  - `DouyinApiClient` 不再请求真实网关，由 `core/DouyinMockProvider` 按 `method` 返回仿真响应
    （字段命名与抖店开放平台一致，snake_case）；`product.getDetail` / `order.detail` 会回显请求中的 ID；
  - `AccessTokenManager` 在 Mock 下跳过「未授权」校验，任意 `shopId` 均可直接调用业务接口；
  - `auth/url` 在未配置 `redirect-uri` 时给出本地默认回调地址，便于走通授权演示；
  - `DouyinProperties.validate()` 在 Mock 下跳过凭证校验，应用可无凭证启动。
- 仿真数据示例：店铺「抖店Mock旗舰店」、商品 2 条、订单 2 条、素材 2 条、经营数据日汇总等。
- ⚠️ **生产环境务必关闭**（默认值即为 false），避免返回伪造数据。

---

## 10. 前端接口测试页面

项目内置一个零依赖的单文件测试台 `src/main/resources/static/douyin-api-test.html`，
打包后随 jar 一起发布，访问 `http://<host>:<port>/douyin-api-test.html` 即可使用。
页面为纯原生 HTML/JS，无需构建、无需联网，覆盖全部后端端点。

**功能**
- 顶部统一配置 **Base URL / X-API-Key / 消息 Token**，自动存入浏览器 localStorage；
- 左侧分组导航（授权 / 店铺 / 商品 / 订单 / 素材 / 经营数据 / 消息推送 / 推送记录），点击直达；
- 每个接口自动生成表单（含必填标记、默认值、路径参数替换）；
- 响应区展示 **HTTP 状态码、耗时、JSON 语法高亮、一键复制**；
- **消息推送签名一键生成**：前端用与后端 `PushSignatureVerifier` 完全一致的 SHA1 算法，
  按 `SHA1(排序(token, timestamp, nonce) 拼接)` 计算并填入 `signature/timestamp/nonce`，
  可直接模拟平台推送请求（已用后端黄金向量校验两侧实现一致）。

**用法**
```bash
mvn clean package && java -jar target/douyin-open-api.jar
# 浏览器打开（同源，无需跨域）：
#   http://localhost:8080/douyin-api-test.html
# 也可单独把该 html 放到任意静态服务器/file:// 打开（已开启 CORS，见下方说明）
```

> 页面通过 `/api/**` 的跨域配置（`WebConfig.addCorsMappings`，允许任意来源、不携带凭据、
> API-Key 走请求头而非 Cookie）支持独立托管；生产环境请勿暴露此页面，并收紧 CORS 来源。

---

## 11. 单元测试

已覆盖签名、底层调用、token 管理、接口鉴权、推送签名、配置校验，共 **18 个用例**，全部基于
`MockRestServiceServer` / Mockito，无需联网或真实密钥即可运行：

```bash
mvn test
```

| 测试类 | 覆盖点 |
| --- | --- |
| `SignUtilTest` | MD5 签名：ASCII 排序、空值/sign 跳过、顺序无关（黄金向量断言） |
| `DouyinApiClientTest` | 拼公共参数+签名后表单 POST、响应解析、平台错误码转异常 |
| `AccessTokenManagerTest` | 未授权抛错、正常取用、临近过期自动刷新 |
| `ApiKeyAuthInterceptorTest` | 关闭/放行/密钥正确/密钥错误(401) 四种路径 |
| `PushSignatureVerifierTest` | 推送签名：合法放行、篡改拒绝、缺参拒绝（SHA1 黄金向量） |
| `DouyinPropertiesTest` | 凭证缺失快速失败、配置齐全通过 |

---

## 12. 接口文档（Swagger / OpenAPI）

已集成 `springdoc-openapi`（兼容 Spring Boot 2.7），零注解即可生成交互式文档：

- **在线文档界面**：`http://{host}:{port}/swagger-ui.html`（运行应用后访问，右上角 “Authorize” 可填入 X-API-Key 后直接试调用）
- **OpenAPI 3 规范（JSON）**：`http://{host}:{port}/v3/api-docs`
- **静态规范文件**（已落地，可导入 Postman / Apifox / YApi）：`docs/openapi.json`、`docs/openapi.yaml`

文档涵盖全部端点：授权（auth/url、auth/callback）、店铺、商品（列表/详情）、订单（列表/详情）、素材、经营数据、消息推送（GET 校验 / POST 接收）、推送记录。
所有需鉴权接口均标注 `X-API-Key`（header）安全方案；`@Operation` / `@Parameter` 注解已补充中文摘要与必填说明。

> 生成方式：`mvn package` 后启动应用，规范由 springdoc 在运行时自动产出；`docs/` 下为导出快照，接口变更后建议重新导出。

---

## 13. 待核对 / 后续建议

- **method 名称**：`product.getDetail`、`order.searchList`、`order.detail`、`material.searchMaterial`、
  `compass.*` 等以官方文档最新版为准，必要时调整 `module/*/XxxApi.java` 中的 method 字符串。
- **响应字段**：列表接口容器字段名文档间略有差异，已用 `@JsonAlias` 兼容 `data/list/shops` 等，
  若仍有字段未映射，在对应 DTO 中补充字段即可（`@JsonIgnoreProperties(ignoreUnknown = true)` 保证不报错）。
- **token 持久化**：当前缓存在应用内存，多实例部署请改为 Redis 等共享存储，并对 `refresh_token` 加密保存。
- **消息推送签名**：默认采用 SHA1(排序拼接) 方案，若开放平台实际算法不同，仅改 `PushSignatureVerifier` 即可。
- **安全**：`app_secret` 仅服务端持有；`access_token` 不进 URL query、不落前端；生产环境为推送端点与回调配置
  HTTPS 与固定 IP 白名单，并叠加限流。
