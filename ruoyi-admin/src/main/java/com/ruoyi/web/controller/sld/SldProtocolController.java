package com.ruoyi.web.controller.sld;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
     * 上架数据库查数据协议对象
     */
    @PostMapping("/create-db-query-protocol")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createDbQueryProtocol(@RequestBody Map<String,Object> req) throws Exception{
        //创建db对象
        SldObject dbQuery = new SldObject();
        dbQuery.setObjectCode("dbQuery");
        dbQuery.setObjectStruct(1L);
        dbQuery.setClassCode("protocol");
        sldObjectMapper.insert(dbQuery);

        SldObject dbAddress = new SldObject();
        dbAddress.setObjectCode("dbAddress");
        dbAddress.setObjectStruct(1L);
        dbAddress.setBelongObjectId(dbQuery.getId());
        sldObjectMapper.insert(dbAddress);

        SldObject dbUser = new SldObject();
        dbUser.setObjectCode("dbUser");
        dbUser.setObjectStruct(1L);
        dbUser.setBelongObjectId(dbQuery.getId());
        sldObjectMapper.insert(dbUser);

        SldObject dbPassWord = new SldObject();
        dbPassWord.setObjectCode("dbPassWord");
        dbPassWord.setObjectStruct(1L);
        dbPassWord.setBelongObjectId(dbQuery.getId());
        sldObjectMapper.insert(dbPassWord);

        SldObject dbSchema = new SldObject();
        dbSchema.setObjectCode("dbSchema");
        dbSchema.setObjectStruct(1L);
        dbSchema.setBelongObjectId(dbQuery.getId());
        sldObjectMapper.insert(dbSchema);

        SldObject dbAction = new SldObject();
        dbAction.setObjectCode("dbAction");
        dbAction.setObjectStruct(1L);
        dbAction.setBelongObjectId(dbQuery.getId());
        sldObjectMapper.insert(dbAction);

        SldObject dbReqObject = new SldObject();
        dbReqObject.setObjectCode("dbReqObject");
        dbReqObject.setObjectStruct(1L);
        dbReqObject.setBelongObjectId(dbQuery.getId());
        sldObjectMapper.insert(dbReqObject);

        SldObject dbRetObject = new SldObject();
        dbRetObject.setObjectCode("dbRetObject");
        dbRetObject.setObjectStruct(1L);
        dbRetObject.setBelongObjectId(dbQuery.getId());
        sldObjectMapper.insert(dbRetObject);
        return AjaxResult.success();
    }

    /**
     * 上架数据库写数据协议对象
     */
    @PostMapping("/create-db-write-protocol")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createDbWriteProtocol(@RequestBody Map<String,Object> req) throws Exception{
        //创建db对象
        SldObject dbWrite = new SldObject();
        dbWrite.setObjectCode("dbWrite");
        dbWrite.setObjectStruct(1L);
        dbWrite.setClassCode("protocol");
        sldObjectMapper.insert(dbWrite);

        SldObject dbAddress = new SldObject();
        dbAddress.setObjectCode("dbAddress");
        dbAddress.setObjectStruct(1L);
        dbAddress.setBelongObjectId(dbWrite.getId());
        sldObjectMapper.insert(dbAddress);

        SldObject dbUser = new SldObject();
        dbUser.setObjectCode("dbUser");
        dbUser.setObjectStruct(1L);
        dbUser.setBelongObjectId(dbWrite.getId());
        sldObjectMapper.insert(dbUser);

        SldObject dbPassWord = new SldObject();
        dbPassWord.setObjectCode("dbPassWord");
        dbPassWord.setObjectStruct(1L);
        dbPassWord.setBelongObjectId(dbWrite.getId());
        sldObjectMapper.insert(dbPassWord);

        SldObject dbSchema = new SldObject();
        dbSchema.setObjectCode("dbSchema");
        dbSchema.setObjectStruct(1L);
        dbSchema.setBelongObjectId(dbWrite.getId());
        sldObjectMapper.insert(dbSchema);

        SldObject dbAction = new SldObject();
        dbAction.setObjectCode("dbAction");
        dbAction.setObjectStruct(1L);
        dbAction.setBelongObjectId(dbWrite.getId());
        sldObjectMapper.insert(dbAction);

        SldObject dbReqObject = new SldObject();
        dbReqObject.setObjectCode("dbReqObject");
        dbReqObject.setObjectStruct(1L);
        dbReqObject.setBelongObjectId(dbWrite.getId());
        sldObjectMapper.insert(dbReqObject);

        SldObject dbRetObject = new SldObject();
        dbRetObject.setObjectCode("dbRetObject");
        dbRetObject.setObjectStruct(1L);
        dbRetObject.setBelongObjectId(dbWrite.getId());
        sldObjectMapper.insert(dbRetObject);
        return AjaxResult.success();
    }

    /**
     * 上架http协议对象
     */
    @PostMapping("/create-http-protocol")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createHttpProtocol(@RequestBody Map<String,Object> req) throws Exception
    {
        //创建http对象
        SldObject http = new SldObject();
        http.setObjectCode("http");
        http.setObjectStruct(1L);
        http.setClassCode("protocol");
        sldObjectMapper.insert(http);
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
