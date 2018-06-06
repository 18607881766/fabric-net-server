package cn.aberic.simple.module.manager;

import cn.aberic.simple.base.BaseManager;
import cn.aberic.simple.module.dto.OrdererDTO;
import cn.aberic.simple.module.dto.OrgDTO;
import cn.aberic.simple.module.dto.PeerDTO;
import org.hyperledger.fabric.sdk.aberic.FabricManager;
import org.hyperledger.fabric.sdk.aberic.OrgManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：
 *
 * @author : Aberic 【2018/6/4 10:46】
 */
public class SimpleManager extends BaseManager {

    private static SimpleManager instance;

    private OrgDTO org;
    private List<OrdererDTO> orderers;
    private List<PeerDTO> peers;
    private FabricManager fabricManager;

    public static SimpleManager obtain() {
        if (null == instance) {
            synchronized (SimpleManager.class) {
                if (null == instance) {
                    instance = new SimpleManager();
                }
            }
        }
        return instance;
    }

    public void setOrg(OrgDTO org) {
        this.org = org;
    }

    public void addOrderer(OrdererDTO orderer) {
        orderers.add(orderer);
    }

    public void addPeer(PeerDTO peer) {
        peers.add(peer);
    }

    private SimpleManager() {
        orderers = new ArrayList<>();
        peers = new ArrayList<>();
    }

    public FabricManager get() throws Exception {
        if (null == fabricManager) { // 如果不存在fabricManager则尝试新建一个并放入缓存
            synchronized (SimpleManager.class) {
                fabricManager = createFabricManager(org, orderers, peers);
            }
        }
        return fabricManager;
    }

    private FabricManager createFabricManager(OrgDTO org, List<OrdererDTO> orderers, List<PeerDTO> peers) throws Exception {
        OrgManager orgManager = new OrgManager();
        orgManager
                .init(org.getId(), org.isTls(), org.isCaTls())
                .setUser(org.getUsername(), getCryptoConfigPath(org.getCryptoConfigDir()), getChannleArtifactsPath(org.getChannelArtifactsDir()))
                .setCA(org.getCaName(), org.getCaLocation())
                .setPeers(org.getOrgName(), org.getOrgMSPID(), org.getOrgDomainName())
                .setOrderers(org.getOrdererDomainName())
                .setChannel(org.getChannelName())
                .setChainCode(org.getChaincodeName(), org.getChaincodeSource(), org.getChaincodePath(), org.getChaincodeVersion(), org.getProposalWaitTime(), org.getInvokeWaitTime())
                .setBlockListener(map -> {
                    logger.debug(map.get("code"));
                    logger.debug(map.get("data"));
                });
        for (OrdererDTO orderer : orderers) {
            orgManager.addOrderer(orderer.getName(), orderer.getLocation());
        }
        for (PeerDTO peer : peers) {
            orgManager.addPeer(peer.getPeerName(), peer.getPeerEventHubName(), peer.getPeerLocation(), peer.getPeerEventHubLocation(), peer.isEventListener());
        }
        orgManager.add();
        return orgManager.use(org.getId());
    }

}
