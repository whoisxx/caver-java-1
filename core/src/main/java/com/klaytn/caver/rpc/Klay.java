/*
 * Copyright 2020 The caver-java Authors
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

package com.klaytn.caver.rpc;

import com.klaytn.caver.account.IAccountKey;
import com.klaytn.caver.methods.request.CallObject;
import com.klaytn.caver.methods.request.KlayFilter;
import com.klaytn.caver.methods.request.KlayLogFilter;
import com.klaytn.caver.methods.response.Boolean;
import com.klaytn.caver.methods.response.*;
import com.klaytn.caver.transaction.AbstractFeeDelegatedTransaction;
import com.klaytn.caver.transaction.AbstractTransaction;
import com.klaytn.caver.utils.Utils;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.*;

import java.util.Arrays;
import java.util.Collections;

public class Klay {

    /**
     * JSON-RPC service instance.
     */
    protected final Web3jService web3jService;

    /**
     * Creates a Klay instance
     * @param web3jService JSON-RPC service instance.
     */
    public Klay(Web3jService web3jService) {
        this.web3jService = web3jService;
    }

    /**
     * Returns true if the account associated with the address is created. It returns false otherwise.
     * It sets block tag to "LATEST"
     * @param address The account address
     * @return Boolean - The existence of an input address
     */
    public Request<?, Boolean> accountCreated(String address) {
        return accountCreated(address, DefaultBlockParameterName.LATEST);
    }

    /**
     * Returns true if the account associated with the address is created. It returns false otherwise.
     * @param address The account address
     * @param blockNumber The block number.
     * @return Boolean - The existence of an input address
     */
    public Request<?, Boolean> accountCreated(String address, long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return accountCreated(address, blockParameterNumber);
    }

    /**
     * Returns true if the account associated with the address is created. It returns false otherwise.
     * @param address The account address.
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return Boolean - The existence of an input address
     */
    public Request<?, Boolean> accountCreated(String address, DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_accountCreated",
                Arrays.asList(
                        address,
                        blockTag
                ),
                web3jService,
                Boolean.class
        );
    }

    /**
     * Returns a list of addresses owned by client.
     * @return Addresses - Addresses owned by the client.
     */
    public Request<?, Addresses> getAccounts() {
        return new Request<>(
                "klay_accounts",
                Collections.<String>emptyList(),
                web3jService,
                Addresses.class);
    }

    /**
     * Encodes an account key using the RLP encoding scheme.
     * @param accountKey Account Key Object
     * @return Bytes
     */
    public Request<?, Bytes> encodeAccountKey(IAccountKey accountKey) {
        return new Request<>(
                "klay_encodeAccountKey",
                Arrays.asList(accountKey),
                web3jService,
                Bytes.class);
    }

    /**
     * Decodes an RLP encoded account key.
     * @param encodedAccountKey RLP encoded account key
     * @return AccountKeyResponse
     */
    public Request<?, AccountKey> decodeAccountKey(String encodedAccountKey) {
        return new Request<>(
                "klay_decodeAccountKey",
                Arrays.asList(encodedAccountKey),
                web3jService,
                AccountKey.class
        );
    }


    /**
     * Returns the account information of a given address.
     * There are two different account types in Klaytn: Externally Owned Account (EOA) and Smart Contract Account.
     * It sets block tag to "LATEST"
     * @param address The account address.
     * @return AccountResponse
     */
    public Request<?, Account> getAccount(String address) {
        return getAccount(address, DefaultBlockParameterName.LATEST);
    }

    /**
     * Returns the account information of a given address.
     * There are two different account types in Klaytn: Externally Owned Account (EOA) and Smart Contract Account.
     * @param address The account address.
     * @param blockNumber The block number..
     * @return AccountResponse
     */
    public Request<?, Account> getAccount(String address, long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return getAccount(address, blockParameterNumber);
    }

    /**
     * Returns the account information of a given address.
     * There are two different account types in Klaytn: Externally Owned Account (EOA) and Smart Contract Account.
     * @param address The account address
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return AccountResponse
     */
    public Request<?, Account> getAccount(String address, DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getAccount",
                Arrays.asList(
                        address,
                        blockTag
                ),
                web3jService,
                Account.class);
    }

    /**
     * Returns AccountKey of a given address.
     * If the account has AccountKeyLegacy or the account of the given address is a Smart Contract Account, it will return an empty key value.
     * It sets block tag to "LATEST".
     * @param address The account address
     * @return AccountKeyResponse
     */
    public Request<?, AccountKey> getAccountKey(String address) {
        return getAccountKey(address, DefaultBlockParameterName.LATEST);
    }

    /**
     * Returns AccountKey of a given address.
     * If the account has AccountKeyLegacy or the account of the given address is a Smart Contract Account, it will return an empty key value.
     * @param address The account address
     * @param blockNumber The block number..
     * @return AccountKeyResponse
     */
    public Request<?, AccountKey> getAccountKey(String address, long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return getAccountKey(address, blockParameterNumber);
    }

    /**
     * Returns AccountKey of a given address.
     * If the account has AccountKeyLegacy or the account of the given address is a Smart Contract Account, it will return an empty key value.
     * @param address The account address
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return AccountKeyResponse
     */
    public Request<?, AccountKey> getAccountKey(String address, DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getAccountKey",
                Arrays.asList(
                        address,
                        blockTag.getValue()
                ),
                web3jService,
                AccountKey.class);
    }

    /**
     * Returns the balance of the account of given address.
     * It sets block tag to "LATEST".
     * @param address The account address to check for balance.
     * @return Quantity
     */
    public Request<?, Quantity> getBalance(String address) {
        return getBalance(address, DefaultBlockParameterName.LATEST);
    }

    /**
     * Returns the balance of the account of given address.
     * @param address The account address to check for balance.
     * @param blockNumber The block number.
     * @return Quantity
     */
    public Request<?, Quantity> getBalance(String address, long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return getBalance(address, blockParameterNumber);
    }

    /**
     * Returns the balance of the account of given address.
     * @param address The account address to check for balance.
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return Quantity
     */
    public Request<?, Quantity> getBalance(String address, DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getBalance",
                Arrays.asList(
                        address,
                        blockTag
                ),
                web3jService,
                Quantity.class);
    }

    /**
     * Returns code at a given address.
     * It sets block tag to "LATEST"
     * @param address The account address
     * @return Bytes
     */
    public Request<?, Bytes> getCode(String address) {
        return getCode(address, DefaultBlockParameterName.LATEST);
    }


    /**
     * Returns code at a given address.
     * @param address The account address
     * @param blockNumber The block number.
     * @return Bytes
     */
    public Request<?, Bytes> getCode(String address, long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return getCode(address, blockParameterNumber);
    }

    /**
     * Returns code at a given address.
     * @param address The account address
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return Bytes
     */
    public Request<?, Bytes> getCode(String address, DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getCode",
                Arrays.asList(
                        address,
                        blockTag.getValue()
                ),
                web3jService,
                Bytes.class);
    }

    /**
     * Returns the number of transactions sent from an address.
     * It sets block tag to "LATEST".
     * @param address The account address
     * @return Quantity
     */
    public Request<?, Quantity> getTransactionCount(String address) {
        return getTransactionCount(address, DefaultBlockParameterName.LATEST);
    }

    /**
     * Returns the number of transactions sent from an address.
     * @param address The account address
     * @param blockNumber The block number.
     * @return Quantity
     */
    public Request<?, Quantity> getTransactionCount(String address, long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return getTransactionCount(address, blockParameterNumber);
    }

    /**
     * Returns the number of transaction sent from an address
     * @param address The account address
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return Quantity
     */
    public Request<?, Quantity> getTransactionCount(String address, DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getTransactionCount",
                Arrays.asList(
                        address,
                        blockTag.getValue()
                ),
                web3jService,
                Quantity.class);
    }

    /**
     * Returns true if an input account has a non-empty codeHash at the time of a specific block number.
     * It returns false if the account is an EOA or a smart contract account which doesn't have codeHash.
     * It sets block tag to "LATEST".
     * @param address The account address
     * @return Boolean
     */
    public Request<?, Boolean> isContractAccount(String address) {
        return isContractAccount(address, DefaultBlockParameterName.LATEST);
    }

    /**
     * Returns true if an input account has a non-empty codeHash at the time of a specific block number.
     * It returns false if the account is an EOA or a smart contract account which doesn't have codeHash.
     * @param address The account address
     * @param blockNumber The block number..
     * @return Boolean
     */
    public Request<?, Boolean> isContractAccount(String address, long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return isContractAccount(address, blockParameterNumber);
    }

    /**
     * Returns true if an input account has a non-empty codeHash at the time of a specific block number.
     * It returns false if the account is an EOA or a smart contract account which doesn't have codeHash.
     * @param address The account address
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return Boolean
     */
    public Request<?, Boolean> isContractAccount(String address, DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_isContractAccount",
                Arrays.asList(
                        address,
                        blockTag.getValue()
                ),
                web3jService,
                Boolean.class);
    }

    /**
     * The sign method calculates a Klaytn-specific signature.
     * NOTE : The address to sign with must be unlocked.
     * @param address The account address
     * @param message The message to sign.
     * @return Bytes
     */
    public Request<?, Bytes> sign(String address, String message) {
        return new Request<>(
                "klay_sign",
                Arrays.asList(
                        address,
                        message
                ),
                web3jService,
                Bytes.class);
    }

    /**
     * Returns the number of most recent block.
     * @return Quantity
     */
    public Request<?, Quantity> getBlockNumber() {
        return new Request<>(
                "klay_blockNumber",
                Collections.<String>emptyList(),
                web3jService,
                Quantity.class);
    }

    /**
     * Returns information about a block by block number.
     * It set "isFullTransaction" param to true.
     * @param blockNumber The block number.
     * @return Block
     */
    public Request<?, Block> getBlockByNumber(long blockNumber) {
        return getBlockByNumber(blockNumber, true);
    }

    /**
     * Returns information about a block by block number.
     * It set "isFullTransaction" param to true.
     * @param blockNumber The block number.
     * @param isFullTransaction If true it returns the full transaction objects, if false only the hashes of the transactions.
     * @return Block
     */
    public Request<?, Block> getBlockByNumber(long blockNumber, boolean isFullTransaction) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);
        return getBlockByNumber(blockParameterNumber, isFullTransaction);
    }

    /**
     * Returns information about a block by block number.
     * It set "isFullTransaction" param to true.
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return KlayBlock
     */
    public Request<?, Block> getBlockByNumber(DefaultBlockParameter blockTag) {
        return getBlockByNumber(blockTag, true);
    }

    /**
     * Returns information about a block by block number.
     * @param defaultBlockParameter The string "latest", "earliest" or "pending"
     * @param isFullTransaction If true it returns the full transaction objects, if false only the hashes of the transactions.
     * @return Block
     */
    public Request<?, Block> getBlockByNumber(DefaultBlockParameter defaultBlockParameter, boolean isFullTransaction) {
        return new Request<>(
                "klay_getBlockByNumber",
                Arrays.asList(defaultBlockParameter, isFullTransaction),
                web3jService,
                Block.class);
    }

    /**
     * Returns information about a block by block number.
     * It set "isFullTransaction" param to true.
     * @param blockHash The hash of block.
     * @return Block
     */
    public Request<?, Block> getBlockByHash(String blockHash) {
        return getBlockByHash(blockHash, true);
    }

    /**
     * Returns information about a block by block number.
     * @param blockHash The hash of block.
     * @param isFullTransaction If true it returns the full transaction objects, if false only the hashes of the transactions.
     * @return Block
     */
    public Request<?, Block> getBlockByHash(String blockHash, boolean isFullTransaction) {
        return new Request<>(
                "klay_getBlockByHash",
                Arrays.asList(blockHash, isFullTransaction),
                web3jService,
                Block.class);
    }

    /**
     * Returns receipts included in a block identified by block hash.
     * @param blockHash The hash of block.
     * @return BlockReceipt
     */
    public Request<?, BlockTransactionReceipts> getBlockReceipts(String blockHash) {
        return new Request<>(
                "klay_getBlockReceipts",
                Arrays.asList(blockHash),
                web3jService,
                BlockTransactionReceipts.class);
    }

    /**
     * Returns the number of transactions in a block matching the given block number.
     * @param blockNumber The block number.
     * @return Quantity
     */
    public Request<?, Quantity> getBlockTransactionCountByNumber(long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);
        return getTransactionCountByNumber(blockParameterNumber);
    }

    /**
     * Returns the number of transactions in a block matching the given block number.
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return Quantity
     */
    public Request<?, Quantity> getBlockTransactionCountByNumber(DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getBlockTransactionCountByNumber",
                Arrays.asList(blockTag),
                web3jService,
                Quantity.class);
    }

    /**
     * Returns the number of transactions in a block matching the given block number.
     * @param blockNumber The block number.
     * @return Quantity
     */
    public Request<?, Quantity> getTransactionCountByNumber(long blockNumber) {
        return getBlockTransactionCountByNumber(blockNumber);
    }

    /**
     * Returns the number of transactions in a block matching the given block number.
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return Quantity
     */
    public Request<?, Quantity> getTransactionCountByNumber(DefaultBlockParameter blockTag) {
        return getBlockTransactionCountByNumber(blockTag);
    }

    /**
     * Returns the number of transactions in a block from a block matching the given block hash.
     * @param blockHash The hash of a block
     * @return Quantity
     */
    public Request<?, Quantity> getBlockTransactionCountByHash(String blockHash) {
        return new Request<>(
                "klay_getBlockTransactionCountByHash",
                Arrays.asList(blockHash),
                web3jService,
                Quantity.class);
    }

    /**
     * Returns the number of transactions in a block from a block matching the given block hash.
     * @param blockHash The hash of a block
     * @return Quantity
     */
    public Request<?, Quantity> getTransactionCountByHash(String blockHash) {
        return getBlockTransactionCountByHash(blockHash);
    }

    /**
     * Returns a block with consensus information matched by the given hash.
     * @param blockHash The hash of a block.
     * @return BlockWithConsensusInfo
     */
    public Request<?, BlockWithConsensusInfo> getBlockWithConsensusInfoByHash(String blockHash) {
        return new Request<>(
                "klay_getBlockWithConsensusInfoByHash",
                Arrays.asList(blockHash),
                web3jService,
                BlockWithConsensusInfo.class);
    }

    /**
     * Returns a block with consensus information matched by the given block number.
     * @param blockNumber The block number.
     * @return BlockWithConsensusInfo
     */
    public Request<?, BlockWithConsensusInfo> getBlockWithConsensusInfoByNumber(long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return getBlockWithConsensusInfoByNumber(blockParameterNumber);
    }

    /**
     * Returns a block with consensus information matched by the given block number.
     * @param blockTag The string "latest", "earliest"
     * @return BlockWithConsensusInfo
     */
    public Request<?, BlockWithConsensusInfo> getBlockWithConsensusInfoByNumber(DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getBlockWithConsensusInfoByNumber",
                Arrays.asList(blockTag),
                web3jService,
                BlockWithConsensusInfo.class);
    }

    /**
     * Returns a list of all validators in the committee at the specified block.
     * If the parameter is not set, returns a list of all validators in the committee at the latest block.
     * It sets block tag to "LATEST".
     * @return Addresses
     */
    public Request<?, Addresses> getCommittee() {
        return getCommittee(DefaultBlockParameterName.LATEST);
    }

    /**
     * Returns a list of all validators in the committee at the specified block.
     * If the parameter is not set, returns a list of all validators in the committee at the latest block.
     * @param blockNumber The block number.
     * @return Addresses
     */
    public Request<?, Addresses> getCommittee(long blockNumber) {
        DefaultBlockParameterNumber parameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return getCommittee(parameterNumber);
    }

    /**
     * Returns a list of all validators in the committee at the specified block.
     * If the parameter is not set, returns a list of all validators in the committee at the latest block.
     * @param blockTag The string "latest", "earliest"
     * @return Addresses
     */
    public Request<?, Addresses> getCommittee(DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getCommittee",
                Arrays.asList(blockTag),
                web3jService,
                Addresses.class
        );
    }

    /**
     * Returns the size of the committee at the specified block.
     * If the parameter is not set, returns the size of the committee at the latest block.
     * It sets block tag to "LATEST".
     * @return Quantity
     */
    public Request<?, Quantity> getCommitteeSize() {
        return getCommitteeSize(DefaultBlockParameterName.LATEST);
    }

    /**
     * Returns the size of the committee at the specified block.
     * If the parameter is not set, returns the size of the committee at the latest block.
     * @param blockNumber The block number.
     * @return Quantity
     */
    public Request<?, Quantity> getCommitteeSize(long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return getCommitteeSize(blockParameterNumber);
    }

    /**
     * Returns the size of the committee at the specified block.
     * If the parameter is not set, returns the size of the committee at the latest block.
     * @param blockTag The string "earliest" or "latest".
     * @return
     */
    public Request<?, Quantity> getCommitteeSize(DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getCommitteeSize",
                Arrays.asList(blockTag),
                web3jService,
                Quantity.class
        );
    }

    /**
     * Returns a list of all validators of the council at the specified block.
     * If the parameter is not set, returns a list of all validators of the council at the latest block.
     * It set to block tag to "LATEST".
     * @return Addresses
     */
    public Request<?, Addresses> getCouncil() {
        return getCouncil(DefaultBlockParameterName.LATEST);
    }

    /**
     * Returns a list of all validators of the council at the specified block.
     * If the parameter is not set, returns a list of all validators of the council at the latest block.
     * @param blockNumber The block number.
     * @return Addresses
     */
    public Request<?, Addresses> getCouncil(long blockNumber) {
        DefaultBlockParameterNumber number = new DefaultBlockParameterNumber(blockNumber);
        return getCouncil(number);
    }

    /**
     * Returns a list of all validators of the council at the specified block.
     * If the parameter is not set, returns a list of all validators of the council at the latest block.
     * @param blockTag The string "earliest" or "latest".
     * @return Addresses
     */
    public Request<?, Addresses> getCouncil(DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getCouncil",
                Arrays.asList(blockTag),
                web3jService,
                Addresses.class
        );
    }

    /**
     * Returns the size of the council at the specified block.
     * If the parameter is not set, returns the size of the council at the latest block.
     * It sets block tag to "LATEST".
     * @return Quantity
     */
    public Request<?, Quantity> getCouncilSize() {
        return getCouncilSize(DefaultBlockParameterName.LATEST);
    }

    /**
     * Returns the size of the council at the specified block.
     * If the parameter is not set, returns the size of the council at the latest block.
     * @param blockNumber The block number
     * @return Quantity
     */
    public Request<?, Quantity> getCouncilSize(long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return getCouncilSize(blockParameterNumber);
    }

    /**
     * Returns the size of the council at the specified block.
     * If the parameter is not set, returns the size of the council at the latest block.
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return Quantity
     */
    public Request<?, Quantity> getCouncilSize(DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getCouncilSize",
                Arrays.asList(blockTag),
                web3jService,
                Quantity.class
        );
    }

    /**
     * Returns the value from a storage position at a given address.
     * @param address The account address.
     * @param position Integer of the position in the storage.
     * @param blockNumber The block number.
     * @return Bytes
     */
    public Request<?, Bytes> getStorageAt(
            String address, DefaultBlockParameterNumber position, long blockNumber) {
        DefaultBlockParameterNumber defaultBlockParameterNumber = new DefaultBlockParameterNumber(blockNumber);

        return getStorageAt(address, position, blockNumber);
    }

    /**
     * Returns the value from a storage position at a given address.
     * @param address The account address.
     * @param position Integer of the position in the storage.
     * @param blockTag The string "latest", "earliest" or "pending"
     * @return Bytes
     */
    public Request<?, Bytes> getStorageAt(
            String address, DefaultBlockParameterNumber position, DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_getStorageAt",
                Arrays.asList(
                        address,
                        position,
                        blockTag.getValue()
                ),
                web3jService,
                Bytes.class);
    }

    /**
     * Returns an object with data about the sync status or false.
     * @return KlaySyncing
     */
    public Request<?, KlaySyncing> isSyncing() {
        return new Request<>(
                "klay_syncing",
                Collections.<String>emptyList(),
                web3jService,
                KlaySyncing.class);
    }

    /**
     * Returns the chain ID of the chain.
     * @return Quantity
     */
    public Request<?, Quantity> getChainID() {
        return new Request<>(
                "klay_chainID",
                Collections.<String>emptyList(),
                web3jService,
                Quantity.class);
    }

    /**
     * Executes a new message call immediately without creating a transaction on the block chain.
     * It returns data or an error object of JSON RPC if error occurs.
     * It sets block tag to "LATEST".
     * @param callObject The transaction call object.
     * @return Bytes
     */
    public Request<?, Bytes> call(CallObject callObject) {
        return call(callObject, DefaultBlockParameterName.LATEST);
    }

    /**
     * Executes a new message call immediately without creating a transaction on the block chain.
     * It returns data or an error object of JSON RPC if error occurs.
     * @param callObject The transaction call object.
     * @param blockNumber The block number.
     * @return Bytes
     */
    public Request<?, Bytes> call(CallObject callObject, Quantity blockNumber) {
        return new Request<>(
                "klay_call",
                Arrays.asList(callObject, blockNumber),
                web3jService,
                Bytes.class);
    }

    /**
     * Executes a new message call immediately without creating a transaction on the block chain.
     * It returns data or an error object of JSON RPC if error occurs.
     * @param callObject The transaction call object.
     * @param blockTag the string "latest", "earliest" or "pending"
     * @return Bytes
     */
    public Request<?, Bytes> call(CallObject callObject, DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_call",
                Arrays.asList(callObject, blockTag),
                web3jService,
                Bytes.class);
    }

    /**
     * Generates and returns an estimate of how much gas is necessary to allow the transaction to complete.
     * The transaction will not be added to the blockchain. Note that the estimate may be significantly more
     * than the amount of gas actually used by the transaction, for a variety of reasons including Klaytn Virtual
     * Machine mechanics and node performance.
     * @param callObject The transaction call object.
     * @return Quantity
     */
    public Request<?, Quantity> estimateGas(CallObject callObject) {
        return new Request<>(
                "klay_estimateGas",
                Arrays.asList(callObject),
                web3jService,
                Quantity.class);
    }

    /**
     * Generates and returns an estimate of how much computation cost spent to execute the transaction.
     * Klaytn limits the computation cost of a transaction to 100000000 currently not to take too much time
     * by a single transaction. The transaction will not be added to the blockchain like klay_estimateGas.
     * @param callObject The transaction call object.
     * @return Quantity
     */
    public Request<?, Quantity> estimateComputationCost(CallObject callObject) {
        return estimateComputationCost(callObject, DefaultBlockParameterName.LATEST);
    }

    /**
     * Generates and returns an estimate of how much computation cost spent to execute the transaction.
     * Klaytn limits the computation cost of a transaction to 100000000 currently not to take too much time
     * by a single transaction. The transaction will not be added to the blockchain like klay_estimateGas.
     * @param callObject The transaction call object.
     * @return Quantity
     */
    public Request<?, Quantity> estimateComputationCost(CallObject callObject, long blockNumber) {
        return estimateComputationCost(callObject, new DefaultBlockParameterNumber(blockNumber));
    }

    /**
     * Generates and returns an estimate of how much computation cost spent to execute the transaction.
     * Klaytn limits the computation cost of a transaction to 100000000 currently not to take too much time
     * by a single transaction. The transaction will not be added to the blockchain like klay_estimateGas.
     * @param callObject The transaction call object.
     * @return Quantity
     */
    public Request<?, Quantity> estimateComputationCost(CallObject callObject, DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_estimateComputationCost",
                Arrays.asList(callObject, blockTag),
                web3jService,
                Quantity.class);
    }

    /**
     * Returns information about a transaction by block hash and transaction index position.
     * @param blockHash The hash of a block.
     * @param index Integer of the transaction index position.
     * @return Transaction
     */
    public Request<?, Transaction> getTransactionByBlockHashAndIndex(String blockHash, long index) {
        DefaultBlockParameterNumber indexNumber = new DefaultBlockParameterNumber(index);

        return new Request<>(
                "klay_getTransactionByBlockHashAndIndex",
                Arrays.asList(blockHash, indexNumber),
                web3jService,
                Transaction.class);
    }

    /**
     * Returns information about a transaction by block number and transaction index position.
     * @param blockNumber The block number
     * @param index The transaction index position.
     * @return Transaction
     */
    public Request<?, Transaction> getTransactionByBlockNumberAndIndex(long blockNumber, long index) {
        return getTransactionByBlockNumberAndIndex(
                new DefaultBlockParameterNumber(blockNumber),
                new DefaultBlockParameterNumber(index)
        );
    }


    /**
     * Returns information about a transaction by block number and transaction index position.
     * @param blockTag The string "latest", "earliest" or "pending"
     * @param index The transaction index position.
     * @return Transaction
     */
    public Request<?, Transaction> getTransactionByBlockNumberAndIndex(DefaultBlockParameter blockTag, DefaultBlockParameterNumber index) {
        return new Request<>(
                "klay_getTransactionByBlockNumberAndIndex",
                Arrays.asList(blockTag, index),
                web3jService,
                Transaction.class);
    }

    /**
     * Returns the information about a transaction requested by transaction hash.
     * @param txHash The hash of a transaction
     * @return Transaction
     */
    public Request<?, Transaction> getTransactionByHash(String txHash) {
        return new Request<>(
                "klay_getTransactionByHash",
                Arrays.asList(txHash),
                web3jService,
                Transaction.class);
    }

    /**
     * Returns the information about a transaction requested by sender transaction hash.
     * Please note that this API returns correct result only if indexing feature is enabled by --sendertxhashindexing.
     * This can be checked by call klay_isSenderTxHashIndexingEnabled.
     * @param senderTxHash The hash of a transaction before signing of feePayer(senderTransactionHash)
     * @return Transaction
     */
    public Request<?, Transaction> getTransactionBySenderTxHash(String senderTxHash) {
        return new Request<>(
                "klay_getTransactionBySenderTxHash",
                Arrays.asList(senderTxHash),
                web3jService,
                Transaction.class);
    }

    /**
     * Returns the receipt of a transaction by transaction hash.
     * NOTE: The receipt is not available for pending transactions.
     * @param transactionHash The hash of a transaction.
     * @return TransactionReceipt
     */
    public Request<?, TransactionReceipt> getTransactionReceipt(String transactionHash) {
        return new Request<>(
                "klay_getTransactionReceipt",
                Arrays.asList(transactionHash),
                web3jService,
                TransactionReceipt.class);
    }

    /**
     * Returns the receipt of a transaction by sender transaction hash.
     * @param transactionHash The hash of a transaction before signing of feePayer(senderTransactionHash).
     * @return TransactionReceipt
     */
    public Request<?, TransactionReceipt> getTransactionReceiptBySenderTxHash(String transactionHash) {
        return new Request<>(
                "klay_getTransactionReceiptBySenderTxHash",
                Arrays.asList(transactionHash),
                web3jService,
                TransactionReceipt.class);
    }

    /**
     * Creates a new message call transaction or a contract creation for signed transactions.
     * @param signedTransactionData The signed transaction data.
     * @return Bytes32
     */
    public Request<?, Bytes32> sendRawTransaction(String signedTransactionData) {
        return new Request<>(
                "klay_sendRawTransaction",
                Arrays.asList(signedTransactionData),
                web3jService,
                Bytes32.class);
    }

    /**
     * Creates a new message call transaction or a contract creation for signed transactions.
     * @param transaction A transaction instance.
     * @return Bytes32
     */
    public Request<?, Bytes32> sendRawTransaction(AbstractTransaction transaction) {
        String rawTransaction = transaction.getRLPEncoding();

        return new Request<>(
                "klay_sendRawTransaction",
                Arrays.asList(rawTransaction),
                web3jService,
                Bytes32.class);
    }

    /**
     * Constructs a transaction with given parameters, signs the transaction with a sender's private key and propagates the transaction to Klaytn network.
     * NOTE: The address to sign with must be unlocked.
     * @param transaction The object inherits AbstractTransaction.
     * @return Bytes32
     */
    public Request<?, Bytes32> sendTransaction(AbstractTransaction transaction) {
        return new Request<>(
                "klay_sendTransaction",
                Arrays.asList(transaction),
                web3jService,
                Bytes32.class);
    }

    /**
     * Constructs a transaction with given parameters, signs the transaction with a fee payer's private key and propagates the transaction to Klaytn network.
     * This API supports only fee delegated type (including partial fee delegated type) transactions.
     * NOTE: The fee payer address to sign with must be unlocked.
     * @param transaction The object inherits AbstractFeeDelegatedTransaction.
     * @return Bytes32
     */
    public Request<?, Bytes32> sendTransactionAsFeePayer(AbstractFeeDelegatedTransaction transaction) {
        return new Request<>(
                "klay_sendTransactionAsFeePayer",
                Arrays.asList(transaction),
                web3jService,
                Bytes32.class);
    }

    /**
     * Constructs a transaction with given parameters and signs the transaction with a sender's private key.
     * This method can be used either to generate a sender signature or to make a final raw transaction that is ready to submit to Klaytn network.
     * NOTE: The address to sign with must be unlocked.
     * @param transaction The object inherits AbstractTransaction.
     * @return KlaySignTransaction
     */
    public Request<?, SignTransaction> signTransaction(AbstractTransaction transaction) {
        if(Utils.isEmptySig(transaction.getSignatures())) {
            transaction.getSignatures().remove(0);
        }

        return new Request<>(
                "klay_signTransaction",
                Arrays.asList(transaction),
                web3jService,
                SignTransaction.class);
    }

    /**
     * Constructs a transaction with given parameters and signs the transaction with a fee payer's private key.
     * This method can be used either to generate a fee payer signature or to make a final raw transaction that is ready to submit to Klaytn network.
     * In case you just want to extract the fee-payer signature, simply take the feePayerSignatures from the result.
     * Note that the raw transaction is not final if the sender's signature is not attached (that is, signatures in tx is empty).
     * NOTE: The fee payer address to sign with must be unlocked.
     * @param transaction The object inherits AbstractFeeDelegatedTransaction.
     * @return KlaySignTransaction
     */
    public Request<?, SignTransaction> signTransactionAsFeePayer(AbstractFeeDelegatedTransaction transaction) {
        if(Utils.isEmptySig(transaction.getSignatures())) {
            transaction.getSignatures().remove(0);
        }

        return new Request<>(
                "klay_signTransactionAsFeePayer",
                Arrays.asList(transaction),
                web3jService,
                SignTransaction.class);
    }

    /**
     * Returns the decoded anchored data in the transaction for the given transaction hash.
     * @param hash The hash of transaction
     * @return DecodeAnchoringTransaction
     */
    public Request<?, DecodeAnchoringTransaction> getDecodedAnchoringTransaction(String hash) {
        return new Request<>(
                "klay_getDecodedAnchoringTransactionByHash",
                Arrays.asList(hash),
                web3jService,
                DecodeAnchoringTransaction.class
        );
    }

    /**
     * Returns the current client version of a Klaytn node.
     * @return Bytes
     */
    public Request<?, Bytes> getClientVersion() {
        return new Request<>(
                "klay_clientVersion",
                Collections.<String>emptyList(),
                web3jService,
                Bytes.class);
    }

    /**
     * Returns the current price per gas in peb.
     * NOTE: This API has different behavior from Ethereum's and returns a gas price of Klaytn instead of suggesting a gas price as in Ethereum.
     * @return Quantity
     */
    public Request<?, Quantity> getGasPrice() {
        return new Request<>(
                "klay_gasPrice",
                Collections.<String>emptyList(),
                web3jService,
                Quantity.class);
    }

    /**
     * Returns the unit price of the given block in peb.
     * It returns latest unit price.
     * NOTE: This API has different behavior from Ethereum's and returns a gas price of Klaytn instead of suggesting a gas price as in Ethereum.
     * @return Quantity
     */
    public Request<?, Quantity> getGasPriceAt() {
        return new Request<>(
                "klay_gasPriceAt",
                Arrays.asList(DefaultBlockParameterName.LATEST),
                web3jService,
                Quantity.class
        );
    }

    /**
     * Returns the unit price of the given block in peb.
     * NOTE: This API has different behavior from Ethereum's and returns a gas price of Klaytn instead of suggesting a gas price as in Ethereum.
     * @param blockNumber The block number.
     * @return Quantity
     */
    public Request<?, Quantity> getGasPriceAt(long blockNumber) {
        DefaultBlockParameterNumber blockParameterNumber = new DefaultBlockParameterNumber(blockNumber);
        return getGasPriceAt(blockParameterNumber);
    }

    /**
     * Returns the unit price of the given block in peb.
     * NOTE: This API has different behavior from Ethereum's and returns a gas price of Klaytn instead of suggesting a gas price as in Ethereum.
     * @param blockTag The block tag.
     * @return Quantity
     */
    public Request<?, Quantity> getGasPriceAt(DefaultBlockParameter blockTag) {
        return new Request<>(
                "klay_gasPriceAt",
                Arrays.asList(blockTag),
                web3jService,
                Quantity.class
        );
    }

    /**
     * Returns true if the node is writing blockchain data in parallel manner. It is enabled by default.
     * @return Boolean
     */
    public Request<?, Boolean> isParallelDBWrite() {
        return new Request<>(
                "klay_isParallelDBWrite",
                Collections.<String>emptyList(),
                web3jService,
                Boolean.class);
    }

    /**
     * Returns true if the node is indexing sender transaction hash to transaction hash mapping information.
     * It is disabled by default and can be enabled by --sendertxhashindexing.
     * @return Boolean
     */
    public Request<?, Boolean> isSenderTxHashIndexingEnabled() {
        return new Request<>(
                "klay_isSenderTxHashIndexingEnabled",
                Collections.<String>emptyList(),
                web3jService,
                Boolean.class);
    }

    /**
     * Returns the Klaytn protocol version of the node.
     * @return Bytes
     */
    public Request<?, Bytes> getProtocolVersion() {
        return new Request<>(
                "klay_protocolVersion",
                Collections.<String>emptyList(),
                web3jService,
                Bytes.class);
    }

    /**
     * Returns the reward base of the current node.
     * Reward base is the address of the account where the block rewards goes to. It is only required for CNs.
     * @return Bytes20
     */
    public Request<?, Bytes20> getRewardbase() {
        return new Request<>(
                "klay_rewardbase",
                Collections.<String>emptyList(),
                web3jService,
                Bytes20.class);
    }

    /**
     * Polling method for a filter, which returns an array of logs which occurred since last poll.
     * @param filterId The filter id.
     * @return KlayLogs
     */
    public Request<?, KlayLogs> getFilterChanges(String filterId) {
        return new Request<>(
                "klay_getFilterChanges",
                Arrays.asList(filterId),
                web3jService,
                KlayLogs.class);
    }

    /**
     * Returns an array of all logs matching filter with given id, which has been obtained using klay_newFilter.
     * Note that filter ids returned by other filter creation functions, such as klay_newBlockFilter or klay_newPendingTransactionFilter, cannot be used with this function.
     * @param filterId The filter id.
     * @return KlayLogs
     */
    public Request<?, KlayLogs> getFilterLogs(String filterId) {
        return new Request<>(
                "klay_getFilterLogs",
                Arrays.asList(filterId),
                web3jService,
                KlayLogs.class);
    }

    /**
     * Returns an array of all logs matching a given filter object.
     * @param filterOption The filter options
     * @return KlayLogs
     */
    public Request<?, KlayLogs> getLogs(KlayLogFilter filterOption) {
        return new Request<>(
                "klay_getLogs",
                Arrays.asList(filterOption),
                web3jService,
                KlayLogs.class);
    }

    /**
     * Creates a filter in the node, to notify when a new block arrives.
     * To check if the state has changed, call klay_getFilterChanges.
     * @return Quantity
     */
    public Request<?, Quantity> newBlockFilter() {
        return new Request<>(
                "klay_newBlockFilter",
                Collections.<String>emptyList(),
                web3jService,
                Quantity.class);
    }

    /**
     * Creates a filter object, based on filter options, to notify when the state changes (logs).
     * To check if the state has changed, call getFilterChanges.
     * To obtain all logs matching the filter created by klay_newFilter, call getFilterLogs(String).
     * @param filterOption The filter option.
     * @return Quantity
     */
    public Request<?, Quantity> newFilter(KlayFilter filterOption) {
        return new Request<>(
                "klay_newFilter",
                Arrays.asList(filterOption),
                web3jService,
                Quantity.class);
    }

    /**
     * Creates a filter in the node, to notify when new pending transactions arrive.
     * To check if the state has changed, call klay_getFilterChanges.
     * @return Quantity
     */
    public Request<?, Quantity> newPendingTransactionFilter() {
        return new Request<>(
                "klay_newPendingTransactionFilter",
                Collections.<String>emptyList(),
                web3jService,
                Quantity.class);
    }

    /**
     * Uninstalls a filter with given id. Should always be called when watch is no longer needed.
     * Additionally, filters timeout when they are not requested with klay_getFilterChanges for a period of time.
     * @param filterId A filter id.
     * @return Boolean
     */
    public Request<?, Boolean> uninstallFilter(String filterId) {
        return new Request<>(
                "klay_uninstallFilter",
                Arrays.asList(filterId),
                web3jService,
                Boolean.class);
    }

    /**
     * Returns Keccak-256 (not the standardized SHA3-256) of the given data.
     * @param data The data to convert into a SHA3 hash.
     * @return Bytes
     */
    public Request<?, Bytes> sha3(String data) {
        return new Request<>(
                "klay_sha3",
                Arrays.asList(data),
                web3jService,
                Bytes.class);
    }
}
