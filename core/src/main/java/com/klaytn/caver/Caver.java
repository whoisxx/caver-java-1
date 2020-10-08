/*
 * Copyright 2019 The caver-java Authors
 *
 * Licensed under the Apache License, Version 2.0 (the “License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.klaytn.caver;

import com.klaytn.caver.rpc.RPC;
import com.klaytn.caver.wallet.KeyringContainer;
import okhttp3.OkHttpClient;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;

/**
 * Core Caverj JSON-RPC API.
 */
public class Caver {

    public static String DEFAULT_URL = "http://localhost:8551";
    public static String MAINNET_URL = "https://api.cypress.klaytn.net:8651";
    public static String BAOBAB_URL = "https://api.baobab.klaytn.net:8651";

    /**
     * @deprecated Please use <code>caver.rpc.klay</code> instead.
     * @see RPC#klay
     */
    @Deprecated
    Klay klay;

    /**
     * @deprecated Please use <code>caver.rpc.net</code> instead.
     * @see RPC#net
     */
    @Deprecated
    Net net;

    /**
     * The JSON-RPC API instance
     */
    public RPC rpc;

    /**
     * The KeyringContainer instance.
     */
    public KeyringContainer wallet;

    public Caver() {
        this(new HttpService(DEFAULT_URL));
    }

    /**
     * Creates a Caver instance
     * @param url JSON-RPC request URL
     */
    public Caver(String url) {
        this(new HttpService(url));
    }

    /**
     * Creates a Caver instance
     * @param service Web3jService
     */
    public Caver(Web3jService service) {
        rpc = new RPC(service);
        wallet = new KeyringContainer();
    }

    /**
     * @deprecated Please use {@link #Caver(Web3jService)} instead.
     */
    @Deprecated
    public static Caver build(Web3jService service) {
        return new CaverImpl(service);
    }

    /**
     * @deprecated Please use {@link #Caver(String)} instead.
     */
    @Deprecated
    public static Caver build(String url) {
        return new CaverImpl(url);
    }

    /**
     * @deprecated Please use {@link #Caver()} instead.
     */
    @Deprecated
    public static Caver build() {
        return Caver.build(DEFAULT_URL);
    }

    /**
     * Getter for RPC
     * @return RPC
     */
    public RPC getRpc() {
        return rpc;
    }

    /**
     * Getter for Wallet
     * @return KeyringContainer
     */
    public KeyringContainer getWallet() {
        return wallet;
    }

    /**
     * @deprecated Please use <code>caver.rpc.klay</code> instead.
     * @see RPC#klay
     */
    @Deprecated
    public Klay klay() {
        return klay;
    };

    /**
     * @deprecated Please use <code>caver.rpc.net</code> instead.
     * @see RPC#net
     */
    @Deprecated
    public Net net() {
        return net;
    };
}
