package com.bw.swarm.service;

import com.bw.swarm.dto.res.SwarmCreateTagRes;
import com.bw.swarm.dto.res.SwarmStampsRes;

public interface SwarmTagService {

    Long createTag();

    SwarmCreateTagRes getFileStatus(Long uid);

    SwarmStampsRes buyStamps(Long amount, Long depth);
}
