package application.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;
import tud.tangram.svgplot.options.DiagramType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Preset {

    private String name;
    private GuiSvgOptions options;
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private Date creationDate;
    private DiagramType diagramType;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    // this is necessary for jackson
    public Preset() {
    }

    public Preset(final Preset preset){
        this(preset.getOptions(), preset.getName(), preset.getDiagramType());
    }

    public Preset(GuiSvgOptions options, String name, DiagramType diagramType) {
        this.options = options;
        this.creationDate = new Date();
        this.name = name;
        this.diagramType = diagramType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GuiSvgOptions getOptions() {
        return options;
    }

    public void setOptions(GuiSvgOptions options) {
        this.options = options;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @JsonGetter("diagramType")
    public String getDiagramTypeString() {
        return diagramType.toString();
    }

    public DiagramType getDiagramType() {
        return diagramType;
    }

    public void setDiagramType(DiagramType diagramType) {
        this.diagramType = diagramType;
    }


    @JsonIgnore
    public String getFormattedCreationDate() {
        return sdf.format(creationDate);
    }

    public void update( final Preset preset){
        this.options = preset.getOptions();
        this.name = preset.getName();
        this.diagramType = preset.getDiagramType();
    }

    @Override
    public String toString() {
        return "Preset{" +
                "name=" + name +
                ", options=" + options +
                ", creationDate=" + creationDate +
                ", diagramType=" + diagramType +
                ", sdf=" + sdf +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Preset)) return false;

        Preset preset = (Preset) o;

        return this.name.equals(preset.name) &&
                this.getFormattedCreationDate().equals(preset.getFormattedCreationDate()) &&
                this.diagramType.equals(preset.diagramType);
    }

}
