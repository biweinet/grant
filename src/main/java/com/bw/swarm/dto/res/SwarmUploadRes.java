package com.bw.swarm.dto.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwarmUploadRes {
    private String reference;

    @ApiModelProperty(value = "取件码")
    private String receiveCode;

    @ApiModelProperty(value = "进度id")
    private String uid;

}
