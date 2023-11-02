package com.ruoyi.web.controller.sld;


import com.ruoyi.common.core.domain.AjaxResult;
import com.sld.business.converter.ObjectConverter;
import com.sld.business.domain.SldProtocolOpenRecord;
import com.sld.business.domain.SldProtocolSubOpenRecord;
import com.sld.business.domain.SldObject;
import com.sld.business.mapper.SldProtocolOpenRecordMapper;
import com.sld.business.mapper.SldProtocolSubOpenRecordMapper;
import com.sld.business.mapper.SldObjectMapper;
import com.sld.business.service.SldObjectService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 接口开通记录 前端控制器
 * </p>
 *
 * @author hrz
 * @since 2023-10-19
 */
@RestController
@RequestMapping("/sld-protocol-open-record")
public class SldProtocolOpenRecordController {

    @Resource
    private SldObjectMapper sldObjectMapper;

    @Resource
    private SldProtocolOpenRecordMapper sldProtocolOpenRecordMapper;

    @Resource
    private SldProtocolSubOpenRecordMapper sldProtocolSubOpenRecordMapper;

    @Resource
    private SldObjectService sldObjectService;

    /**
     * 获取协议需要配置的对象
     */
    @PostMapping("/get-protocol-need-config-object")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult getProtocolNeedConfigObject(@RequestBody Map<String,Object> req) throws Exception{
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("protocol_open_record_id",(String)req.get("protocol_open_record_id"));
        columnMap.put("is_need_config",2);
        List<SldProtocolSubOpenRecord> needConfigObject = sldProtocolSubOpenRecordMapper.selectByMap(columnMap);
        List<String> needConifgObjectIds = needConfigObject.stream().map(p->p.getObjectId()).collect(Collectors.toList());
        List<SldObject> needConfigObjectInfos = sldObjectMapper.selectBatchIds(needConifgObjectIds);
        List<String> needConifgBelongObjectIds = needConfigObjectInfos.stream().map(p->p.getBelongObjectId()).collect(Collectors.toList());
        List<SldObject> needConfigBelongObjectInfos = sldObjectMapper.selectBatchIds(needConifgBelongObjectIds);
        Map<String,SldObject> belongObjectIndex = needConfigBelongObjectInfos.stream().collect(Collectors.toMap(p->p.getId(),p->p));
        List<Map<String,Object>> retData = new ArrayList<>();
        for(SldObject oneConfig : needConfigObjectInfos){
            Map<String,Object> elm = new HashMap<>();
            SldObject belongObject = belongObjectIndex.get(oneConfig.getBelongObjectId());
            elm.put("objectId", oneConfig.getId());
            elm.put("belongObjectId",oneConfig.getBelongObjectId());
            elm.put("belongObjectName",belongObject.getObjectCode());
            retData.add(elm);
        }
        return AjaxResult.success(retData);
    }


    /**
     * 更新协议
     */
    @PostMapping("/update-protocol")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateProtocol(@RequestBody Map<String,Object> req) throws Exception{
        String openRecordId = (String)req.get("protoclOpenRecordId");
        List<SldObject> addAttrObjects = new ArrayList<>();
        if(req.containsKey("addAttr")){
            addAttrObjects = sldObjectService.createAttrForObject((Map<String,Object>)req.get("addAttr"));
        }

        List<SldObject> addKvObjects = new ArrayList<>();
        if(req.containsKey("addKvAttr")){
            addKvObjects = sldObjectService.createKeyValueForObject((Map<String,Object>)req.get("addKvAttr"));
        }

        for(SldObject one : addKvObjects){
            SldProtocolSubOpenRecord elm = new SldProtocolSubOpenRecord();
            elm.setProtocolOpenRecordId(openRecordId);
            if(one.getObjectValue()!=null && one.getObjectValue().equals("---")){
                elm.setObjectId(one.getId());
                elm.setIsNeedConfig(2L);
            }else{
                elm.setObjectId(one.getId());
                elm.setIsNeedConfig(1L);
            }
            sldProtocolSubOpenRecordMapper.insert(elm);
        }

        for(SldObject one : addAttrObjects){
            SldProtocolSubOpenRecord elm = new SldProtocolSubOpenRecord();
            elm.setId(openRecordId);
            if(one.getObjectValue()!=null && one.getObjectValue().equals("---")){
                elm.setObjectId(one.getId());
                elm.setIsNeedConfig(2L);
            }else{
                elm.setObjectId(one.getId());
                elm.setIsNeedConfig(1L);
            }
            sldProtocolSubOpenRecordMapper.insert(elm);
        }

        if(req.containsKey("delAttr")){
            List<String> delAttrIds = (List<String>)req.get("delAttr");
            sldObjectMapper.deleteBatchIds(delAttrIds);
            for(String one : delAttrIds){
                Map<String,Object> delMap = new HashMap<>();
                delMap.put("object_id",one);
                sldProtocolSubOpenRecordMapper.deleteByMap(delMap);
            }
        }

        if(req.containsKey("upValueAttr")){
            Map<String,Object> upValueInfo = (Map<String,Object>)req.get("upValueAttr");
            for(Map.Entry<String,Object> one : upValueInfo.entrySet()){
                SldObject so = new SldObject();
                so.setId(one.getKey());
                so.setObjectValue((String)one.getValue());
                sldObjectMapper.updateById(so);
            }
        }


        return AjaxResult.success();
    }


    /**
     * 上架协议
     */
    @PostMapping("/release-protocol")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult releaseProtocol(@RequestBody Map<String,Object> req) throws Exception{
        SldProtocolOpenRecord openRecord = ObjectConverter.convertToInterfaceOpenRecordObject((Map<String,Object>)req.get("openRecord"));
        openRecord.setCreateDate(new Date());
        sldProtocolOpenRecordMapper.insert(openRecord);
        Map<String,SldObject> index = new HashedMap();
        List<SldObject> kvObjects = new ArrayList<>();
        if(req.containsKey("kvAttr")){
            kvObjects = sldObjectService.createKeyValueForObject((Map<String,Object>)req.get("kvAttr"));
        }
        List<SldObject> attrObjects = new ArrayList<>();
        if(req.containsKey("attr")){
            attrObjects = sldObjectService.createAttrForObject((Map<String,Object>)req.get("attr"));
        }
        for(SldObject one : kvObjects){
            SldProtocolSubOpenRecord elm = new SldProtocolSubOpenRecord();
            elm.setProtocolOpenRecordId(openRecord.getId());
            if(one.getObjectValue()!=null && one.getObjectValue().equals("---")){
                elm.setObjectId(one.getId());
                elm.setIsNeedConfig(2L);
            }else{
                elm.setObjectId(one.getId());
                elm.setIsNeedConfig(1L);
            }
            sldProtocolSubOpenRecordMapper.insert(elm);
        }

        for(SldObject one : attrObjects){
            SldProtocolSubOpenRecord elm = new SldProtocolSubOpenRecord();
            elm.setId(openRecord.getId());
            if(one.getObjectValue()!=null && one.getObjectValue().equals("---")){
                elm.setObjectId(one.getId());
                elm.setIsNeedConfig(2L);
            }else{
                elm.setObjectId(one.getId());
                elm.setIsNeedConfig(1L);
            }
            sldProtocolSubOpenRecordMapper.insert(elm);
        }
        return AjaxResult.success();
    }
}
