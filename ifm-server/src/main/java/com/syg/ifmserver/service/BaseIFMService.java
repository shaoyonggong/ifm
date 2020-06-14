package com.syg.ifmserver.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import com.syg.ifmcommon.api.IFMApi;
import com.syg.ifmcommon.constant.ExceptionConst;
import com.syg.ifmcommon.dto.IFMResultErrorInfo;
import com.syg.ifmcommon.dto.ServiceLog;
import com.syg.ifmcommon.dto.vaildate.VaildataUtil;
import com.syg.ifmcommon.exception.IFMException;
import com.syg.ifmcommon.exception.IFMServeRejectException;
import com.syg.ifmcommon.exception.IFMServeRejectRuntimeException;
import com.syg.ifmcommon.result.IFMResult;
import com.syg.ifmserver.broker.ResultSend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
@Component
public abstract class BaseIFMService<T,D> implements IFMApi<T,D> {
    @Value("${spring.application.name}")
    String serverName;

    @Value("${app_env:local}")
    String appEnv;

//    @Autowired
//    LogSend logSend;

    @Resource
    ResultSend resultSend;

    private ExecutorService executor = Executors.newCachedThreadPool();


    protected static Logger logger = LoggerFactory.getLogger(BaseIFMService.class);


    @FunctionalInterface
    public interface InnerService<T, D> {
        IFMResult<D> doService(T param) throws Exception;
    }

    public IFMResult<D> initIFMResult(){
        IFMResult<D> result = new IFMResult<>();
        return result;
    }
    //
    public IFMResult<D> initIFMResult(D body){
        IFMResult<D> result = initIFMResult();

        result.setBody(body);
        return result;
    }

    public IFMResult<D> initIFMResult(D body, Integer totalCount){
        IFMResult<D> result = initIFMResult();
        result.setBody(body);
        result.setTotalCount(totalCount);
        return result;
    }

