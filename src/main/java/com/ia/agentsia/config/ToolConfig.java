package com.ia.agentsia.config;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ia.agentsia.repos.IBookRepo;
import com.ia.agentsia.services.impl.BookToolServiceImpl;
import com.ia.agentsia.services.impl.MockWeatherService;

@Configuration
public class ToolConfig {

    @Bean
    public ToolCallback weatherFunctionInfo() {
        return FunctionToolCallback.builder("weatherFunction", new MockWeatherService())
                .description("Get the weather in location")
                .inputType(MockWeatherService.Request.class)
                .build();
    }

    @Bean
    public ToolCallback bookInfoFunction(IBookRepo repo) {
        return FunctionToolCallback.builder("bookInfoFunction", new BookToolServiceImpl(repo))
                .description("Get book into from book name")
                .inputType(BookToolServiceImpl.Request.class)
                .build();
    }

}
