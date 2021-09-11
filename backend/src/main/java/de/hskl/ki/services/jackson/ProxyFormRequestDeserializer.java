package de.hskl.ki.services.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.hskl.ki.models.proxy.PrefixedFormData;

import java.io.IOException;

public class ProxyFormRequestDeserializer extends JsonDeserializer<PrefixedFormData> {
    @Override
    public PrefixedFormData deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        p.getBinaryValue();
        return null;
    }
}
