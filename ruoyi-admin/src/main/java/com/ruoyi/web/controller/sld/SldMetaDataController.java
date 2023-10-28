package com.ruoyi.web.controller.sld;

import com.ruoyi.common.core.domain.AjaxResult;
import com.sld.business.converter.ConvertMapToUnderLine;
import com.sld.business.converter.ObjectConverter;
import com.sld.business.domain.SldObject;
import com.sld.business.mapper.SldObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
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
            sldObjectMapper.insert(ObjectConverter.convertToSldObject(sldObject));
        }
        return AjaxResult.success();
    }

    /**
     * 更新对象
     */
    @PostMapping("/update-object")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult updateObject(@RequestBody Map<String,Object> req) throws Exception
    {
        if(req.containsKey("objectInfo")){
            Map<String,Object> sldObject = (Map<String,Object>)req.get("objectInfo");
            sldObjectMapper.updateById(ObjectConverter.convertToSldObject(sldObject));
        }
        return AjaxResult.success();
    }


    /**
     * 查询对象列表
     */
    @PostMapping("/detail-object")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult objectDetail(@RequestBody Map<String,Object> req) throws Exception
    {
        Map<String,Object> retData = new HashMap<>();
        if(req.containsKey("objectId")){
            SldObject sldObject = sldObjectMapper.selectById((String)req.get("objectId"));
            retData = ObjectConverter.convertToMap(sldObject);
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
        List<SldObject> retData = sldObjectMapper.selectByMap(ConvertMapToUnderLine.convertKeysToUnderscore(req));
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
            String objectId = (String)req.get("objectId");
            sldObjectMapper.deleteById(objectId);
        }
        return AjaxResult.success();
    }



}
