package application.model.Options;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Range extends tud.tangram.svgplot.coordinatesystem.Range {

    @JsonCreator
    public Range(@JsonProperty("from") double from, @JsonProperty("to") double to, @JsonProperty("name") String name) {
        super(from, to, name);
    }

    public Range(final double from, final double to) {
        super(from, to);
    }

    public Range(tud.tangram.svgplot.coordinatesystem.Range range) {
        super(range.getFrom(), range.getTo(), range.getName());
    }

}
