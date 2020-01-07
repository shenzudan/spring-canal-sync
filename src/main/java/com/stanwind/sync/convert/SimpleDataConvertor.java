package com.stanwind.sync.convert;

import com.google.common.collect.Maps;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * SimpleDataConvertor
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 11:31
 **/
@Component
public class SimpleDataConvertor extends DataConvertor {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected Map<String, Converter> getConvertTypeMapping() {
        Map<String, Converter> typeMapping = Maps.newHashMap();
        typeMapping.put("char", data -> data);
        typeMapping.put("text", data -> data);
        typeMapping.put("blob", data -> data);
        typeMapping.put("int", Long::valueOf);
        typeMapping.put("date", data -> LocalDateTime.parse(data, FORMATTER));
        typeMapping.put("time", data -> LocalDateTime.parse(data, FORMATTER));
        typeMapping.put("float", Double::valueOf);
        typeMapping.put("double", Double::valueOf);
        typeMapping.put("decimal", Double::valueOf);

        return typeMapping;
    }
}
