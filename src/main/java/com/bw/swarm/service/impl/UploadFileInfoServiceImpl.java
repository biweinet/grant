package com.bw.swarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bw.swarm.bean.UploadFileInfo;
import com.bw.swarm.core.PageParam;
import com.bw.swarm.dao.UploadFileInfoMapper;
import com.bw.swarm.dto.req.FileListReq;
import com.bw.swarm.service.UploadFileInfoService;
import com.bw.swarm.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


@Service
public class UploadFileInfoServiceImpl extends ServiceImpl<UploadFileInfoMapper, UploadFileInfo> implements UploadFileInfoService {

    public List<UploadFileInfo> selectByCode(String code) {

        return baseMapper.selectList(new LambdaQueryWrapper<UploadFileInfo>().eq(UploadFileInfo::getReceiveCode, code).eq(UploadFileInfo::getStatus, 0));

    }

    @Override
    public IPage<UploadFileInfo> pages(PageParam<FileListReq> pageParam) {
        QueryWrapper queryWrapper = new QueryWrapper<UploadFileInfo>();
        FileListReq req = pageParam.getData();
        if (Objects.nonNull(req)) {
            if (StringUtils.isNotEmpty(req.getReceiveCode())) {
                queryWrapper.eq("receive_code", req.getReceiveCode());
            }
        }
        Page page = new Page(pageParam.getCurrent(), pageParam.getSize());

        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public UploadFileInfo selectById(Integer id) {
        return baseMapper.selectOne(new LambdaQueryWrapper<UploadFileInfo>().eq(UploadFileInfo::getId, id));

    }

}
