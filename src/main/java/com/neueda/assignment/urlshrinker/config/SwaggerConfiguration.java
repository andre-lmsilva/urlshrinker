package com.neueda.assignment.urlshrinker.config;

import io.swagger.models.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    public static final Contact CONTACT;

    public static final ApiInfo API_INFO = new ApiInfo("URL Shrinker",
        "Simple URL shortner Restful API", "0.1.0", "urn:tos", ApiInfo.DEFAULT_CONTACT,
        "None", "None", Arrays.asList());

    public static final Set<String> SUPPORTED_MIME_TYPES = Set.of("application/json");

    static {
        CONTACT = new Contact();
        CONTACT.setName("Andre Silva");
        CONTACT.setEmail("andre.lmsilva@outlook.com");
        CONTACT.setUrl("https://linkedin.com/almsrj");
    }

    @Bean
    public Docket prepareSwaggerDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(API_INFO)
            .produces(SUPPORTED_MIME_TYPES)
            .consumes(SUPPORTED_MIME_TYPES);
    }


}
