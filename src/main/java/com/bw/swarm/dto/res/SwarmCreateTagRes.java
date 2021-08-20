package com.bw.swarm.dto.res;

import lombok.Data;


/**
 * {"uid":1278066217,"startedAt":"2021-02-04T15:10:47.260477637+01:00","total":0,"processed":0,"synced":0}
 */
@Data
public class SwarmCreateTagRes {
    private Long uid;

    private String startedAt;

    private Long total;

    private Long processed;

    private Long synced;

    private String progressRate;
}