    /**
     * 通用处理业务代码。
     *
     * @param methodName
     * @param param
     * @param is
     * @return
     */
    protected void dealService(
            String methodName,
            IFMResult<D> ifmResult,
            T param,
            ServiceLog track,
            HttpHeaders headers,
            BindingResult bindingResult,
            InnerService<T, D> is)  {

        List<String> sessionId = headers.get("cookie");
        if(sessionId!=null&&sessionId.size()>0){
            String cookie=sessionId.get(0).endsWith(";")?sessionId.get(0):sessionId.get(0)+";";
            if(cookie.contains("sessionId=")){
                String result = cookie.replaceAll("([\\s\\S]*\\s*sessionId=\\s*)([\\s\\S]*?)(;\\s*[\\s\\S]*)", "$2");
                MDC.put("sessionId",result);
            }
        }
        String tracingId=headers.get("tracingid")==null?null:headers.get("tracingid").get(0);
        if(StringUtils.isNotEmpty(tracingId) && headers.get("recordnumber")!=null){
            track.setTracingId(tracingId);
            track.setRecordNumber(Integer.valueOf(headers.get("recordnumber").get(0)));
            track.setIsSync(headers.get("isSync").get(0));
            MDC.put("tracingId",tracingId);
            MDC.put("recordNumber", String.valueOf(track.getRecordNumber()));
        }else{
            track.setIsSync("0");
            track.setRecordNumber(1);
            track.setTracingId(MDC.get("traceRootId"));
            MDC.put("tracingId",track.getTracingId());
            MDC.put("recordNumber", String.valueOf(track.getRecordNumber()));
        }
        methodName = serverName+ "/"+methodName;
        final String paramStr= JSON.toJSONString(param);
        logger.info(String.format("<------%s:%s ----Started",methodName, paramStr));
        //保存原始参数
        ifmResult.setOriginalParam(paramStr);
//        log
        track.setPath(methodName.replace("//","/"));
        track.setServerName(serverName);
        track.setAppEnv(appEnv);
        track.setParam(paramStr);
        track.setState(ServiceLog.status_enum.NEW);
        this.syncAuth(track);


        try {
            //验证异常注解
            VaildataUtil<T> vdataUtil=new VaildataUtil();
            IFMResult<T> ifm = new IFMResult();
            ifm.setBody(param);
            ifm = vdataUtil.verification(ifm);

            // 到了这一步发现参数验证没有通过,则直接收集出错信息并打断处理.
            if (bindingResult.hasFieldErrors() || ifm.getValidationErrors().size() !=0) {
                ifmResult.setValidationErrors(ifm.getValidationErrors());
                logger.info(String.format("<------%s: ----Service Refused", methodName));
                ifmResult.markRefused();
                track.setState(ServiceLog.status_enum.REFUSED);
                track.setEndTime(System.currentTimeMillis());
                track.setExMsg(JSON.toJSONString(ifmResult.getValidationErrors()));
                track.setExType("Validator");
                this.syncAuth(track);
                return;
            }

            // 执行服务处理
            ifmResult.merge(is.doService(param));


            // 如果没有错误,就标记成功,就此处理结束.
            if ( IFMResult.status_enum.SUCCESS.equals(ifmResult.getStatus()) ) {
                logger.info(String.format("<------%s: ----Successful.", methodName));
                ifmResult.markSuccess();
                track.setState(ServiceLog.status_enum.DONE);
                track.setEndTime(System.currentTimeMillis());
                this.syncAuth(track);
            } else {
                logger.info(String.format("<------%s: ----Service Reject Refused", methodName));
                ifmResult.markRefused();
                //log
                track.setState(ServiceLog.status_enum.REFUSED);
                track.setEndTime(System.currentTimeMillis());
                track.setExMsg(ifmResult.getException()==null?JSON.toJSONString(ifmResult.getValidationErrors()):JSON.toJSONString(ifmResult.getException().getMessage()));
                track.setStacks(JSON.toJSONString(Thread.currentThread().getStackTrace()));
                track.setExType("IFMServeRejectException");
                this.syncAuth(track);
            }


        }catch (IFMServeRejectException e){

            //打印告警异常信息
            String buf = String.format("<------%s:%s ----ServiceRejectException." + System.currentTimeMillis(), methodName, this.getClass().getName()) +
                    String.format("IFMServeRejectException:%s", e.getMessage() == null ? "" : e.getMessage());
            logger.warn(buf);
            //将异常信息封装到ifmresult中
            ifmResult.setException(new IFMResultErrorInfo(ExceptionConst.REJECT_ERROR_CODE, String.format(getClass().getName()+":服务拒绝异常:%s",e.getMessage()==null ?"":e.getMessage()),e.getMessageStacks()));
            ifmResult.markRefused();
            //log
            track.setState(ServiceLog.status_enum.REFUSED);
            track.setEndTime(System.currentTimeMillis());
            track.setExMsg(e.getMessage());
            track.setExType("IFMServeRejectException");
            track.setStacks(JSON.toJSONString(e.getStackTrace()));
            this.syncAuth(track);
        }catch (IFMServeRejectRuntimeException e){
            //打印告警异常信息
            String buf = String.format("<------%s:%s ----ServiceRejectException." + System.currentTimeMillis(), methodName, this.getClass().getName()) +
                    String.format("IFMServeRejectException:%s", e.getMessage() == null ? "" : e.getMessage());
            logger.warn(buf);
            //将异常信息封装到ifmresult中
            ifmResult.setException(new IFMResultErrorInfo(e.getErrorCode(), e.getMessage()==null ?"":e.getMessage()));
            ifmResult.markRefused();
            //log
            track.setState(ServiceLog.status_enum.REFUSED);
            track.setEndTime(System.currentTimeMillis());
            track.setExMsg(e.getMessage());
            track.setStacks(JSON.toJSONString(e.getStackTrace()));
            track.setExType("IFMServeRejectException");
            this.syncAuth(track);
        }
        catch (IFMException e) {
            //打印错误异常信息
            String buf = String.format("<------调用id:%s,接口名:%s,类名:%s,时间戳:" + System.currentTimeMillis() + ",", MDC.get("traceRootId"), methodName, this.getClass().getName()) +
                    String.format("IFMException:%s", e.getMessage() == null ? "" : e.getMessage());
            logger.error(buf);
            //将异常信息封装到ifmresult中
            ifmResult.setException(new IFMResultErrorInfo(e.getErrorCode(), String.format(getClass().getName() + ":系统内部异常:%s",e.getMessage()==null ?"":e.getMessage())));
            ifmResult.markFailed();
            e.printStackTrace();
            //log
            track.setState(ServiceLog.status_enum.FAILED);
            track.setEndTime(System.currentTimeMillis());
            track.setExMsg(e.getMessage()==null ?"":e.getMessage());
            track.setExType("IFMException");
            track.setStacks(JSON.toJSONString(e.getStackTrace()));
            this.syncAuth(track);
        }
        catch (Throwable e) {
            e.printStackTrace();
            //打印错误异常信息
            StringBuffer buf=new StringBuffer();
            buf.append(String.format("<------调用id:%s,接口名:%s,类名:%s,时间戳:"+ System.currentTimeMillis()+"," ,MDC.get("traceRootId"), methodName, this.getClass().getName()));
            buf.append(String.format("ServiceException:调用过程发生未知异常:%s",e.getMessage()==null ?"":e.getMessage()));
            logger.error(buf.toString());
            //将异常信息封装到Isbresult中
            ifmResult.setException(new IFMResultErrorInfo(ExceptionConst.UNKNOWN_ERROR_CODE, String.format(getClass().getName()+":调用过程发生未知异常:%s",e.getMessage()==null ?"":e.getMessage())));
            ifmResult.markFailed();
            track.setState(ServiceLog.status_enum.FAILED);
            track.setEndTime(System.currentTimeMillis());
            track.setExMsg(e.getMessage()!=null?e.getMessage():e.toString()==null?"":e.toString());
            track.setExType("Throwable");
            track.setStacks(JSON.toJSONString(e.getStackTrace()));
            this.syncAuth(track);
            e.printStackTrace();
        } finally {
            this.syncBroker(headers.get("messageId"),ifmResult);
        }
    }

    /**
     * 异步发送到ifm-auth
     * @param track
     */
    private void  syncAuth(ServiceLog track){
       // executor.execute(() -> logSend.send(track));
    }

    /**
     * 异步发送到ifm-broker
     */
    private void  syncBroker(List<String> messageId, IFMResult ifmResult){
        executor.execute(() -> resultSend.resultToBroker(messageId,ifmResult));
    }
}
