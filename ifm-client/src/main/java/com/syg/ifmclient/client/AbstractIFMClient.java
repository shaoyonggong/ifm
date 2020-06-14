package com.syg.ifmclient.client;

import com.syg.ifmclient.base.ServiceGateway;
import com.syg.ifmcommon.api.IFMApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

/**
 * @Description 默认的IFMAPI
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public abstract class AbstractIFMClient <T, D> implements IFMApi<T, D> {
    
    protected static Logger logger = LoggerFactory.getLogger(AbstractIFMClient.class);

    protected ServiceGateway sg;

    abstract public String getServiceName();

    abstract public String getServicePath();

    public void setSg(ServiceGateway sg) {
        this.sg = sg;
    }

    protected String getApiUrl() {
        StringBuilder spiUrl = new StringBuilder();
        spiUrl.append(sg.getProtocol()).append("://").append(this.getServiceName()).append("/").append(this.getServicePath()).append("/");
        return spiUrl.toString();
    }

    protected void logRPC(String url) {
        logger.info(String.format("IFM Client 准备发起请求: %s", url));
    }

    protected HttpHeaders addHeaders(){
        HttpHeaders requestHeaders = new HttpHeaders();
        if(!StringUtils.isEmpty(MDC.get("tracingId"))){
            requestHeaders.add("tracingId", MDC.get("tracingId"));
            requestHeaders.add("recordNumber", String.valueOf(Integer.valueOf(MDC.get("recordNumber"))+1));
            MDC.put("recordNumber",String.valueOf(Integer.valueOf(MDC.get("recordNumber"))+1));
            requestHeaders.add("issync", "0");
        }
        if(!StringUtils.isEmpty(MDC.get("sessionId"))){
            requestHeaders.add("cookie", "sessionId="+MDC.get("sessionId"));
        }
        return requestHeaders;
    }
}
