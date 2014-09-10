package com.coinffeine.rps.betting.bitcoinj;

import java.io.File;
import java.math.BigInteger;
import java.security.SecureRandom;

import com.google.bitcoin.kits.WalletAppKit;
import com.google.bitcoin.utils.BriefLogFormatter;

import com.coinffeine.rps.betting.Bet;
import com.coinffeine.rps.betting.BettingShop;
import com.coinffeine.rps.model.Hand;

public class BitcoinjBettingShop implements BettingShop {

    private final Network network;
    private final WalletAppKit kit;
    private final SecureRandom random;

    public BitcoinjBettingShop(Network network, File walletDir) {
        BriefLogFormatter.init();
        this.network = network;
        this.kit = new WalletAppKit(network.params(), walletDir, "wallet");
        this.random = new SecureRandom();
    }

    @Override
    public void startAndWait() {
        kit.setPeerNodes(network.seedPeer());
        kit.startAndWait();
    }

    @Override
    public Bet newBet(BigInteger betSize) {
        return new BitcoinjBet(kit.wallet(), randomHand(), betSize);
    }

    private Hand randomHand() {
        return Hand.values()[random.nextInt(Hand.values().length)];
    }

    @Override
    public void stopAndWait() {
        kit.stopAndWait();
    }
}
