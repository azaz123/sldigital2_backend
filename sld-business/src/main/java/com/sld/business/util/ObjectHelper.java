package com.sld.business.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectHelper {
    public static Boolean judgeStrIsNull(String str){
        if(str==null || str.isEmpty() || str.equals("#######")){
            return true;
        }
        return false;
    }

    public static Map<String,Object> getActionObject(Map<String,Object> data){
        return getMapObject(data,"objectActionInfo");
    }

    public static Map<String,Object> getObjectField(Map<String,Object> data,String fieldEngName){
        if(data.containsKey("fieldList")){
            List<Map<String,Object>> fieldList = (List<Map<String,Object>> )data.get("fieldList");
            for(Map<String,Object> oneField : fieldList){
                if(oneField.containsKey(fieldEngName)){
                    return oneField;
                }
            }
        }
        return new HashMap<>();
    }

    public static List<Map<String,Object>> getObjectFieldList(Map<String,Object> data){
        if(data.containsKey("fieldList")){
            List<Map<String,Object>> fieldList = (List<Map<String,Object>> )data.get("fieldList");
            return fieldList;
        }
        return new ArrayList<>();
    }

    public static String getFieldValue(Map<String,Object> data,String fieldEngName){
        Map<String,Object> targetField = getObjectField(data,fieldEngName);
        if(targetField.containsKey(fieldEngName)){
            Map<String,Object> fieldInfo = (Map<String,Object>)targetField.get(targetField);
            return (String)fieldInfo.get("value");
        }
        return "#######";
    }

    public static Map<String,Object> getMapObject(Map<String,Object> data,String key){
        if(data.containsKey(key)){
            return (Map<String,Object>)data.get(key);
        }
        return new HashMap<>();
    }

    public static List<Map<String,Object>> getListObject(Map<String,Object> data, String key){
        if(data.containsKey(key)){
            return (List<Map<String,Object>>)data.get(key);
        }
        return new ArrayList<>();
    }
}
