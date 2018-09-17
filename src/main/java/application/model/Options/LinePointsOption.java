package application.model.Options;

/**
 * @author Emma Müller
 */
public enum LinePointsOption {
    Hide("off", false),
    ShowWithBorder("on", false),
    ShowBorderless("on", true);

    private String showLinePoints;
    private boolean pointsborderless;

    private LinePointsOption(final String showLinePoints, final boolean pointsborderless) {
        this.showLinePoints = showLinePoints;
        this.pointsborderless = pointsborderless;
    }

    public String getShowLinePoints() {
        return showLinePoints;
    }

    public boolean isShowLinePoints() {
        return showLinePoints.equals("on");
    }

    public boolean isPointsborderless() {
        return pointsborderless;
    }

    public static LinePointsOption getLinePointsOption(final String showLinePoints, final boolean pointsborderless) {
        LinePointsOption linePointsOption = showLinePoints != null && showLinePoints.equals("on") ? ShowWithBorder : Hide;
        if (linePointsOption.equals(ShowWithBorder) && pointsborderless){
            linePointsOption = ShowBorderless;
        }
        return linePointsOption;
    }
}
