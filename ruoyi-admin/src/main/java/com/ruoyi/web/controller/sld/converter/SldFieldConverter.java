package com.ruoyi.web.controller.sld.converter;

import com.sld.business.domain.SldField;
import com.sld.business.domain.SldObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SldFieldConverter {
    public static Map<String, Object> convertToMap(SldField sldField) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = sldField.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(sldField);
                map.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static SldField convertToSldField(Map<String, Object> map) {
        SldField sldField = new SldField();
        Class<?> clazz = sldField.getClass();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(sldField, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return sldField;
    }
}
