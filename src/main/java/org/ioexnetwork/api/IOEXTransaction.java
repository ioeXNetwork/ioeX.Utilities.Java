package org.ioexnetwork.api;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ioexnetwork.common.ErrorCode;
import org.ioexnetwork.common.InterfaceParams;
import org.ioexnetwork.common.SDKException;
import org.ioexnetwork.ioex.*;
import org.ioexnetwork.ioex.payload.PayloadRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.*;

import static org.ioexnetwork.api.Basic.getRawTxMap;
import static org.ioexnetwork.common.InterfaceParams.*;

/**
 * 
 */
public class IOEXTransaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(IOEXTransaction.class);
    /**
     * genRawTx
     * @param inputsAddOutpus inputs and Outpus json string
     * @return  RawTransaction json string
     * @throws Exception
     */
    public static String genRawTx(JSONObject inputsAddOutpus){
        try {
            final JSONObject json_transaction = inputsAddOutpus.getJSONObject(InterfaceParams.TRANSACTION);
            final JSONArray utxoInputs = json_transaction.getJSONArray(INPUTS);
            final JSONArray outputs = json_transaction.getJSONArray(OUTPUTS);

            //Analysis inputs
            UTXOTxInput[] UTXOTxInputs = Basic.parseInputs(utxoInputs).toArray(new UTXOTxInput[utxoInputs.size()]);
            //Analysis outputs
            TxOutput[] txOutputs = Basic.parseOutputs(outputs).toArray(new TxOutput[outputs.size()]);
            //Analysis payloadRecord
            PayloadRecord payload   = Basic.parsePayloadRecord(json_transaction);

            boolean bool = json_transaction.has(MEMO);

            //create rawTransaction
            RawTx rawTx ;

            if (payload != null && bool){
                return ErrorCode.ParamErr("PayloadRecord And Memo can't be used at the same time");
            }else if (payload == null && !bool){
                rawTx = ioeX.makeAndSignTx(UTXOTxInputs,txOutputs);
            }else if (bool){
                String memo = json_transaction.getString(MEMO);
                rawTx = ioeX.makeAndSignTx(UTXOTxInputs,txOutputs,memo);
            }else{
                rawTx = ioeX.makeAndSignTx(UTXOTxInputs,txOutputs,payload);
            }
            LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx.getRawTxString(), rawTx.getTxHash());

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    /**
     * 根據私鑰獲取utxo生成RawTrnsaction
     *
     * @param inputsAddOutpus inputs and Outpus json string
     * @return RawTransaction json string
     * @throws Exception
     */
    public static String genRawTxByPrivatekey(JSONObject inputsAddOutpus){

        try {
            final JSONObject json_transaction = inputsAddOutpus.getJSONObject(InterfaceParams.TRANSACTION);
            final JSONArray PrivateKeys = json_transaction.getJSONArray(PRIVATEKEYS);
            final JSONArray outputs = json_transaction.getJSONArray(OUTPUTS);

            //Analysis PrivateKeys
            List<String> privateList = Basic.parsePrivates(PrivateKeys);
            //Analysis outputs
            LinkedList<TxOutput> outputList = Basic.parseOutputs(outputs);
            //Analysis payloadRecord,tx=2 没有，tx=3 有payload
            PayloadRecord payload   = Basic.parsePayloadRecord(json_transaction);

            Verify.verifyParameter(Verify.Type.ChangeAddress,json_transaction);

            String changeAddress = json_transaction.getString(CHANGE_ADDRESS);

            String rawTx ;
            boolean bool = json_transaction.has(MEMO);
            if (payload != null && bool){
                return ErrorCode.ParamErr("payloadrecord And Memo can't be used at the same time");
            }else if (payload == null && !bool){
                rawTx = UsableUtxo.makeAndSignTx(privateList, outputList, changeAddress);
            }else if (bool){
                String memo = json_transaction.getString(MEMO);
                rawTx = UsableUtxo.makeAndSignTx(privateList, outputList, changeAddress,memo);
            }else{
                rawTx = UsableUtxo.makeAndSignTx(privateList, outputList, changeAddress,payload);
            }
            LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx, UsableUtxo.txHash);

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    /**
     * 多簽生成RawTrnsaction
     *
     * @param inputsAddOutpus inputs and Outpus json string
     * @return RawTransaction json string
     * @throws Exception
     */

    public static String genMultiSignTx(JSONObject inputsAddOutpus){

        final JSONObject json_transaction = inputsAddOutpus.getJSONObject(InterfaceParams.TRANSACTION);
        final JSONArray utxoInputs = json_transaction.getJSONArray(INPUTS);

        if (utxoInputs.size() < 2) {
            final JSONArray outputs = json_transaction.getJSONArray(OUTPUTS);
            final JSONArray privateKeyScripte = json_transaction.getJSONArray(PRIVATEKEY_SCRIPTE);

            try {
                //Analysis inputs
                UTXOTxInput[] UTXOTxInputs = Basic.parseInputsAddress(utxoInputs).toArray(new UTXOTxInput[utxoInputs.size()]);
                //Analysis outputs
                TxOutput[] txOutputs = Basic.parseOutputs(outputs).toArray(new TxOutput[outputs.size()]);
                //Analysis payloadRecord
                PayloadRecord payload = Basic.parsePayloadRecord(json_transaction);

                //Analysis 創建贖回腳本所需要的私鑰
                List<String> privateKeyScripteList = Basic.parsePrivates(privateKeyScripte);

                boolean bool = json_transaction.has(MEMO);

                Verify.verifyParameter(Verify.Type.M,json_transaction);
                final int M = json_transaction.getInt(InterfaceParams.M);

                //得到 簽名所需要的私鑰
                ArrayList<String> privateKeySignList = Basic.genPrivateKeySignByM(M, privateKeyScripte);

                RawTx rawTx;
                if (payload != null && bool){
                    return ErrorCode.ParamErr("payloadrecord And Memo can't be used at the same time");
                }else if (payload == null && !bool){
                    rawTx = ioeX.multiSignTransaction(UTXOTxInputs, txOutputs, privateKeyScripteList, privateKeySignList, M);
                }else if (bool){
                    String memo = json_transaction.getString(MEMO);
                    rawTx = ioeX.multiSignTransaction(UTXOTxInputs, txOutputs, privateKeyScripteList, privateKeySignList, M, memo);
                }else{
                    rawTx = ioeX.multiSignTransaction(UTXOTxInputs, txOutputs, privateKeyScripteList, privateKeySignList, M,payload);
                }
                LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx.getRawTxString(), rawTx.getTxHash());

                return Basic.getSuccess(resultMap);
            } catch (Exception e) {
                LOGGER.error(e.toString());
                return e.toString();
            }
        }
        // TODO 處理多簽多個inputs邏輯
        return (new SDKException(ErrorCode.ParamErr("multi Sign does not support multi inputs"))).toString();
    }

    /**
     * decode rawTransaction to get TXid,address,value
     * @param rawTransaction
     * @return
     * @throws IOException
     */
    public static String decodeRawTx(String rawTransaction) throws IOException {

        byte[] rawTxByte = DatatypeConverter.parseHexBinary(rawTransaction);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(rawTxByte);
        DataInputStream dos = new DataInputStream(byteArrayInputStream);
        Map resultMap = Tx.deserialize(dos);

        LOGGER.info(Basic.getSuccess(resultMap));
        return Basic.getSuccess(resultMap);
    }
}
