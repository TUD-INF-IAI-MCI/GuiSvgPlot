package application.model.Options;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.w3c.dom.Element;

public class Point extends tud.tangram.svgplot.data.Point {

    protected double x;
    protected double y;
    protected String name;
    protected Element symbol;

    @JsonCreator
    public Point(@JsonProperty("x") double x, @JsonProperty("y") double y) {
        super(x, y, "", (Element) null);
    }

    public Point(tud.tangram.svgplot.data.Point point) {
        super(point);
    }
}
