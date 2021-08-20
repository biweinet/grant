package com.bw.swarm.dto.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FileListReq {
    @ApiModelProperty(value = "取件码")
    private String receiveCode;

}
