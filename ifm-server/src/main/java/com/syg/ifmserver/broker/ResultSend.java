package com.syg.ifmserver.broker;


import com.syg.ifmcommon.result.IFMResult;

import java.util.List;

/**
 * @Description
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public class ResultSend {
//    private final BrokerMqSend brokerMqSend;

//    public ResultSend(BrokerMqSend brokerMqSend) {
//        this.brokerMqSend = brokerMqSend;
//    }

    public void resultToBroker(List<String> list, IFMResult isbResult){
        if(list!=null&&!list.isEmpty()){
            //如果头消息中消息id不为空，说明该次调用是由可靠消息服务发起，需要将参数封装好将结果返回至可靠消息服务
//            String messageId=list.get(0);
//            ISBRequestStatusDto isbRequestStatusDto=new ISBRequestStatusDto();
//            isbRequestStatusDto.setMsgId(messageId);
//            isbRequestStatusDto.setTime(System.currentTimeMillis());
//            isbRequestStatusDto.setIsbResult(isbResult);
//            brokerMqSend.send(isbRequestStatusDto);
        }

    }
}
