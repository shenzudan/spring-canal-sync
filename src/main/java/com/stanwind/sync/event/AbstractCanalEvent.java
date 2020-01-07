package com.stanwind.sync.event;

import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import org.springframework.context.ApplicationEvent;

/**
 * AbstractCanalEvent
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 11:15
 **/
public abstract class AbstractCanalEvent extends ApplicationEvent {

    public AbstractCanalEvent(Entry source) {
        super(source);
    }

    public Entry getEntry() {
        return (Entry) source;
    }
}
