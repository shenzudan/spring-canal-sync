package com.stanwind.sync.anno;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Listener
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 15:05
 **/
public class Listener {
    private String db;
    private List<String> tables;
    private List<String> keys;
    private Class<?> claz;
    private Method method;

    public Class<?> getClaz() {
        return claz;
    }

    public void setClaz(Class<?> claz) {
        this.claz = claz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Listener{");
        sb.append("db='").append(db).append('\'');
        sb.append(", tables=").append(tables);
        sb.append(", keys=").append(keys);
        sb.append(", claz=").append(claz);
        sb.append(", method=").append(method);
        sb.append('}');
        return sb.toString();
    }
}
