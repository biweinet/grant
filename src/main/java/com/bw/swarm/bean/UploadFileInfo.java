package com.bw.swarm.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName(value = "upload_file_info")
public class UploadFileInfo {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Long tagUid;

    @ApiModelProperty(value = "文件名")
    private String name;

    private Integer type;

    private String reference;

    @ApiModelProperty(value = "0:上传完成 1:正在上传 2:上传失败")
    private Integer status;

    @ApiModelProperty(value = "取件码")
    private String receiveCode;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String batchId;

    private BigDecimal size;
}