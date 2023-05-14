package com.idc.common.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    private static final String PROXY_SERVER_HOST = "127.0.0.1";
    final static int PROXY_SERVER_PORT = 9999;

    /**
     * 单次请求强制走代理
     */
    public static String vpnGet(String url) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_SERVER_HOST, PROXY_SERVER_PORT));
        HttpRequest httpRequest = HttpRequest.get(url);
        return httpRequest.setProxy(proxy).execute().body();
    }

    public static String toGet(String url, Map<String, Object> map) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_SERVER_HOST, PROXY_SERVER_PORT));
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setProxy(proxy);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        for (String s : map.keySet()) {
            builder.queryParam(s, map.get(s));
        }
        String urlTemplate = builder
                .encode()
                .toUriString();

        Map<String, ?> params = new HashMap<>();
//        params.put("msisdn", msisdn);

        String body = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                null,
                String.class,
                params
        ).getBody();
        return body;
    }

    /**
     * 国内无法强制走代理
     *
     * @return
     */
    public String hutoolGet() {
        System.setProperty("java.net.useSystemProxies", "true");
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "7891");
        System.out.println("success" + HttpUtil.get("www.youtube.com"));
//        String url = "https://api.wto.org/timeseries/v1/data?i=BAT_BV_M&r=156&max=1000000&fmt=json&mode=full&lang=1&meta=false&subscription-key=1dc531027a3b48a588e167c449bdb739";
        String url = "https://api.wto.org/timeseries/v1/data?i=TP_A_0010&r=156&fmt=json&mode=full&lang=1&meta=false&subscription-key=1dc531027a3b48a588e167c449bdb739";
        return HttpUtil.get(url);


    }
}
