package com.klaytn.caver.common;

import com.klaytn.caver.abi.ABI;
import com.klaytn.caver.contract.ContractIOType;
import com.klaytn.caver.contract.ContractMethod;
import org.junit.Test;
import org.web3j.abi.*;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ABITest {

    public static class encodeFunctionSig {
        @Test
        public void encodeFunctionSignatureTest() {
            assertEquals("0xcdcd77c0", ABI.encodeFunctionSignature("baz(uint32,bool)"));
            assertEquals("0xfce353f6", ABI.encodeFunctionSignature("bar(bytes3[2])"));
            assertEquals("0xa5643bf2", ABI.encodeFunctionSignature("sam(bytes,bool,uint256[])"));
        }
    }

    public static class encodeEventSig {
        @Test
        public void encodeEventSignature() {
            assertEquals(
                    ("0x50cb9fe53daa9737b786ab3646f04d0150dc50ef4e75f59509d83667ad5adb20"),
                    ABI.encodeEventSignature("Deposit(address,hash256,uint256)"));

            assertEquals(
                    ("0x71e71a8458267085d5ab16980fd5f114d2d37f232479c245d523ce8d23ca40ed"),
                    ABI.encodeEventSignature("Notify(uint256,uint256)"));

        }
    }


    public static class encodeFunctionCall {
        @Test
        public void encodeFunctionCallTest() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
            String expected = "0xcdcd77c000000000000000000000000000000000000000000000000000000000000000450000000000000000000000000000000000000000000000000000000000000001";
            String functionSig = "baz(uint32,bool)";

            List<String> typeList = Arrays.asList("uint32", "bool");
            List<Object> params = Arrays.asList(69, true);

            assertEquals(expected, ABI.encodeFunctionCall(functionSig, typeList, params));
        }

        @Test
        public void encodeFunctionCallTest2() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
            String expected = "0xfce353f661626300000000000000000000000000000000000000000000000000000000006465660000000000000000000000000000000000000000000000000000000000";
            String functionSig = "bar(bytes3[2])";

            List<String> typeList = Arrays.asList("bytes3[2]");
            List<Object> params = new ArrayList<>();
            params.add(new byte[][] {"abc".getBytes(), "def".getBytes()});


            assertEquals(expected, ABI.encodeFunctionCall(functionSig, typeList, params));
        }

        @Test
        public void encodeFunctionCallTest3() throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
            String expected = "0xa5643bf20000000000000000000000000000000000000000000000000000000000000060000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000a0000000000000000000000000000000000000000000000000000000000000000464617665000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000003000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000020000000000000000000000000000000000000000000000000000000000000003";
            String functionSig = "sam(bytes,bool,uint256[])";

            List<String> typeList = Arrays.asList("bytes", "bool", "uint256[]");
            List<Object> params = Arrays.asList(
                    "dave".getBytes(),
                    true,
                    new BigInteger[] {BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3)}
            );

            assertEquals(expected, ABI.encodeFunctionCall(functionSig, typeList, params));
        }
    }

    public static class encodeParameter {
        @Test
        public void encodeBoolType() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            assertEquals("0000000000000000000000000000000000000000000000000000000000000000",
                    ABI.encodeParameter("bool", false));

            assertEquals("0000000000000000000000000000000000000000000000000000000000000001",
                    ABI.encodeParameter("bool", true));
        }

        @Test
        public void encodeUintType() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            assertEquals("00000000000000000000000000000000000000000000000000000000000000ff",
                    ABI.encodeParameter("uint", BigInteger.valueOf(255)));

            assertEquals(
                    "000000000000000000000000000000000000000000000000000000000000ffff",
                    ABI.encodeParameter("uint", BigInteger.valueOf(65535)));

            assertEquals(
                    "0000000000000000000000000000000000000000000000000000000000010000",
                    ABI.encodeParameter("uint", BigInteger.valueOf(65536)));

            assertEquals(
                    "0000000000000000000000000000000000000000000000ffffffffffffffffff",
                    ABI.encodeParameter("uint", new BigInteger("4722366482869645213695")));

            assertEquals(
                    "0000000000000000000000000000000000000000000000000000000000000000",
                    ABI.encodeParameter("uint", BigInteger.ZERO));
        }

        @Test
        public void encodeIntType() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            assertEquals(
                    ("0000000000000000000000000000000000000000000000000000000000000000"),
                    ABI.encodeParameter("int", BigInteger.ZERO));

            assertEquals(
                    ("000000000000000000000000000000000000000000000000000000000000007f"),
                    ABI.encodeParameter("int", 127));

            assertEquals(
                    ("0000000000000000000000000000000000000000000000000000000000007fff"),
                    ABI.encodeParameter("int", 32767));

            assertEquals(
                    ("000000000000000000000000000000000000000000000000000000007fffffff"),
                    ABI.encodeParameter("int", 2147483647));
        }

        @Test
        public void encodeAddressType() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            assertEquals(
                    ("000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338"),
                    ABI.encodeParameter("address", "0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338"));
        }

        @Test
        public void encodeUtf8StringType() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            assertEquals(
                    ("0000000000000000000000000000000000000000000000000000000000000020"
                            + "000000000000000000000000000000000000000000000000000000000000000d"
                            + "48656c6c6f2c20776f726c642100000000000000000000000000000000000000"),
                    ABI.encodeParameter("string", "Hello, world!"));
        }

        @Test
        public void encodeStaticBytesType() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            assertEquals("0001020304050000000000000000000000000000000000000000000000000000",
                    ABI.encodeParameter("bytes6", new byte[] {0, 1, 2, 3, 4, 5}));

            assertEquals(
                    ("0000000000000000000000000000000000000000000000000000000000000000"),
                    ABI.encodeParameter("bytes1", new byte[] {0}));

            assertEquals(
                    ("7f00000000000000000000000000000000000000000000000000000000000000"),
                    ABI.encodeParameter("bytes1", new byte[] {127}));

            assertEquals(
                    ("6461766500000000000000000000000000000000000000000000000000000000"),
                    ABI.encodeParameter("bytes4", "dave".getBytes()));
        }

        @Test
        public void encodeDynamicBytesType() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            DynamicBytes dynamicBytes = new DynamicBytes(new byte[] {0, 1, 2, 3, 4, 5});
            assertEquals(
                    ("0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000006"
                        + "0001020304050000000000000000000000000000000000000000000000000000"),
                    ABI.encodeParameter("bytes", new byte[] {0,1,2,3,4,5}));

            assertEquals(
                    ("0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000000"),
                    ABI.encodeParameter("bytes", new byte[] {0}));

            assertEquals(
                    ("0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6461766500000000000000000000000000000000000000000000000000000000"),
                    ABI.encodeParameter("bytes", "dave".getBytes()));


            byte[] loremIpsum =  ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod "
                    + "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim "
                    + "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex "
                    + "ea commodo consequat. Duis aute irure dolor in reprehenderit in "
                    + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur "
                    + "sint occaecat cupidatat non proident, sunt in culpa qui officia "
                    + "deserunt mollit anim id est laborum.")
                    .getBytes();

            assertEquals(
                    ("0000000000000000000000000000000000000000000000000000000000000020"
                            + "00000000000000000000000000000000000000000000000000000000000001bd"
                            + "4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73"
                            + "656374657475722061646970697363696e6720656c69742c2073656420646f20"
                            + "656975736d6f642074656d706f7220696e6369646964756e74207574206c6162"
                            + "6f726520657420646f6c6f7265206d61676e6120616c697175612e2055742065"
                            + "6e696d206164206d696e696d2076656e69616d2c2071756973206e6f73747275"
                            + "6420657865726369746174696f6e20756c6c616d636f206c61626f726973206e"
                            + "69736920757420616c697175697020657820656120636f6d6d6f646f20636f6e"
                            + "7365717561742e2044756973206175746520697275726520646f6c6f7220696e"
                            + "20726570726568656e646572697420696e20766f6c7570746174652076656c69"
                            + "7420657373652063696c6c756d20646f6c6f726520657520667567696174206e"
                            + "756c6c612070617269617475722e204578636570746575722073696e74206f63"
                            + "63616563617420637570696461746174206e6f6e2070726f6964656e742c2073"
                            + "756e7420696e2063756c706120717569206f666669636961206465736572756e"
                            + "74206d6f6c6c697420616e696d20696420657374206c61626f72756d2e000000"),
                    ABI.encodeParameter("bytes", loremIpsum));
        }

        @Test
        public void encodeDynamicArrayTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            assertEquals(
                    ("0000000000000000000000000000000000000000000000000000000000000020"
                            + "0000000000000000000000000000000000000000000000000000000000000003"
                            + "0000000000000000000000000000000000000000000000000000000000000001"
                            + "0000000000000000000000000000000000000000000000000000000000000002"
                            + "0000000000000000000000000000000000000000000000000000000000000003"),
                    ABI.encodeParameter("uint256[]", new BigInteger[] {BigInteger.valueOf(1), BigInteger.valueOf(2), BigInteger.valueOf(3)}));
        }

        @Test
        public void encodeStaticArrayTest() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            assertEquals("000000000000000000000000000000000000000000000000000000000000000a"
                    + "0000000000000000000000000000000000000000000000007fffffffffffffff",
                    ABI.encodeParameter("uint256[2]", new BigInteger[] {BigInteger.valueOf(10), BigInteger.valueOf(Long.MAX_VALUE)}));
        }
    }

    public static class decodeParameterTest {
        @Test
        public void decodeBoolType() throws ClassNotFoundException {
            assertEquals(
                    ABI.decodeParameter("bool", "0000000000000000000000000000000000000000000000000000000000000000"),
                    (new Bool(false)));
            assertEquals(
                    ABI.decodeParameter("bool", "0000000000000000000000000000000000000000000000000000000000000001"),
                    (new Bool(true)));
        }

        @Test
        public void decodeUintType() throws ClassNotFoundException {
            Uint uint = new Uint256(BigInteger.valueOf(255));
            assertEquals(
                    ABI.decodeParameter("uint256", "00000000000000000000000000000000000000000000000000000000000000ff"),
                    uint);


            Uint uint2 = new Uint256(BigInteger.valueOf(65535));
            assertEquals(
                    ABI.decodeParameter("uint256", "000000000000000000000000000000000000000000000000000000000000ffff"),
                    uint2
            );

            Uint uint3 = new Uint256(BigInteger.valueOf(65536));
            assertEquals(
                    ABI.decodeParameter("uint256", "0000000000000000000000000000000000000000000000000000000000010000"),
                    uint3);

            Uint uint4 = new Uint256(new BigInteger("4722366482869645213695"));
            assertEquals(
                    ABI.decodeParameter("uint256", "0000000000000000000000000000000000000000000000ffffffffffffffffff"),
                    uint4);

            Uint uint5 = new Uint256(BigInteger.ZERO);
            assertEquals(
                    ABI.decodeParameter("uint256", "0000000000000000000000000000000000000000000000000000000000000000"),
                    uint5
                    );
        }

        @Test
        public void decodeIntType() throws ClassNotFoundException {
            Int int1 = new Int256(BigInteger.ZERO);
            assertEquals(
                    ABI.decodeParameter("int256", "0000000000000000000000000000000000000000000000000000000000000000"),
                    int1);

            Int int2 = new Int256(BigInteger.valueOf(127));
            assertEquals(
                    ABI.decodeParameter("int256", "000000000000000000000000000000000000000000000000000000000000007f"),
                    int2);

            Int int3 = new Int256(BigInteger.valueOf(32767));
            assertEquals(
                    ABI.decodeParameter("int256", "0000000000000000000000000000000000000000000000000000000000007fff"),
                    int3);

            Int int4 = new Int256(BigInteger.valueOf(2147483647));
            assertEquals(
                    ABI.decodeParameter("int256","000000000000000000000000000000000000000000000000000000007fffffff"),
                    int4);
        }

        @Test
        public void decodeAddressType() throws ClassNotFoundException {
            Address address = new Address("0xbe5422d15f39373eb0a97ff8c10fbd0e40e29338");
            assertEquals(
                    ABI.decodeParameter("address","000000000000000000000000be5422d15f39373eb0a97ff8c10fbd0e40e29338"),
                    address);
        }

        @Test
        public void decodeUtf8StringType() throws ClassNotFoundException {
            Utf8String string = new Utf8String("Hello, world!");
            assertEquals(
                    ABI.decodeParameter("string",
                            "0000000000000000000000000000000000000000000000000000000000000020"
                            + "000000000000000000000000000000000000000000000000000000000000000d"
                            + "48656c6c6f2c20776f726c642100000000000000000000000000000000000000"),
                    string);
        }

        @Test
        public void decodeStaticBytesType() throws ClassNotFoundException {
            Bytes staticBytes = new Bytes6(new byte[] {0, 1, 2, 3, 4, 5});
            assertEquals(
                    ABI.decodeParameter("bytes6", "0001020304050000000000000000000000000000000000000000000000000000"),
                    staticBytes);

            Bytes empty = new Bytes1(new byte[] {0});
            assertEquals(
                    ABI.decodeParameter("bytes1", "0000000000000000000000000000000000000000000000000000000000000000"),
                    empty);

            Bytes ones = new Bytes1(new byte[] {127});
            assertEquals(
                    ABI.decodeParameter("bytes1", "7f00000000000000000000000000000000000000000000000000000000000000"),
                    ones);

            Bytes dave = new Bytes4("dave".getBytes());
            assertEquals(
                    ABI.decodeParameter("bytes4", "6461766500000000000000000000000000000000000000000000000000000000"),
                    dave);
        }

        @Test
        public void decodeDynamicBytesType() throws ClassNotFoundException {
            DynamicBytes dynamicBytes = new DynamicBytes(new byte[] {0, 1, 2, 3, 4, 5});
            assertEquals(
                    ABI.decodeParameter("bytes",
                            "0000000000000000000000000000000000000000000000000000000000000020"
                                    + "0000000000000000000000000000000000000000000000000000000000000006"
                                    + "0001020304050000000000000000000000000000000000000000000000000000"),
                    dynamicBytes);

            DynamicBytes empty = new DynamicBytes(new byte[] {0});
            assertEquals(
                    ABI.decodeParameter("bytes",
                            "0000000000000000000000000000000000000000000000000000000000000020"
                            +"0000000000000000000000000000000000000000000000000000000000000001"
                            + "0000000000000000000000000000000000000000000000000000000000000000"),
                    empty);

            DynamicBytes dave = new DynamicBytes("dave".getBytes());
            assertEquals(
                    ABI.decodeParameter("bytes",
                            "0000000000000000000000000000000000000000000000000000000000000020"
                            +"0000000000000000000000000000000000000000000000000000000000000004"
                            + "6461766500000000000000000000000000000000000000000000000000000000"),
                    dave);

            DynamicBytes loremIpsum =
                    new DynamicBytes(
                            ("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod "
                                    + "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim "
                                    + "veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex "
                                    + "ea commodo consequat. Duis aute irure dolor in reprehenderit in "
                                    + "voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur "
                                    + "sint occaecat cupidatat non proident, sunt in culpa qui officia "
                                    + "deserunt mollit anim id est laborum.")
                                    .getBytes());
            assertEquals(
                    ABI.decodeParameter("bytes",
                            "0000000000000000000000000000000000000000000000000000000000000020"
                            +"00000000000000000000000000000000000000000000000000000000000001bd"
                            + "4c6f72656d20697073756d20646f6c6f722073697420616d65742c20636f6e73"
                            + "656374657475722061646970697363696e6720656c69742c2073656420646f20"
                            + "656975736d6f642074656d706f7220696e6369646964756e74207574206c6162"
                            + "6f726520657420646f6c6f7265206d61676e6120616c697175612e2055742065"
                            + "6e696d206164206d696e696d2076656e69616d2c2071756973206e6f73747275"
                            + "6420657865726369746174696f6e20756c6c616d636f206c61626f726973206e"
                            + "69736920757420616c697175697020657820656120636f6d6d6f646f20636f6e"
                            + "7365717561742e2044756973206175746520697275726520646f6c6f7220696e"
                            + "20726570726568656e646572697420696e20766f6c7570746174652076656c69"
                            + "7420657373652063696c6c756d20646f6c6f726520657520667567696174206e"
                            + "756c6c612070617269617475722e204578636570746575722073696e74206f63"
                            + "63616563617420637570696461746174206e6f6e2070726f6964656e742c2073"
                            + "756e7420696e2063756c706120717569206f666669636961206465736572756e"
                            + "74206d6f6c6c697420616e696d20696420657374206c61626f72756d2e000000"),
                    loremIpsum);
        }

        @Test
        public void decodeDynamicArrayTest() throws ClassNotFoundException {
            DynamicArray<Uint256> array =
                    new DynamicArray<>(
                            Uint256.class,
                            new Uint256(BigInteger.ONE),
                            new Uint256(BigInteger.valueOf(2)),
                            new Uint256(BigInteger.valueOf(3)));

            assertEquals(
                    ABI.decodeParameter("uint256[]",
                            "0000000000000000000000000000000000000000000000000000000000000020"
                            + "0000000000000000000000000000000000000000000000000000000000000003"
                            + "0000000000000000000000000000000000000000000000000000000000000001"
                            + "0000000000000000000000000000000000000000000000000000000000000002"
                            + "0000000000000000000000000000000000000000000000000000000000000003"),
                    array);
        }

        @Test
        public void decodeStaticArrayTest() throws ClassCastException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            StaticArray2 expected = new StaticArray2(Uint256.class, Arrays.asList(BigInteger.TEN, BigInteger.valueOf(Long.MAX_VALUE)));
            StaticArray2 actual = (StaticArray2)ABI.decodeParameter("uint256[2]",
                    "000000000000000000000000000000000000000000000000000000000000000a"
                            + "0000000000000000000000000000000000000000000000007fffffffffffffff");

            assertEquals(expected.getValue().get(0), ((Uint256)actual.getValue().get(0)).getValue());
            assertEquals(expected.getValue().get(1), ((Uint256)actual.getValue().get(1)).getValue());
        }
    }

    public static class decodeLogTest {
        @Test
        public void decodeLog() throws ClassNotFoundException {
            List<ContractIOType> ioTypeList= Arrays.asList(
                    new ContractIOType("from", "address", true),
                    new ContractIOType("to", "address", true),
                    new ContractIOType("value", "uint256", false)
            );

            List<String> topics = Arrays.asList(
                    "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef",
                    "0x0000000000000000000000000000000000000000000000000000000000000000",
                    "0x0000000000000000000000002c8ad0ea2e0781db8b8c9242e07de3a5beabb71a"
            );

            String nonIndexedData = "0x00000000000000000000000000000000000000000000152d02c7e14af6800000";

            BigInteger decimals = BigInteger.valueOf(18);
            BigInteger value = BigInteger.valueOf(100_000).multiply(BigInteger.TEN.pow(decimals.intValue())); // 100000 * 10^18

            EventValues eventValues = ABI.decodeLog(ioTypeList, nonIndexedData, topics);

            assertEquals(eventValues.getIndexedValues().get(0).getValue(), "0x0000000000000000000000000000000000000000");
            assertEquals(eventValues.getIndexedValues().get(1).getValue(), "0x2c8ad0ea2e0781db8b8c9242e07de3a5beabb71a");
            assertEquals(eventValues.getNonIndexedValues().get(0).getValue(), value);
        }
    }

}
