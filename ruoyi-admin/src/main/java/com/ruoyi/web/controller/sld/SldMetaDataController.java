package com.ruoyi.web.controller.sld;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.controller.sld.converter.SldConnectorConverter;
import com.ruoyi.web.controller.sld.converter.SldFieldConverter;
import com.ruoyi.web.controller.sld.converter.SldObjectConverter;
import com.sld.business.domain.SldConnector;
import com.sld.business.domain.SldField;
import com.sld.business.domain.SldObject;
import com.sld.business.mapper.SldConnectorMapper;
import com.sld.business.mapper.SldFieldMapper;
import com.sld.business.mapper.SldObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sld-meta")
public class SldMetaDataController {
    @Resource
    private SldObjectMapper sldObjectMapper;

    @Resource
    private SldFieldMapper sldFieldMapper;

    @Resource
    private SldConnectorMapper sldConnectorMapper;

    /**
     * 创建对象
     */
    @PostMapping("/create-object")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createObject(@RequestBody Map<String,Object> req) throws Exception
    {
        if(req.containsKey("objectInfo")){
            Map<String,Object> sldObject = (Map<String,Object>)req.get("objectInfo");
            sldObjectMapper.insert(SldObjectConverter.convertToSldObject(sldObject));
            if(req.containsKey("objectFieldList")){
                List<Map<String,Object>> fieldLIst = (List<Map<String,Object>>)req.get("objectFieldList");
                for(Map<String,Object> oneField : fieldLIst){
                    SldField field = SldFieldConverter.convertToSldField(oneField);
                    sldFieldMapper.insert(field);
                }
            }
        }
        return AjaxResult.success();
    }

    private List<Map<String,Object>> buildFieldInfo(List<SldField> inputFieldList){
        List<Map<String,Object>> fieldListRetData = new ArrayList<>();
        List<Long> fieldTypeIdList = inputFieldList.stream().map(f->f.getFieldType()).collect(Collectors.toList());
        List<SldObject> objectList = sldObjectMapper.selectBatchIds(fieldTypeIdList);
        Map<Long,SldObject> objectIndex = objectList.stream().collect(Collectors.toMap(o->o.getId(),o->o));
        for(SldField oneField : inputFieldList){
            SldObject fieldTypeObject = objectIndex.get(oneField.getFieldType());
            if(fieldTypeObject.getIsAtom()==1){
                Map<String,Object> elm = new HashMap<>();
                elm.put("id",fieldTypeObject.getId());
                elm.put("name",fieldTypeObject.getName());
                fieldListRetData.add(elm);
            }else{
                Map<String, Object> cond = new HashMap<>();
                cond.put("belong_object_id",fieldTypeObject.getId());
                List<SldField> fieldList = sldFieldMapper.selectByMap(cond);
                Map<String,Object> elm = new HashMap<>();
                if(CollectionUtils.isNotEmpty(fieldList)){
                    List<Map<String,Object>> fieldInfoList = buildFieldInfo(fieldList);
                    elm.put("fieldList",fieldInfoList);
                }
                elm.put("id",fieldTypeObject.getId());
                elm.put("name",fieldTypeObject.getName());
            }
        }
        return fieldListRetData;
    }

    private List<Map<String,Object>> queryObjectList(Map<String,Object> req){
        List<Map<String,Object>> retData = new ArrayList<>();
        List<SldObject> objectList = sldObjectMapper.selectByMap(new HashMap<>());
        if(CollectionUtils.isNotEmpty(objectList)){
            for(SldObject object : objectList){
                Map<String,Object> elm = new HashMap<>();
                Map<String, Object> queryCond = new HashMap<>();
                queryCond.put("belong_object_id",object.getId());
                List<SldField> fieldList = sldFieldMapper.selectByMap(queryCond);
                if(CollectionUtils.isNotEmpty(fieldList)){
                    List<Map<String,Object>> fieldInfoList = buildFieldInfo(fieldList);
                    elm.put("fieldList",fieldInfoList);
                }
                elm.put("objectInfo",object);

                retData.add(elm);
            }
        }
        return retData;
    }

