package com.firstclub.membership.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI firstClubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FirstClub Membership API")
                        .description("""
                                Backend for FirstClub's tiered membership program.
                                
                                **Plans**: Monthly · Quarterly · Yearly
                                
                                **Tiers**: Silver · Gold · Platinum — each with configurable benefits and auto-evaluation criteria
                                
                                **Benefits applied automatically** on orders: discounts, free delivery, etc.
                                """)
                        .version("1.0.0")
                        .contact(new Contact().name("FirstClub Engineering")));
    }
}
