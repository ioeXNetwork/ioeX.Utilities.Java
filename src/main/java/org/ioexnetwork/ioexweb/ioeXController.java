package org.ioexnetwork.ioexweb;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.ioexnetwork.api.*;
import org.ioexnetwork.common.Config;
import org.ioexnetwork.common.ErrorCode;
import org.ioexnetwork.ioex.UsableUtxo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class ioeXController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ioeXController.class);
    /**
     * 處理請求
     * @param params
     * @return
     * @throws Exception
     */
    public static String processMethod (String params) throws Exception {

        //read config
        try {
            Config.getConfig();
        }catch (Exception e){
            return e.toString();
        }

        LOGGER.info(params);
        JSONObject jsonObject = JSONObject.fromObject(params);
        String method = jsonObject.getString("method");
        LOGGER.info(method);
    
        JSONObject param = jsonObject.getJSONObject("params");
        switch (method) {
            case "genprivatekey":
                return Basic.genPrivateKey();

            case "genprivpubaddr":
                return Basic.gen_priv_pub_addr();

            case "getaccounts":
                return Account.getAccounts();

            case "getaccountaddresses":
                return Account.getAccountAddresses();

            case "genpublickey":
                return Basic.genPublicKey(param);

            case "genaddress":
                return Basic.genAddress(param);

            case "genneocontracthashandaddress":
                return Basic.genNeoContractHashAndAddress(param);

            case "genneocontractaddress":
                return Basic.genNeoContractAddress(param);

            case "genidentityid":
                return Basic.genIdentityID(param);

            case "gengenesisaddress":
                return Basic.genGenesisAddress(param);

            case "genmultisignaddress":
                return Basic.genMultiSignAddress(param);

            case "checkaddress":
                return Basic.checkAddress(param);

            case "genregistertx":
                return TokenTransaction.genRegisterTx(param);

            case "gentokentx":
                return TokenTransaction.genTokenTx(param);

            case "gentokenmultisigntx":
                return TokenTransaction.genTokenMultiSignTx(param);

            case "genregistertxbyprivatekey":
                return TokenTransaction.genRegisterTxByPrivateKey(param);

            case "gentokentxbyprivatekey":
                return TokenTransaction.genTokenTxByPrivateKey(param);

            case "genrawtx":
                return IOEXTransaction.genRawTx(param);

            case "decoderawtx":
                String rawTransaction = param.getString("rawtransaction");
                return IOEXTransaction.decodeRawTx(rawTransaction);

            case "genrawtxbyprivatekey":
                return IOEXTransaction.genRawTxByPrivatekey(param);

            case "genmultisigntx":
                return IOEXTransaction.genMultiSignTx(param);

            case "gentxbyaccount":
                return Account.genTxByAccount(param);

            case "importaccount":
                return Account.importAccount(param);

            case "removeaccount":
                return Account.removeAccount(param);

            case "createaccount":
                return Account.createAccount(param);

            case "exportprivatekey":
                return Account.exportPrivateKey(param);

            case "getaccountamount":
                return Account.getAccountAmount();

            case "gencrosschaintx":
                return CrossChainTransaction.genCrossChainTx(param);

            case "gencrosschainmultisigntx":
                return CrossChainTransaction.genCrossChainMultiSignTx(param);

            case "gencrosschaintxbyprivatekey":
                return CrossChainTransaction.genCrossChainTxByPrivateKey(param);

            case "gendeploycontracttx":
                return NeoContractTransaction.genDeployContractTx(param);

            case "geninvokecontracttx":
                return NeoContractTransaction.genInvokeContractTx(param);

            case "getblockcount":
                return ioeXApi.getblockcount();

            case "estimatesmartfee":
                return ioeXApi.estimatesmartfee(param);

            case "getblockbyheight":
                return ioeXApi.getblockbyheight(param);

            case "listunspent":
                return ioeXApi.listunspent(param);

            case "getblock":
                return ioeXApi.getblock(param);

            case "getrawtransaction":
                return ioeXApi.getrawtransaction(param);

            case "sendrawtransaction":
                return ioeXApi.sendrawtransaction(param);

            case "getblockhash":
                return ioeXApi.getblockhash(param);

            case "discretemining":
                return ioeXApi.discretemining(param);

            case "getreceivedbyaddress":
                return ioeXApi.getreceivedbyaddress(param);

            default:
                return ErrorCode.ParamErr(method + " method does not exist");
        }
    }
}
