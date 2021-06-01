/*
 * Copyright 2021 The caver-java Authors
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

package com.klaytn.caver.transaction.type.wrapper;

import com.klaytn.caver.rpc.Klay;
import com.klaytn.caver.transaction.type.FeeDelegatedSmartContractDeploy;

/**
 * Represents a FeeDelegatedSmartContractDeployWrapper
 * 1. This class wraps all of static methods of FeeDelegatedSmartContractDeploy
 * 2. This class should be accessed via `caver.transaction.feeDelegatedSmartContractDeploy`
 */
public class FeeDelegatedSmartContractDeployWrapper {
    /**
     * Klay RPC instance
     */
    private Klay klaytnCall;

    /**
     * Creates a FeeDelegatedSmartContractDeployWrapper instance.
     * @param klaytnCall Klay RPC instance
     */
    public FeeDelegatedSmartContractDeployWrapper(Klay klaytnCall) {
        this.klaytnCall = klaytnCall;
    }

    /**
     * Creates a FeeDelegatedSmartContractDeploy instance derived from a RLP-encoded FeeDelegatedSmartContractDeploy string.
     * @param rlpEncoded RLP-encoded FeeDelegatedSmartContractDeploy string
     * @return FeeDelegatedSmartContractDeploy
     */
    public FeeDelegatedSmartContractDeploy create(String rlpEncoded) {
        FeeDelegatedSmartContractDeploy feeDelegatedSmartContractDeploy = FeeDelegatedSmartContractDeploy.decode(rlpEncoded);
        feeDelegatedSmartContractDeploy.setKlaytnCall(this.klaytnCall);
        return feeDelegatedSmartContractDeploy;
    }

    /**
     * Creates a FeeDelegatedSmartContractDeploy instance derived from a RLP-encoded FeeDelegatedSmartContractDeploy byte array.
     * @param rlpEncoded RLP-encoded FeeDelegatedSmartContractDeploy byte array.
     * @return FeeDelegatedSmartContractDeploy
     */
    public FeeDelegatedSmartContractDeploy create(byte[] rlpEncoded) {
        FeeDelegatedSmartContractDeploy feeDelegatedSmartContractDeploy = FeeDelegatedSmartContractDeploy.decode(rlpEncoded);
        feeDelegatedSmartContractDeploy.setKlaytnCall(this.klaytnCall);
        return feeDelegatedSmartContractDeploy;
    }

    /**
     * Creates a FeeDelegatedSmartContractDeploy instance using FeeDelegatedSmartContractDeploy.Builder
     * @param builder FeeDelegatedSmartContractDeploy.Builder
     * @return FeeDelegatedSmartContractDeploy
     */
    public FeeDelegatedSmartContractDeploy create(FeeDelegatedSmartContractDeploy.Builder builder) {
        return builder
                .setKlaytnCall(this.klaytnCall)
                .build();
    }

    /**
     * Decodes a RLP-encoded FeeDelegatedSmartContractDeploy string.
     * @param rlpEncoded RLP-encoded FeeDelegatedSmartContractDeploy string.
     * @return FeeDelegatedSmartContractDeploy
     */
    public FeeDelegatedSmartContractDeploy decode(String rlpEncoded) {
        return FeeDelegatedSmartContractDeploy.decode(rlpEncoded);
    }

    /**
     * Decodes a RLP-encoded FeeDelegatedSmartContractDeploy byte array.
     * @param rlpEncoded RLP-encoded FeeDelegatedSmartContractDeploy byte array.
     * @return FeeDelegatedSmartContractDeploy
     */
    public FeeDelegatedSmartContractDeploy decode(byte[] rlpEncoded) {
        return FeeDelegatedSmartContractDeploy.decode(rlpEncoded);
    }
}