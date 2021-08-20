package com.bw.swarm.bean;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WalletTransferInfo {
    private Integer id;

    private String dir;

    private String address;

    private BigDecimal xbzzValue;

    private String createBy;

    private Date createTime;

    private String updateBy;

    private Date updateTime;

    private String privateKey;

    private String password;

    private Integer xdaiStatus;


}