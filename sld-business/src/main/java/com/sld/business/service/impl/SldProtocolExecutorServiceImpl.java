package com.sld.business.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
        List<String> includeIds = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(tenantConfigObjects)){
            includeIds = tenantConfigObjects.stream().map(p->p.getId()).collect(Collectors.toList());
        }
        Map<SldObject,SldObject> baseInfoKeyValue = sldObjectService.getKeyValueObject(objectId,includeIds);
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

    public List<Map<String, Object>> extractListData(Map<String,Object> srcData,String targetObject) {
        String[] objects = targetObject.split(",");
        Object tmp = srcData;
        for (String one : objects) {
            Map<String,Object> tmp2 = (Map<String,Object>)tmp;
            tmp = tmp2.get(one);
        }
        return (List<Map<String, Object>>)tmp;
    }

    public Map<String, Object> extractSingleData(Map<String,Object> srcData,String targetObject) {
        String[] objects = targetObject.split(",");
        Object tmp = srcData;
        for (String one : objects) {
            Map<String,Object> tmp2 = (Map<String,Object>)tmp;
            tmp = tmp2.get(one);
        }
        return (Map<String, Object>)tmp;
    }



    private void excuteSingleDataProtocol(SldObject protocol,List<SldObject> tenantConfigObjects,Map<String,Object> inputData){
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> attrInfo = getKvInfo(protocol.getId(), tenantConfigObjects);
        String srcBusinessId = (String)attrInfo.get("srcBusinessId");
        String srcExtractData = (String)attrInfo.get("srcExtractData");
        String targetBusinessId = (String)attrInfo.get("targetBusinessId");
        String action = (String)attrInfo.get("action");
        String fieldMapInfo = (String)attrInfo.get("fieldMaoInfo");
        Map<String,Object> mapInfo = convertSplitorStringToMapData(fieldMapInfo);
        if(action.equals("list")){
            List<Map<String, Object>> srcList = new ArrayList<>();
            if(srcExtractData.equals("---")){
                srcList = (List<Map<String, Object>>)inputData.get(srcBusinessId);
            }else{
                srcList = extractListData((Map<String,Object>)inputData.get(srcBusinessId),srcExtractData);
            }

            List<Map<String, Object>> targetList = new ArrayList<>();
            for(Map<String, Object> one : srcList){
                Map<String,Object> elm = new HashMap<>();
                for(Map.Entry<String, Object> subOne: one.entrySet()){
                    if(mapInfo.containsKey(subOne.getKey())){
                        String targetField = (String)mapInfo.get(subOne.getKey());
                        elm.put(targetField,subOne.getValue());
                        targetList.add(elm);
                    }
                }
            }
            inputData.put(targetBusinessId,targetList);
        }else if(action.equals("single")){
            Map<String, Object> srcObject = new HashMap<>();
            if(srcExtractData.equals("---")){
                srcObject = (Map<String, Object>)inputData.get(srcBusinessId);
            }else{
                srcObject = extractSingleData((Map<String,Object>)inputData.get(srcBusinessId),srcExtractData);
            }

            Map<String, Object> targetObject = new HashMap<>();
            for(Map.Entry<String, Object> subOne: srcObject.entrySet()){
                if(mapInfo.containsKey(subOne.getKey())){
                    String targetField = (String)mapInfo.get(subOne.getKey());
                    targetObject.put(targetField,subOne.getValue());
                }
            }
            inputData.put(targetBusinessId,targetObject);
        }
    }

    private Map<String,Object> excuteHttpProtocol(SldObject protocol,List<SldObject> tenantConfigObjects,Map<String,Object> inputData){
        Map<String, Object> retData = new HashMap<>();
        List<SldObject> oneLevelProtocolObjects = sldObjectService.listSubObjects(protocol.getId(), null);
        Map<String, Object> baseInfo = new HashMap<>();
        Map<String, Object> header = new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> retObject = new HashMap<>();
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
            } else if (one.getObjectCode().equals("retObject")) {
                // Handle retData if needed
                retObject = getKvInfo(one.getId(), tenantConfigObjects);
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
                }else{
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    String jsonBody = "{}";
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
//                    retData = parseJson(retObject,jsonStr);
                    return retData;
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


    private  Map<String, Object> excuteDbProtocol(SldObject protocol, List<SldObject> tenantConfigObjects, Map<String, Object> inputData) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> dbInfo = getKvInfo(protocol.getId(), tenantConfigObjects);
        // Extract database connection information
        String dbAddress = (String) dbInfo.get("dbAddress");
        String dbPort = (String) dbInfo.get("dbPort");
        String dbSchema = (String) dbInfo.get("dbSchema");
        String dbTable = (String) dbInfo.get("dbTable");
        String dbUser = (String) dbInfo.get("dbUser");
        String dbPassword = (String) dbInfo.get("dbPassword");
        String dbOperType = (String) dbInfo.get("dbOperType");

        if (dbOperType.equals("batchWrite")) {
            String srcFieldNameForListData = (String) dbInfo.get("srcFieldNameForListData");
            // Batch write logic
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) inputData.get(srcFieldNameForListData);
            // JDBC connection and statement
            Connection connection = null;
            PreparedStatement preparedStatement = null;

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
        } else if (dbOperType.equals("dbQuery")) {
            // Query logic
            String srcFieldNameForListData = (String) dbInfo.get("srcFieldNameForListData");
            String dbFieldNameForBatchQuery = (String) dbInfo.get("dbFieldNameForBatchQuery");
            List<Object> dataList = (List<Object>) inputData.get("srcFieldNameForListData");

            Map<String, Object> eqFieldList = new HashMap<>();
            String srcFieldNameForEqFieldList = (String) dbInfo.get("srcFieldNameForEqFieldList");
            if(srcFieldNameForEqFieldList!=null && !srcFieldNameForEqFieldList.isEmpty()){
                eqFieldList = (Map<String, Object>) inputData.get(srcFieldNameForEqFieldList);
            }else{
                String eqFieldInfo = (String) dbInfo.get("eqFieldInfo");
                if(StringUtils.isNotBlank(eqFieldInfo)){
                    eqFieldList = convertSplitorStringToMapData(eqFieldInfo);
                }
            }

            Map<String, Object> likeFieldList = new HashMap<>();
            String srcFieldNameForLikeFieldList = (String) dbInfo.get("srcFieldNameForLikeFieldList");
            if(srcFieldNameForLikeFieldList!=null && !srcFieldNameForLikeFieldList.isEmpty()){
                likeFieldList = (Map<String, Object>) inputData.get("srcFieldNameForEqFieldList");
            }else{
                String likeFieldInfo = (String) dbInfo.get("likeFieldInfo");
                if(StringUtils.isNotBlank(likeFieldInfo)){
                    likeFieldList = convertSplitorStringToMapData(likeFieldInfo);
                }
            }

            //排序处理，待增加

            // JDBC connection and statement
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                // Create JDBC connection
                String jdbcUrl = "jdbc:mysql://" + dbAddress + ":" + dbPort + "/" + dbSchema;
                connection = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

                // Create SQL statement for batch query
                String selectSql = generateBatchSelectSql(dbTable, eqFieldList, likeFieldList);
                preparedStatement = connection.prepareStatement(selectSql);

                // Set query parameters
                int paramIndex = 1;
                for (Map.Entry<String, Object> eqField : eqFieldList.entrySet()) {
                    Object value = eqField.getValue();
                    preparedStatement.setObject(paramIndex++, value);
                }

                for (Map.Entry<String, Object> likeField : likeFieldList.entrySet()) {
                    Object value = likeField.getValue();
                    preparedStatement.setObject(paramIndex++, "%" + value + "%");
                }

                // Execute the query
                resultSet = preparedStatement.executeQuery();

                // Process the query result
                List<Map<String, Object>> retList = new ArrayList<>();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = resultSet.getObject(i);
                        row.put(columnName, value);
                    }
                    retList.add(row);
                }

                // Set the query result to the output parameter
                inputData.put("retList", retList);

            } catch (SQLException e) {
                // Handle any exceptions
                e.printStackTrace();
            } finally {
                // Close result set, statement, and connection
                try {
                    if (resultSet != null)
                        resultSet.close();
                    if (preparedStatement != null)
                        preparedStatement.close();
                    if (connection != null)
                        connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultMap;
    }

    private static String generateBatchSelectSql(String tableName, Map<String, Object> eqFieldList, Map<String, Object> likeFieldList) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM ").append(tableName).append(" WHERE ");

        // Append equal conditions
        for (Map.Entry<String, Object> eqField : eqFieldList.entrySet()) {
            String columnName = eqField.getKey();
            sqlBuilder.append(columnName).append(" = ? AND ");
        }

        // Append like conditions
        for (Map.Entry<String, Object> likeField : likeFieldList.entrySet()) {
            String columnName = likeField.getKey();
            sqlBuilder.append(columnName).append(" LIKE ? AND ");
        }

        // Remove the trailing "AND"
        sqlBuilder.delete(sqlBuilder.length() - 5, sqlBuilder.length());

        return sqlBuilder.toString();
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

    private Map<String,Object> convertSplitorStringToMapData(String input){
        Map<String, Object> map = new HashMap<>();
        String[] keyValuePairs = input.split(",");

        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String key = keyValuePairs[i];
            String value = keyValuePairs[i + 1];
            map.put(key, value);
        }
        return map;
    }





    @Override
    public Map<String,Object> excute(SldObject protocol,List<SldObject> tenantConfigObjects,Map<String,Object> inputData) {
        if(protocol.getObjectCode().equals("http")){
            return excuteHttpProtocol(protocol,tenantConfigObjects,inputData);
        }else if(protocol.getObjectCode().equals("db")){
            return excuteDbProtocol(protocol,tenantConfigObjects,inputData);
        }else if(protocol.getObjectCode().equals("singleData")){
            excuteSingleDataProtocol(protocol,tenantConfigObjects,inputData);
            return inputData;
        }
        return new HashMap<>();
    }
}
