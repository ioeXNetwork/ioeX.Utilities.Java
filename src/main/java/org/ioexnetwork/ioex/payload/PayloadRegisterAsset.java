package org.ioexnetwork.ioex.payload;

import org.ioexnetwork.common.ErrorCode;
import org.ioexnetwork.common.SDKException;
import org.ioexnetwork.ioex.Asset;
import org.ioexnetwork.common.Util;

import javax.xml.bind.DatatypeConverter;
import java.io.DataOutputStream;

public class PayloadRegisterAsset {
    private Asset asset;
    private long   Amount;
    private String Controller; //Uint168

    public static int MaxPrecision = 18;
    public static int ioeXPrecision = 8;
    public static int MinPrecision = 0;

    public PayloadRegisterAsset(Asset asset,long amount, String address){
        this.asset = asset;
        this.Amount  = amount;
        this.Controller = DatatypeConverter.printHexBinary(Util.ToScriptHash(address));
    }

    public void Serialize(DataOutputStream o) throws SDKException {
        try {
            this.asset.serialize(o);
            o.writeLong(Long.reverseBytes(this.Amount));
            o.write(DatatypeConverter.parseHexBinary(this.Controller));
        }catch (Exception e){
            throw new SDKException(ErrorCode.ParamErr("payloadRegisterAsset serialize exception :" + e));
        }
    }

    public long getAmount() {
        return Amount;
    }

    public String getController() {
        return Controller;
    }
}
