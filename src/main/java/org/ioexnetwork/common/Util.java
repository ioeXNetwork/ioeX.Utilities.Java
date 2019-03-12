package org.ioexnetwork.common;


import org.ioexnetwork.ioex.ioeX;
import org.ioexnetwork.ioex.FormatTransfer;
import org.ioexnetwork.ioex.PublicX;
import org.ioexnetwork.ioex.bitcoinj.Base58;
import org.ioexnetwork.ioex.bitcoinj.Sha256Hash;
import org.ioexnetwork.ioex.bitcoinj.Utils;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * TODO:
 */
public class Util {
    public static void WriteVarUint(DataOutputStream writer, long value) throws IOException {
        byte[] buf =new byte[9];
        if (value < 0xFD) {
            writer.writeByte((byte)value);
        } else if (value <= 0xFFFF) {
            writer.writeByte(0xFD);
            short s = FormatTransfer.reverseShort((short)value);
            writer.writeShort(s);
        } else if (value <= 0xFFFFFFFFL) {
            writer.writeByte(0xFE);
            int n = Integer.reverseBytes((int)value);
            writer.writeInt(n);
        } else {
            writer.writeByte(0xFF);
            long l = Long.reverseBytes(value);
            writer.writeLong(l);
        }
        return ;
    }
    public static void WriteVarBytes(DataOutputStream writer,byte[] value) throws IOException {
        WriteVarUint(writer,value.length);
        writer.write(value);
    }


    public static long ReadVarUint(DataInputStream read) throws IOException {
        byte n = read.readByte();
        if ((n & 0xFF) < 0xFD) {
            return n & 0xFF;
        } if ((n & 0xFF) == 0xFD) {
            short shortNumber = read.readShort();
            short number = FormatTransfer.reverseShort(shortNumber);
            return number;
        }else if ((n & 0xFF) == 0xFE){
            int intNumber = read.readInt();
            int number = Integer.reverseBytes(intNumber);
            return number;
        }else if ((n & 0xFF) == 0xFF){
            long longNumber = read.readLong();
            long number = Long.reverseBytes(longNumber);
            return number;
        }
        return 0;
    }

    /**
     * 地址到 公鑰/腳本 哈希 轉換 可逆(ToAddress)
     * @param address
     * @return program hash 21byte
     */
    public static byte[]  ToScriptHash(String address ){

        byte[] decoded = Base58.decodeChecked(address);

        BigInteger bi = new BigInteger(decoded);
        byte[] ph = new byte[21];
        System.arraycopy(bi.toByteArray(),0,ph,0,21);

        return ph;
    }

