package com.sib.ibanklosucl.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
@Bean
    public OpenApiCustomiser customOpenAPIConfig() {
        return new OpenApiCustomiser() {

            @Override
            public void customise(OpenAPI openApi) {
                openApi.addServersItem(new Server().url("https://infobankuat.sib.co.in:8443/analytical/"));
                // Adjust the URL to your server, port, and context path
            }
        };
    }
}
