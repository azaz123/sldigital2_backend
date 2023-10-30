package com.sld.business.service.impl;

import com.sld.business.domain.SldBusiness;
import com.sld.business.mapper.SldBusinessMapper;
import com.sld.business.service.SldBusinessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 业务表(可以承载用户级接口，中间表) 服务实现类
 * </p>
 *
 * @author hrz
 * @since 2023-10-30
 */
@Service
public class SldBusinessServiceImpl extends ServiceImpl<SldBusinessMapper, SldBusiness> implements SldBusinessService {

}
