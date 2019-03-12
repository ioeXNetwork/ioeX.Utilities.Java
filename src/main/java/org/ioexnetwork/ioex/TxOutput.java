package org.ioexnetwork.ioex;

import org.ioexnetwork.common.Common;
import org.ioexnetwork.common.Util;
import org.ioexnetwork.ioex.bitcoinj.Utils;
import static org.ioexnetwork.ioex.payload.PayloadRegisterAsset.ioeXPrecision;
import javax.xml.bind.DatatypeConverter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 
 */
public class TxOutput {
    private byte[] SystemAssetID; //32 byte unit256
    private byte[] TokenAssetID; //32 byte unit256
    private long Value; //Fixed64
    private BigInteger TokenValue;
    private int OutputLock = 0; //uint32
    private byte[] ProgramHash; //21byte unit168
    private String Address;
    private final String DESTROY_ADDRESS = "0000000000000000000000000000000000";


    public TxOutput(String address,String amount,String assetId,int precision){
        this.Address = address;
        if (assetId.toLowerCase().equals(Common.SYSTEM_ASSET_ID)){
            this.SystemAssetID = Common.IOEX_ASSETID;
            this.Value = Util.multiplyAmountIOEX(new BigDecimal(amount), ioeXPrecision).toBigInteger().longValue();
        }else {
            this.TokenAssetID =  Utils.reverseBytes(DatatypeConverter.parseHexBinary(assetId));
            this.TokenValue = Util.multiplyAmountETH(new BigDecimal(amount),precision).toBigIntegerExact();
        }

        //programHash
        if (address.equals(DESTROY_ADDRESS)){
            this.ProgramHash = new byte[21];
        }else {
            this.ProgramHash = Util.ToScriptHash(address);
        }
    }

    void Serialize(DataOutputStream o) throws IOException {
        if (this.SystemAssetID != null){
            o.write(this.SystemAssetID);
            o.writeLong(Long.reverseBytes(this.Value));
        }else if (this.TokenAssetID != null){
            o.write(this.TokenAssetID);
            //因為TokenValue始終為正整數，所以取消bigInter第一個字節（補碼）
            byte[] toPositiveBigInteger = Util.BigIntegerToPositiveBigInteger(this.TokenValue);
            Util.WriteVarBytes(o,toPositiveBigInteger);
        }
        o.writeInt(Integer.reverseBytes(this.OutputLock));
        o.write(this.ProgramHash);
    }

    public static Map DeSerialize(DataInputStream o) throws IOException {
        // AssetID
        byte[] buf = new byte[32];
        o.read(buf,0,32);
        DatatypeConverter.printHexBinary(Utils.reverseBytes(buf));

        // Value
        long value =  o.readLong();
        long v = Long.reverseBytes(value);

        // OutputLock
        long outputLock =  o.readInt();
        Long.reverseBytes(outputLock);

        // ProgramHash
        byte[] program = new byte[21];
        o.read(program,0,21);
        byte[] programHash = program;
        String address = Util.ToAddress(programHash);

        Map<String, Object> outputMap = new LinkedHashMap<String, Object>();
        outputMap.put("address:",address);
        outputMap.put("amount:",v);
        return outputMap;
    }

    public byte[] getSystemAssetID() {return this.SystemAssetID; }

    public long getValue() {return this.Value;}

    public BigInteger getTokenValue() {return this.TokenValue;}

    public byte[] getTokenAssetID(){return this.TokenAssetID;}

    public long getOutputLock() {
        return this.OutputLock;
    }

    public byte[] getProgramHash() {
        return this.ProgramHash;
    }

    public String getAddress() {
        return this.Address;
    }
}
