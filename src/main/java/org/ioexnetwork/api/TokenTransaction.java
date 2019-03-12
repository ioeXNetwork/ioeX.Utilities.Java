package org.ioexnetwork.api;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ioexnetwork.common.InterfaceParams;
import org.ioexnetwork.ioex.*;
import org.ioexnetwork.ioex.payload.PayloadRecord;
import org.ioexnetwork.ioex.payload.PayloadRegisterAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static org.ioexnetwork.api.Basic.getRawTxAndAssetIdMap;
import static org.ioexnetwork.api.Basic.getRawTxMap;
import static org.ioexnetwork.common.InterfaceParams.*;

public class TokenTransaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenTransaction.class);

    public static String genRegisterTx(JSONObject inputsAddOutpus){
        try {
            final JSONObject json_transaction = inputsAddOutpus.getJSONObject(TRANSACTION);
            final JSONArray utxoInputs = json_transaction.getJSONArray(INPUTS);

            //Analysis inputs
            UTXOTxInput[] UTXOTxInputs = Basic.parseInputs(utxoInputs).toArray(new UTXOTxInput[utxoInputs.size()]);
            //Analysis PayloadRegisterAsset
            PayloadRegisterAsset payload   = Basic.payloadRegisterAsset(json_transaction);
            //Analysis outputs
            TxOutput[] txOutputs = Basic.parseRegisterOutput(payload,json_transaction);

            //create rawTransaction
            RawTx rawTx = ioeX.makeAndSignTx(UTXOTxInputs,txOutputs,payload);
            LinkedHashMap<String, Object> resultMap = getRawTxAndAssetIdMap(rawTx.getRawTxString(), rawTx.getTxHash(),Asset.AssetId);

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    public static String genRegisterTxByPrivateKey(JSONObject inputsAddOutpus){
        try {
            final JSONObject json_transaction = inputsAddOutpus.getJSONObject(TRANSACTION);
            final JSONArray PrivateKeys = json_transaction.getJSONArray(PRIVATEKEYS);

            List<String> privateList = Basic.parsePrivates(PrivateKeys);
            //Analysis PayloadRegisterAsset
            PayloadRegisterAsset payload   = Basic.payloadRegisterAsset(json_transaction);
            //Analysis outputs
            LinkedList<TxOutput> outputList = new LinkedList<TxOutput>();

            String changeAddress = json_transaction.getString(CHANGE_ADDRESS);

            //create rawTransaction
            String rawTx = UsableUtxo.makeAndSignTxByToken(privateList, outputList, changeAddress ,payload);
            LinkedHashMap<String, Object> resultMap = getRawTxAndAssetIdMap(rawTx, UsableUtxo.txHash,Asset.AssetId);

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    public static String genTokenTxByPrivateKey(JSONObject inputsAddOutpus){
        try {
            final JSONObject json_transaction = inputsAddOutpus.getJSONObject(TRANSACTION);
            final JSONArray PrivateKeys = json_transaction.getJSONArray(PRIVATEKEYS);
            final JSONArray outputs = json_transaction.getJSONArray(OUTPUTS);

            List<String> privateList = Basic.parsePrivates(PrivateKeys);
            //Analysis outputs
            LinkedList<TxOutput> txOutputs = Basic.parseOutputsByAsset(outputs);

            String changeAddress = json_transaction.getString(CHANGE_ADDRESS);

            //create rawTransaction

            String rawTx = UsableUtxo.makeAndSignTx(privateList, txOutputs, changeAddress);

            LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx, UsableUtxo.txHash);

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    public static String genTokenTx(JSONObject inputsAddOutpus){
        try {
            final JSONObject json_transaction = inputsAddOutpus.getJSONObject(TRANSACTION);
            final JSONArray utxoInputs = json_transaction.getJSONArray(INPUTS);
            final JSONArray outputs = json_transaction.getJSONArray(OUTPUTS);

            //Analysis inputs
            UTXOTxInput[] UTXOTxInputs = Basic.parseInputs(utxoInputs).toArray(new UTXOTxInput[utxoInputs.size()]);
            //Analysis outputs
            TxOutput[] txOutputs = Basic.parseOutputsByAsset(outputs).toArray(new TxOutput[outputs.size()]);

            //create rawTransaction
            RawTx rawTx  = ioeX.makeAndSignTx(UTXOTxInputs,txOutputs);

            LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx.getRawTxString(), rawTx.getTxHash());

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    public static String genTokenMultiSignTx(JSONObject inputsAddOutpus){

        final JSONObject json_transaction = inputsAddOutpus.getJSONObject(TRANSACTION);
        final JSONArray utxoInputs = json_transaction.getJSONArray(INPUTS);

        final JSONArray outputs = json_transaction.getJSONArray(OUTPUTS);
        final JSONArray privateKeyScripte = json_transaction.getJSONArray(PRIVATEKEY_SCRIPTE);

        try {
            //Analysis inputs
            UTXOTxInput[] UTXOTxInputs = Basic.parseInputsAddress(utxoInputs).toArray(new UTXOTxInput[utxoInputs.size()]);
            //Analysis outputs
            TxOutput[] txOutputs = Basic.parseOutputsByAsset(outputs).toArray(new TxOutput[outputs.size()]);
            //Analysis payloadRecord
            PayloadRecord payload = Basic.parsePayloadRecord(json_transaction);

            //Analysis 創建贖回腳本所需要的私鑰
            List<String> privateKeyScripteList = Basic.parsePrivates(privateKeyScripte);


            Verify.verifyParameter(Verify.Type.M,json_transaction);
            final int M = json_transaction.getInt(InterfaceParams.M);

            //得到 簽名所需要的私鑰
            ArrayList<String> privateKeySignList = Basic.genPrivateKeySignByM(M, privateKeyScripte);

            RawTx rawTx  = ioeX.multiSignTransaction(UTXOTxInputs, txOutputs, privateKeyScripteList, privateKeySignList, M);
            LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx.getRawTxString(), rawTx.getTxHash());

            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }
}
