package com.a407.back.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String HOST;

    @Value("${spring.data.redis.certification.port}")
    private int CERTIFICATION_PORT;

    @Value("${spring.data.redis.association.port}")
    private int ASSOCIATION_PORT;

    @Value("${spring.data.redis.token.port}")
    private int TOKEN_PORT;

    @Value("${spring.data.redis.sse.port}")
    private int SSE_PORT;

    @Value("${spring.data.redis.password}")
    private String PASSWORD;

    @Bean(name = "sseRedisConnectionFactory")
    public RedisConnectionFactory sseRedisConnectionFactory() {
        RedisStandaloneConfiguration config = standaloneConfiguration(HOST, SSE_PORT, PASSWORD);
        return new LettuceConnectionFactory(config);
    }

    @Primary
    @Bean(name = "certificationRedisConnectionFactory")
    public RedisConnectionFactory certificationRedisConnectionFactory() {
        RedisStandaloneConfiguration config = standaloneConfiguration(HOST, CERTIFICATION_PORT, PASSWORD);
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "associationRedisConnectionFactory")
    public RedisConnectionFactory associationRedisConnectionFactory() {
        RedisStandaloneConfiguration config = standaloneConfiguration(HOST, ASSOCIATION_PORT, PASSWORD);
        return new LettuceConnectionFactory(config);
    }

    @Bean(name = "refreshTokenRedisConnectionFactory")
    public RedisConnectionFactory refreshTokenRedisConnectionFactory() {
        RedisStandaloneConfiguration config = standaloneConfiguration(HOST, TOKEN_PORT, PASSWORD);
        return new LettuceConnectionFactory(config);
    }

    @Primary
    @Bean(name = "certificationRedisTemplate")
    public RedisTemplate<String, String> certificationRedisTemplate(
        @Qualifier(value = "certificationRedisConnectionFactory") RedisConnectionFactory certificationRedisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplateSetting(certificationRedisConnectionFactory, redisTemplate);
        return redisTemplate;
    }

    @Bean(name = "associationRedisTemplate")
    public RedisTemplate<String, String> associationRedisTemplate(
        @Qualifier(value = "associationRedisConnectionFactory") RedisConnectionFactory associationRedisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplateSetting(associationRedisConnectionFactory, redisTemplate);
        return redisTemplate;
    }

    @Bean(name = "refreshTokenRedisTemplate")
    public RedisTemplate<String, String> refreshTokenRedisTemplate(
        @Qualifier(value = "refreshTokenRedisConnectionFactory") RedisConnectionFactory refreshTokenRedisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplateSetting(refreshTokenRedisConnectionFactory, redisTemplate);
        return redisTemplate;
    }

    @Bean(name = "sseRedisTemplate")
    public RedisTemplate<String, Object> sseRedisTemplate(
        @Qualifier(value = "sseRedisConnectionFactory") RedisConnectionFactory sseRedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        sseRedisTemplateSetting(sseRedisConnectionFactory, redisTemplate);
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListener(
        @Qualifier(value = "sseRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    private static RedisStandaloneConfiguration standaloneConfiguration(String host, int port, String password) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(RedisPassword.of(password));
        return config;
    }

    private static void redisTemplateSetting(
        RedisConnectionFactory redisConnectionFactory,
        RedisTemplate<String, String> redisTemplate) {
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(RedisSerializer.string());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
    }

    private static void sseRedisTemplateSetting(@Qualifier(value = "sseRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory, RedisTemplate<String, Object> redisTemplate) {
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
    }

}