package com.bw.swarm.service;

import com.bw.swarm.bean.UploadFileInfo;
import org.springframework.http.ResponseEntity;

public interface FileDownloadService {

    ResponseEntity<byte[]> downloadFiles(UploadFileInfo info);
}
