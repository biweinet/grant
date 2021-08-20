package com.bw.swarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bw.swarm.bean.WalletTransferInfo;
import com.bw.swarm.dao.WalletTransferInfoMapper;
import com.bw.swarm.service.WalletTransferInfoService;
import com.bw.swarm.service.XdaiChainService;
import com.bw.swarm.utils.EthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static com.bw.swarm.constants.ContractConstant.XBZZ_CONTRACT;

@Service
public class XdaiChainServiceImpl implements XdaiChainService {

    @Autowired
    private WalletTransferInfoService walletTransferInfoService;

    @Autowired
    private WalletTransferInfoMapper walletTransferInfoMapper;

    @Override
    public int queryXbzzBatch(Web3j web3j) {
        File ff = new File("E:\\wallet\\1");
        File[] ffs = ff.listFiles();
        for(File file : ffs){
            File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
            for (File f : fs) {
                if (f.isDirectory()) {
                    String absolutePath = String.format("%s\\docker-data\\volumes", f.getAbsolutePath());
                    File absoluteFile = new File(absolutePath);
                    File[] absFiles = absoluteFile.listFiles();
                    for (File absFile : absFiles) {
                        String pwdPath = String.format("%s\\_data\\password", absFile.getAbsolutePath());
                        File pwdFile = new File(pwdPath);

                        WalletTransferInfo info = new WalletTransferInfo();
                        info.setDir(absFile.getAbsolutePath());

                        if (!pwdFile.exists()) {

                            walletTransferInfoService.save(info);
                        } else {
                            FileReader fileReader = null;
                            String pwd = null;
                            LineNumberReader reader = null;
                            try {
                                fileReader = new FileReader(pwdPath);
                                reader = new LineNumberReader(fileReader);
                                pwd = reader.readLine();
                                File keyFile = new File(String.format("%s\\_data\\keystore", absFile.getAbsolutePath()));
                                File key = keyFile.listFiles()[0];
                                Credentials credentials = EthUtils.getCredentials(pwd, key);
                                String address = credentials.getAddress();
                                String privateKey = credentials.getEcKeyPair().getPrivateKey().toString(16);
                                BigInteger value = EthUtils.balanceOfErc20(web3j, XBZZ_CONTRACT, address);
                                BigDecimal decimal = EthUtils.toBzz(value);

                                info.setAddress(address);
                                info.setXbzzValue(decimal);
                                info.setPrivateKey(privateKey);
                                info.setPassword(pwd);
                                walletTransferInfoService.save(info);

                            } catch (Exception e) {
                                walletTransferInfoService.save(info);
                            }finally {
                                if(reader != null){
                                    try {
                                        reader.close();
                                    } catch (IOException e) {

                                    }
                                }
                                if(fileReader != null){
                                    try {
                                        fileReader.close();
                                    } catch (IOException e) {

                                    }
                                }
                            }

//                        EthUtils
                        }
                    }

                }
            }

        }

        return 0;
    }

    /**
     * 读取数据库，批量转xdai币
     */
    public void transferXdaiBatch(Web3j web3j){
        List<WalletTransferInfo> infoList = walletTransferInfoMapper.selectList(new LambdaQueryWrapper<WalletTransferInfo>().isNotNull(WalletTransferInfo::getAddress)
                .eq(WalletTransferInfo::getXdaiStatus,0).ne(WalletTransferInfo::getXbzzValue,0.0000000000000000).orderByAsc(WalletTransferInfo::getId));
        String from = EthUtils.getAddressByPrivateKey("");
        BigInteger nonce = EthUtils.getNonce(web3j, from);
        for(WalletTransferInfo info : infoList) {
            try {
                EthUtils.sendEthBatch(web3j, "", info.getAddress(), new BigDecimal("0.002"), nonce);
                info.setXdaiStatus(1);
                walletTransferInfoService.updateById(info);
                nonce = nonce.add(BigInteger.valueOf(1));
            } catch (Exception e) {

            }
        }
    }

    public void transferXbzzBatch(Web3j web3j){
        List<WalletTransferInfo> infoList = walletTransferInfoMapper.selectList(new LambdaQueryWrapper<WalletTransferInfo>().isNotNull(WalletTransferInfo::getAddress)
                .eq(WalletTransferInfo::getXdaiStatus,1).orderByAsc(WalletTransferInfo::getId));
        for(WalletTransferInfo info : infoList) {
            try{
                EthUtils.sendErc20All(web3j, XBZZ_CONTRACT, info.getPrivateKey(), "");
                info.setXdaiStatus(2);
                walletTransferInfoService.updateById(info);
            }catch (Exception e){

            }
        };

    }
}
