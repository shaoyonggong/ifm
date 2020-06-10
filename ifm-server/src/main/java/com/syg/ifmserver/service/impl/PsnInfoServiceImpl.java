package com.syg.ifmserver.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syg.ifmapi.po.PsnInfoPo;
import com.syg.ifmserver.dao.PsnInfoMapper;
import com.syg.ifmserver.service.PsnInfoService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shaoyonggong
 * @since 2020-06-09
 */
@Service
public class PsnInfoServiceImpl extends ServiceImpl<PsnInfoMapper, PsnInfoPo> implements PsnInfoService {

}
