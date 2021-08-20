package com.bw.swarm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.bw.swarm.dao")
@SpringBootApplication
public class GrantsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrantsApplication.class, args);
    }

}
