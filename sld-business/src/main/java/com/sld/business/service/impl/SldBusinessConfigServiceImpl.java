package com.sld.business.service.impl;

import com.sld.business.domain.SldBusinessConfig;
import com.sld.business.mapper.SldBusinessConfigMapper;
import com.sld.business.service.SldBusinessConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务配置（租户级） 服务实现类
 * </p>
 *
 * @author hrz
 * @since 2023-10-30
 */
@Service
public class SldBusinessConfigServiceImpl extends ServiceImpl<SldBusinessConfigMapper, SldBusinessConfig> implements SldBusinessConfigService {

}
