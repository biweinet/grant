package com.bw.swarm.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "grants")
public class PathConfig {

    /**
     * 上传路径
     */
    private static String profile;

    public static String getProfile()
    {
        return profile;
    }

    public void setProfile(String profile)
    {
        PathConfig.profile = profile;
    }
    /**
     * 获取下载路径
     */
    public static String getDownloadPath() {
        return getProfile() + "/download/";
    }

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getProfile() + "/upload";
    }
}
