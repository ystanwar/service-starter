//package com.thoughtworks;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.web.servlet.view.InternalResourceViewResolver;
//
//// @Configuration
//// public class StaticResourceConfig extends WebMvcConfigurationSupport {
//
////     private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
////             "classpath:/META-INF/resources/", "classpath:/static/" };
//
////     @Override
////     protected void addResourceHandlers(ResourceHandlerRegistry registry) {
////         super.addResourceHandlers(registry);
//
////         registry
////                 .addResourceHandler("/**")
////                 .addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
////     }
//
////     @Bean
////     public InternalResourceViewResolver defaultViewResolver() {
////         return new InternalResourceViewResolver();
////     }
//
//// }
