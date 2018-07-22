package application.model;

import java.util.Date;

public class Preset {

    private GuiSvgOptions options;
    private Date creationDate;
    private String presetName;

    public Preset(GuiSvgOptions options, Date creationDate, String presetName) {
        this.options = options;
        this.creationDate = creationDate;
        this.presetName = presetName;
    }

    public String getPresetName() {
        return presetName;
    }

    public void setPresetName(String presetName) {
        this.presetName = presetName;
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

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
