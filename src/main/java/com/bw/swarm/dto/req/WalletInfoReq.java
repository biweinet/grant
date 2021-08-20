package com.bw.swarm.dto.req;

import com.bw.swarm.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WalletInfoReq {

 /*   @Excel(name = "钱包私钥")
    private String privateKey;

    @Excel(name = "到账钱包地址")
    private String toAddress;

    @Excel(name = "转账金额")
    private BigDecimal value;*/

    @Excel(name = "privateKey")
    private String privateKey;

    @Excel(name = "toAddress")
    private String toAddress;

    @Excel(name = "value")
    private BigDecimal value;
}
