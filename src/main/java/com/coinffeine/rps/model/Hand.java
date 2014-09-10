package com.coinffeine.rps.model;

import java.util.HashMap;
import java.util.Map;

public enum Hand {
    Rock,
    Paper,
    Scissors;

    private static final Map<Hand, Hand> beatMap = new HashMap<Hand, Hand>() {{
        put(Rock, Scissors);
        put(Scissors, Paper);
        put(Paper, Rock);
    }};

    public boolean beats(Hand otherHand) {
        return beatMap.get(this) == otherHand;
    }
}
