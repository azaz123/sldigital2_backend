package com.sld.business.util;

import com.sld.business.domain.SldObject;
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

    public static Map<String,Object> buildObject(SldObject rootObject,List<SldObject> subObjectList){
        Map<String,Object> retData = new HashMap<>();
        retData.put("rootObject",rootObject);
        retData.put("objectList",subObjectList);
        return retData;
    }


}
