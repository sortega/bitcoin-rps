package com.coinffeine.rps.betting.bitcoinj;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerAddress;
import com.google.bitcoin.params.TestNet3Params;

public final class TestNetwork implements Network {

    private static final TestNetwork INSTANCE = new TestNetwork();

    private final TestNet3Params params;
    private final PeerAddress seedPeer;

    private TestNetwork() {
        this.params = new TestNet3Params() {
            @Override
            public String[] getDnsSeeds() {
                return new String[]{};
            }
        };
        try {
            this.seedPeer = new PeerAddress(InetAddress.getByName("testnet.test.coinffeine.com"), 19000);
        } catch (UnknownHostException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public NetworkParameters params() {
        return params;
    }

    @Override
    public PeerAddress seedPeer() {
        return this.seedPeer;
    }

    public static TestNetwork get() {
        return INSTANCE;
    }
}
