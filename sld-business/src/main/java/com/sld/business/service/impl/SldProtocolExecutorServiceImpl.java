package com.sld.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sld.business.domain.SldObject;
import com.sld.business.mapper.SldObjectMapper;
import com.sld.business.service.SldObjectService;
import com.sld.business.service.SldProtocolExecutorService;
import okhttp3.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.sql.*;
import java.util.*;

@Service
public class SldProtocolExecutorServiceImpl implements SldProtocolExecutorService {

    @Resource
    private SldObjectMapper sldObjectMapper;

    @Resource
    private SldObjectService sldObjectService;



    private Map<String,Object> getKvInfo(String objectId,List<SldObject> tenantConfigObjects){
        Map<String,Object> retData = new HashMap<>();
        Map<SldObject,SldObject> baseInfoKeyValue = sldObjectService.getKeyValueObject(objectId);
        for(Map.Entry<SldObject,SldObject> one : baseInfoKeyValue.entrySet()){
            if(one.getValue().getObjectValue().equals("---")){
                List<SldObject> tenantValues = sldObjectService.listSubObjects(one.getValue().getId(),tenantConfigObjects.stream().map(p->p.getId()).collect(Collectors.toList()));
                if(CollectionUtils.isNotEmpty(tenantValues)){
                    SldObject value = tenantValues.get(0);
                    retData.put(one.getKey().getObjectCode(),value.getObjectValue());
                }
            }else{
                retData.put(one.getKey().getObjectCode(),one.getValue().getObjectValue());
            }
        }
        return retData;
    }




