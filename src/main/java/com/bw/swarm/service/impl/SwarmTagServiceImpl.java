package com.bw.swarm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bw.swarm.constants.enums.SwarmResultCode;
import com.bw.swarm.dto.res.SwarmCreateTagRes;
import com.bw.swarm.dto.res.SwarmStampsRes;
import com.bw.swarm.service.SwarmTagService;
import com.bw.swarm.utils.RestTemplateUtil;
import com.bw.swarm.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

import static com.bw.swarm.constants.SwarmConstant.CREATE_TAG;
import static com.bw.swarm.constants.SwarmConstant.STAMPS;

@Service
@Slf4j
public class SwarmTagServiceImpl implements SwarmTagService {

    @Autowired
    private UrlUtils urlUtils;


    @Override
    public Long createTag() {
        ResponseEntity<String> res = RestTemplateUtil.postJson(urlUtils.generateSwarmUrl(CREATE_TAG), JSON.toJSONString(new Object()), null);
        //201接口成功
        if (Objects.nonNull(res) && res.getStatusCodeValue() == SwarmResultCode.OK.getCode()) {

            SwarmCreateTagRes swarmCreateTagRes = JSONObject.parseObject(res.getBody(), SwarmCreateTagRes.class);
            return swarmCreateTagRes.getUid();
        } else {
            return null;
        }
    }

    @Override
    public SwarmCreateTagRes getFileStatus(Long uid) {
        String downFileUrl = String.format("%s/%d", urlUtils.generateSwarmUrl(CREATE_TAG), uid);
        ResponseEntity<String> res = RestTemplateUtil.getForm(downFileUrl);

        //201接口成功
        if (Objects.nonNull(res) && res.getStatusCodeValue() == SwarmResultCode.SUCCESS.getCode()) {

            SwarmCreateTagRes swarmCreateTagRes = JSONObject.parseObject(res.getBody(), SwarmCreateTagRes.class);
            if(Objects.nonNull(swarmCreateTagRes.getTotal()) && Objects.nonNull(swarmCreateTagRes.getProcessed())){
                swarmCreateTagRes.setProgressRate(String.format("%s%s",
                        BigDecimal.valueOf(swarmCreateTagRes.getProcessed()).divide(BigDecimal.valueOf(swarmCreateTagRes.getTotal()), 2, BigDecimal.ROUND_HALF_UP)
                                .multiply(new BigDecimal("100")), "%").toString());
            }
            return swarmCreateTagRes;
        } else {
            return null;
        }
    }

    @Override
    public SwarmStampsRes buyStamps(Long amount, Long depth) {

        String downFileUrl = String.format("%s/%d/%d", urlUtils.generateSwarmUrl(STAMPS), amount, depth);
        ResponseEntity<String> res = RestTemplateUtil.postJson(downFileUrl, JSON.toJSONString(new Object()), null);

        //201接口成功
        if (Objects.nonNull(res) && res.getStatusCodeValue() == SwarmResultCode.SUCCESS.getCode()) {

            return JSONObject.parseObject(res.getBody(), SwarmStampsRes.class);
        } else {
            return null;
        }
    }

}
