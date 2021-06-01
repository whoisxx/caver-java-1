package com.klaytn.caver.ipfs;

import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.TxPropertyBuilder;
import com.klaytn.caver.transaction.response.PollingTransactionReceiptProcessor;
import com.klaytn.caver.transaction.response.TransactionReceiptProcessor;
import com.klaytn.caver.transaction.type.ValueTransferMemo;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import org.junit.Test;
import org.web3j.protocol.exceptions.TransactionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

import static com.klaytn.caver.base.Accounts.BRANDON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IPFSTest {
    static String key_klayProvider = "0x2359d1ae7317c01532a58b01452476b796a3ac713336e97d8d3c9651cc0aecc3";
    static String text = "This is IPFS test.";
    static String fileName = "./ipfs.txt";

    public void createFile(String fileName, String text) {
        File file = new File(fileName);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file, false);
            fw.write(text);
            fw.flush();

            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TransactionReceipt.TransactionReceiptData sendFileHash(Caver caver, String fileHash) throws IOException, TransactionException {
        SingleKeyring klayProvider_keyring = caver.wallet.keyring.createFromPrivateKey(key_klayProvider);
        caver.wallet.add(klayProvider_keyring);


        ValueTransferMemo tx = caver.transaction.valueTransferMemo.create(
                TxPropertyBuilder.valueTransferMemo()
                        .setFrom(klayProvider_keyring.getAddress())
                        .setTo(BRANDON.getAddress())
                        .setGas(BigInteger.valueOf(25000))
                        .setValue("0x00")
                        .setInput(fileHash)
        );

        caver.wallet.sign(klayProvider_keyring.getAddress(), tx);

        String txHash = caver.rpc.klay.sendRawTransaction(tx).send().getResult();
        TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(caver, 1000, 15);

        TransactionReceipt.TransactionReceiptData receiptData = receiptProcessor.waitForTransactionReceipt(txHash);
        return receiptData;
    }

    @Test
    public void fromHex() {
        Caver caver = new Caver();
        String multiHashData = "0x12209cbc07c3f991725836a3aa2a581ca2029198aa420b9d99bc0e131d9f3e2cbe47";
        String expectedEncodedString = "QmYtUc4iTCbbfVSDNKvtQqrfyezPPnFvE33wFmutw9PBBk";

        assertEquals(caver.ipfs.fromHex(multiHashData), expectedEncodedString);
    }

    @Test
    public void toHex() {
        Caver caver = new Caver();
        String encodedMultiHash = "QmYtUc4iTCbbfVSDNKvtQqrfyezPPnFvE33wFmutw9PBBk";
        String expectedMultiHashData = "0x12209cbc07c3f991725836a3aa2a581ca2029198aa420b9d99bc0e131d9f3e2cbe47";

        assertEquals(caver.ipfs.toHex(encodedMultiHash), expectedMultiHashData);
    }

    @Test
    public void addFile() throws IOException {
        createFile(fileName, text);
        Caver caver = new Caver();
        caver.ipfs.setIPFSNode("ipfs.infura.io", 5001, true);

        String encodedHash = caver.ipfs.add(fileName);
        assertNotNull(encodedHash);
    }

    @Test
    public void addByteArray() throws IOException {
        byte[] data = text.getBytes();

        Caver caver = new Caver();
        caver.ipfs.setIPFSNode("ipfs.infura.io", 5001, true);

        String cid = caver.ipfs.add(data);
        assertNotNull(cid);


        byte[] content = caver.ipfs.get(cid);
        assertEquals(text, new String(content));
    }

    @Test
    public void get() throws IOException {
        String cid = "QmYzW1fXbapdxkZXMQeCYoDCjVc18H8tLfMfrxXRySmQiq";

        Caver caver = new Caver();
        caver.ipfs.setIPFSNode("ipfs.infura.io", 5001, true);

        byte[] content = caver.ipfs.get(cid);
        String data = new String(content);
        assertEquals(text, data);
    }

    @Test
    public void integrationTest() throws IOException, TransactionException {
        Caver caver = new Caver();
        caver.ipfs.setIPFSNode("ipfs.infura.io", 5001, true);

        createFile(fileName, text);
        String encodedHash = caver.ipfs.add(fileName);

        TransactionReceipt.TransactionReceiptData receiptData = sendFileHash(caver, caver.ipfs.toHex(encodedHash));
        String multiHash = receiptData.getInput();

        String encoded = caver.ipfs.fromHex(multiHash);
        byte[] data = caver.ipfs.get(encoded);

        assertEquals(text, new String(data));
    }
}
