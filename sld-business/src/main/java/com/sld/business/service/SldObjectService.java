package com.sld.business.service;

import com.sld.business.domain.SldObject;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * <p>
 * 对象表 服务类
 * </p>
 *
 * @author hrz
 * @since 2023-10-16
 */
public interface SldObjectService extends IService<SldObject> {
    public SldObject createObject(Map<String,Object> req);

    public List<SldObject> createAttrForObject(Map<String,Object> req);


    public SldObject createListObject(Map<String,Object> req);

    public List<SldObject> createKeyValueForObject(Map<String,Object> req);

    public List<SldObject> listSubObjects(String id,List<String> includeIds);

    public Map<SldObject,SldObject> getKeyValueObject(String id,List<String> includeIds);
}
