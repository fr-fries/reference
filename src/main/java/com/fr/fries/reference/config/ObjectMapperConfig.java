package com.fr.fries.reference.config;

import com.amazonaws.HttpMethod;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fr.fries.reference.config.policy.UrlPolicy;
import com.fr.fries.reference.common.SignerType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(DataSize.class, new DataSizeSerializer());
        simpleModule.addDeserializer(DataSize.class, new DataSizeDeserializer());
        simpleModule.addDeserializer(UrlPolicy.class, new UrlPolicyDeserializer());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(simpleModule);
        mapper.findAndRegisterModules();

        return mapper;
    }

    public static class DataSizeSerializer extends StdSerializer<DataSize> {

        DataSizeSerializer() {
            this(null);
        }

        DataSizeSerializer(Class<DataSize> t) {
            super(t);
        }

        @Override
        public void serialize(DataSize dataSize, JsonGenerator jsonGenerator,
                              SerializerProvider serializer) throws IOException {
            jsonGenerator.writeString(String.format("%,dB", dataSize.toBytes()));
        }
    }

    public static class DataSizeDeserializer extends StdDeserializer<DataSize> {

        DataSizeDeserializer() {
            this(null);
        }

        DataSizeDeserializer(Class<?> t) {
            super(t);
        }

        @Override
        public DataSize deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            return DataSize.parse(node.asText().replace(",", ""));
        }
    }

    public static class UrlPolicyDeserializer extends StdDeserializer<UrlPolicy> {

        UrlPolicyDeserializer() {
            this(null);
        }

        UrlPolicyDeserializer(Class<?> t) {
            super(t);
        }

        @Override
        public UrlPolicy deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            HttpMethod method = HttpMethod.valueOf(node.get("method").asText());
            SignerType type = SignerType.valueOf(node.get("type").asText());
            String domain;
            if (node.has("domain")) {
                domain = node.get("domain").asText();
            } else {
                domain = PolicyConfig.getDefaultDomain(type);
            }
            Duration expires = Duration.parse(node.get("expires").asText());

            return new UrlPolicy(method, type, domain, expires);
        }
    }
}