    private Map<String,Object> excuteHttpProtocol(SldObject http,List<SldObject> tenantConfigObjects,Map<String,Object> inputData){
        Map<String, Object> retData = new HashMap<>();
        List<SldObject> oneLevelProtocolObjects = sldObjectService.listSubObjects(http.getId(), null);
        Map<String, Object> baseInfo = new HashMap<>();
        Map<String, Object> header = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> body = new HashMap<>();

        for (SldObject one : oneLevelProtocolObjects) {
            if (one.getObjectCode().equals("baseInfo")) {
                baseInfo = getKvInfo(one.getId(),tenantConfigObjects);
            } else if (one.getObjectCode().equals("header")) {
                header = getKvInfo(one.getId(), tenantConfigObjects);
            } else if (one.getObjectCode().equals("param")) {
                param = getKvInfo(one.getId(), tenantConfigObjects);
            } else if (one.getObjectCode().equals("body")) {
                body = getKvInfo(one.getId(), tenantConfigObjects);
                body.putAll(inputData);
            } else if (one.getObjectCode().equals("retData")) {
                // Handle retData if needed
            }
        }

        OkHttpClient client = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder();

        if (baseInfo.containsKey("url") && baseInfo.containsKey("requestType")) {
            String url = baseInfo.get("url").toString();
            String requestType = baseInfo.get("requestType").toString();

            requestBuilder.url(url);

            if (requestType.equalsIgnoreCase("GET")) {
                if (!header.isEmpty()) {
                    for (Map.Entry<String, Object> entry : header.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        requestBuilder.addHeader(key, value);
                    }
                }

                if (!param.isEmpty()) {
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
                    for (Map.Entry<String, Object> entry : param.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        urlBuilder.addQueryParameter(key, value);
                    }
                    String newUrl = urlBuilder.build().toString();
                    requestBuilder.url(newUrl);
                }

                requestBuilder.get();
            } else if (requestType.equalsIgnoreCase("POST") || requestType.equalsIgnoreCase("PUT")) {
                if (!header.isEmpty()) {
                    for (Map.Entry<String, Object> entry : header.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        requestBuilder.addHeader(key, value);
                    }
                }

                if (!param.isEmpty()) {
                    FormBody.Builder formBodyBuilder = new FormBody.Builder();
                    for (Map.Entry<String, Object> entry : param.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue().toString();
                        formBodyBuilder.add(key, value);
                    }
                    RequestBody requestBody = formBodyBuilder.build();
                    requestBuilder.method(requestType, requestBody);
                }

                if (!body.isEmpty()) {
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    String jsonBody = new Gson().toJson(body);
                    RequestBody requestBody = RequestBody.create(JSON, jsonBody);
                    requestBuilder.method(requestType, requestBody);
                }
            }

            Response response = null;
            try {
                response = client.newCall(requestBuilder.build()).execute();
                if (response.isSuccessful()) {
                    // Process the successful response
                    // Assuming the response is in JSON format
                    String responseBody = response.body().string();
                    retData = new Gson().fromJson(responseBody, new TypeToken<Map<String, Object>>() {}.getType());
                } else {
                    // Handle the error response
                    throw new RuntimeException("HTTP request failed with status code: " + response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return retData;
    }


    private Map<String, Object> excuteDbBatchWriteProtocol(SldObject dbBatchWrite, List<SldObject> tenantConfigObjects, Map<String, Object> inputData) {
        Map<String, Object> dbInfo = getKvInfo(dbBatchWrite.getId(), tenantConfigObjects);
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) inputData.get("dataList");

        // Extract database connection information
        String dbAddress = (String) dbInfo.get("dbAddress");
        String dbPort = (String) dbInfo.get("dbPort");
        String dbSchema = (String) dbInfo.get("dbSchema");
        String dbTable = (String) dbInfo.get("dbTable");
        String dbUser = (String) dbInfo.get("dbUser");
        String dbPassword = (String) dbInfo.get("dbPassword");

        // JDBC connection and statement
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        Map<String, Object> resultMap = new HashMap<>();

        try {
            // Create JDBC connection
            String jdbcUrl = "jdbc:mysql://" + dbAddress + ":" + dbPort + "/" + dbSchema;
            connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

            // Create SQL statement for batch insert
            String insertSql = generateBatchInsertSql(dbTable, dataList.get(0));
            preparedStatement = connection.prepareStatement(insertSql);

            // Set auto-commit to false for batch processing
            connection.setAutoCommit(false);

            // Iterate over dataList and add batch parameters
            for (Map<String, Object> data : dataList) {
                setPreparedStatementParameters(preparedStatement, data);
                preparedStatement.addBatch();
            }

            // Execute the batch insert
            int[] batchResult = preparedStatement.executeBatch();

            // Commit the changes
            connection.commit();

            // Convert batch result to Map
            resultMap.put("batchResult", Arrays.asList(batchResult));

        } catch (SQLException e) {
            // Handle any exceptions
            e.printStackTrace();
        } finally {
            // Close statement and connection
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return resultMap;
    }

    private String generateBatchInsertSql(String tableName, Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        StringBuilder valuesSb = new StringBuilder();
        sb.append("INSERT INTO ").append(tableName).append(" (");

        for (String key : data.keySet()) {
            String columnName = convertToSnakeCase(key);
            sb.append(columnName).append(", ");
            valuesSb.append("?, ");
        }

        sb.delete(sb.length() - 2, sb.length());
        valuesSb.delete(valuesSb.length() - 2, valuesSb.length());
        sb.append(") VALUES (").append(valuesSb).append(")");

        return sb.toString();
    }

    private void setPreparedStatementParameters(PreparedStatement preparedStatement, Map<String, Object> data) throws SQLException {
        int index = 1;
        for (String key : data.keySet()) {
            Object value = data.get(key);
            preparedStatement.setObject(index++, value);
        }
    }

    private String convertToSnakeCase(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_").append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }





    @Override
    public Map<String,Object> excute(SldObject protocol,List<SldObject> tenantConfigObjects,Map<String,Object> inputData) {
        if(protocol.getObjectCode().equals("http")){
            return excuteHttpProtocol(protocol,tenantConfigObjects,inputData);
        }else if(protocol.getObjectCode().equals("dbBatchWrite")){
            return excuteDbBatchWriteProtocol(protocol,tenantConfigObjects,inputData);
        }
        return new HashMap<>();
    }
}
