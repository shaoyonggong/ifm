package com.syg.ifmserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syg.ifmserver.po.PsnInfoPo;
import com.syg.ifmserver.dao.PsnInfoMapper;
import com.syg.ifmserver.service.PsnInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

    @Autowired
    PsnInfoMapper psnInfoMapper;

    @Override
    public List<PsnInfoPo> searchList(String str) {
        PsnInfoPo psnInfoPo = new PsnInfoPo();

        return psnInfoMapper.searchList(psnInfoPo);
    }
}