    private Map<String,Object> querySingleObject(Long objectId){
        Map<String,Object> cond = new HashMap<>();
        cond.put("id",objectId);
        List<SldObject> objectList = sldObjectMapper.selectByMap(cond);
        if(CollectionUtils.isNotEmpty(objectList)){
            for(SldObject object : objectList){
                Map<String,Object> elm = new HashMap<>();
                Map<String, Object> queryCond = new HashMap<>();
                queryCond.put("belong_object_id",object.getId());
                List<SldField> fieldList = sldFieldMapper.selectByMap(queryCond);
                if(CollectionUtils.isNotEmpty(fieldList)){
                    List<Map<String,Object>> fieldInfoList = buildFieldInfo(fieldList);
                    elm.put("fieldList",fieldInfoList);
                }
                elm.put("objectInfo",object);
                return elm;
            }
        }
        return new HashMap<>();
    }

    /**
     * 查询对象列表
     */
    @PostMapping("/object-detail")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult objectDetail(@RequestBody Map<String,Object> req) throws Exception
    {
        Map<String,Object> retData = new HashMap<>();
        if(req.containsKey("objectId")){
            retData = querySingleObject((Long)req.get("objectId"));
        }
        return AjaxResult.success(retData);
    }

    /**
     * 查询对象列表
     */
    @PostMapping("/list-object")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult listObject(@RequestBody Map<String,Object> req) throws Exception
    {
        List<Map<String,Object>> retData = queryObjectList(req);
        return AjaxResult.success(retData);
    }

    /**
     * 删除对象
     */
    @PostMapping("/del-object")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult delObject(@RequestBody Map<String,Object> req) throws Exception
    {
        if(req.containsKey("objectId")){
            Long objectId = (Long)req.get("objectId");
            Map<String,Object> oneObject = querySingleObject(objectId);
            if(oneObject.containsKey("fieldList")){
                List<SldField> fieldList = (List<SldField>)oneObject.get("fieldList");
                List<Long> ids = fieldList.stream().map(f->f.getId()).collect(Collectors.toList());
                sldFieldMapper.deleteBatchIds(ids);
            }
            sldObjectMapper.deleteById(objectId);
        }

        return AjaxResult.success();
    }

    /**
     * 创建连接器
     */
    @PostMapping("/create-connector")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createConnector(@RequestBody Map<String,Object> req) throws Exception
    {
        SldConnector po = SldConnectorConverter.convertToSldConnector(req);
        sldConnectorMapper.insert(po);
        return AjaxResult.success();
    }

    /**
     * 创建连接器
     */
    @PostMapping("/del-connector")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult delConnector(@RequestBody Map<String,Object> req) throws Exception
    {
        if(req.containsKey("connectorId")){
            Long connectorId = (Long)req.get("connectorId");
            sldConnectorMapper.deleteById(connectorId);
        }
        return AjaxResult.success();
    }

