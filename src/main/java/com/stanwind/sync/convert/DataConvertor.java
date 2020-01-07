package com.stanwind.sync.convert;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.InitializingBean;

/**
 * DataConvert
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 11:24
 **/
public abstract class DataConvertor implements InitializingBean {

    protected Map<String, Converter> typeMapping;

    protected abstract Map<String, Converter> getConvertTypeMapping();


    public Object getElasticsearchTypeObject(String mysqlType, String data) {
        Optional<Map.Entry<String, Converter>> result = typeMapping.entrySet().parallelStream()
                .filter(entry -> mysqlType.toLowerCase().contains(entry.getKey())).findFirst();
        return (result.isPresent() ? result.get().getValue() : (Converter) data1 -> data1).convert(data);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.typeMapping = getConvertTypeMapping();
    }

    protected interface Converter {

        Object convert(String data);
    }
}
