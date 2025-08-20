package com.veche.api.config

import com.veche.api.security.JwtAuthFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
) {
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val cfg = CorsConfiguration()
        cfg.allowedOrigins = listOf("https://localhost:5173")
        cfg.allowedMethods = listOf("GET", "POST", "PATCH", "DELETE")
        cfg.allowedHeaders = listOf("Content-Type", "Authorization", "X-Requested-With")
        cfg.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", cfg)
        return source
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/api/auth/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
