package com.stanwind.sync.event;

import com.alibaba.otter.canal.protocol.CanalEntry.Entry;

/**
 * SimpleDeleteCanalEvent
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 11:15
 **/
public class SimpleDeleteCanalEvent extends AbstractCanalEvent {

    public SimpleDeleteCanalEvent(Entry source) {
        super(source);
    }
}
