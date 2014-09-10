package com.coinffeine.rps.model;

import static com.coinffeine.rps.model.Hand.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MatchTest {

    @Test
    public void shouldTieAMathWhenPlayersPlayTheSameHand() throws Exception {
        assertEquals(MatchResult.Tie, Match.play(Rock, Rock));
        assertEquals(MatchResult.Tie, Match.play(Paper, Paper));
        assertEquals(MatchResult.Tie, Match.play(Scissors, Scissors));
    }

    @Test
    public void shouldWinTheLeftHandIfBeatsTheRightOne() throws Exception {
        assertEquals(MatchResult.LeftWins, Match.play(Rock, Scissors));
    }

    @Test
    public void shouldWinTheRightHandIfBeatsTheLeftOne() throws Exception {
        assertEquals(MatchResult.RightWins, Match.play(Rock, Paper));
    }
}
