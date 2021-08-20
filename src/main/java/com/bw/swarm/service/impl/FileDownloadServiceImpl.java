package com.bw.swarm.service.impl;

import com.bw.swarm.bean.UploadFileInfo;
import com.bw.swarm.constants.enums.SwarmResultCode;
import com.bw.swarm.exception.BizException;
import com.bw.swarm.service.FileDownloadService;
import com.bw.swarm.utils.RestTemplateUtil;
import com.bw.swarm.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.bw.swarm.constants.SwarmConstant.FILE_URL;

@Service
public class FileDownloadServiceImpl implements FileDownloadService {

    @Autowired
    private UrlUtils urlUtils;

    @Override
    public ResponseEntity<byte[]> downloadFiles(UploadFileInfo info) {
        String downFileUrl = String.format("%s/%s/%s", urlUtils.generateSwarmUrl(FILE_URL), info.getReference(), info.getName());
        ResponseEntity<byte[]> res = RestTemplateUtil.getForm(downFileUrl, null);
        if(Objects.isNull(res)){
            throw new BizException("调用接口失败！");
        }
        //200接口成功
        if (res.getStatusCodeValue() == SwarmResultCode.SUCCESS.getCode()) {
            return res;
        } else {
            throw new BizException(res.getStatusCodeValue(), "接口异常！");
        }
    }
}
