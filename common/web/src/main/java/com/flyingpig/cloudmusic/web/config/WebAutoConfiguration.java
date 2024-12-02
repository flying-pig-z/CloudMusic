package com.flyingpig.cloudmusic.web.config;

import com.flyingpig.cloudmusic.web.exception.GlobalExceptionHandler;
import com.flyingpig.cloudmusic.web.init.InitializeDispatcherServletController;
import com.flyingpig.cloudmusic.web.init.InitializeDispatcherServletHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class WebAutoConfiguration {

    public final static String INITIALIZE_PATH = "/initialize/dispatcher-servlet";

    @Bean
    @ConditionalOnMissingBean// 只有在 Spring 容器中没有定义特定类型的 Bean 时，才会创建该 Bean
    @ConditionalOnMissingClass("org.springframework.web.reactive.DispatcherHandler") // 检查 WebFlux 是否存在
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingClass("org.springframework.web.reactive.DispatcherHandler") // 检查 WebFlux 是否存在
    public InitializeDispatcherServletController initializeDispatcherServletController() {
        return new InitializeDispatcherServletController();
    }

    @Bean
    @ConditionalOnMissingClass("org.springframework.web.reactive.DispatcherHandler") // 检查 WebFlux 是否存在
    public RestTemplate simpleRestTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }

    @Bean
    @ConditionalOnMissingClass("org.springframework.web.reactive.DispatcherHandler") // 检查 WebFlux 是否存在
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);
        return factory;
    }

    @Bean
    @ConditionalOnMissingClass("org.springframework.web.reactive.DispatcherHandler") // 检查 WebFlux 是否存在
    public InitializeDispatcherServletHandler initializeDispatcherServletHandler(RestTemplate simpleRestTemplate, ConfigurableEnvironment configurableEnvironment) {
        return new InitializeDispatcherServletHandler(simpleRestTemplate, configurableEnvironment);
    }
}