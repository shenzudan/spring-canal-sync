package com.stanwind.sync;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.Message;
import com.stanwind.sync.event.SimpleDeleteCanalEvent;
import com.stanwind.sync.event.SimpleInsertCanalEvent;
import com.stanwind.sync.event.SimpleUpdateCanalEvent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * SyncScheduling 同步定时任务 Event分发
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 10:48
 **/
@Component
//@Conditional(SyncCondition.class)
@ConditionalOnProperty(value = "canal.sync", matchIfMissing = true)
public class SyncScheduling implements Runnable, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SyncScheduling.class);
    private ApplicationContext applicationContext;

    private boolean init = false;

    @Autowired
    private CanalConnector canalConnector;

    @Autowired
    private CanalClient canalClient;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Scheduled(fixedDelay = 100)
    @Override
    public void run() {
        if (!init) {
            //只允许一个监听 否则会阻塞 不要放在容器初始化代码中
            canalClient.reconnect();
            init = true;
        }

        try {
            Message message = canalConnector.getWithoutAck(CanalConstant.BATCH_SIZE);
            long batchId = message.getId();
            try {
                List<Entry> entries = message.getEntries();
                if (batchId != -1 && entries.size() > 0) {
//                    logger.info("忽略........{}", batchId);
                    logger.info("fetch {} ,id {}", CanalConstant.BATCH_SIZE, batchId);
                    entries.forEach(entry -> {
                        if (entry.getEntryType() == EntryType.ROWDATA) {
                            publishCanalEvent(entry);
                        }
                    });
                }
                canalConnector.ack(batchId);
            } catch (Exception e) {
                logger.error("event exception, rollback batchId = " + batchId, e);
                canalConnector.rollback(batchId);
            }
        } catch (Exception e) {
            logger.error("canal_scheduled异常！", e);
            canalClient.reconnect();
        }
    }

    private void publishCanalEvent(Entry entry) {
        EventType eventType = entry.getHeader().getEventType();
        switch (eventType) {
            case INSERT:
                applicationContext.publishEvent(new SimpleInsertCanalEvent(entry));
                break;
            case UPDATE:
                applicationContext.publishEvent(new SimpleUpdateCanalEvent(entry));
                break;
            case DELETE:
                applicationContext.publishEvent(new SimpleDeleteCanalEvent(entry));
                break;
            default:
                logger.warn("not support event type {}", eventType.getNumber());
                break;
        }
    }
}
