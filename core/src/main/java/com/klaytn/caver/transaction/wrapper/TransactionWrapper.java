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

package com.klaytn.caver.transaction.wrapper;

import com.klaytn.caver.rpc.Klay;
import com.klaytn.caver.transaction.AbstractTransaction;
import com.klaytn.caver.transaction.TransactionDecoder;
import com.klaytn.caver.transaction.type.wrapper.*;

/**
 * Represents a TransactionWrapper
 * 1. This class contains all types of transaction wrapper classes as member variables
 * 2. This class should be accessed via `caver.transaction`
 */
public class TransactionWrapper {
    /**
     * LegacyTransactionWrapper instance
     */
    public LegacyTransactionWrapper legacyTransaction;

    /**
     * ValueTransferWrapper instance
     */
    public ValueTransferWrapper valueTransfer;

    /**
     * FeeDelegatedValueTransferWrapper instance
     */
    public FeeDelegatedValueTransferWrapper feeDelegatedValueTransfer;

    /**
     * FeeDelegatedValueTransferWithRatioWrapper instance
     */
    public FeeDelegatedValueTransferWithRatioWrapper feeDelegatedValueTransferWithRatio;

    /**
     * ValueTransferMemoWrapper instance
     */
    public ValueTransferMemoWrapper valueTransferMemo;

    /**
     * FeeDelegatedValueTransferMemoWrapper instance
     */
    public FeeDelegatedValueTransferMemoWrapper feeDelegatedValueTransferMemo;

    /**
     * FeeDelegatedValueTransferMemoWithRatioWrapper instance
     */
    public FeeDelegatedValueTransferMemoWithRatioWrapper feeDelegatedValueTransferMemoWithRatio;

    /**
     * AccountUpdateWrapper instance
     */
    public AccountUpdateWrapper accountUpdate;

    /**
     * FeeDelegatedAccountUpdateWrapper instance
     */
    public FeeDelegatedAccountUpdateWrapper feeDelegatedAccountUpdate;

    /**
     * FeeDelegatedAccountUpdateWithRatioWrapper instance
     */
    public FeeDelegatedAccountUpdateWithRatioWrapper feeDelegatedAccountUpdateWithRatio;

    /**
     * SmartContractDeployWrapper instance
     */
    public SmartContractDeployWrapper smartContractDeploy;

    /**
     * FeeDelegatedSmartContractDeployWrapper instance
     */
    public FeeDelegatedSmartContractDeployWrapper feeDelegatedSmartContractDeploy;

    /**
     * FeeDelegatedSmartContractDeployWithRatioWrapper instance
     */
    public FeeDelegatedSmartContractDeployWithRatioWrapper feeDelegatedSmartContractDeployWithRatio;

    /**
     * SmartContractExecutionWrapper instance
     */
    public SmartContractExecutionWrapper smartContractExecution;

    /**
     * FeeDelegatedSmartContractExecutionWrapper instance
     */
    public FeeDelegatedSmartContractExecutionWrapper feeDelegatedSmartContractExecution;

    /**
     * FeeDelegatedSmartContractExecutionWithRatioWrapper instance
     */
    public FeeDelegatedSmartContractExecutionWithRatioWrapper feeDelegatedSmartContractExecutionWithRatio;

    /**
     * CancelWrapper instance
     */
    public CancelWrapper cancel;

    /**
     * FeeDelegatedCancelWrapper instance
     */
    public FeeDelegatedCancelWrapper feeDelegatedCancel;

    /**
     * FeeDelegatedCancelWithRatioWrapper instance
     */
    public FeeDelegatedCancelWithRatioWrapper feeDelegatedCancelWithRatio;

    /**
     * ChainDataAnchoringWrapper instance
     */
    public ChainDataAnchoringWrapper chainDataAnchoring;

    /**
     * FeeDelegatedChainDataAnchoringWrapper instance
     */
    public FeeDelegatedChainDataAnchoringWrapper feeDelegatedChainDataAnchoring;

    /**
     * FeeDelegatedChainDataAnchoringWithRatioWrapper instance
     */
    public FeeDelegatedChainDataAnchoringWithRatioWrapper feeDelegatedChainDataAnchoringWithRatio;

    /**
     * Creates a Transaction instance
     * @param klaytnCall Klay RPC instance
     */
    public TransactionWrapper(Klay klaytnCall) {
        this.legacyTransaction = new LegacyTransactionWrapper(klaytnCall);

        this.valueTransfer = new ValueTransferWrapper(klaytnCall);
        this.feeDelegatedValueTransfer = new FeeDelegatedValueTransferWrapper(klaytnCall);
        this.feeDelegatedValueTransferWithRatio = new FeeDelegatedValueTransferWithRatioWrapper(klaytnCall);

        this.valueTransferMemo = new ValueTransferMemoWrapper(klaytnCall);
        this.feeDelegatedValueTransferMemo = new FeeDelegatedValueTransferMemoWrapper(klaytnCall);
        this.feeDelegatedValueTransferMemoWithRatio = new FeeDelegatedValueTransferMemoWithRatioWrapper(klaytnCall);

        this.accountUpdate = new AccountUpdateWrapper(klaytnCall);
        this.feeDelegatedAccountUpdate = new FeeDelegatedAccountUpdateWrapper(klaytnCall);
        this.feeDelegatedAccountUpdateWithRatio = new FeeDelegatedAccountUpdateWithRatioWrapper(klaytnCall);

        this.smartContractDeploy = new SmartContractDeployWrapper(klaytnCall);
        this.feeDelegatedSmartContractDeploy = new FeeDelegatedSmartContractDeployWrapper(klaytnCall);
        this.feeDelegatedSmartContractDeployWithRatio = new FeeDelegatedSmartContractDeployWithRatioWrapper(klaytnCall);

        this.smartContractExecution = new SmartContractExecutionWrapper(klaytnCall);
        this.feeDelegatedSmartContractExecution = new FeeDelegatedSmartContractExecutionWrapper(klaytnCall);
        this.feeDelegatedSmartContractExecutionWithRatio = new FeeDelegatedSmartContractExecutionWithRatioWrapper(klaytnCall);

        this.cancel = new CancelWrapper(klaytnCall);
        this.feeDelegatedCancel = new FeeDelegatedCancelWrapper(klaytnCall);
        this.feeDelegatedCancelWithRatio = new FeeDelegatedCancelWithRatioWrapper(klaytnCall);

        this.chainDataAnchoring = new ChainDataAnchoringWrapper(klaytnCall);
        this.feeDelegatedChainDataAnchoring = new FeeDelegatedChainDataAnchoringWrapper(klaytnCall);
        this.feeDelegatedChainDataAnchoringWithRatio = new FeeDelegatedChainDataAnchoringWithRatioWrapper(klaytnCall);
    }

    /**
     * Decodes a RLP-encoded transaction and returns it with matching type of transaction
     * @param rlpEncoded RLP-encoded transaction
     * @return AbstractTransaction
     */
    public AbstractTransaction decode(String rlpEncoded) {
        return TransactionDecoder.decode(rlpEncoded);
    }
}
