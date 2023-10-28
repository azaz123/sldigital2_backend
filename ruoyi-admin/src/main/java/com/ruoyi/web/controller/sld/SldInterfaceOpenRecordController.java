package com.ruoyi.web.controller.sld;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.common.core.domain.AjaxResult;
import com.sld.business.converter.ObjectConverter;
import com.sld.business.domain.SldInterfaceOpenRecord;
import com.sld.business.domain.SldInterfaceSubOpenRecord;
import com.sld.business.domain.SldObject;
import com.sld.business.mapper.SldInterfaceOpenRecordMapper;
import com.sld.business.mapper.SldInterfaceSubOpenRecordMapper;
import com.sld.business.mapper.SldObjectMapper;
import com.sld.business.service.SldObjectService;
import com.sld.business.util.ObjectHelper;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 接口开通记录 前端控制器
 * </p>
 *
 * @author hrz
 * @since 2023-10-19
 */
@RestController
@RequestMapping("/sld-interface-open-record")
public class SldInterfaceOpenRecordController {

    @Resource
    private SldObjectMapper sldObjectMapper;

    @Resource
    private SldInterfaceOpenRecordMapper sldInterfaceOpenRecordMapper;

    @Resource
    private SldInterfaceSubOpenRecordMapper sldInterfaceSubOpenRecordMapper;

    @Resource
    private SldObjectService sldObjectService;


    /**
     * 上架接口
     */
    @PostMapping("/create-interface")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createInterface(@RequestBody Map<String,Object> req) throws Exception{
        SldInterfaceOpenRecord openRecord = ObjectConverter.convertToInterfaceOpenRecordObject((Map<String,Object>)req.get("openRecord"));
        sldInterfaceOpenRecordMapper.insert(openRecord);
        Map<String,SldObject> index = new HashedMap();
        sldObjectService.createKeyValueForObject((Map<String,Object>)req.get("httpAttr"),(sldObject) -> {
            index.put(sldObject.getBelongObjectId(),sldObject);
        });
        List<String> valueObjectIdList = new ArrayList<>(index.keySet());
        List<SldObject> valueObjects = sldObjectMapper.selectBatchIds(valueObjectIdList);
        for(SldObject oneValue : valueObjects){
            if(index.containsKey(oneValue.getId())){
                SldObject keyObject = index.get(oneValue.getId());
                SldInterfaceSubOpenRecord elm = new SldInterfaceSubOpenRecord();
                elm.setId(openRecord.getId());
                if(oneValue.getObjectValue()!=null && oneValue.getObjectValue().equals("---")){
                    elm.setObjectId(keyObject.getId());
                    elm.setIsNeedConfig(2L);
                }else{
                    elm.setObjectId(keyObject.getId());
                    elm.setIsNeedConfig(1L);
                }
                sldInterfaceSubOpenRecordMapper.insert(elm);
            }
        }
        return AjaxResult.success();
    }
}
