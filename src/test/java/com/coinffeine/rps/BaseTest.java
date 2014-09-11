package com.coinffeine.rps;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.DumpedPrivateKey;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;

import com.coinffeine.rps.betting.bitcoinj.TestNetwork;

public abstract class BaseTest {

    private static final URL CONFIG_URL = BaseTest.class.getResource("/test.properties");

    protected final Properties config = new Properties();
    protected final List<ECKey> testKeys = new LinkedList<>();

    public BaseTest() {
        try {
            try (InputStream stream = CONFIG_URL.openStream()) {
                config.load(stream);
            }
            NetworkParameters params = TestNetwork.get().params();
            for (String encodedKey: config.getProperty("test-keys", "").split(",")) {
                if (!encodedKey.trim().isEmpty()) {
                    testKeys.add(new DumpedPrivateKey(params, encodedKey.trim()).getKey());
                }
            }
        } catch (IOException e) {
            throw new Error("Cannot load test config from " + CONFIG_URL, e);
        } catch (AddressFormatException e) {
            throw new Error("Invalid key on test-keys", e);
        }
    }
}
