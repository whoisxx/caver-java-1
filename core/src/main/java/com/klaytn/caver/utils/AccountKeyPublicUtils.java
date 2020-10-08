/*
 * Modifications copyright 2019 The caver-java Authors
 * Copyright 2016 Conor Svensson
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
 *
 * This file is derived from web3j/crypto/src/main/java/org/web3j/crypto/Sign.java (2019/06/13).
 * Modified and improved for the caver-java development.
 */

package com.klaytn.caver.utils;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;
import com.klaytn.caver.tx.account.AccountKeyPublic;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

public class AccountKeyPublicUtils {

    public static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    static final ECDomainParameters CURVE = new ECDomainParameters(
            CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());

    /**
     * Convert uncompressed public key to compress format
     * @param publicKey uncompressed public key biginteger
     * @return compressed public key string
     */
    @Deprecated
    public static String toCompressedPublicKey(BigInteger publicKey) {
        String hexStringPublicKey = Numeric.toHexStringNoPrefixZeroPadded(publicKey, 128);
        String publicKeyX = hexStringPublicKey.substring(0, 64);
        String pubKeyYPrefix = publicKey.testBit(0) ? "03" : "02";
        return pubKeyYPrefix + publicKeyX;
    }

    /**
     * Creates AccountKeyPublic(caver.tx.account.AccountKeyPublic) instance with converting uncompressed format
     * @param compressedPublicKey compressed public key
     * @return AccountKeyPublic(caver.tx.account.AccountKeyPublic)
     */
    @Deprecated
    public static AccountKeyPublic decompressKey(String compressedPublicKey) {
        boolean yBit = Numeric.cleanHexPrefix(compressedPublicKey).substring(0, 2).equals("03");
        BigInteger xBN = Numeric.toBigInt(compressedPublicKey.substring(2));
        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.getCurve()));
        compEnc[0] = (byte)(yBit ? 0x03 : 0x02);
        ECPoint ecPoint = CURVE.getCurve().decodePoint(compEnc);
        return AccountKeyPublic.create(
                Numeric.toHexStringWithPrefixZeroPadded(ecPoint.getAffineXCoord().toBigInteger(), 64),
                Numeric.toHexStringWithPrefixZeroPadded(ecPoint.getAffineYCoord().toBigInteger(), 64)
        );
    }

    /**
     * Creates ECPoint instance using compressed public key
     * @param key public key
     * @return ECPoint
     */
    public static ECPoint getECPoint(String key) {
        if(isUncompressedPublicKey(key)) {
            key = Utils.compressPublicKey(key);
        }

        String noPrefixPublicKey = Numeric.cleanHexPrefix(key);
        boolean yBit = noPrefixPublicKey.startsWith("03");
        BigInteger xBN = Numeric.toBigInt(noPrefixPublicKey);
        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(CURVE.getCurve()));
        compEnc[0] = (byte)(yBit ? 0x03 : 0x02);
        ECPoint ecPoint = CURVE.getCurve().decodePoint(compEnc);
        return ecPoint;
    }

    /**
     * Creates ECPoint instance using ecc public key x,y coordinates
     * @param x x point
     * @param y y point
     * @return ECPoint
     */
    public static ECPoint getECPoint(String x, String y) {
        BigInteger bigIntegerX = Numeric.toBigInt(x);
        BigInteger bigIntegerY = Numeric.toBigInt(y);

        ECPoint ecPoint = CURVE.getCurve().createPoint(bigIntegerX, bigIntegerY);
        return ecPoint;
    }

    /**
     * Check if the X, Y coordinates included in the public key are valid.
     * @param compressedPubKey compressed public key
     * @return valid or not
     */
    public static boolean validateXYPoint(String compressedPubKey) {
        ECPoint point = getECPoint(compressedPubKey);
        return point.isValid();
    }

    /**
     * Check that given X, Y coordinates included in the public key.
     * @param x x point
     * @param y y point
     * @return valid or not
     */
    public static boolean validateXYPoint(String x, String y) {
        ECPoint point = getECPoint(x, y);
        return point.isValid();
    }

    /**
     * Check if the given key is in compressed format.
     * @param key public key
     * @return valid or not
     */
    public static boolean isCompressedPublicKey(String key) {
        String noPrefixKey = Numeric.cleanHexPrefix(key);

        if(noPrefixKey.length() == 66 && (noPrefixKey.startsWith("02") || noPrefixKey.startsWith("03"))) {
            if(!validateXYPoint(noPrefixKey)) {
                throw new RuntimeException("Invalid public key.");
            }
            return true;
        }
        return false;
    }

    /**
     * Check if the given key is in compressed format.
     * @param key public key
     * @return valid or not
     */
    public static boolean isUncompressedPublicKey(String key) {
        String noPrefixKey = Numeric.cleanHexPrefix(key);

        if(noPrefixKey.length() == 130 && noPrefixKey.startsWith("04")) {
            noPrefixKey = noPrefixKey.substring(2);
        }

        if(noPrefixKey.length() == 128) {
            String x = noPrefixKey.substring(0, 64);
            String y = noPrefixKey.substring(64);

            if(!validateXYPoint(x, y)) {
                throw new RuntimeException("Invalid public key.");
            }
            return true;
        }
        return false;
    }

}
