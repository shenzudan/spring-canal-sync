package com.stanwind.sync;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.google.common.collect.Lists;
import java.net.InetSocketAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * CanalClient canal链接配置
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 10:44
 **/
@Component
public class CanalClient implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(CanalClient.class);
    private CanalConnector canalConnector;

    @Value("${dubbo.registry.address:}")
    private String zkHost;
    /**
     * 是否开启同步
     */
    @Value("${canal.sync:true}")
    private boolean sync;

    /**
     * 是否使用单例canal配置
     */
    @Value("${canal.enable:false}")
    private boolean enable;
    @Value("${canal.host}")
    private String canalHost;
    @Value("${canal.port}")
    private String canalPort;
    @Value("${canal.destination}")
    private String canalDestination;
    @Value("${canal.username}")
    private String canalUsername;
    @Value("${canal.password}")
    private String canalPassword;

    @Bean
    public CanalConnector getCanalConnector() {
        if(!sync) {
            return null;
        }

        if (!enable && zkHost != null && zkHost.length() > 0) {
            canalConnector = CanalConnectors.newClusterConnector(zkHost, canalDestination, canalUsername, canalPassword);
        } else {
            canalConnector = CanalConnectors
                    .newClusterConnector(Lists.newArrayList(new InetSocketAddress(canalHost, Integer.valueOf(canalPort))),
                            canalDestination, canalUsername, canalPassword);
        }

        //reconnect();
        return canalConnector;
    }

    public void reconnect() {
        canalConnector.connect();
        // 指定filter，格式 {database}.{table}，这里不做过滤，过滤操作留给用户
        canalConnector.subscribe();
        // 回滚寻找上次中断的位置
        canalConnector.rollback();
        logger.info("canal客户端链接成功");
    }

    @Override
    public void destroy() {
        if (canalConnector != null) {
            canalConnector.disconnect();
        }
    }
}
