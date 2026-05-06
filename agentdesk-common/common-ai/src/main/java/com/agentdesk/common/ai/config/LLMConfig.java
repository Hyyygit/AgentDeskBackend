package com.agentdesk.common.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "llm.deepseek")
public class LLMConfig {

    private String apiKey;

    private String baseUrl = "https://api.deepseek.com";

    private String model = "deepseek-chat";

    private Integer maxTokens = 4096;

    private Double temperature = 0.7;

    private Integer timeout = 60000;
}
