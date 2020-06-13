package com.syg.ifmserver.spi.psninfo;

import com.syg.ifmserver.po.PsnInfoPo;
import com.syg.ifmserver.service.PsnInfoService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/13
 */
@Api(tags = {"分类：人员信息","人员信息列表查询"})
@RestController
@RequestMapping(value = "/search/")
public class SearchPsnInfoListSpi {

    @Autowired
    private PsnInfoService psnInfoService;

    @PostMapping("/")
    public List<PsnInfoPo> search(String str){

        System.out.println("qqqqqqqqqqqqqqqq");
        return psnInfoService.searchList(str);
    }
}
