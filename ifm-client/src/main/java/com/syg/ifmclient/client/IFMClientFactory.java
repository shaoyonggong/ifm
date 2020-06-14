package com.syg.ifmclient.client;

import com.syg.ifmcommon.api.IFMApi;

/**
 * @Description 工厂
 * @Author shaoyonggong
 * @Date 2020/6/14
 */
public class IFMClientFactory {

    private static IFMClientFactory instance;

    private IFMClientFactory() {
    }

    private String host;

    private String port;


    public static void init(String host, String port) {
        IFMClientFactory factory = new IFMClientFactory();
        factory.host = host;
        factory.port = port;
        instance = factory;
    }


    public static IFMClientFactory getInstance() {

        return instance;
    }


    public IFMApi buildCilent(String serviceName, String servicePath) {

        //TODO: 准备实际实现工厂构建.
        return null;
    }

    public static void main(String[] args) {

        String host = "";
        String port = "";
        String serviceName = "";
        String servicePath = "";

        IFMClientFactory.init(host, port);

        IFMClientFactory.getInstance().buildCilent(serviceName, servicePath);

    }
}
