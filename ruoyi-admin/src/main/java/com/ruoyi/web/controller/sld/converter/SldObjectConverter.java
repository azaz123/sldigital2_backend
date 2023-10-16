package com.ruoyi.web.controller.sld.converter;

import com.sld.business.domain.SldObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SldObjectConverter {
    public static Map<String, Object> convertToMap(SldObject sldObject) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = sldObject.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(sldObject);
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
                field.set(sldObject, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return sldObject;
    }
}
