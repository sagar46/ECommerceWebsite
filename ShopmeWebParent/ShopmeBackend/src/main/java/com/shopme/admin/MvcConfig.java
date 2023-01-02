package com.shopme.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "user-photos";
        Path userPhotos = Paths.get(dirName);
        String userPhotosPath = userPhotos.toFile().getAbsolutePath();
        registry.addResourceHandler("/" + dirName +"/**")
                .addResourceLocations("file:/" + userPhotosPath +"/");
    }
}
