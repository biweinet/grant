package com.bw.swarm.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * swarm配置类
 */
@Configuration
@Data
public class SwarmConfig {

    @Value("${swarm.prefix.url}")
    private String prefixUrl;

    @Value("${Swarm-Postage-Batch-Id}")
    private String batchId;


}
