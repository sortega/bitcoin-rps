package com.coinffeine.rps.model;

public class Match {
    public static MatchResult play(Hand leftHand, Hand rightHand) {
        if (leftHand.beats(rightHand)) return MatchResult.LeftWins;
        if (rightHand.beats(leftHand)) return MatchResult.RightWins;
        return MatchResult.Tie;
    }
}
