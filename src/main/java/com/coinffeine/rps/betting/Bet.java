package com.coinffeine.rps.betting;

import com.coinffeine.rps.model.Hand;

public interface Bet {

    enum Winner {
        User,
        Tie,
        House
    }

    String getBetAddress();
    boolean waitForPayment();
    Winner play(Hand userHand, String userAddress);
}
