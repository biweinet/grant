package com.bw.swarm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bw.swarm.bean.UploadFileInfo;
import com.bw.swarm.core.PageParam;
import com.bw.swarm.dto.req.FileListReq;

import java.util.List;


public interface UploadFileInfoService extends IService<UploadFileInfo> {
    List<UploadFileInfo> selectByCode(String code);

    IPage<UploadFileInfo> pages(PageParam<FileListReq> pageParam);

    UploadFileInfo selectById(Integer id);
}
