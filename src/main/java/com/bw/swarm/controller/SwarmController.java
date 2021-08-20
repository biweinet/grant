package com.bw.swarm.controller;


import com.bw.swarm.bean.UploadFileInfo;
import com.bw.swarm.core.PageParam;
import com.bw.swarm.dto.RestResponse;
import com.bw.swarm.dto.req.FileListReq;
import com.bw.swarm.exception.BizException;
import com.bw.swarm.service.FileDownloadService;
import com.bw.swarm.service.FileUploadService;
import com.bw.swarm.service.SwarmTagService;
import com.bw.swarm.service.UploadFileInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Api(tags = "swarm操作")
@RestController
@RequestMapping("/swarm")
@Slf4j
public class SwarmController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private FileDownloadService fileDownloadService;

    @Autowired
    private SwarmTagService swarmTagService;

    @Autowired
    private UploadFileInfoService uploadFileInfoService;

    @ApiOperation(value = "文件上传，支持选取多个文件", notes = "文件上传，支持选取多个文件")
    @PostMapping("/upload")
    @ResponseBody
    @Async
    public RestResponse upload(@RequestParam("file") MultipartFile[] files, @RequestParam("uid") Long uid) throws Exception {

        return RestResponse.ok(fileUploadService.uploadByTag(files, uid));
    }

    /**
     * 上传文件前生成uid
     *
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "上传文件前生成uid", notes = "上传文件前生成uid")
    @GetMapping("/tags/create")
    public RestResponse createTagUid() {
        Long tagUid = swarmTagService.createTag();
        if (tagUid == null) {
            log.error("创建taguid 失败!!!!");
            throw new BizException("创建taguid 失败!!!!");
        }
        return RestResponse.ok(tagUid);
    }


   /* *//**
     * 购买邮票功能
     *
     * @param amount
     * @param depth
     * @return
     * @throws Exception
     *//*
    @ApiOperation(value = "购买邮票功能", notes = "购买邮票功能")
    @PostMapping("/stamps/{amount}/{depth}")
    @ResponseBody
    public RestResponse buy(@PathVariable Long amount, @PathVariable Long depth) {

        return RestResponse.ok(swarmTagService.buyStamps(amount, depth));
    }*/

    /**
     * 查看文件上传进度状态
     *
     * @return
     */
    @ApiOperation(value = "查看文件上传进度", notes = "查看文件上传进度")
    @GetMapping("/tags/{uid}")
    public RestResponse getFileStatus(@PathVariable Long uid) {

        return RestResponse.ok(swarmTagService.getFileStatus(uid));
    }


    /**
     * 根据收件码批量下载文件
     *
     * @param code
     * @return
     */
    @ApiOperation(value = "根据收件码下载文件", notes = "根据收件码下载文件")
    @GetMapping("/download/code/{code}")
    public ResponseEntity<byte[]> downloadByCode(@PathVariable String code) {
        List<UploadFileInfo> infoList = uploadFileInfoService.selectByCode(code);
        if (CollectionUtils.isEmpty(infoList)) {
            log.error("收件码：{}, 未查询到文件，下载失败！", code);
            throw new BizException("未查询到文件，下载失败！");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        //单个文件直接下载，多个文件采取压缩包的方式下载
        if (infoList.size() == 1) {
            ResponseEntity<byte[]> res = fileDownloadService.downloadFiles(infoList.get(0));
            String fileName = res.getHeaders().getContentDisposition().getFilename();
            //指定文件名
            headers.setContentDispositionFormData("attachment", fileName);
            //指定以流的形式下载文件
            return new ResponseEntity<>(res.getBody(), headers, HttpStatus.OK);
        } else {
            //文件名
            String batchDonwloadFileName = String.format("%s.zip", code);
            headers.setContentDispositionFormData("attachment", batchDonwloadFileName);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(bos);
            try {
                //打包输出流
                for (UploadFileInfo info : infoList) {
                    ResponseEntity<byte[]> res = fileDownloadService.downloadFiles(info);
                    zos.putNextEntry(new ZipEntry(info.getName()));
                    zos.write(res.getBody());
                    zos.closeEntry();
                }
            } catch (IOException e) {
                throw new BizException("下载失败！");
            } finally {
                try {
                    zos.close();
                    bos.close();
                } catch (IOException e) {
                    throw new BizException("下载失败！");
                }
            }
            return new ResponseEntity<>(bos.toByteArray(), headers, HttpStatus.CREATED);
        }

    }

    /**
     * 文件列表
     *
     * @return
     */
    @ApiOperation(value = "文件列表", notes = "文件列表")
    @PostMapping("/files/page")
    @ResponseBody
    public RestResponse filesPage(@RequestBody PageParam<FileListReq> pageParam) {

        return RestResponse.ok(uploadFileInfoService.pages(pageParam));
    }

    /**
     * 选中列表下载文件
     *
     * @return
     */
    @ApiOperation(value = "选中列表下载文件", notes = "选中列表下载文件")
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> filesPage(@PathVariable Integer id) {

        UploadFileInfo info = uploadFileInfoService.selectById(id);
        if (Objects.isNull(info)) {
            log.error("文件id：{}, 未查询到文件，下载失败！", id);
            throw new BizException("未查询到文件，下载失败！");
        }
        ResponseEntity<byte[]> res = fileDownloadService.downloadFiles(info);
        String fileName = res.getHeaders().getContentDisposition().getFilename();
        HttpHeaders headers = new HttpHeaders();
        //指定文件名
        headers.setContentDispositionFormData("attachment", fileName);
        //指定以流的形式下载文件
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        //图片url转二进制数组
        return new ResponseEntity<>(res.getBody(), headers, HttpStatus.OK);
    }

}
