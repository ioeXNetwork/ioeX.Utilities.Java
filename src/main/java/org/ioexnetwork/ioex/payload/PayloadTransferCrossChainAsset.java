package org.ioexnetwork.ioex.payload;

import org.ioexnetwork.common.Util;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 
 */
public class PayloadTransferCrossChainAsset {
    private String   CrossChainAddress;
    private int      OutputIndex;
    private long     CrossChainAmount;


    public PayloadTransferCrossChainAsset(String address, long amount , int index){
        this.CrossChainAddress = address;
        this.CrossChainAmount = amount;
        this.OutputIndex = index;
    }


    public void Serialize(DataOutputStream o) throws IOException {
        o.write(this.CrossChainAddress.length());
        o.writeBytes(this.CrossChainAddress);
        Util.WriteVarUint(o,this.OutputIndex);
        o.writeLong(Long.reverseBytes(this.CrossChainAmount));
    }

    public String getCrossChainAddress() {return CrossChainAddress;}
    public int getOutputIndex() {return OutputIndex;}
    public long getCrossChainAmount() {return CrossChainAmount;}
}
