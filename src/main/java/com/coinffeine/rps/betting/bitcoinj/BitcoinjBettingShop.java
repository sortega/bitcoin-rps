package com.coinffeine.rps.betting.bitcoinj;

import java.io.File;
import java.math.BigInteger;

import com.google.bitcoin.kits.WalletAppKit;
import com.google.bitcoin.utils.BriefLogFormatter;

import com.coinffeine.rps.betting.BettingShop;

public class BitcoinjBettingShop implements BettingShop {

    private final Network network;
    private final WalletAppKit kit;

    public BitcoinjBettingShop(Network network, File walletDir) {
        BriefLogFormatter.init();
        this.network = network;
        this.kit = new WalletAppKit(network.params(), walletDir, "wallet");
    }

    @Override
    public void startAndWait() {
        kit.setPeerNodes(network.seedPeer());
        kit.startAndWait();
    }

    @Override
    public void newBet(BigInteger betSize) {

    }

    @Override
    public void stopAndWait() {
        kit.stopAndWait();
    }
}
