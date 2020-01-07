package com.stanwind.sync.listener;

import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.stanwind.sync.ListenerScanner;
import com.stanwind.sync.event.SimpleUpdateCanalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * SimpleUpdateCanalListener
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 11:41
 **/
@Component
@DependsOn("listenerScanner")
public class SimpleUpdateCanalListener extends AbstractCanalListener<SimpleUpdateCanalEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleUpdateCanalListener.class);

    @Autowired
    private ListenerScanner scanner;

    @Override
    protected void doSync(String database, String table, RowData rowData) {
        logger.info("UPDATE {}.{} ", database, table);
        scanner.UPDATE_METHODS.parallelStream().forEach(e -> {
            if (StringUtils.isEmpty(e.getDb()) || e.getDb().equals(database)) {
                if (e.getTables().contains(table)) {
                    execListener(e, rowData, false, e.getTables().indexOf(table));
                }
            }
        });
    }
}
