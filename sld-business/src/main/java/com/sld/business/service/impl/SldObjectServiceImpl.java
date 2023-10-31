package com.sld.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.AES;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.sld.business.converter.ObjectConverter;
import com.sld.business.domain.SldObject;
import com.sld.business.mapper.SldObjectMapper;
import com.sld.business.service.SldObjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * <p>
 * 对象表 服务实现类
 * </p>
 *
 * @author hrz
 * @since 2023-10-16
 */
@Service
public class SldObjectServiceImpl extends ServiceImpl<SldObjectMapper, SldObject> implements SldObjectService {

    @Resource
    SldObjectMapper sldObjectMapper;

    @Override
    public SldObject createObject(Map<String, Object> req) {
        SldObject po = ObjectConverter.convertToSldObject(req);
        sldObjectMapper.insert(po);
        return po;
    }

    @Override
    public SldObject createAttrForObject(Map<String, Object> req) {
        String objectId = (String)req.get("mainObject");
        Map<String,Object> attrObjectMap = (Map<String,Object>)req.get("attrObject");
        SldObject attrObject = ObjectConverter.convertToSldObject(attrObjectMap);
        SldObject mainObject = sldObjectMapper.selectById(objectId);
        attrObject.setBelongObjectId(mainObject.getId());
        sldObjectMapper.insert(attrObject);
        return attrObject;
    }

    @Override
    public SldObject createListObject(Map<String, Object> req) {
        Map<String,Object> typeObjectMap = (Map<String,Object>)req.get("typeObject");
        SldObject typeObject = ObjectConverter.convertToSldObject(typeObjectMap);
        Map<String,Object> listObjectMap = (Map<String,Object>)req.get("listObject");
        SldObject listObject = ObjectConverter.convertToSldObject(listObjectMap);
        listObject.setBelongObjectId(typeObject.getId());
        sldObjectMapper.insert(typeObject);
        sldObjectMapper.insert(listObject);
        return listObject;
    }

    @Override
    public List<SldObject> listSubObjects(String id,List<String> excludeIds){
        QueryWrapper<SldObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("belong_object_id",id);
        if(CollectionUtils.isNotEmpty(excludeIds)){
            queryWrapper.in("id",excludeIds);
        }
        List<SldObject> subObjects = sldObjectMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(subObjects)){
            return new ArrayList<>();
        }
        return subObjects;
    }

    @Override
    public Map<SldObject,SldObject> getKeyValueObject(String id){
        Map<SldObject,SldObject> retData = new HashMap<>();
        List<SldObject> keyObjects = listSubObjects(id,null);
        List<String> keyObjectIds = keyObjects.stream().map(p->p.getId()).collect(Collectors.toList());
        QueryWrapper<SldObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("belong_object_id",keyObjectIds);
        List<SldObject> valueObjectInfos = sldObjectMapper.selectList(queryWrapper);
        Map<String,SldObject> keyObjectIndex = keyObjects.stream().collect(Collectors.toMap(p->p.getId(),p->p));
        Map<String, SldObject> valueObjectIndex = valueObjectInfos.stream().collect(Collectors.toMap(p->p.getBelongObjectId(),p->p));
        for(Map.Entry<String,SldObject> one : keyObjectIndex.entrySet()){
            if(valueObjectIndex.containsKey(one.getKey())){
                SldObject value = valueObjectIndex.get(one.getKey());
                retData.put(one.getValue(),value);
            }
        }
        return retData;
     }


    @Override
    public SldObject createKeyValueForObject(Map<String, Object> req, Consumer<SldObject> callback) {
        String objectId = (String)req.get("mainObject");
        for(Map.Entry<String,Object> one : req.entrySet()){
            if(one.getKey().equals("mainObject")){
                continue;
            }
            SldObject keyObject = new SldObject();
            keyObject.setObjectCode(one.getKey());
            keyObject.setObjectStruct(1L);
            keyObject.setBelongObjectId(objectId);
            sldObjectMapper.insert(keyObject);

            SldObject valueObject = new SldObject();
            valueObject.setObjectCode(one.getKey() + "value");
            valueObject.setObjectStruct(1L);
            valueObject.setObjectValue((String)one.getValue());
            valueObject.setObjectValueType("string");
            valueObject.setBelongObjectId(keyObject.getId());
            sldObjectMapper.insert(valueObject);
        }
        return null;
    }

}
