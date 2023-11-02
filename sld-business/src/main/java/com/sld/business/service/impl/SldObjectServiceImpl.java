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
    public List<SldObject> createAttrForObject(Map<String, Object> req) {
        List<SldObject> retData = new ArrayList<>();
        for(Map.Entry<String, Object> one : req.entrySet()){
            String mainObjectId = one.getKey();
            SldObject mainObject = sldObjectMapper.selectById(mainObjectId);
            SldObject valueObject = new SldObject();
            valueObject.setBelongObjectId(mainObjectId);
            valueObject.setObjectValueType("string");
            valueObject.setObjectCode(mainObject.getObjectCode() + "_value");
            valueObject.setObjectStruct(1L);
            valueObject.setObjectValue((String)one.getValue());
            sldObjectMapper.insert(valueObject);
            retData.add(valueObject);
        }
        return retData;
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
    public List<SldObject> listSubObjects(String id,List<String> includeIds){
        QueryWrapper<SldObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("belong_object_id",id);
        if(CollectionUtils.isNotEmpty(includeIds)){
            queryWrapper.in("id",includeIds);
        }
        List<SldObject> subObjects = sldObjectMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(subObjects)){
            return new ArrayList<>();
        }
        return subObjects;
    }

    @Override
    public Map<SldObject,SldObject> getKeyValueObject(String id,List<String> incluedIds){
        Map<SldObject,SldObject> retData = new HashMap<>();
        List<SldObject> keyObjects = listSubObjects(id,null);
        List<String> keyObjectIds = keyObjects.stream().map(p->p.getId()).collect(Collectors.toList());
        QueryWrapper<SldObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("belong_object_id",keyObjectIds);
        if(CollectionUtils.isNotEmpty(incluedIds)){
            queryWrapper.in("id",incluedIds);
        }
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
    public List<SldObject> createKeyValueForObject(Map<String, Object> req) {
        List<SldObject> retList = new ArrayList<>();
        for(Map.Entry<String,Object> one : req.entrySet()){
            String mainObjectId = one.getKey();
            Map<String,Object> kvInfo = (Map<String,Object>)one.getValue();
            for(Map.Entry<String,Object> kvOne: kvInfo.entrySet()){
                SldObject keyObject = new SldObject();
                keyObject.setObjectCode(kvOne.getKey());
                keyObject.setObjectStruct(1L);
                keyObject.setBelongObjectId(mainObjectId);
                sldObjectMapper.insert(keyObject);
                retList.add(keyObject);

                SldObject valueObject = new SldObject();
                valueObject.setObjectCode(kvOne.getKey() + "_value");
                valueObject.setObjectStruct(1L);
                valueObject.setObjectValue((String)kvOne.getValue());
                valueObject.setObjectValueType("string");
                valueObject.setBelongObjectId(keyObject.getId());
                sldObjectMapper.insert(valueObject);
                retList.add(valueObject);
            }

        }
        return retList;
    }

}
