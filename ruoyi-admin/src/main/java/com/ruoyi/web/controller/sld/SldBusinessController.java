package com.ruoyi.web.controller.sld;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.sld.business.converter.ObjectConverter;
import com.sld.business.domain.*;
import com.sld.business.mapper.*;
import com.sld.business.service.SldObjectService;
import com.sld.business.service.SldProtocolExecutorService;
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
import java.util.stream.Collectors;

/**
 * <p>
 * 业务表(可以承载用户级接口，中间表) 前端控制器
 * </p>
 *
 * @author hrz
 * @since 2023-10-30
 */
@RestController
@RequestMapping("/sld-business")
public class SldBusinessController {

    @Resource
    private SldBusinessConfigMapper sldBusinessConfigMapper;

    @Resource
    private SldBusinessMapper sldBusinessMapper;

    @Resource
    private SldObjectService sldObjectService;

    @Resource
    private SldObjectMapper sldObjectMapper;

    @Resource
    private SldProtocolOpenRecordMapper sldProtocolOpenRecordMapper;

    @Resource
    private SldProtocolSubOpenRecordMapper sldProtocolSubOpenRecordMapper;

    @Resource
    private SldProtocolExecutorService sldProtocolExecutorService;


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
     * 查询业务对象
     */
    @PostMapping("/list-business")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult listBusiness(@RequestBody SldBusiness req) throws Exception
    {
        QueryWrapper<SldBusiness> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(req);
        List<SldBusiness> businessList = sldBusinessMapper.selectList(queryWrapper);
        return AjaxResult.success(businessList);
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
        List<SldObject> attrObjects = new ArrayList<>();
        if(req.containsKey("attr")){
            attrObjects = sldObjectService.createAttrForObject((Map<String,Object>)req.get("attr"));
        }
        for(SldObject one : attrObjects){
            SldBusinessConfig elm = new SldBusinessConfig();
            elm.setBusinessId(businessId);
            elm.setObjectId(one.getId());
            sldBusinessConfigMapper.insert(elm);
        }
        return AjaxResult.success();
    }

    /**
     * 执行业务对象
     */
    @PostMapping("/excute-business")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult excuteBusiness(@RequestBody Map<String,Object> req) throws Exception
    {
        String businessId = (String)req.get("businessId");
        SldBusiness business = sldBusinessMapper.selectById(businessId);
        Map<String,Object> sldBusinessConifgReq = new HashMap<>();
        sldBusinessConifgReq.put("business_id",business.getId());
        List<SldBusinessConfig> busConifgList = sldBusinessConfigMapper.selectByMap(sldBusinessConifgReq);
        SldProtocolOpenRecord sldProtocolOpenRecord = sldProtocolOpenRecordMapper.selectById(business.getProtocolOpenRecordId());
        SldObject protocolObject = sldObjectMapper.selectById(sldProtocolOpenRecord.getProtocolObjectId());
        Map<String,Object> spsorReq = new HashMap<>();
        spsorReq.put("protocol_open_record_id",business.getProtocolOpenRecordId());
        List<SldProtocolSubOpenRecord> spsorList = sldProtocolSubOpenRecordMapper.selectByMap(spsorReq);
        List<String> tenantProtocolObjectIds = spsorList.stream().map(p->p.getObjectId()).collect(Collectors.toList());
        List<String> tenantConifgObjectIds = busConifgList.stream().map(p->p.getObjectId()).collect(Collectors.toList());
        List<String> mergedObjects = new ArrayList<>();
        mergedObjects.addAll(tenantProtocolObjectIds);
        mergedObjects.addAll(tenantConifgObjectIds);
        List<SldObject> objects = sldObjectMapper.selectBatchIds(mergedObjects);
        Map<String,Object> retData = sldProtocolExecutorService.excute(protocolObject,objects,new HashMap<>());
        return AjaxResult.success(retData);
    }
}
