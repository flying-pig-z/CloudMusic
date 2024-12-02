package com.flyingpig.cloudmusic.idempotent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * 幂等属性配置
 */
@Data
@ConfigurationProperties(prefix = IdempotentProperties.PREFIX)
public class IdempotentProperties {

    public static final String PREFIX = "flyingpig.idempotent.token";
}
