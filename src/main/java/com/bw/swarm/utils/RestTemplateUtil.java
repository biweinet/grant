package com.bw.swarm.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Slf4j
public class RestTemplateUtil {


    public static RestTemplate getRestTemplateBean() {
        return SpringContextUtils.getBean("restTemplate", RestTemplate.class);
    }


    /**
     * restTemplate 发送post请求
     * 请求参数是json类型
     *
     * @param url
     * @param json
     * @return
     */
    public static ResponseEntity<String> postJson(String url, String json, Map headersMap) {
        log.info("请求url: {}, 入参参数：{}, 请求头信息：{}", url, json, JSONObject.toJSONString(headersMap));
        // 设置·header信息
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        if (Objects.nonNull(headersMap)) {
            requestHeaders.setAll(headersMap);
        }
        HttpEntity<String> requestEntity = new HttpEntity<String>(json, requestHeaders);
        ResponseEntity<String> entity = getRestTemplateBean().postForEntity(url, requestEntity, String.class);
        log.info("请求url: {}, 返参结果：{}", url, JSONObject.toJSONString(entity));
        return entity;
    }

    /**
     * restTemplate 发送post请求
     * 请求参数是form-data类型
     *
     * @param url
     * @param json
     * @return
     */
    public static ResponseEntity<String> postForm(String url, String json, Map headersMap) {
        // 设置·header信息
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        if (Objects.nonNull(headersMap)) {
            requestHeaders.setAll(headersMap);
        }
        HttpEntity<String> requestEntity = new HttpEntity<String>(json, requestHeaders);
        ResponseEntity<String> entity = getRestTemplateBean().postForEntity(url, requestEntity, String.class);
        return entity;
    }

    /**
     * restTemplate 发送post请求
     *
     * @param url
     * @return
     */
    public static ResponseEntity<String> postMultipartForm(String url, MultiValueMap<String, Object> parameters, Map headersMap) {
        log.info("请求url: {}, 请求头信息：{}", url, JSONObject.toJSONString(headersMap));
        // 设置·header信息
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        if (Objects.nonNull(headersMap)) {
            requestHeaders.setAll(headersMap);
        }
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parameters, requestHeaders);
        ResponseEntity<String> entity = getRestTemplateBean().postForEntity(url, requestEntity, String.class);
        log.info("请求url: {}, 返参结果：{}", url, JSONObject.toJSONString(entity));
        return entity;
    }

    /**
     * restTemplate 发送get请求
     * 有重定向问题的
     * @param url
     * @return
     */
    public static ResponseEntity<byte[]> getForm(String url, MultiValueMap<String, Object> parameters) {
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        // 发送请求
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);
        ResponseEntity<byte[]> obj = getRestTemplateBean().exchange(url, HttpMethod.GET ,entity,byte[].class);
        return obj;
    }

    public static ResponseEntity<String> getForm(String url) {
        // 请求头
        HttpHeaders headers = new HttpHeaders();
        // 发送请求
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(headers);
        ResponseEntity<String> obj = getRestTemplateBean().getForEntity(url, String.class);
        return obj;
    }
}
