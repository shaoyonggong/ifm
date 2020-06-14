package com.syg.ifmclient.client;

import com.syg.ifmcommon.constant.ExceptionConst;
import com.syg.ifmcommon.dto.IFMResultErrorInfo;
import com.syg.ifmcommon.result.IFMResult;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public class BaseResultClient {
    
    public  static IFMResult result(IFMResult result, Throwable e, String urlString){
        result.markFailed();
        IFMResultErrorInfo exception=new IFMResultErrorInfo();
        exception.setErrorCode(ExceptionConst.IFM_ERROR_CODE);
        exception.setMessage("调用"+urlString+"异常："+e.getMessage());
        result.setException(exception);
        return result;
    }

    public  static IFMResult result(IFMResult result, String e, String urlString){
        result.markFailed();
        IFMResultErrorInfo exception=new IFMResultErrorInfo();
        exception.setErrorCode(ExceptionConst.IFM_ERROR_CODE);
        exception.setMessage("调用"+urlString+"异常："+e);
        result.setException(exception);
        return result;
    }
}
