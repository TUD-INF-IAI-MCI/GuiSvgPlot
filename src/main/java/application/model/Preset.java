package application.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Preset {

    private GuiSvgOptions options;
    private Date creationDate;
    private String presetName;

    public Preset(GuiSvgOptions options, String presetName) {
        this.options = options;
        this.creationDate = Calendar.getInstance().getTime();
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
}
