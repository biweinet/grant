package com.bw.swarm.utils;

import com.bw.swarm.configuration.SwarmConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 请求url组装工具类
 */
@Component
public class UrlUtils {

    @Autowired
    private SwarmConfig swarmConfig;

    public String generateSwarmUrl(String suffixUrl) {
        return String.format("%s%s", swarmConfig.getPrefixUrl(), suffixUrl);
    }
}
