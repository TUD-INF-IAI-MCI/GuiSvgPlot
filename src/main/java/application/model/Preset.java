package application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import tud.tangram.svgplot.options.DiagramType;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Preset {
    private GuiSvgOptions options;
    private StringProperty presetName;
    private StringProperty creationDate;
    private StringProperty diagramType;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private Date date = new Date();


    public Preset(GuiSvgOptions options, String presetName, DiagramType type) {
        this.options = options;
        this.creationDate = new SimpleStringProperty(sdf.format(date));
        this.presetName = new SimpleStringProperty(presetName);
        this.diagramType = new SimpleStringProperty(type.toString());
    }

    public String getPresetName() {
        return presetName.get();
    }

    public void setPresetName(String presetName) {
        this.presetName.set(presetName);
    }

    public GuiSvgOptions getOptions() {
        return options;
    }

    public void setOptions(GuiSvgOptions options) {
        this.options = options;
    }

    public String getCreationDate() {
        return creationDate.get();
    }

    public String getDiagramType() {
        return diagramType.get();
    }

    public void setDiagramType(DiagramType diagramType) {
        this.diagramType.set(diagramType+"");
    }

    public StringProperty presetName(){
        return presetName;
    }

    public StringProperty creationDate(){
        return creationDate;
    }

    public StringProperty diagramType(){
        return diagramType;
    }

}
