package com.gestion_tarea;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CustomCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:8081");
        config.addAllowedOrigin("http://192.168.100.137:8081");
        config.addAllowedOrigin("http://192.168.100.128:8081");
        config.addAllowedOrigin("http://23.20.232.178");  // Cambiar el origen a la IP del servidor
          config.addAllowedOrigin("https://gestion-tarea-vue-x46i.vercel.app");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
