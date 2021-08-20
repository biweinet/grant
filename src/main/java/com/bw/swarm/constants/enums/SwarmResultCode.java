package com.bw.swarm.constants.enums;

import lombok.Getter;

@Getter
public enum SwarmResultCode {
    SUCCESS(200),
    OK(201);


    private int code;

    SwarmResultCode(int code) {
        this.code = code;
    }
}
