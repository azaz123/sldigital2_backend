package com.sld.business.converter;

import com.sld.business.domain.SldInterfaceOpenRecord;
import com.sld.business.domain.SldObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectConverter {
    public static Map<String, Object> convertToMap(Object object) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                map.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static SldObject convertToSldObject(Map<String, Object> map) {
        SldObject sldObject = new SldObject();
        Class<?> clazz = sldObject.getClass();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                if(field.getType().equals(Long.class)){
                    field.set(sldObject, Long.valueOf(value.toString()));
                }else{
                    field.set(sldObject, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(sldObject.getBelongObjectId()==null){
            sldObject.setBelongObjectId("-1");
        }
        return sldObject;
    }

    public static SldInterfaceOpenRecord convertToInterfaceOpenRecordObject(Map<String, Object> map) {
        SldInterfaceOpenRecord retData = new SldInterfaceOpenRecord();
        Class<?> clazz = retData.getClass();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                if(field.getType().equals(Long.class)){
                    field.set(retData, Long.valueOf(value.toString()));
                }else{
                    field.set(retData, value);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return retData;
    }
}
