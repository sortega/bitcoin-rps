package com.coinffeine.rps.betting.bitcoinj;

import java.math.BigInteger;

import com.google.bitcoin.core.*;
import com.google.common.util.concurrent.SettableFuture;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Rule;
import org.junit.Test;

import com.coinffeine.rps.betting.Bet;
import com.coinffeine.rps.model.Hand;

public class BitcoinjBetTest {

    private static BigInteger BET_SIZE = BigInteger.valueOf(500000);

    @Rule
    public TestWallet houseWallet =
            new TestWallet("cNrvrFRtiu4o5vauQvLX9WeFFaPvSMwzEM4WdewaYDPA4arctd7b");
    @Rule
    public TestWallet userWallet =
            new TestWallet("cQwrHJ1eA9rxXJ7nUzARVpwQwqaTRVDTnpRtMer372uK2iNAxAi3");
    private Bet instance;

    @Test
    public void shouldProvideABetAddress() throws Exception {
        instance = new BitcoinjBet(houseWallet.get(), Hand.Rock, BET_SIZE);
        parseBetAddress();
    }

    @Test(timeout = 300000)
    public void shouldWaitUntilHavingEnoughFundsReceived() throws Exception {
        instance = new BitcoinjBet(houseWallet.get(), Hand.Rock, BET_SIZE);
        givenUserPayABet(parseBetAddress());
        assertTrue(instance.waitForPayment());
    }

    @Test(timeout = 300000)
    public void shouldPayToWinnerUsers() throws Exception {
        instance = new BitcoinjBet(houseWallet.get(), Hand.Rock, BET_SIZE);
        Address betAddress = parseBetAddress();
        givenUserPayABet(betAddress);
        SettableFuture<Transaction> paymentFuture =
                paymentFuture(BET_SIZE.multiply(BigInteger.valueOf(2)));
        assertTrue(instance.waitForPayment());
        assertEquals(Bet.Winner.User, instance.play(Hand.Paper, userWallet.address().toString()));
        paymentFuture.get();
    }

    @Test(timeout = 300000)
    public void shouldRefundTiedUsers() throws Exception {
        instance = new BitcoinjBet(houseWallet.get(), Hand.Rock, BET_SIZE);
        Address betAddress = parseBetAddress();
        givenUserPayABet(betAddress);
        SettableFuture<Transaction> paymentFuture = paymentFuture(BET_SIZE);
        assertTrue(instance.waitForPayment());
        assertEquals(Bet.Winner.Tie, instance.play(Hand.Rock, userWallet.address().toString()));
        paymentFuture.get();
    }

    @Test(timeout = 300000)
    public void shouldNotPayOtherwise() throws Exception {
        instance = new BitcoinjBet(houseWallet.get(), Hand.Rock, BET_SIZE);
        givenUserPayABet(parseBetAddress());
        assertTrue(instance.waitForPayment());
        BigInteger previousBalance = userWallet.get().getBalance();
        assertEquals(Bet.Winner.House, instance.play(Hand.Scissors, userWallet.address().toString()));
        assertEquals(previousBalance, userWallet.get().getBalance());
    }

    private SettableFuture<Transaction> paymentFuture(final BigInteger expectedAmount) {
        final SettableFuture<Transaction> paymentPromise = SettableFuture.create();
        userWallet.get().addEventListener(new AbstractWalletEventListener() {
            @Override
            public void onCoinsReceived(Wallet wallet, Transaction tx,
                                        BigInteger prevBalance, BigInteger newBalance) {
                if (!newBalance.subtract(prevBalance).equals(expectedAmount)) return;
                paymentPromise.set(tx);
            }
        });
        return paymentPromise;
    }

    private void givenUserPayABet(Address betAddress) throws InsufficientMoneyException {
        final Wallet.SendRequest request = Wallet.SendRequest.to(betAddress, BET_SIZE);
        request.changeAddress = userWallet.address();
        userWallet.get().sendCoins(request);
    }

    private Address parseBetAddress() throws AddressFormatException {
        return new Address(TestNetwork.get().params(), instance.getBetAddress());
    }
}
