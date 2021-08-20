package com.bw.swarm.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwarmUploadFileReq {
    @ApiModelProperty(value = "文件名")
    private String name;
}
