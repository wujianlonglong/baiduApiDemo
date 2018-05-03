package com.wjl.baiduapi.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.deploy.util.StringUtils;
import com.wjl.baiduapi.dao.DbTestDao;
import com.wjl.baiduapi.domain.AddressDetail;
import com.wjl.baiduapi.model.ReportData;
import com.wjl.baiduapi.repository.AddressDetailRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DbTestService {

    @Value("${baiduParameter.url}")
    private String url;

    @Value("${baiduParameter.ak}")
    private String ak;

    @Autowired
    DbTestDao dbTestDao;

    @Autowired
    AddressDetailRepository addressDetailRepository;

    RestTemplate restTemplate = new RestTemplate();

    public void dbTest() {
        List<String> latAndLog = dbTestDao.dbTest();
        int length = latAndLog.size();
        int num = 20;
        int loop = (num + length - 1) / num;
        for (int i = 0; i < loop; i++) {
            int startIndex = i * num;
            int endIndex = length >= (i + 1) * num ? (i + 1) * num : length;
            List<String> sub = latAndLog.subList(startIndex, endIndex);
            baiduAddress(sub);
        }
    }


    public String baiduAddress(List<String> sub) {
        String jionstr = StringUtils.join(sub, "|");
        Map<String, String> praMap = new HashMap<>();
        praMap.put("location", jionstr);
        praMap.put("output", "json");
        praMap.put("batch", "true");
        praMap.put("ak", ak);
        String allurl = url + "?ak={ak}&location={location}&output={output}&batch={batch}";
        String res = restTemplate.getForObject(allurl, String.class, praMap);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONArray arrays = jsonObject.getJSONArray("areas");
        List<AddressDetail> list = new ArrayList<>();
        for (int i = 0, size = arrays.size(); i < size; i++) {
            String latndLong = sub.get(i);
            AddressDetail addressDetail = new AddressDetail();
            addressDetail.setLatlng(latndLong);
            list.add(addressDetail);
            JSONObject jsonObject1 = (JSONObject) arrays.get(i);
            String countryCode = jsonObject1.getString("country_code");
            if (countryCode.equals("-1"))
                continue;
            String country = jsonObject1.getString("country");
            String province = jsonObject1.getString("province");
            String city = jsonObject1.getString("city");
            String district = jsonObject1.getString("district");
            addressDetail.setCountry(country);
            addressDetail.setProvince(province);
            addressDetail.setCity(city);
            addressDetail.setDistrict(district);

        }
        List<AddressDetail> saveList = addressDetailRepository.saveAll(list);

        return res;
    }


    public Workbook reportData() {
        List<ReportData> reportDatas = dbTestDao.getReportData();
        List<AddressDetail> addressDetails = addressDetailRepository.findAll();
        Map<String, AddressDetail> map = new HashMap<>();
        for (int i = 0, len = addressDetails.size(); i < len; i++) {
            AddressDetail addressDetail = addressDetails.get(i);
            map.put(addressDetail.getLatlng(), addressDetail);
        }

        for (int i = 0, len = reportDatas.size(); i < len; i++) {
            ReportData reportData = reportDatas.get(i);
            if (org.springframework.util.StringUtils.isEmpty(reportData.getLatAndLong()))
                continue;
            AddressDetail addressDetail = map.get(reportData.getLatAndLong());
            if (addressDetail == null)
                continue;
            String country = addressDetail.getCountry();
            String province = addressDetail.getProvince();
            String city = addressDetail.getCity();
            String district = addressDetail.getDistrict();
            reportData.setCountry(country);
            reportData.setProvince(province);
            reportData.setCity(city);
            reportData.setDistrict(district);
        }
        Map<String, List<ReportData>> reportMap = reportDatas.stream().collect(Collectors.groupingBy(ReportData::getUid));
        return excel(reportMap);

    }

    public Workbook excel(Map<String, List<ReportData>> reportMap) {
        Workbook wb = new XSSFWorkbook();
        try {
            Sheet sheet = wb.createSheet("sheet0");
            Row row = sheet.createRow(0);//首行
            row.createCell(0).setCellValue("用户id");
            row.createCell(1).setCellValue("手机号");
            row.createCell(2).setCellValue("性别");
            row.createCell(3).setCellValue("注册时间");
            row.createCell(4).setCellValue("最近登录时间");
            row.createCell(5).setCellValue("设备id");
            row.createCell(6).setCellValue("设备型号");
            row.createCell(7).setCellValue("国家");
            row.createCell(8).setCellValue("省、州");
            row.createCell(9).setCellValue("市");
            row.createCell(10).setCellValue("区、县");

            int i = 1;
            for (Map.Entry<String, List<ReportData>> entry : reportMap.entrySet()) {
                String spcashierno = entry.getKey();
                List<ReportData> reportData = entry.getValue();
                int size = reportData.size();
                if (size > 1) {
                    CellRangeAddress cra1 = new CellRangeAddress(i, i + size - 1, 0, 0);
                    sheet.addMergedRegion(cra1);
                    CellRangeAddress cra2 = new CellRangeAddress(i, i + size - 1, 1, 1);
                    sheet.addMergedRegion(cra2);
                    CellRangeAddress cra3 = new CellRangeAddress(i, i + size - 1, 2, 2);
                    sheet.addMergedRegion(cra3);
                    CellRangeAddress cra4 = new CellRangeAddress(i, i + size - 1, 3, 3);
                    sheet.addMergedRegion(cra4);
                    CellRangeAddress cra5 = new CellRangeAddress(i, i + size - 1, 4, 4);
                    sheet.addMergedRegion(cra5);
                }
                row = sheet.createRow(i);
                row.createCell(0).setCellValue(reportData.get(0).getUid());
                row.createCell(1).setCellValue(reportData.get(0).getPhone());
                row.createCell(2).setCellValue(reportData.get(0).getGender());
                row.createCell(3).setCellValue(reportData.get(0).getCreatedTime());
                row.createCell(4).setCellValue(reportData.get(0).getLastLoginTime());
                row.createCell(5).setCellValue(reportData.get(0).getDeviceId());
                row.createCell(6).setCellValue(reportData.get(0).getModel());
                row.createCell(7).setCellValue(reportData.get(0).getCountry());
                row.createCell(8).setCellValue(reportData.get(0).getProvince());
                row.createCell(9).setCellValue(reportData.get(0).getCity());
                row.createCell(10).setCellValue(reportData.get(0).getDistrict());
                for(int j=1;j<size;j++){
                    row = sheet.createRow(i+j);
                    row.createCell(5).setCellValue(reportData.get(j).getDeviceId());
                    row.createCell(6).setCellValue(reportData.get(j).getModel());
                    row.createCell(7).setCellValue(reportData.get(j).getCountry());
                    row.createCell(8).setCellValue(reportData.get(j).getProvince());
                    row.createCell(9).setCellValue(reportData.get(j).getCity());
                    row.createCell(10).setCellValue(reportData.get(j).getDistrict());
                }

                i=i+size;
            }

        } catch (Exception ex) {
        }
        return wb;

    }

}
