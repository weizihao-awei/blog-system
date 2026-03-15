package com.ykw.blog_system.Config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.ykw.blog_system.enums.OrderEnum;

import java.io.IOException;

/**
 * OrderEnum 反序列化器
 */
public class OrderEnumDeserializer extends JsonDeserializer<OrderEnum> {
    
    @Override
    public OrderEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText();
        return OrderEnum.fromValue(value);
    }
}
