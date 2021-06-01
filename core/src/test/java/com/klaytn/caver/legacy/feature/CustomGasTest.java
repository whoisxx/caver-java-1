package com.klaytn.caver.legacy.feature;

import com.klaytn.caver.Caver;
import com.klaytn.caver.crypto.KlayCredentials;
import com.klaytn.caver.fee.FeePayerManager;
import com.klaytn.caver.tx.account.AccountKeyPublic;
import com.klaytn.caver.tx.manager.TransactionManager;
import com.klaytn.caver.tx.model.*;
import com.klaytn.caver.tx.type.AbstractTxType;
import com.klaytn.caver.utils.CodeFormat;
import com.klaytn.caver.utils.TransactionDecoder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.web3j.crypto.Keys;
import com.klaytn.caver.utils.ChainId;
import org.web3j.utils.Numeric;
import static junit.framework.TestCase.assertEquals;

import java.math.BigInteger;

public class CustomGasTest {
    private static final BigInteger GAS_PRICE = BigInteger.valueOf(10L);
    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(3000000L);

    private static int CHAIN_ID;
    private static Caver caver;

    private static KlayCredentials sender;
    private static KlayCredentials feePayer;
    private static KlayCredentials to;

    private static BigInteger nonce;
    private static BigInteger value;

    @BeforeClass
    public static void setup() throws Exception {
        CHAIN_ID = ChainId.BAOBAB_TESTNET;
        caver = Caver.build("https://api.baobab.klaytn.net:8651/");

        sender = KlayCredentials.create(Keys.createEcKeyPair());
        feePayer = KlayCredentials.create(Keys.createEcKeyPair());
        to = KlayCredentials.create(Keys.createEcKeyPair());

        nonce = BigInteger.ZERO;
        value = BigInteger.ZERO;
    }

    @Test
    public void testCustomGasPriceWithValueTransfer() throws Exception {
        ValueTransferTransaction tx = ValueTransferTransaction.create(
                sender.getAddress(),
                to.getAddress(),
                value,
                GAS_PRICE,
                GAS_LIMIT
        );
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        TransactionManager tm = new TransactionManager.Builder(caver, sender).build();
        KlayRawTransaction rawTransaction = tm.sign(tx);
        AbstractTxType decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeDelegate();
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        FeePayerManager fm = new FeePayerManager.Builder(caver, feePayer).build();
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeRatio(BigInteger.valueOf(50));
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);
    }

    @Test
    public void testCustomGasPriceWithValueTransferMemo() throws Exception {
        ValueTransferTransaction tx = ValueTransferTransaction.create(
                sender.getAddress(),
                to.getAddress(),
                value,
                GAS_PRICE,
                GAS_LIMIT
        ).memo("test string");
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        TransactionManager tm = new TransactionManager.Builder(caver, sender).build();
        KlayRawTransaction rawTransaction = tm.sign(tx);
        AbstractTxType decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeDelegate();
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        FeePayerManager fm = new FeePayerManager.Builder(caver, feePayer).build();
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeRatio(BigInteger.valueOf(50));
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);
    }

    @Test
    public void testCustomGasPriceWithAccountUpdate() throws Exception {
        AccountUpdateTransaction tx = AccountUpdateTransaction.create(
                sender.getAddress(),
                AccountKeyPublic.create(Keys.createEcKeyPair().getPublicKey()),
                GAS_PRICE,
                GAS_LIMIT
        );
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        TransactionManager tm = new TransactionManager.Builder(caver, sender).build();
        KlayRawTransaction rawTransaction = tm.sign(tx);
        AbstractTxType decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeDelegate();
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        FeePayerManager fm = new FeePayerManager.Builder(caver, feePayer).build();
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeRatio(BigInteger.valueOf(50));
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);
    }

    @Test
    public void testCustomGasPriceWithSmartContractDeploy() throws Exception {
        SmartContractDeployTransaction tx = SmartContractDeployTransaction.create(
                sender.getAddress(),
                value,
                Numeric.hexStringToByteArray("0x608060405234801561001057600080fd5b506101de806100206000396000f3006080604052600436106100615763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416631a39d8ef81146100805780636353586b146100a757806370a08231146100ca578063fd6b7ef8146100f8575b3360009081526001602052604081208054349081019091558154019055005b34801561008c57600080fd5b5061009561010d565b60408051918252519081900360200190f35b6100c873ffffffffffffffffffffffffffffffffffffffff60043516610113565b005b3480156100d657600080fd5b5061009573ffffffffffffffffffffffffffffffffffffffff60043516610147565b34801561010457600080fd5b506100c8610159565b60005481565b73ffffffffffffffffffffffffffffffffffffffff1660009081526001602052604081208054349081019091558154019055565b60016020526000908152604090205481565b336000908152600160205260408120805490829055908111156101af57604051339082156108fc029083906000818181858888f193505050501561019c576101af565b3360009081526001602052604090208190555b505600a165627a7a72305820627ca46bb09478a015762806cc00c431230501118c7c26c30ac58c4e09e51c4f0029"),
                GAS_PRICE,
                GAS_LIMIT,
                CodeFormat.EVM
        );
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        TransactionManager tm = new TransactionManager.Builder(caver, sender).build();
        KlayRawTransaction rawTransaction = tm.sign(tx);
        AbstractTxType decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeDelegate();
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        FeePayerManager fm = new FeePayerManager.Builder(caver, feePayer).build();
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeRatio(BigInteger.valueOf(50));
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);
    }

    @Test
    public void testCustomGasPriceWithSmartContractExecution() throws Exception {
        SmartContractExecutionTransaction tx = SmartContractExecutionTransaction.create(
                sender.getAddress(),
                to.getAddress(),
                value,
                Numeric.hexStringToByteArray("0xd14e62b8000000000000000000000000000000000000000000000000dc67327b51f7c636"),
                GAS_PRICE,
                GAS_LIMIT
        );
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        TransactionManager tm = new TransactionManager.Builder(caver, sender).build();
        KlayRawTransaction rawTransaction = tm.sign(tx);
        AbstractTxType decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeDelegate();
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        FeePayerManager fm = new FeePayerManager.Builder(caver, feePayer).build();
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeRatio(BigInteger.valueOf(50));
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);
    }

    @Test
    public void testCustomGasPriceWithCancel() throws Exception {
        CancelTransaction tx = CancelTransaction.create(
                sender.getAddress(),
                GAS_PRICE,
                GAS_LIMIT
        );
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        TransactionManager tm = new TransactionManager.Builder(caver, sender).build();
        KlayRawTransaction rawTransaction = tm.sign(tx);
        AbstractTxType decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeDelegate();
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        FeePayerManager fm = new FeePayerManager.Builder(caver, feePayer).build();
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);

        tx.feeRatio(BigInteger.valueOf(50));
        assertEquals(tx.getGasPrice(), GAS_PRICE);

        rawTransaction = tm.sign(tx);
        rawTransaction = fm.sign(rawTransaction.getValueAsString());
        decoded = TransactionDecoder.decode(rawTransaction.getValueAsString());
        assertEquals(decoded.getGasPrice(), GAS_PRICE);
    }
}
