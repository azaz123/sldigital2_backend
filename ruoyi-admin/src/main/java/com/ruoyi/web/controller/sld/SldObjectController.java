package com.ruoyi.web.controller.sld;


import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.sld.business.domain.SldObject;
import com.sld.business.mapper.SldObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * <p>
 * 对象表 前端控制器
 * </p>
 *
 * @author hrz
 * @since 2023-10-16
 */
@Controller
@RequestMapping("/sld-object")
public class SldObjectController {

    @Resource
    private SldObjectMapper sldObjectMapper;

    /**
     * 创建对象
     */
    @PostMapping("/create-object")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult createObject(@RequestBody SldObject req) throws Exception
    {
        sldObjectMapper.insert(req);
        return AjaxResult.success();
    }

    /**
     * 删除对象
     */
    @PostMapping("/del-object")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult delObject(@RequestBody SldObject req) throws Exception
    {
        sldObjectMapper.deleteById(req);
        return AjaxResult.success();
    }

    /**
     * 删除对象
     */
    @PostMapping("/list-object")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult listObject(@RequestBody SldObject req) throws Exception
    {

        sldObjectMapper.deleteById(req);
        return AjaxResult.success();
    }

}
