package com.bw.swarm.utils;

import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 以太坊工具类
 */
@Slf4j
public class EthUtils {

    public static final BigDecimal ETH_DECIMALS = new BigDecimal(1_000_000_000_000_000_000L);
    public static final BigDecimal BZZ_DECIMALS = new BigDecimal(10_000_000_000_000_000L);

    public static final BigInteger ETH_GAS_LIMIT = new BigInteger("21000"); //目前用xdai的

    /**
     * 获取区块数据
     *
     * @param web3j
     * @param block                  块高
     * @param fullTransactionObjects 是否需要交易数据
     * @return
     */
    public static EthBlock getBlock(Web3j web3j, long block, boolean fullTransactionObjects) {
        try {
            return web3j.ethGetBlockByNumber(new DefaultBlockParameterNumber(block), fullTransactionObjects).send();
        } catch (Throwable t) {
            log.error(String.format("Get Block Error %d", block), t);
        }
        return null;
    }

    /**
     * 获取当前块高
     *
     * @param web3j
     * @return
     */
    public static long getNowBlockNumber(Web3j web3j) {
        try {
            EthBlockNumber send = web3j.ethBlockNumber().send();
            return send.getBlockNumber().longValue();
        } catch (Throwable t) {
            log.error("GetBlockNumberError", t);
        }
        return -1;
    }

