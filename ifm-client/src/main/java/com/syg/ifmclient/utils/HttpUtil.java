package com.syg.ifmclient.utils;

import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Set;

/**
 * @Description 根据参数拼接url进行Get查询时使用
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public class HttpUtil {

    /**
     * 根据参数拼接url进行Get查询时使用
     *
     * @param map
     * @param url
     * @return
     */
    public static String findUri(Map<String, Object> map, String url) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        if (null != map) {
            Set<String> keys = map.keySet();
            for (String key : keys) {
                builder.queryParam(key, map.get(key));
            }
        }
        return builder.toUriString();
    }
}
