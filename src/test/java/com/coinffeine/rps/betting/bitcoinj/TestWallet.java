package com.coinffeine.rps.betting.bitcoinj;

import com.google.bitcoin.core.*;
import com.google.bitcoin.kits.WalletAppKit;
import com.google.bitcoin.utils.BriefLogFormatter;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

public class TestWallet extends ExternalResource {

    private final ECKey privateKey;
    private TemporaryFolder folder = new TemporaryFolder();
    private WalletAppKit kit;

    public TestWallet(ECKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    protected void before() throws Throwable {
        folder.create();
        BriefLogFormatter.init();
        kit = new WalletAppKit(TestNetwork.get().params(), folder.getRoot(), "wallet") {
            @Override
            protected void onSetupCompleted() {
                kit.wallet().addKey(privateKey);
            }
        };
        kit.setPeerNodes(TestNetwork.get().seedPeer());
        kit.startAndWait();
    }

    @Override
    protected void after() {
        kit.stopAndWait();
        folder.delete();
    }

    public Wallet get() {
        return kit.wallet();
    }

    public Address address() {
        return privateKey.toAddress(TestNetwork.get().params());
    }
}
