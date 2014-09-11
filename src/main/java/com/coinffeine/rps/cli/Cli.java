package com.coinffeine.rps.cli;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import com.coinffeine.rps.betting.Bet;
import com.coinffeine.rps.betting.BettingShop;
import com.coinffeine.rps.model.Hand;

public class Cli {
    private static final BigInteger BET_SIZE = BigInteger.valueOf(500000);

    private final BettingShop bettingShop;
    private final BufferedReader input;
    private String userAddress;

    public Cli(BettingShop bettingShop) {
        this.bettingShop = bettingShop;
        this.input = new BufferedReader(new InputStreamReader(System.in));
    }

    public void start() {
        setUp();
        askForUserAddress();
        do {
            playGame();
        } while (askForAnotherGame());
        tearDown();
    }

    private Map<String, Hand> hands = new HashMap<String, Hand>() {{
        put("r", Hand.Rock);
        put("p", Hand.Paper);
        put("s", Hand.Scissors);
    }};

    private void playGame() {
        Bet bet = bettingShop.newBet(BET_SIZE);
        final BigDecimal betCoins = BigDecimal.valueOf(BET_SIZE.longValue())
                .setScale(8, RoundingMode.UNNECESSARY)
                .divide(BigDecimal.valueOf(100000000), RoundingMode.UNNECESSARY);
        System.out.println("Send " + betCoins + " to " + bet.getBetAddress() +
                " to enter the bet. Waiting for your funds...");
        bet.waitForPayment();
        switch (bet.play(readHand(), this.userAddress)) {
            case House:
                System.out.println("The house wins.");
                break;
            case Tie:
                System.out.println("Tie: we have chosen the same. Sending you a refund.");
                break;
            default:
                System.out.println("You win and your bet has doubled. Sending you the coins.");
        }
    }

    private Hand readHand() {
        System.out.println("Choose rock, paper or scissors");
        while(true) {
            System.out.print("(r/p/s): ");
            String input = readLine().trim().toLowerCase();
            if (hands.containsKey(input)) {
                return hands.get(input);
            }
        }
    }

    private boolean askForAnotherGame() {
        System.out.print("Wanna play another game? (y/N): ");
        return readLine().trim().toLowerCase().startsWith("y");
    }

    private void setUp() {
        printWelcomeMessage();
        bettingShop.startAndWait();
    }

    private void tearDown() {
        System.out.println("Stopping...");
        bettingShop.stopAndWait();
        System.out.println("Done");
    }

    private void askForUserAddress() {
        System.out.println("Where do you want to receive your prices?");
        while (true) {
            System.out.print("Address: ");
            userAddress = readLine().trim();
            if (!userAddress.isEmpty()) {
                return;
            }
        }
    }

    private void printWelcomeMessage() {
        System.out.println(
                "         ROCK                     PAPER                       SCISSORS\n" +
                "          _______                _______                      _______\n" +
                "      ---'   ____)           ---'   ____)____             ---'   ____)____\n" +
                "            (_____)                     ______)                     ______)\n" +
                "            (_____)                    _______)                  __________)\n" +
                "            (____)                   _______)                   (____)\n" +
                "      ---.__(___)           ---.__________)                ---.__(___)\n\n" +
                "Initializing...");
    }

    private String readLine() {
        try {
            return input.readLine();
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
