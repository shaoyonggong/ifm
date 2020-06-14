package com.syg.ifmcommon.api;

import com.syg.ifmcommon.result.IFMResult;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public interface IFMApi<T, D> {

    public IFMResult<D> process(T param) throws Exception;
}
