package com.coinffeine.rps.betting;

import com.coinffeine.rps.model.Hand;
import com.coinffeine.rps.model.MatchResult;

public interface Bet {
    String getMatchAddress();
    void waitForPayment();
    MatchResult play(Hand userHand);
}
