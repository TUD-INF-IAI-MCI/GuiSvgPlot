package application.infrastructure.JacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;
import tud.tangram.svgplot.coordinatesystem.Range;

import java.io.IOException;

public class RangeDeserializer extends StdDeserializer<Range> {
    public RangeDeserializer() {
        this(null);
    }

    public RangeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Range deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        double from = (Double) ((DoubleNode) node.get("from")).numberValue();
        double to = (Double) ((DoubleNode) node.get("to")).numberValue();
        String name = node.get("name").asText();

        return new Range(from, to, name);

    }
}

