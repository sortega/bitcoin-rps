package com.coinffeine.rps.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import static com.coinffeine.rps.model.Hand.*;

public class HandTest {

    @Test
    public void handsShouldBeatOtherHands() throws Exception {
        assertTrue(Rock.beats(Scissors));
        assertTrue(Scissors.beats(Paper));
        assertTrue(Paper.beats(Rock));
    }

    @Test
    public void handsShouldNotBeatThemselves() throws Exception {
        assertFalse(Rock.beats(Rock));
        assertFalse(Scissors.beats(Scissors));
        assertFalse(Paper.beats(Paper));
    }
}
