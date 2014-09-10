package com.coinffeine.rps.betting.bitcoinj;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BitcoinjBettingShopTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldStartAndStopGracefully() throws Exception {
        BitcoinjBettingShop instance =
                new BitcoinjBettingShop(TestNetwork.get(), temporaryFolder.getRoot());
        instance.startAndWait();
        instance.stopAndWait();
    }
}
