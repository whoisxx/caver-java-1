/*
 * Modifications copyright 2021 The caver-java Authors
 * Copyright 2019 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is derived from web3j/abi/src/main/java/org/web3j/abi/TypeDecoder.java (2021/04/05).
 * Modified and improved for the caver-java development.
 */

package com.klaytn.caver.abi;

import com.klaytn.caver.abi.datatypes.*;
import com.klaytn.caver.abi.datatypes.generated.Uint160;
import org.web3j.utils.Numeric;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;

import static com.klaytn.caver.abi.DefaultFunctionReturnDecoder.getDataOffset;
import static com.klaytn.caver.abi.TypeReference.makeTypeReference;
import static com.klaytn.caver.abi.Utils.getSimpleTypeName;

/**
 * Contract Application Binary Interface (ABI) decoding for types. Decoding is not
 * documented, but is the reverse of the encoding details located <a
 * href="https://docs.soliditylang.org/en/latest/abi-spec.html">here</a>.
 */
public class TypeDecoder {

    static final int MAX_BYTE_LENGTH_FOR_HEX_STRING = Type.MAX_BYTE_LENGTH << 1;

    public static Type instantiateType(String solidityType, Object value)
            throws InvocationTargetException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, ClassNotFoundException {
        return instantiateType(makeTypeReference(solidityType), value);
    }

