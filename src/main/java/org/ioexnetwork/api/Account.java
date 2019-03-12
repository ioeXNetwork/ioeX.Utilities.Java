package org.ioexnetwork.api;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ioexnetwork.common.Config;
import org.ioexnetwork.common.ErrorCode;
import org.ioexnetwork.common.InterfaceParams;
import org.ioexnetwork.common.SDKException;
import org.ioexnetwork.ioex.UsableUtxo;
import org.ioexnetwork.ioex.payload.PayloadRecord;
import org.ioexnetwork.ioex.TxOutput;
import org.ioexnetwork.framework.rpc.Rpc;
import org.ioexnetwork.wallet.KeystoreFile;
import org.ioexnetwork.wallet.WalletMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.ioexnetwork.api.Basic.getRawTxMap;

/**
 * 
 */
public class Account {
    private static final Logger LOGGER = LoggerFactory.getLogger(Account.class);

    /**
     * create transaction by account
     * @param outpus
     * @return
     * @throws Exception
     */
    public static String genTxByAccount(JSONObject outpus){
        try {
            final JSONObject json_transaction = outpus.getJSONObject(InterfaceParams.TRANSACTION);
            final JSONArray outputs = json_transaction.getJSONArray(InterfaceParams.OUTPUTS);

            List<String> privateList = new LinkedList<String>();

            Object account = json_transaction.get(InterfaceParams.ACCOUNT);
            if (account != null) {
                JSONArray accountArray = (JSONArray) account;
                for (int i = 0; i < accountArray.size(); i++) {
                    JSONObject JsonAccount = (JSONObject) accountArray.get(i);

                    try {
                        Verify.verifyParameter(Verify.Type.Address, JsonAccount);
                        Verify.verifyParameter(Verify.Type.Password, JsonAccount);
                    } catch (Exception e) {
                        LOGGER.error(e.toString());
                        return e.toString();
                    }

                    String address = JsonAccount.getString(InterfaceParams.ADDRESS);
                    if (!KeystoreFile.isExistAccount(address)) {
                        LOGGER.error((new SDKException(ErrorCode.ParamErr("[" + address + "] Account not exist"))).toString());
                        return (new SDKException(ErrorCode.ParamErr("[" + address + "] Account not exist"))).toString();
                    }
                }
                //analysis inputs
                for (int i = 0; i < accountArray.size(); i++) {
                    JSONObject JsonAccount = (JSONObject) accountArray.get(i);
                    String password = JsonAccount.getString(InterfaceParams.PASSWORD);
                    String address = JsonAccount.getString(InterfaceParams.ADDRESS);
                    privateList.add(WalletMgr.getAccountPrivateKey(password, address));
                }
            }
            //analysis outputs
            LinkedList<TxOutput> txOutputs = Basic.parseOutputs(outputs);
            //analysis payloadRecord
            PayloadRecord payload   = Basic.parsePayloadRecord(json_transaction);

            Verify.verifyParameter(Verify.Type.ChangeAddress,json_transaction);
            String changeAddress = json_transaction.getString(InterfaceParams.CHANGE_ADDRESS);

            //create rawTransaction
            String rawTx = "";
            boolean bool = json_transaction.has(InterfaceParams.MEMO);
            if (payload != null && bool){
                return ErrorCode.ParamErr("payloadrecord and memo can't be used at the same time");
            }else if (payload == null && !bool){
                rawTx = UsableUtxo.makeAndSignTx(privateList, txOutputs, changeAddress);
            }else if (bool){
                String memo = json_transaction.getString(InterfaceParams.MEMO);
                rawTx = UsableUtxo.makeAndSignTx(privateList, txOutputs, changeAddress,memo);
            }else{
                rawTx = UsableUtxo.makeAndSignTx(privateList, txOutputs, changeAddress,payload);
            }

            LinkedHashMap<String, Object> resultMap = getRawTxMap(rawTx, UsableUtxo.txHash);

            LOGGER.info(Basic.getSuccess(resultMap));
            return Basic.getSuccess(resultMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }


    public static String createAccount(JSONObject param){
        final JSONArray accountArray = param.getJSONArray(InterfaceParams.ACCOUNT);
        JSONArray account = new JSONArray();
        for (int i = 0; i < accountArray.size(); i++) {
            JSONObject JsonAccount = (JSONObject) accountArray.get(i);
            try {
                Verify.verifyParameter(Verify.Type.Password,JsonAccount);
                String password = JsonAccount.getString(InterfaceParams.PASSWORD);
                account = WalletMgr.createAccount(password);
            } catch (Exception e) {
                LOGGER.error(e.toString());
                return e.toString();
            }
        }

        LOGGER.info(Basic.getSuccess(account));
        return Basic.getSuccess(account);
    }

    public static String importAccount(JSONObject param){

        final JSONArray accountArray = param.getJSONArray(InterfaceParams.ACCOUNT);
        JSONArray account = new JSONArray();
        for (int i = 0; i < accountArray.size(); i++) {
            JSONObject JsonAccount = (JSONObject) accountArray.get(i);
            try {
                Verify.verifyParameter(Verify.Type.Password,JsonAccount);
                Verify.verifyParameter(Verify.Type.PrivateKey,JsonAccount);
                String privateKey = JsonAccount.getString(InterfaceParams.PRIVATEKEY);
                String password = JsonAccount.getString(InterfaceParams.PASSWORD);
                account = WalletMgr.addAccount(password,privateKey);
            } catch (Exception e) {
                LOGGER.error(e.toString());
                return e.toString();
            }
        }

        LOGGER.info(Basic.getSuccess(account));
        return Basic.getSuccess(account);
    }

    public static String removeAccount(JSONObject param){
        try {
            final JSONArray accountArray = param.getJSONArray(InterfaceParams.ACCOUNT);
            JSONObject JsonAccount = (JSONObject) accountArray.get(0);

            Verify.verifyParameter(Verify.Type.Password,JsonAccount);
            Verify.verifyParameter(Verify.Type.Address,JsonAccount);
            String address = JsonAccount.getString(InterfaceParams.ADDRESS);
            String password = JsonAccount.getString(InterfaceParams.PASSWORD);
            JSONArray account = WalletMgr.removeAccount(password,address);
            LOGGER.info(Basic.getSuccess(account));
            return Basic.getSuccess(account);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    public static String getAccountAddresses(){
        try {
            String account = WalletMgr.getAccountAllAddress().toString();
            LOGGER.info(Basic.getSuccess(account));
            return Basic.getSuccess(account);
        } catch (SDKException e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    public static String getAccountAmount(){
        try {
            List addresses =  WalletMgr.getAccountAllAddress();

            HashMap<String, String> hashMap = new HashMap<>();
            for (int i = 0; i < addresses.size(); i++) {
                String address =(String) addresses.get(i);
                String getreceivedbyaddress = Rpc.getreceivedbyaddress(address, Config.getRpcUrl());
                JSONObject fromObject = JSONObject.fromObject(getreceivedbyaddress);
                String amount = fromObject.getString("result");
                hashMap.put(address,amount);
            }
            LOGGER.info(Basic.getSuccess(hashMap));
            return Basic.getSuccess(hashMap);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return e.toString();
        }
    }

    public static String exportPrivateKey(JSONObject param){

        final JSONArray accountArray = param.getJSONArray(InterfaceParams.ACCOUNT);

        LinkedList<String> privateKeyList = new LinkedList<String>();
        for (int i = 0; i < accountArray.size(); i++) {
            JSONObject JsonAccount = (JSONObject) accountArray.get(i);
            String privateKey ;
            try {
                Verify.verifyParameter(Verify.Type.Password,JsonAccount);
                Verify.verifyParameter(Verify.Type.Address,JsonAccount);
                String address = JsonAccount.getString(InterfaceParams.ADDRESS);
                String password = JsonAccount.getString(InterfaceParams.PASSWORD);
                privateKey = WalletMgr.getAccountPrivateKey(password,address);
            }catch (Exception e){
                LOGGER.error(e.toString());
                return e.toString();
            }
            privateKeyList.add(privateKey);
        }

        LOGGER.info(Basic.getSuccess(privateKeyList));
        return Basic.getSuccess(privateKeyList);
    }

    public static String getAccounts(){
        JSONArray account = KeystoreFile.readAccount();

        LOGGER.info(Basic.getSuccess(account));
        return Basic.getSuccess(account);
    }

}
