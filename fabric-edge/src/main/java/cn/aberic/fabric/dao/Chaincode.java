package cn.aberic.fabric.dao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 作者：Aberic on 2018/6/27 21:16
 * 邮箱：abericyang@gmail.com
 */
@Setter
@Getter
@ToString
public class Chaincode {

    private int id; // required
    private String name; // required
    private String source; // optional
    private String path; // optional
    private String policy; // optional
    private String version; // required
    private int proposalWaitTime = 90000; // required
    private int invokeWaitTime = 120; // required
    private int channelId; // required
    private String date; // optional
    private String channelName; // optional
    private String peerName; // optional
    private String orgName; // optional
    private String leagueName; // optional
}
