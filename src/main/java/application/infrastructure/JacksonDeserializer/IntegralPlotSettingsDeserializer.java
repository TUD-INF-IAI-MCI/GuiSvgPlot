package application.infrastructure.JacksonDeserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

import java.io.IOException;

public class IntegralPlotSettingsDeserializer extends StdDeserializer<IntegralPlotSettings> {
    public IntegralPlotSettingsDeserializer() {
        this(null);
    }

    public IntegralPlotSettingsDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public IntegralPlotSettings deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        JsonNode xRangeNode = node.get("xRange");
        Range xRange = new Range(0,0);
        xRange.setFrom(xRangeNode.get("from").doubleValue());
        xRange.setTo(xRangeNode.get("to").doubleValue());
        xRange.setName(xRangeNode.get("name").asText());

        int function1 = node.get("function1").intValue();
        int function2 = node.get("function2").intValue();
        String name = node.get("name").asText();

        return new IntegralPlotSettings(function1, function2, name, xRange);
    }
}
