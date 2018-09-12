package application.infrastructure.JacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.DoubleNode;
import org.w3c.dom.Element;
import tud.tangram.svgplot.data.Point;

import java.io.IOException;

public class PointDeserializer extends StdDeserializer<Point> {
    public PointDeserializer() {
        this(null);
    }

    public PointDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Point deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        double x = (Double) ((DoubleNode) node.get("x")).numberValue();
        double y = (Double) ((DoubleNode) node.get("y")).numberValue();
        String name = node.get("name").asText();
        if (!node.get("symbol").isNull()){
            Element symbol = (Element) (node.get("symbol"));
            return new Point(x, y, name, symbol);
        }
        return new Point(x, y, name);

    }
}

