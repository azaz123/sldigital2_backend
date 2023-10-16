package com.ruoyi.web.controller.sld.converter;

import com.sld.business.domain.SldConnector;
import com.sld.business.domain.SldField;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SldConnectorConverter {
    public static Map<String, Object> convertToMap(SldConnector sldConnector) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = sldConnector.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(sldConnector);
                map.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static SldConnector convertToSldConnector(Map<String, Object> map) {
        SldConnector sldConnector = new SldConnector();
        Class<?> clazz = sldConnector.getClass();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(sldConnector, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return sldConnector;
    }
}
