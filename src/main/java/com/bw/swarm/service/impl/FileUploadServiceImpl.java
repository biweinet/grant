package com.bw.swarm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.bw.swarm.bean.UploadFileInfo;
import com.bw.swarm.configuration.SwarmConfig;
import com.bw.swarm.constants.enums.SwarmResultCode;
import com.bw.swarm.dto.res.SwarmUploadRes;
import com.bw.swarm.exception.BizException;
import com.bw.swarm.service.FileUploadService;
import com.bw.swarm.service.SwarmTagService;
import com.bw.swarm.service.UploadFileInfoService;
import com.bw.swarm.utils.FileSizeUtil;
import com.bw.swarm.utils.RandomMessageUtil;
import com.bw.swarm.utils.RestTemplateUtil;
import com.bw.swarm.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import static com.bw.swarm.constants.SwarmConstant.FILE_URL;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private SwarmConfig swarmConfig;

    @Autowired
    private SwarmTagService swarmTagService;

    @Autowired
    private UploadFileInfoService uploadFileInfoService;

    @Autowired
    private UrlUtils urlUtils;

    /**
     * 目前多文件上传采用同步方式，
     * @param files
     * @return
     * @throws Exception
     */
    @Override
    public SwarmUploadRes uploadByTag(MultipartFile[] files, Long uid) throws Exception {
      /*  Long tagUid = swarmTagService.createTag();
        if (tagUid == null) {
            log.error("创建taguid 失败!!!!");
            throw new Exception();
        }*/
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();

        List<UploadFileInfo> list = new ArrayList<>();
        for (MultipartFile file : files) {
            UploadFileInfo info = new UploadFileInfo();
            info.setTagUid(uid);
            info.setName(file.getOriginalFilename());
            info.setType(1);
            info.setBatchId(swarmConfig.getBatchId());
            list.add(info);
            ByteArrayResource is = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            parameters.add("file", is);

            info.setSize(getFileSize(file.getSize()));
        }
        uploadFileInfoService.saveBatch(list);

        Map<String, String> headersMap = new HashMap();
        headersMap.put("swarm-postage-batch-id", swarmConfig.getBatchId());
        headersMap.put("swarm-tag", String.valueOf(uid));

        ResponseEntity<String> res = RestTemplateUtil.postMultipartForm(urlUtils.generateSwarmUrl(FILE_URL), parameters, headersMap);
        if (Objects.nonNull(res) && res.getStatusCodeValue() == SwarmResultCode.OK.getCode()) {

            SwarmUploadRes swarmUploadRes = JSONObject.parseObject(res.getBody(), SwarmUploadRes.class);
            String receiveCode = RandomMessageUtil.getKey();
            list.stream().forEach(info -> {
                info.setReference(swarmUploadRes.getReference());
                info.setStatus(0);
                info.setReceiveCode(receiveCode);
            });
            uploadFileInfoService.updateBatchById(list);
            swarmUploadRes.setReceiveCode(receiveCode);
            return swarmUploadRes;
        } else {
            list.stream().forEach(info -> {
                info.setStatus(2);
            });
            uploadFileInfoService.updateBatchById(list);
            throw new BizException(res.getStatusCodeValue(), "上传失败！");
        }
    }


    public BigDecimal getFileSize(long fileSize){

        double size = FileSizeUtil.getFileSize(fileSize, 3);
        return BigDecimal.valueOf(size);
    }

}
