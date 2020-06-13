package com.syg.ifmserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.syg.ifmserver.po.PsnInfoPo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author shaoyonggong
 * @since 2020-06-09
 */
public interface PsnInfoService extends IService<PsnInfoPo> {

    List<PsnInfoPo> searchList(String str);
}
