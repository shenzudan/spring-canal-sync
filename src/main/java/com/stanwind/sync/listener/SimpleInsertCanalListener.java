package com.stanwind.sync.listener;

import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.stanwind.sync.ListenerScanner;
import com.stanwind.sync.event.SimpleInsertCanalEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * SimpleInsertCanalListener
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 11:39
 **/
@Component
public class SimpleInsertCanalListener extends AbstractCanalListener<SimpleInsertCanalEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SimpleInsertCanalListener.class);

    @Autowired
    private ListenerScanner scanner;

    @Override
    protected void doSync(String database, String table, RowData rowData) {
        logger.info("INSERT {}.{} ", database, table);
        scanner.INSERT_METHODS.parallelStream().forEach(e -> {
            if (StringUtils.isEmpty(e.getDb()) || e.getDb().equals(database)) {
                if (e.getTables().contains(table)) {
                    execListener(e, rowData, false, e.getTables().indexOf(table));
                }
            }
        });
    }
}
