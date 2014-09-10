package com.coinffeine.rps.betting;

import java.math.BigInteger;

public interface BettingShop {
    void startAndWait();
    void newBet(BigInteger betSize);
    void stopAndWait();
}
