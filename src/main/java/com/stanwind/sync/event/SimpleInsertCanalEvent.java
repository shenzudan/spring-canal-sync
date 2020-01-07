package com.stanwind.sync.event;

import com.alibaba.otter.canal.protocol.CanalEntry.Entry;

/**
 * SimpleInsertCanalEvent
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 11:15
 **/
public class SimpleInsertCanalEvent extends AbstractCanalEvent {

    public SimpleInsertCanalEvent(Entry source) {
        super(source);
    }
}
