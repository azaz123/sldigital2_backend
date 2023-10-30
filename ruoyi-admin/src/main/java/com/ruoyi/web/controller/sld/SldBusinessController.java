package com.ruoyi.web.controller.sld;


import com.ruoyi.common.core.domain.AjaxResult;
import com.sld.business.converter.ObjectConverter;
import com.sld.business.domain.SldBusiness;
import com.sld.business.domain.SldObject;
import com.sld.business.mapper.SldBusinessMapper;
import com.sld.business.mapper.SldObjectMapper;
import com.sld.business.service.SldObjectService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 业务表(可以承载用户级接口，中间表) 前端控制器
 * </p>
 *
 * @author hrz
 * @since 2023-10-30
 */
@Controller
@RequestMapping("/sld-business")
public class SldBusinessController {

    @Resource
    private SldBusinessMapper sldBusinessMapper;

    @Resource
    private SldObjectService sldObjectService;

    @Resource
    private SldObjectMapper sldObjectMapper;

    /**
     * 创建业务
     */
    @PostMapping("/create-business")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createBusiness(@RequestBody SldBusiness req) throws Exception
    {
        sldBusinessMapper.insert(req);
        return AjaxResult.success();
    }

    /**
     * 配置业务
     */
    @PostMapping("/config-business")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult configBusiness(@RequestBody Map<String,Object> req) throws Exception
    {
        String businessId = (String)req.get("businessId");
        SldBusiness business = sldBusinessMapper.selectById(businessId);
        Map<String,Object> configData = (Map<String,Object>)req.get("businessConfig");
        for(Map.Entry<String,Object> one: configData.entrySet()){
            SldObject rootValueObject = sldObjectMapper.selectById(one.getKey());
            SldObject configValueObject = new SldObject();
            configValueObject.setBelongObjectId(rootValueObject.getId());
            configValueObject.setObjectValue((String)one.getValue());
            configValueObject.setObjectCode(rootValueObject.getObjectCode() + "_value");
            Map<String,Object> addAttrForObject = new HashMap<>();
            addAttrForObject.put("mainObject",rootValueObject.getId());
            addAttrForObject.put("attrObject", ObjectConverter.convertToMap(configValueObject));
            sldObjectService.createAttrForObject(addAttrForObject);
        }
        return AjaxResult.success();
    }
}
