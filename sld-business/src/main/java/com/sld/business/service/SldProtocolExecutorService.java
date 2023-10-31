package com.sld.business.service;

import com.sld.business.domain.SldObject;

import java.util.List;
import java.util.Map;

public interface SldProtocolExecutorService {
    public Map<String,Object> excute(SldObject protocol, List<SldObject> tenantConfigObjects, Map<String,Object> inputData);
}
