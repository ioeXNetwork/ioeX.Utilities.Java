package org.ioexnetwork.api;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ioexnetwork.ioex.ioeX;
import org.ioexnetwork.ioex.RawTx;
import org.ioexnetwork.ioex.TxOutput;
import org.ioexnetwork.ioex.UTXOTxInput;
import org.ioexnetwork.ioex.payload.PayloadInvoke;
import org.ioexnetwork.ioex.payload.PayloadDeploy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;

import static org.ioexnetwork.api.Basic.genfunctionCode;
import static org.ioexnetwork.api.Basic.getRawTxMap;
import static org.ioexnetwork.common.InterfaceParams.*;

public class NeoContractTransaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(NeoContractTransaction.class);

    public static String genDeployContractTx(JSONObject inputsAddOutpus){
        try {
            final JSONObject json_transaction = inputsAddOutpus.getJSONObject(TRANSACTION);
            final JSONArray utxoInputs = json_transaction.getJSONArray(INPUTS);
            final JSONArray outputs = json_transaction.getJSONArray(OUTPUTS);

            //Analysis inputs
            UTXOTxInput[] UTXOTxInputs = Basic.parseInputs(utxoInputs).toArray(new UTXOTxInput[utxoInputs.size()]);
            // outputs
            TxOutput[] txOutputs = Basic.parseOutputs(outputs).toArray(new TxOutput[outputs.size()]);
            //functionCode
            genfunctionCode(json_transaction);
            //PayloadDeploy
            PayloadDeploy payloadDeploy = Basic.parsePayloadDeploy(json_transaction);

            RawTx rawTx = ioeX.deployContractTransaction(UTXOTxInputs,txOutputs,payloadDeploy);
            LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx.getRawTxString(), rawTx.getTxHash());

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    public static String genInvokeContractTx(JSONObject inputsAddOutpus){
        try {
            final JSONObject json_transaction = inputsAddOutpus.getJSONObject(TRANSACTION);
            final JSONArray utxoInputs = json_transaction.getJSONArray(INPUTS);
            final JSONArray outputs = json_transaction.getJSONArray(OUTPUTS);

            //Analysis inputs
            UTXOTxInput[] UTXOTxInputs = Basic.parseInputs(utxoInputs).toArray(new UTXOTxInput[utxoInputs.size()]);
            // outputs
            TxOutput[] txOutputs = Basic.parseOutputs(outputs).toArray(new TxOutput[outputs.size()]);
            // invoke
            PayloadInvoke payloadInvoke = Basic.genPayloadInvoke(json_transaction);

            RawTx rawTx = ioeX.invokenContractTransaction(UTXOTxInputs,txOutputs,payloadInvoke);
            LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx.getRawTxString(), rawTx.getTxHash());

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }
}
