package cn.aberic.simple.module.service.impl;

import cn.aberic.simple.module.dto.OrdererDTO;
import cn.aberic.simple.module.dto.OrgDTO;
import cn.aberic.simple.module.dto.PeerDTO;
import cn.aberic.simple.module.manager.SimpleManager;
import cn.aberic.simple.module.mapper.SimpleMapper;
import cn.aberic.simple.module.service.SimpleService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.codec.binary.Hex;
import org.hyperledger.fabric.sdk.aberic.FabricManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 描述：
 *
 * @author : Aberic 【2018/6/4 15:03】
 */
@Service("simpleService")
public class SimpleServiceImpl implements SimpleService {

    @Resource
    private SimpleMapper simpleMapper;

    @Override
    public String chainCode(JSONObject json) {
        String type = json.getString("type");
        String fcn = json.getString("fcn");
        JSONArray arrayJson = json.getJSONArray("array");
        Map<String, String> resultMap;
        int length = arrayJson.size();
        String[] argArray = new String[length];
        for (int i = 0; i < length; i++) {
            argArray[i] = arrayJson.getString(i);
        }
        try {
            FabricManager manager = SimpleManager.obtain().get();
            switch (type) {
                case "install":
                    resultMap = manager.install();
                    break;
                case "instantiate":
                    resultMap = manager.instantiate(argArray);
                    break;
                case "upgrade":
                    resultMap = manager.upgrade(argArray);
                    break;
                case "invoke":
                    resultMap = manager.invoke(fcn, argArray);
                    break;
                case "query":
                    resultMap = manager.query(fcn, argArray);
                    break;
                default:
                    throw new RuntimeException(String.format("no type was found with name %s", type));
            }
            if (resultMap.get("code").equals("error")) {
                return responseFail(resultMap.get("data"));
            } else {
                return responseSuccess(resultMap.get("data"), resultMap.get("txid"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return responseFail(String.format("请求失败： %s", e.getMessage()));
        }
    }

    @Override
    public String trace(JSONObject json) {
        String fcn = json.getString("fcn");
        String traceId = json.getString("traceId");
        Map<String, String> resultMap;
        try {
            FabricManager manager = SimpleManager.obtain().get();
            switch (fcn) {
                case "queryBlockByTransactionID":
                    resultMap = manager.queryBlockByTransactionID(traceId);
                    break;
                case "queryBlockByHash":
                    resultMap = manager.queryBlockByHash(Hex.decodeHex(traceId.toCharArray()));
                    break;
                case "queryBlockByNumber":
                    resultMap = manager.queryBlockByNumber(Long.valueOf(traceId));
                    break;
                case "queryBlockchainInfo":
                    resultMap = manager.getBlockchainInfo();
                    break;
                default:
                    return responseFail("No func found, please check and try again.");
            }
            return responseSuccess(JSONObject.parseObject(resultMap.get("data")));
        } catch (Exception e) {
            e.printStackTrace();
            return responseFail(String.format("请求失败： %s", e.getMessage()));
        }
    }

    @Override
    public int addOrg(JSONObject json) {
        OrgDTO org = JSON.parseObject(json.toJSONString(), new TypeReference<OrgDTO>() {});
        SimpleManager.obtain().setOrg(org);
        return 0;
    }

    @Override
    public int addOrderer(JSONObject json) {
        OrdererDTO orderer = JSON.parseObject(json.toJSONString(), new TypeReference<OrdererDTO>() {});
        SimpleManager.obtain().addOrderer(orderer);
        return 0;
    }

    @Override
    public int addPeer(JSONObject json) {
        PeerDTO peer = JSON.parseObject(json.toJSONString(), new TypeReference<PeerDTO>() {});
        SimpleManager.obtain().addPeer(peer);
        return 0;
    }
}
