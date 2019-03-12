package org.ioexnetwork.ioex.payload;

import org.ioexnetwork.common.ErrorCode;
import org.ioexnetwork.common.SDKException;
import org.ioexnetwork.common.Util;

import java.io.DataOutputStream;
import java.io.IOException;

import static org.ioexnetwork.common.Util.WriteVarUint;
public class PayloadRecord {
    String RecordType;
    byte[] RecordData;

    public void Serialize(DataOutputStream o) throws SDKException {
        try {
            WriteVarUint(o, this.RecordType.length());
            o.write(this.RecordType.getBytes());
            Util.WriteVarBytes(o,this.RecordData);
        }catch (Exception e){
            throw new SDKException(ErrorCode.ParamErr("PayloadRecord serialize exception :" + e));
        }
    }

    public PayloadRecord(String type, String data){
        this.RecordType = type;
        this.RecordData = data.getBytes();
    }
}
