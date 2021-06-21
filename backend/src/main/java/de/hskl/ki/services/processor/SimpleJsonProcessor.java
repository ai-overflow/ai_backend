package de.hskl.ki.services.processor;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class SimpleJsonProcessor<T> extends SimpleFileProcessor<T> {
    public SimpleJsonProcessor(Class<T> classType) {
        super(classType);
        mapper = new ObjectMapper(new JsonFactory());
        mapper.findAndRegisterModules();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
