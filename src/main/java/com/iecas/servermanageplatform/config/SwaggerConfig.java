/**
 * @Time: 2025/2/6 16:03
 * @Author: guoxun
 * @File: SwaggerConfig
 * @Description:
 */

package com.iecas.communitycommon.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("融合创新社区 API 文档")
                        .version("1.0")
                        .description("融合创新开发文档"));
    }
}
