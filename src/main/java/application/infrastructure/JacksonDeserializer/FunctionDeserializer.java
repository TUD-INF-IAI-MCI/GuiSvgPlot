package application.infrastructure.JacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import tud.tangram.svgplot.plotting.Function;

import java.io.IOException;

public class FunctionDeserializer extends StdDeserializer<Function> {
    public FunctionDeserializer() {
        this(null);
    }

    public FunctionDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Function deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        String title = node.get("title").asText();
        String function = node.get("function").asText();

        return new Function(title, function);
    }
}
