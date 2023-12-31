package com.ruoyi.web.controller.sld;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.ruoyi.common.core.domain.AjaxResult;
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
@RequestMapping("/sld-protocol")
public class SldProtocolController {

    @Resource
    private SldObjectMapper sldObjectMapper;

    /**
     * 上架单对象数据处理协议
     */
    @PostMapping("/create-connector-protocol")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createConnectorProtocol(@RequestBody Map<String,Object> req) throws Exception{
        QueryWrapper<SldObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("class_code","protocol");
        queryWrapper.eq("object_code","connector");
        List<SldObject> exisitObject = sldObjectMapper.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(exisitObject)){
            return AjaxResult.error("已经存在connector协议");
        }
        //创建db对象
        SldObject dbQuery = new SldObject();
        dbQuery.setObjectCode("connector");
        dbQuery.setObjectStruct(1L);
        dbQuery.setClassCode("protocol");
        dbQuery.setBelongObjectId("-1");
        sldObjectMapper.insert(dbQuery);
        return AjaxResult.success();
    }

    /**
     * 上架单对象数据处理协议
     */
    @PostMapping("/create-single-data-process-protocol")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createSingleDataProcessProtocol(@RequestBody Map<String,Object> req) throws Exception{
        QueryWrapper<SldObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("class_code","protocol");
        queryWrapper.eq("object_code","singleDataProcess");
        List<SldObject> exisitObject = sldObjectMapper.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(exisitObject)){
            return AjaxResult.error("已经存在singleDataProcess协议");
        }
        //创建db对象
        SldObject dbQuery = new SldObject();
        dbQuery.setObjectCode("singleDataProcess");
        dbQuery.setObjectStruct(1L);
        dbQuery.setClassCode("protocol");
        dbQuery.setBelongObjectId("-1");
        sldObjectMapper.insert(dbQuery);
        return AjaxResult.success();
    }

    /**
     * 上架数据库查数据协议对象
     */
    @PostMapping("/create-db-protocol")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createDbQueryProtocol(@RequestBody Map<String,Object> req) throws Exception{
        QueryWrapper<SldObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("class_code","protocol");
        queryWrapper.eq("object_code","db");
        List<SldObject> exisitObject = sldObjectMapper.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(exisitObject)){
            return AjaxResult.error("已经存在db协议");
        }
        //创建db对象
        SldObject dbQuery = new SldObject();
        dbQuery.setObjectCode("db");
        dbQuery.setObjectStruct(1L);
        dbQuery.setClassCode("protocol");
        dbQuery.setBelongObjectId("-1");
        sldObjectMapper.insert(dbQuery);
        return AjaxResult.success();
    }

    /**
     * 上架http协议对象
     */
    @PostMapping("/create-http-protocol")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createHttpProtocol(@RequestBody Map<String,Object> req) throws Exception
    {
        QueryWrapper<SldObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("class_code","protocol");
        queryWrapper.eq("object_code","http");
        List<SldObject> exisitObject = sldObjectMapper.selectList(queryWrapper);
        if(CollectionUtils.isNotEmpty(exisitObject)){
            return AjaxResult.error("已经存在http协议");
        }
        //创建http对象
        SldObject http = new SldObject();
        http.setObjectCode("http");
        http.setObjectStruct(1L);
        http.setClassCode("protocol");
        http.setBelongObjectId("-1");
        sldObjectMapper.insert(http);

        //创建baseInfo对象
        SldObject baseInfo = new SldObject();
        baseInfo.setObjectCode("baseInfo");
        baseInfo.setObjectStruct(1L);
        baseInfo.setBelongObjectId(http.getId());
        sldObjectMapper.insert(baseInfo);

        //创建header对象
        SldObject header = new SldObject();
        header.setObjectCode("header");
        header.setObjectStruct(1L);
        header.setBelongObjectId(http.getId());
        sldObjectMapper.insert(header);

        // 创建param对象
        SldObject param = new SldObject();
        param.setObjectCode("param");
        param.setObjectStruct(1L);
        param.setBelongObjectId(http.getId());
        sldObjectMapper.insert(param);

        // 创建body对象
        SldObject body = new SldObject();
        body.setObjectCode("body");
        body.setObjectStruct(1L);
        body.setBelongObjectId(http.getId());
        sldObjectMapper.insert(body);

        // 创建retData对象
        SldObject retData = new SldObject();
        retData.setObjectCode("retData");
        retData.setObjectStruct(1L);
        retData.setBelongObjectId(http.getId());
        sldObjectMapper.insert(retData);
        return AjaxResult.success(http);
    }

    /**
     * 获取协议对象列表
     */
    @PostMapping("/get-protocol-list")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult getProtocolList(@RequestBody Map<String,Object> req) throws Exception
    {
        QueryWrapper<SldObject> subObjectQueryWrapper = new QueryWrapper<>();
        subObjectQueryWrapper.eq("class_code", req.get("classCode"));
        List<SldObject> objectList = sldObjectMapper.selectList(subObjectQueryWrapper);
        return AjaxResult.success(objectList);
    }

    /**
     * 获取指定code对象
     */
    @PostMapping("/get-spec-protocol-object")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult getSpecProtocolObject(@RequestBody Map<String,Object> req) throws Exception
    {
        Map<String, Object> retData = new HashMap<>();
        QueryWrapper<SldObject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("object_code", req.get("code"));
        SldObject rootObject = sldObjectMapper.selectOne(queryWrapper);

        if (rootObject != null) {
            String rootObjectId = rootObject.getId();

            QueryWrapper<SldObject> subObjectQueryWrapper = new QueryWrapper<>();
            subObjectQueryWrapper.eq("belong_object_id", rootObjectId);
            List<SldObject> subObjectList = sldObjectMapper.selectList(subObjectQueryWrapper);


            retData.put("rootObject", rootObject);
            retData.put("subObjectList", subObjectList);

        }
        return AjaxResult.success(retData);
    }

}
