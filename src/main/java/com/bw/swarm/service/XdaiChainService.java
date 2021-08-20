package com.bw.swarm.service;

import org.web3j.protocol.Web3j;

public interface XdaiChainService {

    int queryXbzzBatch(Web3j web3j);

    void transferXdaiBatch(Web3j web3j);

    void transferXbzzBatch(Web3j web3j);
}
