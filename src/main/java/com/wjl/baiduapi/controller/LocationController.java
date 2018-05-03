package com.wjl.baiduapi.controller;

import com.wjl.baiduapi.service.DbTestService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
public class LocationController {


    @Autowired
    private DbTestService dbTestService;


    /**
     * 经纬度转换具体地址
     *
     * @param lat
     * @param lng
     * @return
     */
    @RequestMapping("/ltToAddress")
    public String ltToAddress(String lat, String lng) {


        List<String> list = new ArrayList<String>();
        list.add("first");
        list.add("second");
        list.add("third");

       String join= String.join(",",list);
        return join;
    }


    @GetMapping("/dbTest")
    public String dbTest() {
        dbTestService.dbTest();
        return "1";
    }

    @GetMapping("/reportData")
    public String reportData(HttpServletResponse response) throws IOException {
        Workbook workbook=dbTestService.reportData();
        response.reset();
        // 指定下载的文件名
        response.setHeader("Content-Disposition", "attachment;filename=" + new String("用户设备表".getBytes("gb2312"), "ISO8859-1") + ".xlsx");
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
//        response.setHeader("Pragma", "no-cache");
//        response.setHeader("Cache-Control", "no-cache");
//        response.setDateHeader("Expires", 0);
        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.close();


        return "1";
    }

}