    /**
     * 檢查地址合法性
     * @param address
     * @return
     */
    public static boolean checkAddress(String address){
        try {
            byte[] sh = ToScriptHash(address);
            if(sh[0]!=33&&sh[0]!=18){
                return false;
            }
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 公鑰/腳本合約 到 公鑰/腳本合約 哈希 轉換 單向
     * @param code
     * @param signType
     * @return
     */
    public static byte[] ToCodeHash(byte[] code, byte signType) {

        byte[] f = Utils.sha256hash160(code);
        byte[] g = new byte[f.length + 1];
        if (signType == Common.PREFIX_CONTRANCT){
            g[0] = signType;
            f = Utils.reverseBytes(f);
        }else{
            g[0] = signType;
        }
        System.arraycopy(f,0,g,1,f.length);
        return g;
    }


    /**
     * 公鑰/腳本 哈希 到地址轉換 可逆（ToScriptHash)
     * @param programHash
     * @return
     */
    public static String ToAddress(byte[] programHash){
        byte[] f = Sha256Hash.hashTwice(programHash);
        byte[] g = new byte[programHash.length+4];
        System.arraycopy(programHash,0,g,0,programHash.length);
        System.arraycopy(f,0,g,programHash.length,4);

        //BigInteger bi = new BigInteger(g);

        return Base58.encode(g);
    }

    public static byte[] CreateSingleSignatureRedeemScript(byte[] pubkey, byte signType) {
        byte[] script = new byte[35];
        script[0] = 33;
        System.arraycopy(pubkey,0,script,1,33);
        script[34] = signType;
        return script;
    }

    public static byte[] createNeoContranctRedeemScript(byte[] hash){
        byte[] script = new byte[21];
        System.arraycopy(hash,0,script,0,21);
        return script;
    }

    public static void sortByteArrayArrayUseRevertBytesSequence(byte[][] hashes) {
        Arrays.sort(hashes,new Comparator(){

            public int compare(Object o1, Object o2) {
                int ret;
                byte[] ba1 = (byte[])o1;
                byte[] ba2 = (byte[])o2;
                for(int i=ba1.length-1;i>=0;i--){
                    ret = (ba1[i]&0xff) - (ba2[i]&0xff);
                    if(ret !=0 ) return ret;
                }
                return 0;
            }
        });
    }

    public static long IntByString(String value){
        String[] split = value.split("\\.");
        if (split.length == 2){
            long front = (long) Integer.parseInt(split[0]) * 100000000;

            String after = split[1];
            if (after.length() == 8){
                long afterInt = (long)Integer.parseInt(after);
                long Value = front + afterInt;
                return Value;

            }else{
                after = after + '0' * (8 - after.length());
                long afterInt = (long)Integer.parseInt(after);
                long Value = front + afterInt;
                return Value;
            }
        }else {
            long Value = (long) Integer.parseInt(value) * 100000000;
            return Value;
        }
    }

    public static BigDecimal multiplyAmountIOEX(BigDecimal price, Integer decimal) {
        BigDecimal coefficient = new BigDecimal(Math.pow(10, decimal));
        return price.multiply(coefficient).setScale(8, BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal divideAmountIOEX(BigDecimal price,Integer decimal) {
        BigDecimal coefficient = new BigDecimal(Math.pow(10, decimal));
        return price.divide(coefficient).setScale(8, BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal multiplyAmountETH(BigDecimal price, Integer decimal) {
        BigDecimal coefficient = new BigDecimal(Math.pow(10, decimal));
        return price.multiply(coefficient).setScale(18, BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal divideAmountETH(BigDecimal price,Integer decimal) {
        BigDecimal coefficient = new BigDecimal(Math.pow(10, decimal));
        return price.divide(coefficient).setScale(18, BigDecimal.ROUND_DOWN);
    }


    // 完整的判斷中文漢字和符號
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }


    // 根據Unicode編碼完美的判斷中文漢字和符號
    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    public static byte[] byteToByteArray(List<Byte> list){
        int size = list.size();
        byte[] bytes = new byte[size];
        for(int i= 0; i < size; i++ ){
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    public static boolean isAscii(String str){
        char[] chars = str.toCharArray();
        for (int i : chars){
            if (32 <= i && 126 >= i){
            }else return false;
        }
        return true;
    }


    /**
     * 生成x地址
     * @param genesisBlockHash
     * @return
     */
    public static byte[] GenGenesisAddressRedeemScript(String genesisBlockHash) throws SDKException {
        byte[] reversedGenesisBlockBytes = Utils.reverseBytes(DatatypeConverter.parseHexBinary(genesisBlockHash));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            dos.write((byte)reversedGenesisBlockBytes.length);
            dos.write(reversedGenesisBlockBytes);
            dos.write((byte)0xAF);
            return baos.toByteArray();
        }catch (Exception e){
            throw new SDKException(ErrorCode.ParamErr("create GenGenesisAddress redeem Script failure , " + e));
        }
    }

    /**
     * 創建多簽贖回腳本
     * @param privateKeyList
     * @param M
     * @return
     */
    public static byte[] CreatemultiSignatureRedeemScript(List<PublicX> privateKeyList , int M) throws SDKException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            if (M == 0){
                M = privateKeyList.size()/2 + 1;
            }
            dos.write((byte)(0x51 + M - 1 ));

            Collections.sort(privateKeyList);

            for (int i= 0 ; i < privateKeyList.size() ; i++ ){
                dos.writeByte((byte)33);
                dos.write(DatatypeConverter.parseHexBinary(ioeX.getPublicFromPrivate(privateKeyList.get(i).toString())));
            }

            dos.write((byte)(0x51 + privateKeyList.size() - 1));
            dos.write((byte)0xAE);

            return baos.toByteArray();
        }catch (Exception e){
            throw new SDKException(ErrorCode.ParamErr("create multiSignature redeem Script failure , " + e));
        }
    }

    /**
     * conversion Positive BigInteger
     * @param bigInteger
     * @return
     */
    public static byte[] BigIntegerToPositiveBigInteger(BigInteger bigInteger){
        byte[] bytes = bigInteger.toByteArray();
        if (bytes[0] == 0){
            byte[] tmp = new byte[bytes.length - 1];
            System.arraycopy(bytes,1,tmp,0,tmp.length);
            bytes = tmp;
        }
        return bytes;
    }
}

