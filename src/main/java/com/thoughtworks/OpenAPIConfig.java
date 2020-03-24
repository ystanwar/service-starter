// package com.thoughtworks;


// import io.swagger.v3.oas.models.Components;
// import io.swagger.v3.oas.models.OpenAPI;
// import io.swagger.v3.oas.models.info.Contact;
// import io.swagger.v3.oas.models.info.Info;
// import io.swagger.v3.oas.models.info.License;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class OpenAPIConfig {

//     @Bean
//     public OpenAPI customOpenAPI() {
//         return new OpenAPI()
//                 .components(new Components())
//                 .info(new Info()
//                         .title("payment service")
//                         .description("payment service used to make the transaction between two users")
//                         .version("1.0.0")
//                         .contact(new Contact().email("example@gmail.com"))
//                         .license(new License()
//                                 .name("Apache 2.0")
//                                 .url("http://www.apache.org/licenses/LICENSE-2.0.html"))
//                 );
//     }
// }


