package application.model;

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

    public boolean isPointsborderless() {
        return pointsborderless;
    }
}
