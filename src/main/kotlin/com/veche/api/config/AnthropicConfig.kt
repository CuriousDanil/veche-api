package com.veche.api.config

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * TODO()
 */
@Configuration
class AnthropicConfig {
    /**
     * TODO()
     *
     * @return TODO()
     */
    @Bean
    fun anthropicClient(): AnthropicClient = AnthropicOkHttpClient.fromEnv()
}
