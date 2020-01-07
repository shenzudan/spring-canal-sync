package com.stanwind.sync.event;

import com.alibaba.otter.canal.protocol.CanalEntry.Entry;

/**
 * SimpleUpdateCanalEvent
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 11:15
 **/
public class SimpleUpdateCanalEvent extends AbstractCanalEvent {

    public SimpleUpdateCanalEvent(Entry source) {
        super(source);
    }
}