    private List<Map<String,Object>> queryConnectorList(Map<String,Object> req){
        List<Map<String,Object>> retData = new ArrayList<>();
        List<SldConnector> connectorList = sldConnectorMapper.selectByMap(new HashMap<>());
        if(CollectionUtils.isNotEmpty(connectorList)){
            for(SldConnector connector : connectorList){
                Map<String,Object> elm = new HashMap<>();
                elm.put("id",connector.getId());
                elm.put("connectorName",connector.getConnectorName());
                elm.put("connectorType",connector.getConnectorType());
                elm.put("desc",connector.getDesc());
                Long srcConId = connector.getSrcConObjectId();
                Long targetConId = connector.getTargetConObjectId();
                Long srcInputId = connector.getSrcInputObjectId();
                Long srcOutputId = connector.getSrcOutputObjectId();
                Long targetInputId = connector.getTargetInputObjectId();
                Long targetOutputId = connector.getTargetOutputObjectId();
                Long transId = connector.getTransformFunctionId();
                if(srcConId!=null){
                    Map<String,Object> data = querySingleConnector(srcConId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("srcConObject",data);
                    }
                }

                if(targetConId!=null){
                    Map<String,Object> data = querySingleConnector(targetConId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("targetConObject",data);
                    }
                }

                if(srcInputId!=null){
                    Map<String,Object> data = querySingleConnector(srcInputId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("srcInputObjectId",data);
                    }
                }

                if(srcOutputId!=null){
                    Map<String,Object> data = querySingleConnector(srcOutputId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("srcOutputObjectId",data);
                    }
                }

                if(targetInputId!=null){
                    Map<String,Object> data = querySingleConnector(targetInputId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("targetInputObjectId",data);
                    }
                }

                if(targetOutputId!=null){
                    Map<String,Object> data = querySingleConnector(targetOutputId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("targetOutputObjectId",data);
                    }
                }

                if(transId!=null){
                    Map<String,Object> data = querySingleConnector(transId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("transformFunctionId",data);
                    }
                }
                retData.add(elm);
            }
        }
        return retData;
    }

    private Map<String,Object> querySingleConnector(Long objectId){
        List<SldConnector> connectorList = sldConnectorMapper.selectByMap(new HashMap<>());
        if(CollectionUtils.isNotEmpty(connectorList)){
            for(SldConnector connector : connectorList){
                Map<String,Object> elm = new HashMap<>();
                elm.put("id",connector.getId());
                elm.put("connectorName",connector.getConnectorName());
                elm.put("connectorType",connector.getConnectorType());
                elm.put("desc",connector.getDesc());
                Long srcConId = connector.getSrcConObjectId();
                Long targetConId = connector.getTargetConObjectId();
                Long srcInputId = connector.getSrcInputObjectId();
                Long srcOutputId = connector.getSrcOutputObjectId();
                Long targetInputId = connector.getTargetInputObjectId();
                Long targetOutputId = connector.getTargetOutputObjectId();
                Long transId = connector.getTransformFunctionId();
                if(srcConId!=null){
                    Map<String,Object> data = querySingleConnector(srcConId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("srcConObject",data);
                    }
                }

                if(targetConId!=null){
                    Map<String,Object> data = querySingleConnector(targetConId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("targetConObject",data);
                    }
                }

                if(srcInputId!=null){
                    Map<String,Object> data = querySingleConnector(srcInputId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("srcInputObjectId",data);
                    }
                }

                if(srcOutputId!=null){
                    Map<String,Object> data = querySingleConnector(srcOutputId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("srcOutputObjectId",data);
                    }
                }

                if(targetInputId!=null){
                    Map<String,Object> data = querySingleConnector(targetInputId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("targetInputObjectId",data);
                    }
                }

                if(targetOutputId!=null){
                    Map<String,Object> data = querySingleConnector(targetOutputId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("targetOutputObjectId",data);
                    }
                }

                if(transId!=null){
                    Map<String,Object> data = querySingleConnector(transId);
                    if(MapUtils.isNotEmpty(data)){
                        elm.put("transformFunctionId",data);
                    }
                }
                return elm;
            }
        }
        return new HashMap<>();
    }

    /**
     * 查询连接器详情
     */
    @PostMapping("/connector-detail")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult listConnectorDetail(@RequestBody Map<String,Object> req) throws Exception
    {
        Map<String,Object> retData = new HashMap<>();
        if(req.containsKey("connectorId")){
            Long connectorId = (Long)req.get("connectorId");
            retData = querySingleConnector(connectorId);
        }
        return AjaxResult.success(retData);
    }

    /**
     * 查询连接器列表
     */
    @PostMapping("/list-connector")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult listConnector(@RequestBody Map<String,Object> req) throws Exception
    {
        List<Map<String,Object>> retData = queryConnectorList(req);
        return AjaxResult.success(retData);
    }
}
