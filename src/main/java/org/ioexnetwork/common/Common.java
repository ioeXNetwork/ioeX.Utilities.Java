package org.ioexnetwork.common;

import org.ioexnetwork.ioex.bitcoinj.Utils;

import javax.xml.bind.DatatypeConverter;

/**
 * 
 */
public class Common {

    public final static byte SUFFIX_STANDARD   = (byte)0xAC;
    public final static byte SUFFIX_MULTISIG   = (byte)0xAE;
    public final static byte SUFFIX_CROSSCHAIN = (byte)0xAF;

    public final static byte PREFIX_SINGLESIG  = 0x21;
    public final static byte PREFIX_MULTISIG   = 0x12;
    public final static byte PREFIX_IDENTITYID = 0x67;

    public final static byte PREFIX_CONTRANCT  = 0x1c;
    public final static byte PREFIX_CROSSCHAIN = (byte)0x4B;
    public final static byte PREFIX_PLEDGE     = 0x1F;

    public final static String SYSTEM_ASSET_ID = "61ccbfae9f8ce9660a71321041917139cb72cbb85bd105e92f0ed32cb1d1298f";
    public final static byte[] IOEX_ASSETID = Utils.reverseBytes(DatatypeConverter.parseHexBinary(SYSTEM_ASSET_ID));
}
