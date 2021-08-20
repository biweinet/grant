package com.bw.swarm.service;

import com.bw.swarm.dto.res.SwarmUploadRes;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    SwarmUploadRes uploadByTag(MultipartFile[] file, Long uid) throws Exception;
}
