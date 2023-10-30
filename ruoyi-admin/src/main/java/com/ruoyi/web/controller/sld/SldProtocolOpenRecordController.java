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
import java.util.ArrayList;
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
@RequestMapping("/sld-protocol-open-record")
public class SldProtocolOpenRecordController {

    @Resource
    private SldObjectMapper sldObjectMapper;

    @Resource
    private SldProtocolOpenRecordMapper sldInterfaceOpenRecordMapper;

    @Resource
    private SldProtocolSubOpenRecordMapper sldInterfaceSubOpenRecordMapper;

    @Resource
    private SldObjectService sldObjectService;




    /**
     * 上架协议
     */
    @PostMapping("/release-protocol")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult releaseProtocol(@RequestBody Map<String,Object> req) throws Exception{
        SldProtocolOpenRecord openRecord = ObjectConverter.convertToInterfaceOpenRecordObject((Map<String,Object>)req.get("openRecord"));
        sldInterfaceOpenRecordMapper.insert(openRecord);
        Map<String,SldObject> index = new HashedMap();
        sldObjectService.createKeyValueForObject((Map<String,Object>)req.get("attr"),(sldObject) -> {
            index.put(sldObject.getBelongObjectId(),sldObject);
        });
        List<String> valueObjectIdList = new ArrayList<>(index.keySet());
        List<SldObject> valueObjects = sldObjectMapper.selectBatchIds(valueObjectIdList);
        for(SldObject oneValue : valueObjects){
            if(index.containsKey(oneValue.getId())){
                SldObject keyObject = index.get(oneValue.getId());
                SldProtocolSubOpenRecord elm = new SldProtocolSubOpenRecord();
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
