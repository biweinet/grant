package com.bw.swarm.controller;

import com.bw.swarm.configuration.Web3jConfig;
import com.bw.swarm.dto.RestResponse;
import com.bw.swarm.dto.req.WalletInfoReq;
import com.bw.swarm.service.XdaiChainService;
import com.bw.swarm.utils.EthUtils;
import com.bw.swarm.utils.poi.ExcelUtil;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.protocol.Web3j;

import java.math.BigDecimal;
import java.util.List;

import static com.bw.swarm.constants.ContractConstant.XBZZ_CONTRACT;

@Api(tags = "swarm钱包交易相关操作")
@RestController
@RequestMapping("/swarm")
@Slf4j
public class XdaiChainController {

    @Autowired
    private Web3jConfig web3jConfig;

    @Autowired
    private XdaiChainService xdaiChainService;


    /**
     * 发送xBZZ转账
     * 用excel ，全量转接口
     * @return
     */
    @PostMapping("/xbzz/transferAll")
    @ResponseBody
    public RestResponse sendXbzzAllByExcel(MultipartFile file) throws Exception {
        ExcelUtil<WalletInfoReq> util = new ExcelUtil<WalletInfoReq>(WalletInfoReq.class);
        List<WalletInfoReq> list = util.importExcel(file.getInputStream());
        Web3j web3j = web3jConfig.getXdaiWeb3j();
        list.stream().forEach(l -> {
            //每个钱包全量转
            try {
                String txid = EthUtils.sendErc20All(web3j, XBZZ_CONTRACT, l.getPrivateKey(), l.getToAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        web3j.shutdown();
        return RestResponse.ok("进行xbzz批量转账完成！");
    }


    /**
     * 发送xdai转账
     * 用excel
     * @return
     */
    @PostMapping("/xdai/transferByValue")
    @ResponseBody
    public RestResponse sendXdaiByExcel(@RequestParam("file") MultipartFile file) throws Exception {
        ExcelUtil<WalletInfoReq> util = new ExcelUtil<WalletInfoReq>(WalletInfoReq.class);
        List<WalletInfoReq> list = util.importExcel(file.getInputStream());
        Web3j web3j = web3jConfig.getXdaiWeb3j();
        list.stream().forEach(l -> {
            //每个钱包全量转
            try {
                String txid = EthUtils.sendEth(web3j, l.getPrivateKey(), l.getToAddress(),new BigDecimal("0.01"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        web3j.shutdown();
        return RestResponse.ok("进行xdai批量转账完成！");

    }

    /**
     * 读文件，批量查询xbzz值,落表记录
     * @return
     */
    @PostMapping("/xdai/queryXbzzBatch")
    @ResponseBody
    public RestResponse queryXbzzBatch(){
        Web3j web3j = web3jConfig.getXdaiWeb3j();
        xdaiChainService.queryXbzzBatch(web3j);
        web3j.shutdown();
        return RestResponse.ok("进行批量查询xbzz余额成功！");
    }

    @PostMapping("/xdai/transferXdaiBatch")
    @ResponseBody
    public RestResponse transferXdaiBatch(){
        Web3j web3j = web3jConfig.getXdaiWeb3j();
        xdaiChainService.transferXdaiBatch(web3j);
        web3j.shutdown();
        return RestResponse.ok("进行批量转xdai成功！");
    }

    @PostMapping("/xdai/transferXbzzBatch")
    @ResponseBody
    public RestResponse transferXbzzBatch(){
        Web3j web3j = web3jConfig.getXdaiWeb3j();
        xdaiChainService.transferXbzzBatch(web3j);
        web3j.shutdown();
        return RestResponse.ok("进行批量转xdai成功！");
    }
}
