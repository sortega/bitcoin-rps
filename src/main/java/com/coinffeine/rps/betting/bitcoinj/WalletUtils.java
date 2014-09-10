package com.coinffeine.rps.betting.bitcoinj;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import com.google.bitcoin.core.*;

final class WalletUtils {

    private WalletUtils() {}

    public static Wallet.SendResult sendCoins(
            Wallet wallet, BigInteger amount, Address source, Address target)
            throws InsufficientMoneyException {
        List<TransactionOutput> funds = collectFundsFromSource(wallet, amount, source);
        completeFunds(wallet, funds, amount);
        Transaction transaction = new Transaction(wallet.getParams());
        BigInteger change = amount.negate();
        for (TransactionOutput input : funds) {
            transaction.addInput(input);
            change = change.add(input.getValue());
        }
        transaction.addOutput(amount, target);
        transaction.addOutput(change, source);
        transaction.signInputs(Transaction.SigHash.ALL, wallet);
        Wallet.SendRequest request = Wallet.SendRequest.forTx(transaction);
        request.feePerKb = BigInteger.ZERO;
        request.fee = BigInteger.ZERO;
        request.ensureMinRequiredFee = false;
        return wallet.sendCoins(request);
    }

    private static List<TransactionOutput> collectFundsFromSource(
            Wallet wallet, BigInteger amount, Address source) throws InsufficientMoneyException {
        List<TransactionOutput> funds = new LinkedList<>();
        BigInteger inputAmount = BigInteger.ZERO;
        for (TransactionOutput output: wallet.calculateAllSpendCandidates(true)) {
            if (inputAmount.compareTo(amount) >= 0) break;
            if (source.equals(addressOf(wallet, output))) {
                funds.add(output);
            }
        }
        if (funds.isEmpty()) {
            throw new IllegalArgumentException("No funds from " + source);
        } else {
            return funds;
        }
    }

    private static List<TransactionOutput> completeFunds(
            Wallet wallet, List<TransactionOutput> funds, BigInteger amount)
            throws InsufficientMoneyException {
        BigInteger remainingAmount = amount;
        for (TransactionOutput output : funds) {
            remainingAmount = remainingAmount.subtract(output.getValue());
        }
        for (TransactionOutput output: wallet.calculateAllSpendCandidates(true)) {
            if (remainingAmount.compareTo(BigInteger.ZERO) <= 0) break;
            if (!funds.contains(output)) {
                funds.add(output);
                remainingAmount = remainingAmount.subtract(output.getValue());
            }
        }
        if (remainingAmount.compareTo(BigInteger.ZERO) <= 0) {
            return funds;
        } else {
            throw new IllegalArgumentException("No available funds: short by " + remainingAmount);
        }
    }

    public static Address addressOf(Wallet wallet, TransactionOutput output) {
        if (output.getScriptPubKey().isSentToAddress()) {
            return output.getScriptPubKey().getToAddress(wallet.getParams());
        }
        return null;
    }
}
