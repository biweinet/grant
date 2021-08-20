package com.bw.swarm.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@Data
@Slf4j
public class Web3jConfig {

    @Value("${web3j.xdai.url}")
    private String xdaiUrl;

    public Web3j getXdaiWeb3j() {
        try {
            return Web3j.build(new HttpService(xdaiUrl));
        } catch (Exception e) {
            log.error("xdai web3j 实例化失败", e);
        }
        return null;
    }
}
