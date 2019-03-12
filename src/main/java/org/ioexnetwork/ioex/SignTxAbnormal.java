package org.ioexnetwork.ioex;

import org.ioexnetwork.common.Common;
import org.ioexnetwork.common.ErrorCode;
import org.ioexnetwork.common.SDKException;
import org.ioexnetwork.common.Util;
import org.ioexnetwork.ioex.payload.PayloadRecord;
import org.ioexnetwork.ioex.payload.PayloadRegisterAsset;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import static org.ioexnetwork.ioex.Tx.RECORD;
import static org.ioexnetwork.ioex.Tx.REGISTER_ASSET;
import static org.ioexnetwork.ioex.Tx.TRANSFER_ASSET;

/**
 * 
 */
public class SignTxAbnormal {

    /**
     * 生成並簽名交易
     * @param inputs    交易輸入
     * @param outputs   交易輸出
     * @return  原始交易數據 可以使用rest接口api/v1/transaction發送給節點
     * @throws IOException
     */
    public static RawTx makeSingleSignTx(UTXOTxInput[] inputs, TxOutput[] outputs , List<String> privateKeySign) throws SDKException {
        Tx tx = Tx.newTransferAssetTransaction(Tx.TRANSFER_ASSET,inputs, outputs);
        return SingleSignTx(tx,privateKeySign);
    }

    public static RawTx makeSingleSignTx(UTXOTxInput[] inputs, TxOutput[] outputs , List<String> privateKeySign, PayloadRecord payloadRecord) throws SDKException {
        Tx tx = Tx.recordTransaction(RECORD,inputs, outputs,payloadRecord);
        return SingleSignTx(tx,privateKeySign);
    }

    public static RawTx makeSingleSignTx(UTXOTxInput[] inputs, TxOutput[] outputs , List<String> privateKeySign, String memo) throws SDKException {
        Tx tx = Tx.newTransferAssetTransaction(TRANSFER_ASSET,inputs, outputs,memo);
        return SingleSignTx(tx,privateKeySign);
    }

    public static RawTx makeSingleSignTxByToken(UTXOTxInput[] inputs, TxOutput[] outputs , List<String> privateKeySign, PayloadRegisterAsset payload) throws SDKException {
        Tx tx = Tx.registerAssetTransaction(REGISTER_ASSET,inputs, outputs,payload);
        return SingleSignTx(tx,privateKeySign);
    }

    public static RawTx SingleSignTx(Tx tx,List<String> privateKeySign) throws SDKException {

        try {
            for(int i = 0 ; i < privateKeySign.size() ; i ++){
                ECKey ec = ECKey.fromPrivate(DatatypeConverter.parseHexBinary(privateKeySign.get(i)));
                byte[] code = Util.CreateSingleSignatureRedeemScript(ec.getPubBytes(), Common.SUFFIX_STANDARD);
                tx.sign(privateKeySign.get(i), code);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            tx.serialize(dos);

            String rawTxString = DatatypeConverter.printHexBinary(baos.toByteArray());
            String txHash =  DatatypeConverter.printHexBinary(tx.getHash());
            return  new RawTx(txHash,rawTxString);
        }catch (Exception e){
            throw new SDKException(ErrorCode.ParamErr("SingleSignTx err : " + e));
        }

    }

}
