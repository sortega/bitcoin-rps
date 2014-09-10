package com.coinffeine.rps.betting.bitcoinj;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerAddress;

public interface Network {
    NetworkParameters params();
    PeerAddress seedPeer();
}
