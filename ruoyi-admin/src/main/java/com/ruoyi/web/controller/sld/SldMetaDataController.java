package com.ruoyi.web.controller.sld;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.web.controller.sld.converter.SldObjectConverter;
import com.sld.business.domain.SldObject;
import com.sld.business.mapper.SldObjectMapper;
import org.apache.commons.collections.CollectionUtils;
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
        }
        return AjaxResult.success();
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
            SldObject sldObject = sldObjectMapper.selectById((Long)req.get("objectId"));
            retData = SldObjectConverter.convertToMap(sldObject);
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
        List<SldObject> retData = sldObjectMapper.selectByMap(req);
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
            sldObjectMapper.deleteById(objectId);
        }
        return AjaxResult.success();
    }

}
