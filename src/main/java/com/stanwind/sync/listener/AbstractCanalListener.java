package com.stanwind.sync.listener;

import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.google.protobuf.InvalidProtocolBufferException;
import com.stanwind.sync.anno.Listener;
import com.stanwind.sync.convert.DataConvertor;
import com.stanwind.sync.event.AbstractCanalEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.util.ReflectionUtils;

/**
 * RowDataSync Canal binlog sync listener
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 11:19
 **/
public abstract class AbstractCanalListener<EVENT extends AbstractCanalEvent> implements ApplicationListener<EVENT> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractCanalListener.class);

    private static final String DEFAULT_PK = "id";

    @Autowired
    protected DataConvertor dataConvertor;

    @Autowired
    protected ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(EVENT event) {
        Entry entry = event.getEntry();
        String database = entry.getHeader().getSchemaName();
        String table = entry.getHeader().getTableName();
        RowChange change;
        try {
            change = RowChange.parseFrom(entry.getStoreValue());
        } catch (InvalidProtocolBufferException e) {
            logger.error("canalEntry_parser_error,根据CanalEntry获取RowChange失败！", e);
            return;
        }
        change.getRowDatasList().forEach(rowData -> doSync(database, table, rowData));
    }

    /**
     * 使用新ID
     *
     * @param listener
     * @param rowData
     * @param beforePK 使用数据库变更前的数据
     * @param index    主键配置索引
     */
    protected void execListener(Listener listener, RowData rowData, boolean beforePK, int index) {
        Map<String, Object> map = parseColumnsToMap(
                beforePK ? rowData.getBeforeColumnsList() : rowData.getAfterColumnsList());
        Object bean = applicationContext.getBean(listener.getClaz());
        String key = listener.getKeys().size() > index ? listener.getKeys().get(index) : DEFAULT_PK;
        Object param = map.get(key);
        logger.debug("exec sync method: {}, param {}={}", listener.getMethod().getName(), key, param);
        ReflectionUtils.invokeMethod(listener.getMethod(), bean, param);
    }

    protected Map<String, Object> parseColumnsToMap(List<Column> columns) {
        Map<String, Object> jsonMap = new HashMap<>();
        columns.forEach(column -> {
            if (column == null) {
                return;
            }
            jsonMap.put(column.getName(), column.getIsNull() ? null
                    : dataConvertor.getElasticsearchTypeObject(column.getMysqlType(), column.getValue()));
        });

        return jsonMap;
    }

    /**
     * 同步方法
     * @param database
     * @param table
     * @param rowData
     */
    protected abstract void doSync(String database, String table, RowData rowData);
}