    public static Type instantiateType(TypeReference ref, Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException, ClassNotFoundException {
        Class rc = ref.getClassType();
        if (Array.class.isAssignableFrom(rc)) {
            //if class is a StructType, instantiate Struct type
            if(StructType.class.isAssignableFrom(rc)) {
                return instantiateStructType(ref, value);
            }
            return instantiateArrayType(ref, value);
        }
        return instantiateAtomicType(rc, value);
    }

    public static <T extends Array> T decode(
            String input, int offset, TypeReference<T> typeReference) {
        Class cls = ((ParameterizedType) typeReference.getType()).getRawType().getClass();
        if (StaticArray.class.isAssignableFrom(cls)) {
            return decodeStaticArray(input, offset, typeReference, 1);
        } else if (DynamicArray.class.isAssignableFrom(cls)) {
            return decodeDynamicArray(input, offset, typeReference);
        } else {
            throw new UnsupportedOperationException(
                    "Unsupported TypeReference: "
                            + cls.getName()
                            + ", only Array types can be passed as TypeReferences");
        }
    }

    @SuppressWarnings("unchecked")
    static <T extends Type> T decode(String input, int offset, Class<T> type) {
        if (NumericType.class.isAssignableFrom(type)) {
            return (T) decodeNumeric(input.substring(offset), (Class<NumericType>) type);
        } else if (Address.class.isAssignableFrom(type)) {
            return (T) decodeAddress(input.substring(offset));
        } else if (Bool.class.isAssignableFrom(type)) {
            return (T) decodeBool(input, offset);
        } else if (Bytes.class.isAssignableFrom(type)) {
            return (T) decodeBytes(input, offset, (Class<Bytes>) type);
        } else if (DynamicBytes.class.isAssignableFrom(type)) {
            return (T) decodeDynamicBytes(input, offset);
        } else if (Utf8String.class.isAssignableFrom(type)) {
            return (T) decodeUtf8String(input, offset);
        } else if (Array.class.isAssignableFrom(type)) {
            throw new UnsupportedOperationException(
                    "Array types must be wrapped in a TypeReference");
        } else {
            throw new UnsupportedOperationException("Type cannot be encoded: " + type.getClass());
        }
    }

    static <T extends Type> T decode(String input, Class<T> type) {
        return decode(input, 0, type);
    }

    static Address decodeAddress(String input) {
        return new Address(decodeNumeric(input, Uint160.class));
    }

    static <T extends NumericType> T decodeNumeric(String input, Class<T> type) {
        try {
            byte[] inputByteArray = Numeric.hexStringToByteArray(input);
            int typeLengthAsBytes = getTypeLengthInBytes(type);

            byte[] resultByteArray = new byte[typeLengthAsBytes + 1];

            if (Int.class.isAssignableFrom(type) || Fixed.class.isAssignableFrom(type)) {
                resultByteArray[0] = inputByteArray[0]; // take MSB as sign bit
            }

            int valueOffset = Type.MAX_BYTE_LENGTH - typeLengthAsBytes;
            System.arraycopy(inputByteArray, valueOffset, resultByteArray, 1, typeLengthAsBytes);

            BigInteger numericValue = new BigInteger(resultByteArray);
            return type.getConstructor(BigInteger.class).newInstance(numericValue);

        } catch (NoSuchMethodException
                | SecurityException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new UnsupportedOperationException(
                    "Unable to create instance of " + type.getName(), e);
        }
    }

    static <T extends NumericType> int getTypeLengthInBytes(Class<T> type) {
        return getTypeLength(type) >> 3; // divide by 8
    }

    static <T extends NumericType> int getTypeLength(Class<T> type) {
        if (IntType.class.isAssignableFrom(type)) {
            String regex = "(" + Uint.class.getSimpleName() + "|" + Int.class.getSimpleName() + ")";
            String[] splitName = type.getSimpleName().split(regex);
            if (splitName.length == 2) {
                return Integer.parseInt(splitName[1]);
            }
        } else if (FixedPointType.class.isAssignableFrom(type)) {
            String regex =
                    "(" + Ufixed.class.getSimpleName() + "|" + Fixed.class.getSimpleName() + ")";
            String[] splitName = type.getSimpleName().split(regex);
            if (splitName.length == 2) {
                String[] bitsCounts = splitName[1].split("x");
                return Integer.parseInt(bitsCounts[0]) + Integer.parseInt(bitsCounts[1]);
            }
        }
        return Type.MAX_BIT_LENGTH;
    }

    static Type instantiateStructType(TypeReference ref, Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException, ClassNotFoundException {
        List values;
        if (value instanceof List) {
            values = (List) value;
        } else if (value.getClass().isArray()) {
            values = arrayToList(value);
        } else {
            throw new ClassCastException(
                    "Arg of type "
                            + value.getClass()
                            + " should be a list to instantiate Array");
        }


        ArrayList<Type> transformedList = new ArrayList<Type>(values.size());

        // Get a component TypeReference list
        List<TypeReference> subTypeReference = ((TypeReference.StructTypeReference)ref).getTypeList();

        // Instantiate a component type in struct with a value.
        for(int i=0; i<subTypeReference.size(); i++) {
            transformedList.add(instantiateType(subTypeReference.get(i), values.get(i)));
        }

        if(DynamicStruct.class.isAssignableFrom(ref.getClassType())) {
            return new DynamicStruct(transformedList);
        }

        return new StaticStruct(transformedList);

    }

    static Type instantiateArrayType(TypeReference ref, Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException, ClassNotFoundException {
        List values;
        if (value instanceof List) {
            values = (List) value;
        } else if (value.getClass().isArray()) {
            values = arrayToList(value);
        } else {
            throw new ClassCastException(
                    "Arg of type "
                            + value.getClass()
                            + " should be a list to instantiate Array");
        }
        Constructor listcons;
        int arraySize =
                ref instanceof TypeReference.StaticArrayTypeReference
                        ? ((TypeReference.StaticArrayTypeReference) ref).getSize()
                        : -1;
        if (arraySize <= 0) {
            listcons = DynamicArray.class.getConstructor(Class.class, List.class);
        } else {
            Class<?> arrayClass =
                    Class.forName("com.klaytn.caver.abi.datatypes.generated.StaticArray" + arraySize);
            listcons = arrayClass.getConstructor(Class.class, List.class);
        }
        // create a list of arguments coerced to the correct type of sub-TypeReference
        ArrayList<Type> transformedList = new ArrayList<Type>(values.size());
        TypeReference subTypeReference = ref.getSubTypeReference();
        for (Object o : values) {
            transformedList.add(instantiateType(subTypeReference, o));
        }
        return (Type) listcons.newInstance(subTypeReference.getClassType(), transformedList);
    }

    static Type instantiateAtomicType(Class<?> referenceClass, Object value)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException, ClassNotFoundException {
        Object constructorArg = null;
        if (NumericType.class.isAssignableFrom(referenceClass)) {
            constructorArg = asBigInteger(value);
        } else if (BytesType.class.isAssignableFrom(referenceClass)) {
            if (value instanceof byte[]) {
                constructorArg = value;
            } else if (value instanceof BigInteger) {
                constructorArg = ((BigInteger) value).toByteArray();
            } else if (value instanceof String) {
                constructorArg = Numeric.hexStringToByteArray((String) value);
            }
        } else if (Utf8String.class.isAssignableFrom(referenceClass)) {
            constructorArg = value.toString();
        } else if (Address.class.isAssignableFrom(referenceClass)) {
            if (value instanceof BigInteger || value instanceof Uint160) {
                constructorArg = value;
            } else {
                constructorArg = value.toString();
            }
        } else if (Bool.class.isAssignableFrom(referenceClass)) {
            if (value instanceof Boolean) {
                constructorArg = value;
            } else {
                BigInteger bival = asBigInteger(value);
                constructorArg = bival == null ? null : !bival.equals(BigInteger.ZERO);
            }
        }
        if (constructorArg == null) {
            throw new InstantiationException(
                    "Could not create type "
                            + referenceClass
                            + " from arg "
                            + value.toString()
                            + " of type "
                            + value.getClass());
        }
        Class<?>[] types = new Class[] {constructorArg.getClass()};
        Constructor cons = referenceClass.getConstructor(types);
        return (Type) cons.newInstance(constructorArg);
    }

    @SuppressWarnings("unchecked")
    static <T extends Type> int getSingleElementLength(String input, int offset, TypeReference typeReference) throws ClassNotFoundException {
        Class type = typeReference.getClassType();

        if (input.length() == offset) {
            return 0;
        } else if (DynamicBytes.class.isAssignableFrom(type)
                || Utf8String.class.isAssignableFrom(type)) {
            // length field + data value
            return (decodeUintAsInt(input, offset) / Type.MAX_BYTE_LENGTH) + 2;
        } else if (StaticStruct.class.isAssignableFrom(type)) {
            return Utils.getStaticStructComponentSize((TypeReference.StructTypeReference)typeReference);
        } else if (StaticArray.class.isAssignableFrom(type)) {
            if(isDynamic(typeReference)) {
                return 1;
            }
            return Utils.getStaticArrayElementSize((TypeReference.StaticArrayTypeReference) typeReference);
        } else {
            return 1;
        }
    }

    static int decodeUintAsInt(String rawInput, int offset) {
        String input = rawInput.substring(offset, offset + MAX_BYTE_LENGTH_FOR_HEX_STRING);
        return decode(input, 0, Uint.class).getValue().intValue();
    }

    static Bool decodeBool(String rawInput, int offset) {
        String input = rawInput.substring(offset, offset + MAX_BYTE_LENGTH_FOR_HEX_STRING);
        BigInteger numericValue = Numeric.toBigInt(input);
        boolean value = numericValue.equals(BigInteger.ONE);
        return new Bool(value);
    }

    static <T extends Bytes> T decodeBytes(String input, Class<T> type) {
        return decodeBytes(input, 0, type);
    }

    static <T extends Bytes> T decodeBytes(String input, int offset, Class<T> type) {
        try {
            String simpleName = type.getSimpleName();
            String[] splitName = simpleName.split(Bytes.class.getSimpleName());
            int length = Integer.parseInt(splitName[1]);
            int hexStringLength = length << 1;

            byte[] bytes =
                    Numeric.hexStringToByteArray(input.substring(offset, offset + hexStringLength));
            return type.getConstructor(byte[].class).newInstance(bytes);
        } catch (NoSuchMethodException
                | SecurityException
                | InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException e) {
            throw new UnsupportedOperationException(
                    "Unable to create instance of " + type.getName(), e);
        }
    }

    static DynamicBytes decodeDynamicBytes(String input, int offset) {
        int encodedLength = decodeUintAsInt(input, offset);
        int hexStringEncodedLength = encodedLength << 1;

        int valueOffset = offset + MAX_BYTE_LENGTH_FOR_HEX_STRING;

        String data = input.substring(valueOffset, valueOffset + hexStringEncodedLength);
        byte[] bytes = Numeric.hexStringToByteArray(data);

        return new DynamicBytes(bytes);
    }

    static Utf8String decodeUtf8String(String input, int offset) {
        DynamicBytes dynamicBytesResult = decodeDynamicBytes(input, offset);
        byte[] bytes = dynamicBytesResult.getValue();

        return new Utf8String(new String(bytes, StandardCharsets.UTF_8));
    }

    /** Static array length cannot be passed as a type. */
    @SuppressWarnings("unchecked")
    static <T extends Type> T decodeStaticArray(
            String input, int offset, TypeReference<T> typeReference, int length) {

        BiFunction<List<T>, String, T> function =
                (elements, typeName) -> {
                    if (elements.isEmpty()) {
                        throw new UnsupportedOperationException(
                                "Zero length fixed array is invalid type");
                    } else {
                        return instantiateStaticArray(elements, length);
                    }
                };

        return decodeArrayElements(input, offset, typeReference, length, function);
    }

    public static <T extends Type> T decodeStaticStruct(
            final String input, final int offset, final TypeReference<T> typeReference) throws ClassNotFoundException {
        return decodeStaticStructElement(input, offset, (TypeReference.StructTypeReference<T>)typeReference);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Type> T decodeStaticStructElement(
            final String input,
            final int startOffset,
            final TypeReference.StructTypeReference<T> staticStructTypeRef) throws ClassNotFoundException {

        /**
         * Static struct is set of static type, so it just decoded head.
         * (T1,...,Tk) for k >= 0 and any types T1, …, Tk
         * if Ti is static:
         *   - head(X(i)) = enc(X(i)) and tail(X(i)) = "" (the empty string)
         */
        final int length = staticStructTypeRef.getTypeList().size();
        List<Type> elements = new ArrayList<>(length);

        for (int i = 0, currOffset = startOffset; i < length; i++) {
            Type value;
            final Class<T> elementTypeCls = staticStructTypeRef.getTypeList().get(i).getClassType();

            if (StaticStruct.class.isAssignableFrom(elementTypeCls)) {
                TypeReference.StructTypeReference<T> elementTypeRef = (TypeReference.StructTypeReference)staticStructTypeRef.getTypeList().get(i);
                final int nestedStructLength = Utils.getStaticStructComponentSize(elementTypeRef) * MAX_BYTE_LENGTH_FOR_HEX_STRING;

                value =
                        decodeStaticStruct(
                                input.substring(currOffset, currOffset + nestedStructLength),
                                0,
                                elementTypeRef);
                currOffset += nestedStructLength;
            } else if(StaticArray.class.isAssignableFrom(elementTypeCls)) {
                TypeReference.StaticArrayTypeReference elementTypeRef = (TypeReference.StaticArrayTypeReference)staticStructTypeRef.getTypeList().get(i);
                int arraySize = elementTypeRef.getSize();

                value = decodeStaticArray(input.substring(currOffset), 0, elementTypeRef, elementTypeRef.getSize());
                currOffset += Utils.getStaticArrayElementSize(elementTypeRef) * MAX_BYTE_LENGTH_FOR_HEX_STRING;
            } else {
                value = decode(input.substring(currOffset, currOffset + MAX_BYTE_LENGTH_FOR_HEX_STRING), 0, elementTypeCls);
                currOffset += MAX_BYTE_LENGTH_FOR_HEX_STRING;
            }
            elements.add(value);
        }

        return (T)new StaticStruct(elements);
    }

    @SuppressWarnings("unchecked")
    static <T extends Type> T decodeDynamicArray(
            String input, int offset, TypeReference<T> typeReference) {

        int length = decodeUintAsInt(input, offset);

        BiFunction<List<T>, String, T> function =
                (elements, typeName) -> {
                    Class baseTypeCls = Array.class.isAssignableFrom(elements.get(0).getClass())
                            ? (Class<T>) elements.get(0).getClass()
                            : (Class<T>) AbiTypes.getType(elements.get(0).getTypeAsString());

                    return (T) new DynamicArray(baseTypeCls, elements);
                };

        int valueOffset = offset + MAX_BYTE_LENGTH_FOR_HEX_STRING;

        return decodeArrayElements(input, valueOffset, typeReference, length, function);
    }

    static <T extends Type> T decodeDynamicStruct(
            String input, int offset, TypeReference<T> typeReference) throws ClassNotFoundException {
        return decodeDynamicStructElements(input, offset, (TypeReference.StructTypeReference<T>)typeReference);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Type> T decodeDynamicStructElements(
            final String input,
            final int offset,
            final TypeReference.StructTypeReference<T> typeReference) throws ClassNotFoundException {

        final int length = typeReference.getTypeList().size();

        /**
         * Dynamic struct is a set with at least 1 dynamic type.
         * So we considered both head and tail.
         * (T1,...,Tk) for k >= 0 and any types T1, …, Tk
         * if Ti is static:
         * head(X(i)) = enc(X(i)) and tail(X(i)) = "" (the empty string)
         *
         * otherwise, i.e. if Ti is dynamic:
         * head(X(i)) = enc(len( head(X(1)) ... head(X(k)) tail(X(1)) ... tail(X(i-1)) )) tail(X(i)) = enc(X(i))
         */
        int staticOffset = 0;

        //A map of element index and values.
        final Map<Integer, T> parameters = new HashMap<>();

        //If a element type is a dynamic type, set a offset that located dynamic type value.
        final List<Integer> parameterOffsets = new ArrayList<>();

        //Processed a head part.
        for (int i = 0; i < length; ++i) {
            final TypeReference<T> elementTypeRef = typeReference.getTypeList().get(i);
            final Class<T> elementTypeCls = elementTypeRef.getClassType();
            final T value;
            final int beginIndex = offset + staticOffset;

            if (isDynamic(elementTypeRef)) {
                //save a offset that located dynamic type value.
                final boolean isOnlyParameterInStruct = length == 1;
                final int parameterOffset =
                        isOnlyParameterInStruct
                                ? offset
                                : (decodeDynamicStructDynamicParameterOffset(
                                input.substring(beginIndex, beginIndex + MAX_BYTE_LENGTH_FOR_HEX_STRING)))
                                + offset;
                parameterOffsets.add(parameterOffset);
                staticOffset += MAX_BYTE_LENGTH_FOR_HEX_STRING;
            } else {
                // Decoded a value and save.
                if (StaticStruct.class.isAssignableFrom(elementTypeCls)) {
                    value =
                            (T) decodeStaticStruct(
                                    input.substring(beginIndex),
                                    0,
                                    typeReference.getTypeList().get(i));
                    staticOffset +=
                            Utils.getStaticStructComponentSize((TypeReference.StructTypeReference) typeReference.getTypeList().get(i))
                                    * MAX_BYTE_LENGTH_FOR_HEX_STRING;
                } else if(StaticArray.class.isAssignableFrom(elementTypeCls)) {
                    TypeReference.StaticArrayTypeReference staticArrayTypeReference = (TypeReference.StaticArrayTypeReference)typeReference.getTypeList().get(i);
                    int arraySize = staticArrayTypeReference.getSize();
                    value = (T) decodeStaticArray(input.substring(beginIndex), 0, staticArrayTypeReference, arraySize);

                    if (isDynamic(elementTypeRef)) {
                        staticOffset += arraySize * MAX_BYTE_LENGTH_FOR_HEX_STRING;
                    } else {
                        staticOffset += getSingleElementLength(input, staticOffset, elementTypeRef) * MAX_BYTE_LENGTH_FOR_HEX_STRING;
                    }

                } else {
                    value = decode(input.substring(beginIndex), 0, elementTypeCls);
                    staticOffset += value.bytes32PaddedLength() * 2;
                }
                parameters.put(i, value);
            }
        }

        //Processed tail part to decoded dynamic type.
        int dynamicParametersProcessed = 0;
        int dynamicParametersToProcess =
                getDynamicStructDynamicParametersCount(typeReference.getTypeList());
        for (int i = 0; i < length; ++i) {
            final TypeReference<T> subTypeReference = typeReference.getTypeList().get(i);
            final Class<T> subClsType = subTypeReference.getClassType();
            if (isDynamic(subTypeReference)) {
                final boolean isLastParameterInStruct =
                        dynamicParametersProcessed == (dynamicParametersToProcess - 1);
                final int parameterLength =
                        isLastParameterInStruct
                                ? input.length()
                                - parameterOffsets.get(dynamicParametersProcessed)
                                : parameterOffsets.get(dynamicParametersProcessed + 1)
                                - parameterOffsets.get(dynamicParametersProcessed);

                parameters.put(
                        i,
                        (T)decodeDynamicParameterFromStruct(
                                input,
                                parameterOffsets.get(dynamicParametersProcessed),
                                parameterLength,
                                typeReference.getTypeList().get(i)
                        ));
                dynamicParametersProcessed++;
            }
        }

        final List<Type> elements = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            elements.add(parameters.get(i));
        }

        return (T) new DynamicStruct(elements);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Type> int getDynamicStructDynamicParametersCount(
            final List<TypeReference> list) {
        return (int)list.stream().filter(t -> {
            try {
                return isDynamic(t);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }).count();
    }

    private static <T extends Type> T decodeDynamicParameterFromStruct(
            final String input,
            final int parameterOffset,
            final int parameterLength,
            final TypeReference<T> typeReference
    ) throws ClassNotFoundException {
        final String dynamicElementData =
                input.substring(parameterOffset, parameterOffset + parameterLength);

        final T value;
        if (DynamicStruct.class.isAssignableFrom(typeReference.getClassType())) {
            value = decodeDynamicStruct(dynamicElementData, 0, typeReference);
        } else if (StaticStruct.class.isAssignableFrom(typeReference.getClassType())) {
            value = decodeStaticStruct(dynamicElementData, 0, typeReference);
        } else if (DynamicArray.class.isAssignableFrom(typeReference.getClassType())) {
            value = decodeDynamicArray(dynamicElementData, 0, typeReference);
        } else if (StaticArray.class.isAssignableFrom(typeReference.getClassType())) {
            TypeReference.StaticArrayTypeReference<T> reference = (TypeReference.StaticArrayTypeReference<T>)typeReference;
            value = decodeStaticArray(dynamicElementData, 0, reference, reference.getSize());
        } else {
            value = decode(dynamicElementData, typeReference.getClassType());
        }
        return value;
    }

    private static int decodeDynamicStructDynamicParameterOffset(final String input) {
        return (decodeUintAsInt(input, 0) * 2);
    }

    static <T extends Type> boolean isDynamic(TypeReference<T> parameter) throws ClassNotFoundException {
        Class<T> cls = parameter.getClassType();

        // Check if the element type of static array is dynamic.
        if(StaticArray.class.isAssignableFrom(cls)) {
            if(StaticStruct.class.isAssignableFrom(cls)) {
                return false;
            }
            TypeReference subTypeRef = parameter.getSubTypeReference();
            return isDynamic(subTypeRef);
        }

        return DynamicBytes.class.isAssignableFrom(cls)
                || Utf8String.class.isAssignableFrom(cls)
                || DynamicArray.class.isAssignableFrom(cls);
    }

    static <T extends Type> boolean isDynamic(Class<T> parameter) {
        return DynamicBytes.class.isAssignableFrom(parameter)
                || Utf8String.class.isAssignableFrom(parameter)
                || DynamicArray.class.isAssignableFrom(parameter);
    }

    static BigInteger asBigInteger(Object arg) {
        if (arg instanceof BigInteger) {
            return (BigInteger) arg;
        } else if (arg instanceof BigDecimal) {
            return ((BigDecimal) arg).toBigInteger();
        } else if (arg instanceof String) {
            return Numeric.toBigInt((String) arg);
        } else if (arg instanceof byte[]) {
            return Numeric.toBigInt((byte[]) arg);
        } else if (arg instanceof Double
                || arg instanceof Float
                || arg instanceof java.lang.Double
                || arg instanceof java.lang.Float) {
            return BigDecimal.valueOf(((Number) arg).doubleValue()).toBigInteger();
        } else if (arg instanceof Number) {
            return BigInteger.valueOf(((Number) arg).longValue());
        }
        return null;
    }

    static List arrayToList(Object array) {
        int len = java.lang.reflect.Array.getLength(array);
        ArrayList<Object> rslt = new ArrayList<Object>(len);
        for (int i = 0; i < len; i++) {
            rslt.add(java.lang.reflect.Array.get(array, i));
        }
        return rslt;
    }

    @SuppressWarnings("unchecked")
    private static <T extends Type> T instantiateStaticArray(List<T> elements, int length) {
        try {
            Class<? extends StaticArray> arrayClass =
                    (Class<? extends StaticArray>)
                            Class.forName("com.klaytn.caver.abi.datatypes.generated.StaticArray" + length);

            // Check a condition that the element type is Array class type in order to be able to accept the struct type and array type as an element.
            Class baseTypeCls = Array.class.isAssignableFrom(elements.get(0).getClass())
                    ? (Class<T>) elements.get(0).getClass()
                    : (Class<T>) AbiTypes.getType(elements.get(0).getTypeAsString());

            return (T)arrayClass.getConstructor(Class.class, List.class).newInstance(baseTypeCls, elements);
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private static <T extends Type> T decodeArrayElements(
            String input,
            int startOffset,
            TypeReference<T> typeReference,
            int length,
            BiFunction<List<T>, String, T> consumer) {

        try {
            TypeReference<T> elementTypeRef = typeReference.getSubTypeReference();
            Class<T> elementTypeCls = elementTypeRef.getClassType();

            //Element type is Struct type.
            if (StructType.class.isAssignableFrom(elementTypeCls)) {
                List<T> elements = new ArrayList<>(length);
                //currOffset is a current array element offset.
                int currOffset = startOffset;

                for (int i = 0; i < length; i++) {
                    T value;

                    // hexStringOffset is where the actual data to be decoded is located.
                    int hexStringOffset = 0;
                    if (DynamicStruct.class.isAssignableFrom(elementTypeCls)) {
                        //If a element type is a dynamic type, calculate the offset that the data to be decoded using currOffset.
                        hexStringOffset = startOffset + getDataOffset(input, currOffset, typeReference);
                        value = TypeDecoder.decodeDynamicStruct(input, hexStringOffset, elementTypeRef);
                    } else {
                        hexStringOffset = currOffset;
                        value =
                                TypeDecoder.decodeStaticStruct(input, hexStringOffset, elementTypeRef);
                    }
                    elements.add(value);

                    //calculate offset that located next element.
                    currOffset += getSingleElementLength(input, currOffset, elementTypeRef) * MAX_BYTE_LENGTH_FOR_HEX_STRING;
                }

                //Instantiate element type.
                String typeName = getSimpleTypeName(elementTypeCls);
                return consumer.apply(elements, typeName);
            }
            // Element type is a array type.
            else if (Array.class.isAssignableFrom(elementTypeCls)) {
                List<T> elements = new ArrayList<>(length);
                int currOffset = startOffset;

                for(int i=0; i<length; i++) {
                    T value;
                    if(DynamicArray.class.isAssignableFrom(elementTypeCls)) {
                        //If a element type is a dynamic type, calculate the offset that the data to be decoded using currOffset.
                        int hexStringDataOffset = getDataOffset(input, currOffset, elementTypeRef);
                        value = (T)decodeDynamicArray(input, startOffset + hexStringDataOffset, elementTypeRef);
                    } else {
                        int arraySize = ((TypeReference.StaticArrayTypeReference)elementTypeRef).getSize();
                        int hexStringDataOffset = 0;

                        if(isDynamic(elementTypeRef.getSubTypeReference())) {
                            //If a element type is a dynamic type, calculate the offset that the data to be decoded using currOffset.
                            hexStringDataOffset = startOffset + getDataOffset(input, currOffset, elementTypeRef);
                        } else {
                            hexStringDataOffset = currOffset;
                        }

                        value = (T)decodeStaticArray(input, hexStringDataOffset, elementTypeRef, arraySize);
                    }
                    elements.add(value);

                    //calculate offset that located next element.
                    currOffset += getSingleElementLength(input, currOffset, elementTypeRef) * MAX_BYTE_LENGTH_FOR_HEX_STRING;
                }

                //Instantiate element type.
                String typeName = getSimpleTypeName(elementTypeCls);
                return consumer.apply(elements, typeName);
            }
            // element type is a atomic type.
            else {
                List<T> elements = new ArrayList<>(length);
                int currOffset = startOffset;
                for (int i = 0; i < length; i++) {
                    T value;
                    if (isDynamic(elementTypeRef)) {
                        //If a element type is a dynamic type, calculate the offset that the data to be decoded using currOffset.
                        int hexStringDataOffset = getDataOffset(input, currOffset, elementTypeRef);
                        value = decode(input, startOffset + hexStringDataOffset, elementTypeCls);

                        //calculate offset that located next element.
                        currOffset += MAX_BYTE_LENGTH_FOR_HEX_STRING;
                    } else {
                        value = decode(input, currOffset, elementTypeCls);

                        //calculate offset that located next element.
                        currOffset +=
                                getSingleElementLength(input, currOffset, elementTypeRef)
                                        * MAX_BYTE_LENGTH_FOR_HEX_STRING;
                    }
                    elements.add(value);
                }

                //Instantiate element type.
                String typeName = getSimpleTypeName(elementTypeCls);
                return consumer.apply(elements, typeName);
            }
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(
                    "Unable to access parameterized type " + typeReference.getType().getTypeName(), e);
        }
    }
}