    /**
     * 发送erc20
     *
     * @param web3j
     * @param contractAddress 合约地址
     * @param privateKey      私钥
     * @param to              收款地址
     * @param valueDecimal    额度
     * @return
     */
    public static String sendErc20(Web3j web3j, String contractAddress, String privateKey,
                                   String to, BigDecimal valueDecimal) {
        BigInteger value = toWei(valueDecimal);
        String from = getAddressByPrivateKey(privateKey);
        log.info(String.format("Start:SendErc20 from:%s to:%s amount:%s erc20:%s", from, to, value.toString(), contractAddress));
        try {
            //加载转账所需的凭证，用私钥
            Credentials credentials = Credentials.create(privateKey);
            //获取nonce，交易笔数
            BigInteger nonce = getNonce(web3j, from);
            if (nonce == null) {
                log.error(String.format("END:GetNonceError from:%s to:%s amount:%s erc20:%s", from, to, value.toString(), contractAddress));
                return null;
            }
            //gasPrice和gasLimit 都可以手动设置
            BigInteger gasPrice = getGasPrice(web3j);
            if (gasPrice == null) {
                log.error(String.format("END:GetGasPriceError from:%s to:%s amount:%s erc20:%s", from, to, value.toString(), contractAddress));
                return null;
            }
            //BigInteger.valueOf(4300000L) 如果交易失败 很可能是手续费的设置问题
            BigInteger gasLimit = BigInteger.valueOf(60000L);
            //ERC20代币合约方法
            Function function = new Function(
                    "transfer",
                    Arrays.asList(new Address(to), new Uint256(value)),
                    Collections.singletonList(new TypeReference<Type>() {
                    }));
            //创建RawTransaction交易对象
            String encodedFunction = FunctionEncoder.encode(function);
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit,
                    contractAddress, encodedFunction);

            //签名Transaction
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signMessage);
            //发送交易
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String hash = ethSendTransaction.getTransactionHash();
            if (hash != null) {
                return hash;
            }
            log.error(String.format("END:HashIsNull from:%s to:%s amount:%s erc20:%s", from, to, value.toString(), contractAddress));
        } catch (Throwable t) {
            log.error(String.format("发送ERC20失败 from=%s to=%s erc20=%s amount=%s",
                    from, to, contractAddress, value.toString()), t);
        }
        return null;
    }

    /**
     * 转币全量转方法
     *
     * @param web3j
     * @param contractAddress
     * @param privateKey
     * @param to
     * @return
     */
    public static String sendErc20All(Web3j web3j, String contractAddress, String privateKey,
                                      String to) throws Exception {
        String from = getAddressByPrivateKey(privateKey);
        //1000000000000000
        BigInteger value = balanceOfErc20(web3j, contractAddress, from);
        log.info(String.format("Start:SendErc20 from:%s to:%s amount:%s erc20:%s", from, to, value.toString(), contractAddress));
        try {
            //加载转账所需的凭证，用私钥
            Credentials credentials = Credentials.create(privateKey);
            //获取nonce，交易笔数
            BigInteger nonce = getNonce(web3j, from);
            if (nonce == null) {
                log.error(String.format("END:GetNonceError from:%s to:%s amount:%s erc20:%s", from, to, value.toString(), contractAddress));
                return null;
            }
            //gasPrice和gasLimit 都可以手动设置
            BigInteger gasPrice = getGasPrice(web3j);
            if (gasPrice == null) {
                log.error(String.format("END:GetGasPriceError from:%s to:%s amount:%s erc20:%s", from, to, value.toString(), contractAddress));
                return null;
            }
            //BigInteger.valueOf(4300000L) 如果交易失败 很可能是手续费的设置问题
            BigInteger gasLimit = new BigInteger("94333");
            //ERC20代币合约方法
            Function function = new Function(
                    "transfer",
                    Arrays.asList(new Address(to), new Uint256(value)),
                    Collections.singletonList(new TypeReference<Type>() {
                    }));
            //创建RawTransaction交易对象
            String encodedFunction = FunctionEncoder.encode(function);
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit,
                    contractAddress, encodedFunction);

            //签名Transaction
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signMessage);
            //发送交易
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String hash = ethSendTransaction.getTransactionHash();
            if (hash != null) {
                return hash;
            }else{
                log.error(String.format("END:HashIsNull from:%s to:%s amount:%s erc20:%s", from, to, value.toString(), contractAddress));
                throw new Exception();
            }
        } catch (Throwable t) {
            log.error(String.format("发送ERC20失败 from=%s to=%s erc20=%s amount=%s",
                    from, to, contractAddress, value.toString()), t);
            throw new Exception();

        }
    }



    /**
     * 列出交易信息
     *
     * @param block  区块高度
     * @param filter 过滤器
     * @return
     */
    public static List<EthBlock.TransactionResult> getTransactions(Web3j web3j, long block, java.util.function.Function<EthBlock.TransactionResult, Boolean> filter) {
        EthBlock send = getBlock(web3j, block, true);
        if (send == null) {
            log.error(String.format("GetBlockDataError:%d", block));
            return Collections.emptyList();
        }
        List<EthBlock.TransactionResult> transactions = send.getBlock().getTransactions();
        if (filter != null) {
            List<EthBlock.TransactionResult> result = new ArrayList<>();
            for (EthBlock.TransactionResult e : transactions) {
                try {
                    if (filter.apply(e)) {
                        result.add(e);
                    }
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
            }
            return result;
        }
        return transactions;

    }

    /**
     * 根据私钥获取地址
     *
     * @param privateKey
     * @return
     */
    public static String getAddressByPrivateKey(String privateKey) {
        ECKeyPair ecKeyPair = ECKeyPair.create(new BigInteger(privateKey, 16));
        return "0x" + Keys.getAddress(ecKeyPair).toLowerCase();
    }


    /**
     * 创建地址
     *
     * @return
     */
 /*   public static EthAddress createAddress() {
        try {
            String seed = UUID.randomUUID().toString();
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();

            String sPrivatekeyInHex = privateKeyInDec.toString(16);

            WalletFile aWallet = Wallet.createLight(seed, ecKeyPair);
            String sAddress = aWallet.getAddress();

            EthAddress address = new EthAddress();
            address.setAddress("0x" + sAddress);
            address.setPrivateKey(sPrivatekeyInHex);
            return address;
        } catch (Throwable t) {
            log.error("创建地址失败", t);
        }
        return null;
    }
*/

    /**
     * 查询地址以太坊数量
     *
     * @param web3j
     * @param address 查询地址
     * @return
     */
    public static BigDecimal balanceOf(Web3j web3j, String address) {
        try {
            EthGetBalance balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            BigInteger amount = balance.getBalance();
            if (amount == null || amount.compareTo(BigInteger.ZERO) <= 0) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(amount).divide(ETH_DECIMALS, 18, RoundingMode.FLOOR);
        } catch (Throwable t) {
            log.error(String.format("获取以太坊数量出错 %s", address), t);
        }
        return BigDecimal.ZERO;
    }


    /**
     * 转换成最小单位 Wei
     *
     * @param ethAmount
     * @return
     */
    public static BigInteger toWei(BigDecimal ethAmount) {
        return ethAmount.multiply(ETH_DECIMALS).toBigInteger();
    }

    /**
     * wei to eth
     *
     * @param wei
     * @return
     */
    public static BigDecimal toEth(BigInteger wei) {
        return new BigDecimal(wei).divide(ETH_DECIMALS, 18, RoundingMode.FLOOR);
    }

    public static BigDecimal toBzz(BigInteger wei) {
        return new BigDecimal(wei).divide(BZZ_DECIMALS, 16, RoundingMode.FLOOR);
    }

    /**
     * 查询erc20的余额
     *
     * @param web3j
     * @param contract 合约地址
     * @param address  查询地址
     * @return
     */
    public static BigInteger balanceOfErc20(Web3j web3j, String contract, String address) {
        try {
            final String DATA_PREFIX = "0x70a08231000000000000000000000000";
            String value = web3j.ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(address,
                    contract, DATA_PREFIX + address.substring(2)), DefaultBlockParameterName.PENDING).send().getValue();
            if (StringUtils.isEmpty(value)) {
                return BigInteger.ZERO;
            }
            return new BigInteger(value.substring(2), 16);
        } catch (Throwable t) {
            log.error(String.format("查询ERC20失败 contract:%s address:%s", contract, address), t);
        }
        return BigInteger.ZERO;
    }


    /**
     * 获取gas-price
     *
     * @param web3j
     * @return
     */
    public static BigInteger getGasPrice(Web3j web3j) {
        try {
            EthGasPrice ethGasPrice = web3j.ethGasPrice().sendAsync().get();
            if (ethGasPrice == null) {
                log.error("GetGasPriceError");
                return null;
            }
            return ethGasPrice.getGasPrice();
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        return null;
    }

    /**
     * 获取nonce
     *
     * @param web3j
     * @param address
     * @return
     */
    public static BigInteger getNonce(Web3j web3j, String address) {
        try {
            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send();
            if (ethGetTransactionCount == null) {
                log.error("GetNonceError:" + address);
                return null;
            }
            return ethGetTransactionCount.getTransactionCount();
        } catch (Throwable t) {
            log.error("GetNonceError:" + address);
        }
        return null;
    }

    /**
     * 发送以太坊
     *
     * @param web3j
     * @param privateKey 发送者私钥
     * @param to         收款地址
     * @param wei        wei为单位的数量
     * @param gasPrice   gas-price
     * @param gasLimit   gas-limit
     * @return
     */
    public static String sendEth(Web3j web3j, String privateKey, String to, BigInteger wei, BigInteger gasPrice, BigInteger gasLimit) throws Exception {
        String from = getAddressByPrivateKey(privateKey);
        try {
            //加载转账所需的凭证，用私钥
            Credentials credentials = Credentials.create(privateKey);
            //获取nonce，交易笔数
            BigInteger nonce = getNonce(web3j, from);
            //创建RawTransaction交易对象
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, wei);
            //签名Transaction，这里要对交易做签名
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signMessage);
            //发送交易
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String hash = ethSendTransaction.getTransactionHash();
            if (hash != null) {
                return hash;
            }else{
                log.error(String.format("END:HashIsNull from:%s to:%s ", from, to));
                throw new Exception();
            }
        } catch (Throwable t) {
            log.error(String.format("发送ETH失败 from:%s to:%s amount-eth:%s", from, to, toEth(wei).toString()));
            throw new Exception();
        }
    }

    public static String sendEthBatch(Web3j web3j, String privateKey, String to, BigInteger wei, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) throws Exception {
        String from = getAddressByPrivateKey(privateKey);
        try {
            //加载转账所需的凭证，用私钥
            Credentials credentials = Credentials.create(privateKey);
            //获取nonce，交易笔数
            //创建RawTransaction交易对象
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, wei);
            //签名Transaction，这里要对交易做签名
            byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signMessage);
            //发送交易
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
            String hash = ethSendTransaction.getTransactionHash();
            if (hash != null) {
                return hash;
            }else{
                log.error(String.format("END:HashIsNull from:%s to:%s ", from, to));
                throw new Exception();
            }
        } catch (Throwable t) {
            log.error(String.format("发送ETH失败 from:%s to:%s amount-eth:%s", from, to, toEth(wei).toString()));
            throw new Exception();
        }
    }

    /**
     * 发送以太坊
     *
     * @param web3j
     * @param privateKey 发送者私钥
     * @param to         收款地址
     * @param wei        wei为单位的数量
     * @return
     */
    public static String sendEth(Web3j web3j, String privateKey, String to, BigInteger wei) throws Exception {
        return sendEth(web3j, privateKey, to, wei, getGasPrice(web3j), ETH_GAS_LIMIT);
    }

    /**
     * 发送以太坊
     *
     * @param web3j
     * @param privateKey 发送者私钥
     * @param to         收款地址
     * @param eth        wei为单位的数量
     * @param gasPrice   gas-price
     * @param gasLimit   gas-limit
     * @return
     */
    public static String sendEth(Web3j web3j, String privateKey, String to, BigDecimal eth, BigInteger gasPrice, BigInteger gasLimit) throws Exception {
        return sendEth(web3j, privateKey, to, toWei(eth), gasPrice, gasLimit);
    }

    /**
     * 发送以太坊
     *
     * @param web3j
     * @param privateKey 发送者私钥
     * @param to         收款地址
     * @param eth        wei为单位的数量
     * @return
     */
    public static String sendEth(Web3j web3j, String privateKey, String to, BigDecimal eth) throws Exception {
        return sendEth(web3j, privateKey, to, toWei(eth), getGasPrice(web3j), ETH_GAS_LIMIT);
    }


    /**
     * 发送以太坊
     *
     * @param web3j
     * @param privateKey 发送者私钥
     * @param to         收款地址
     * @param eth        wei为单位的数量
     * @return
     */
    public static String sendEthBatch(Web3j web3j, String privateKey, String to, BigDecimal eth, BigInteger nonce) throws Exception {
        return sendEthBatch(web3j, privateKey, to, toWei(eth), getGasPrice(web3j), ETH_GAS_LIMIT, nonce);
    }


    /**
     * 根据hash获取交易信息
     *
     * @param web3j
     * @param hash
     * @return
     */
    public static EthTransaction getTransaction(Web3j web3j, String hash) {
        try {
            EthTransaction tx = web3j.ethGetTransactionByHash(hash).send();
            return tx;
        } catch (Throwable t) {
            log.error("GetTransactionError:" + hash, t);
        }
        return null;
    }

    /**
     * 创建钱包私钥
     *
     * @param password 生成的keyStore密码
     * @param outPath  生成的keyStore文件存放路径
     */
    public static String generatePrivateKey(String password, String outPath) {
        try {
            //创建新钱包KeyStore文件
            String fileName = WalletUtils.generateFullNewWalletFile(password, new File(outPath));
            //通过KeyStore文件加载钱包
            Credentials credentials = WalletUtils.loadCredentials(password, String.format("%s%s", outPath, fileName));
            //通过钱包获取私钥
            BigInteger privateKey = credentials.getEcKeyPair().getPrivateKey();
            return privateKey.toString(16);
        } catch (Exception e) {
            log.error("生成私钥失败，异常信息为： {}", e);
        }
        return null;
    }

    public static Credentials getCredentials(String pwd, File key) throws CipherException, IOException {

        return WalletUtils.loadCredentials(pwd, key);
//        String address = credentials.getAddress();
//        System.out.println("address:" + address);
//        System.out.println("PrivateKey:" + credentials.getEcKeyPair().getPrivateKey().toString(16));
    }
}

