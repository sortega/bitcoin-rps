package com.coinffeine.rps;

import java.io.File;

import com.coinffeine.rps.betting.bitcoinj.BitcoinjBettingShop;
import com.coinffeine.rps.betting.bitcoinj.TestNetwork;
import com.coinffeine.rps.cli.Cli;

public class Main {
    public static void main(String[] args) {
        new Cli(new BitcoinjBettingShop(TestNetwork.get(), new File("."))).start();
    }
}
