package org.ioexnetwork.ioex;

import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;

/**
 * 
 */
public class PublicX implements Comparable<PublicX>{

    private BigInteger pubX;
    private String privateKey;

    public PublicX(String privateKey){
        ECKey ec = ECKey.fromPrivate(DatatypeConverter.parseHexBinary(privateKey));
        this.privateKey = privateKey;
        this.pubX = ec.getPublickeyX().toBigInteger();
    }

    public int compareTo(PublicX o) {
        return this.pubX.compareTo(o.pubX);
    }

    @Override
    public String toString(){
        return privateKey;
    }
}
