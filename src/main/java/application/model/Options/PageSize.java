package application.model.Options;

import tud.tangram.svgplot.data.Point;

/**
 * @author Emma Müller
 */
public enum PageSize {
    CUSTOM("custom", 210, 297),
    A0("A0", 841, 1189),
    A1("A1", 594, 841),
    A2("A2", 420, 594),
    A3("A3", 297, 420),
    A4("A4", 210, 297),
    A5("A5", 148, 210),
    A6("A6", 105, 148);
//    A7("A7", 74, 105),
//    A8("A8", 52, 74),
//    A9("A9", 37, 52),
//    A10("A10", 26, 37),


    private final int width;
    private final int height;
    private final String name;

    private PageSize(final String name, final int width, final int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public Point getPageSizeWithOrientation(PageOrientation pageOrientation){
        Point size = new Point(width, height);
        if (pageOrientation.equals(PageOrientation.LANDSCAPE)){
            size = new Point(height, width);
        }
        return size;
    }

    public enum PageOrientation {
        PORTRAIT(0), LANDSCAPE(1);

        private final int index;

        private PageOrientation(final int index){
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}
