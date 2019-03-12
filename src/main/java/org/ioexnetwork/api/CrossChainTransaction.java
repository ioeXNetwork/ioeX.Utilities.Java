package org.ioexnetwork.api;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ioexnetwork.common.ErrorCode;
import org.ioexnetwork.common.InterfaceParams;
import org.ioexnetwork.common.SDKException;
import org.ioexnetwork.ioex.*;
import org.ioexnetwork.ioex.payload.PayloadTransferCrossChainAsset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import static org.ioexnetwork.api.Basic.getRawTxMap;

/**
 * 
 */
public class CrossChainTransaction {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrossChainTransaction.class);

    /**
     * 單簽跨鏈轉賬
     *
     * @param inputsAddOutpus 交易輸入和交易輸出的json字符串
     * @return 返回RawTransaction的json字符串
     * @throws Exception
     */

    public static String genCrossChainTx(JSONObject inputsAddOutpus){
        try {
            final JSONObject json_transaction = inputsAddOutpus.getJSONObject(InterfaceParams.TRANSACTION);
            final JSONArray utxoInputs = json_transaction.getJSONArray(InterfaceParams.INPUTS);
            final JSONArray outputs = json_transaction.getJSONArray(InterfaceParams.OUTPUTS);
            final JSONArray CrossChainAsset = json_transaction.getJSONArray(InterfaceParams.CROSS_CHAIN_ASSET);
            final JSONArray privateKeySign = json_transaction.getJSONArray(InterfaceParams.PRIVATEKEY_SIGN);

            //Analysis inputs
            UTXOTxInput[] UTXOTxInputs = Basic.parseInputsAddress(utxoInputs).toArray(new UTXOTxInput[utxoInputs.size()]);
            //Analysis outputs
            TxOutput[] txOutputs = Basic.parseCrossChainOutputs(outputs).toArray(new TxOutput[outputs.size()]);
            //Analysis CrossChain
            PayloadTransferCrossChainAsset[] payloadTransferCrossChainAssets = Basic.parseCrossChainAsset(CrossChainAsset).toArray(new PayloadTransferCrossChainAsset[CrossChainAsset.size()]);
            //Analysis 簽名所需要的私鑰
            List<String> privateKeySignList = Basic.parsePrivates(privateKeySign);

            RawTx rawTx = ioeX.crossChainSignTx(UTXOTxInputs, txOutputs,payloadTransferCrossChainAssets, privateKeySignList);
            LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx.getRawTxString(), rawTx.getTxHash());

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    public static String genCrossChainTxByPrivateKey(JSONObject inputsAddOutpus){
        try {
            final JSONObject json_transaction = inputsAddOutpus.getJSONObject(InterfaceParams.TRANSACTION);
            final JSONArray PrivateKeys = json_transaction.getJSONArray(InterfaceParams.PRIVATEKEYS);
            final JSONArray outputs = json_transaction.getJSONArray(InterfaceParams.OUTPUTS);
            final JSONArray CrossChainAsset = json_transaction.getJSONArray(InterfaceParams.CROSS_CHAIN_ASSET);

            List<String> privateList = Basic.parsePrivates(PrivateKeys);
            //Analysis outputs
            LinkedList<TxOutput> txOutputs = Basic.parseCrossChainOutputs(outputs);
            //Analysis CrossChain
            PayloadTransferCrossChainAsset[] payloadTransferCrossChainAssets = Basic.parseCrossChainAsset(CrossChainAsset).toArray(new PayloadTransferCrossChainAsset[CrossChainAsset.size()]);

            Verify.verifyParameter(Verify.Type.ChangeAddress,json_transaction);
            String changeAddress = json_transaction.getString(InterfaceParams.CHANGE_ADDRESS);


            RawTx rawTx = UsableUtxo.makeAndSignTxByCrossChain(privateList, txOutputs,payloadTransferCrossChainAssets,changeAddress);

            LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx.getRawTxString(), rawTx.getTxHash());

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    /**
     * 跨鏈多簽生成RawTrnsaction
     *
     * @param inputsAddOutpus 交易輸入和交易輸出的json字符串
     * @return 返回RawTransaction的json字符串
     * @throws Exception
     */

    public static String genCrossChainMultiSignTx(JSONObject inputsAddOutpus){

        final JSONObject json_transaction = inputsAddOutpus.getJSONObject(InterfaceParams.TRANSACTION);
        final JSONArray utxoInputs = json_transaction.getJSONArray(InterfaceParams.INPUTS);

        if (utxoInputs.size() < 2) {
            final JSONArray outputs = json_transaction.getJSONArray(InterfaceParams.OUTPUTS);
            final JSONArray privateKeyScripte = json_transaction.getJSONArray(InterfaceParams.PRIVATEKEY_SCRIPTE);
            final JSONArray CrossChainAsset = json_transaction.getJSONArray(InterfaceParams.CROSS_CHAIN_ASSET);

            try {
                //Analysis inputs
                UTXOTxInput[] UTXOTxInputs = Basic.parseInputsAddress(utxoInputs).toArray(new UTXOTxInput[utxoInputs.size()]);
                //Analysis outputs
                TxOutput[] txOutputs = Basic.parseCrossChainOutputs(outputs).toArray(new TxOutput[outputs.size()]);
                //Analysis CrossChain
                PayloadTransferCrossChainAsset[] payloadTransferCrossChainAssets = Basic.parseCrossChainAsset(CrossChainAsset).toArray(new PayloadTransferCrossChainAsset[CrossChainAsset.size()]);
                //Analysis 創建贖回腳本所需要的私鑰
                List<String> privateKeyScripteList = Basic.parsePrivates(privateKeyScripte);

                final int M = json_transaction.getInt(InterfaceParams.M);

                //得到 簽名所需要的私鑰
                ArrayList<String> privateKeySignList = Basic.genPrivateKeySignByM(M, privateKeyScripte);

                RawTx rawTx = ioeX.crossChainMultiSignTx(UTXOTxInputs,txOutputs,payloadTransferCrossChainAssets, privateKeyScripteList, privateKeySignList, M);
                LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx.getRawTxString(), rawTx.getTxHash());

                LOGGER.info(Basic.getSuccess(resultMap));
                return Basic.getSuccess(resultMap);
            } catch (Exception e) {
                LOGGER.error(e.toString());
                return e.toString();
            }
        } else {
            // TODO 處理多簽多個inputs邏輯
            return (new SDKException(ErrorCode.ParamErr("multi Sign does not support multi inputs"))).toString();
        }
    }
}
