package com.stanwind.sync;

/**
 * CanalConstant
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 10:51
 **/
public class CanalConstant {

    public static final Integer BATCH_SIZE = 50;

    public enum EventType {
        ALL(0, "all"),
        INSERT(1, "insert"),
        UPDATE(2, "update"),
        DELETE(3, "delete");

        private final int value;
        private final String remark;

        public int getValue() {
            return value;
        }

        public String getRemark() {
            return remark;
        }

        private EventType(int value, String remark) {
            this.value = value;
            this.remark = remark;
        }
    }
}
