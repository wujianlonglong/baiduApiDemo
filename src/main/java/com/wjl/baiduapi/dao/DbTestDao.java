package com.wjl.baiduapi.dao;

import com.wjl.baiduapi.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DbTestDao {

    @Autowired
    JdbcTemplate aliJdbcTemplate;

    public List<String> dbTest() {

        String sql = "SELECT latitude,longitude FROM inner_db.device_info where  (latitude is not null and longitude is not null) and (latitude<>'' and longitude<>'')  group by latitude,longitude; ";
//        Map<String, Object> param = new HashMap<>();
//        param.put("mac", "34:ea:34:ed:42:d3");

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(aliJdbcTemplate);
        return jdbcTemplate.query(sql, getLastYearDailyXyanReportRowMapper());
    }

    private RowMapper<String> getLastYearDailyXyanReportRowMapper() {
        return (ResultSet, i) -> {
            String lat = ResultSet.getString("latitude");
            String log = ResultSet.getString("longitude");
            return lat + "," + log;
        };
    }


    public List<ReportData> getReportData() {
        String sql = "SELECT\n" +
                "\tui.uid ,\n" +
                "\tui.phone ,\n" +
                "\tui.country ,\n" +
                "\tui.region ,\n" +
                "\tui.city ,\n" +
                "\tFROM_UNIXTIME(ui.created_at) created_at,\n" +
                "\tFROM_UNIXTIME(ui.last_login_time) last_login_time,\n" +
                "\tCASE ui.gender\n" +
                "WHEN 'M' THEN\n" +
                "\t'男'\n" +
                "WHEN 'F' THEN\n" +
                "\t'女'\n" +
                "ELSE\n" +
                "\t'未知'\n" +
                "END gender,\n" +
                " ud.device_id ,\n" +
                "dmi.model ,\n" +
                "di.latitude,di.longitude\n" +
                "FROM\n" +
                "\tinner_db.app_user_info ui\n" +
                "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\t*\n" +
                "\tFROM\n" +
                "\t\tinner_db.user_device\n" +
                "\tWHERE\n" +
                "\t\tis_trash = 0\n" +
                ") ud ON ui.uid = ud.uid\n" +
                "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\t*\n" +
                "\tFROM\n" +
                "\t\tinner_db.device_info\n" +
                "\tWHERE\n" +
                "\t\tis_trash = 0\n" +
                ") di ON ud.device_id = di.device_id\n" +
                "LEFT JOIN (\n" +
                "\tSELECT\n" +
                "\t\t*\n" +
                "\tFROM\n" +
                "\t\tinner_db.device_model_info\n" +
                "\tWHERE\n" +
                "\t\tis_trash = 0\n" +
                ") dmi ON di.model_id = dmi.model_id";

        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(aliJdbcTemplate);
        return jdbcTemplate.query(sql, getReportDataRowMapper());

    }

    private RowMapper<ReportData> getReportDataRowMapper() {
        return (ResultSet, i) -> {
            ReportData reportData = new ReportData();
            reportData.setUid(ResultSet.getString("uid"));
            reportData.setPhone(ResultSet.getString("phone"));
            reportData.setCountry(ResultSet.getString("country"));
            reportData.setProvince(ResultSet.getString("region"));
            reportData.setCity(ResultSet.getString("city"));
//            reportData.setDistrict(ResultSet.getString("district"));
            reportData.setCreatedTime(ResultSet.getString("created_at"));
            reportData.setLastLoginTime(ResultSet.getString("last_login_time"));
            reportData.setGender(ResultSet.getString("gender"));
            reportData.setDeviceId(ResultSet.getString("device_id"));
            reportData.setModel(ResultSet.getString("model"));
            String lat = ResultSet.getString("latitude");
            String log = ResultSet.getString("longitude");
            reportData.setLatitude(lat);
            reportData.setLongitude(log);
            if (!StringUtils.isEmpty(lat) && !StringUtils.isEmpty(log)) {
                reportData.setLatAndLong(lat + "," + log);
            }

            return reportData;
        };
    }


}
