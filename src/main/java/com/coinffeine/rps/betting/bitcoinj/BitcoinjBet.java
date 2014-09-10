package com.coinffeine.rps.betting.bitcoinj;

import java.math.BigInteger;

import com.google.bitcoin.core.*;
import static com.google.bitcoin.core.TransactionConfidence.ConfidenceType;
import com.google.bitcoin.script.Script;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import com.coinffeine.rps.betting.Bet;
import com.coinffeine.rps.model.Hand;
import com.coinffeine.rps.model.Match;

class BitcoinjBet implements Bet {

    private final Wallet wallet;
    private final Hand houseHand;
    private final BigInteger betSize;
    private final Address betAddress;
    private final ListenableFuture<Boolean> paymentFuture;

    public BitcoinjBet(Wallet wallet, Hand houseHand, BigInteger betSize) {
        this.wallet = wallet;
        this.houseHand = houseHand;
        this.betSize = betSize;
        this.betAddress = createBetAddress(wallet);
        this.paymentFuture = listenForUserPayment(wallet);
    }

    private SettableFuture<Boolean> listenForUserPayment(Wallet wallet) {
        final SettableFuture<Boolean> future = SettableFuture.create();
        wallet.addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
                TransactionConfidence confidence = tx.getConfidence();
                if (confidence.getConfidenceType() != ConfidenceType.BUILDING) return;
                if (confidence.getDepthInBlocks() <= 0) return;
                Script script = tx.getOutput(0).getScriptPubKey();
                if (!script.isSentToAddress()) return;
                if (betAddress.equals(script.getToAddress(wallet.getParams()))) {
                    future.set(true);
                    wallet.removeEventListener(this);
                }
            }
        });
        return future;
    }

    private static Address createBetAddress(Wallet wallet) {
        ECKey key = new ECKey();
        wallet.addKey(key);
        return key.toAddress(wallet.getParams());
    }

    @Override
    public String getBetAddress() {
        return this.betAddress.toString();
    }

    @Override
    public boolean waitForPayment() {
        try {
            paymentFuture.get();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    @Override
    public Winner play(Hand userHand, String userAddress) {
        switch (Match.play(userHand, houseHand)) {
            case LeftWins:
                payBack(betSize.multiply(BigInteger.valueOf(2)), userAddress);
                return Winner.User;

            case Tie:
                payBack(betSize, userAddress);
                return Winner.Tie;

            default:
                return Winner.House;
        }
    }

    private void payBack(BigInteger amount, String userAddress) {
        try {
            Address parsedUserAddress = new Address(wallet.getParams(), userAddress);
            WalletUtils.sendCoins(wallet, amount, betAddress, parsedUserAddress);
        } catch (Throwable ex) {
            throw new IllegalStateException(ex);
        }
    }
}
