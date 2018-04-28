package com.wjl.baiduapi.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LocationController {


    @Value("${baiduParameter.url}")
    private String url;

    @Value("${baiduParameter.ak}")
    private String ak;

    /**
     * 经纬度转换具体地址
     * @param lat
     * @param lng
     * @return
     */
    @RequestMapping("/ltToAddress")
    public String ltToAddress(String lat, String lng) {

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> praMap = new HashMap<>();
//        praMap.put("callback", "renderReverse");
        praMap.put("location", lat + "," + lng);
        praMap.put("output", "json");
        praMap.put("pois", "1");
        praMap.put("ak", ak);
        url = url + "?ak={ak}&location={location}&output={output}&pois={pois}";
        String res = restTemplate.getForObject(url, String.class, praMap);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONObject result = jsonObject.getJSONObject("result");
        JSONObject location = result.getJSONObject("location");
        JSONObject addressComponent = result.getJSONObject("addressComponent");
        String country = addressComponent.getString("country");
        String province = addressComponent.getString("province");
        String city = addressComponent.getString("city");

        return res.toString();
    }

}
