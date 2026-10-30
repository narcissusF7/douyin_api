package com.dsh.douyin.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI（Swagger）配置：生成交互式接口文档，并在 Swagger UI 提供 X-API-Key 鉴权按钮。
 *
 * <p>访问地址：
 * <ul>
 *   <li>文档界面：<code>http://{host}:{port}/swagger-ui.html</code></li>
 *   <li>OpenAPI 3 规范（JSON）：<code>http://{host}:{port}/v3/api-docs</code></li>
 * </ul>
 * 说明：除 <code>/api/douyin/auth/**</code>（平台 OAuth 回跳）与 <code>/api/douyin/push/**</code>（消息签名校验）外，
 * 其余接口均需在请求头携带 X-API-Key；在 Swagger UI 点 “Authorize” 填入即可。
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME_API_KEY = "apiKey";

    @Bean
    public OpenAPI douyinOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("抖音（抖店）开放平台 API")
                        .description("抖店开放平台 Java 集成脚手架接口文档。覆盖授权、店铺、商品、订单、素材、经营数据，"
                                + "以及消息推送订阅。\n\n"
                                + "鉴权：除授权回调与消息推送外，所有 /api/douyin/** 接口需在请求头携带 X-API-Key。"
                                + "在右上角 “Authorize” 中填入与 douyin.api-key 一致的值（本仓库 application.yml 已内置本地联调默认值）。")
                        .version("1.0.0")
                        .contact(new Contact().name("douyin-open-api").email("dev@example.com")))
                .components(new Components().addSecuritySchemes(SCHEME_API_KEY,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-API-Key")
                                .description("对外接口 API-Key，值为 douyin.api.security.api-key")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_API_KEY));
    }
}
