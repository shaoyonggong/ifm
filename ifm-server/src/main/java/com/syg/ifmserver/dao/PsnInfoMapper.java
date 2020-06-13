package com.syg.ifmserver.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syg.ifmserver.po.PsnInfoPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author shaoyonggong
 * @since 2020-06-09
 */
@Mapper
@Repository
public interface PsnInfoMapper extends BaseMapper<PsnInfoPo> {

    List<PsnInfoPo> searchList(PsnInfoPo psnInfoPo);
}
