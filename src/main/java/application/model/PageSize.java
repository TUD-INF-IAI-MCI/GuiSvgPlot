package application.model;

public enum PageSize {
    A0("A0", PageOrientation.PORTRAIT, 841, 1189),
    A1("A1", PageOrientation.PORTRAIT, 594, 841),
    A2("A2", PageOrientation.PORTRAIT, 420, 594),
    A3("A3", PageOrientation.PORTRAIT, 297, 420),
    A4("A4", PageOrientation.PORTRAIT, 210, 297),
    A5("A5", PageOrientation.PORTRAIT, 148, 210),
    A6("A6", PageOrientation.PORTRAIT, 105, 148),
//    A7("A7", PageOrientation.PORTRAIT, 74, 105),
//    A8("A8", PageOrientation.PORTRAIT, 52, 74),
//    A9("A9", PageOrientation.PORTRAIT, 37, 52),
//    A10("A10", PageOrientation.PORTRAIT, 26, 37),

    A0_landscape("A0", PageOrientation.LANDSCAPE, 1189, 841),
    A1_landscape("A1", PageOrientation.LANDSCAPE, 841, 594),
    A2_landscape("A2", PageOrientation.LANDSCAPE, 594, 420),
    A3_landscape("A3", PageOrientation.LANDSCAPE, 420, 297),
    A4_landscape("A4", PageOrientation.LANDSCAPE, 297, 210),
    A5_landscape("A5", PageOrientation.LANDSCAPE, 210, 148),
    A6_landscape("A6", PageOrientation.LANDSCAPE, 148, 105);
//    A7_landscape("A7", PageOrientation.LANDSCAPE, 105, 74),
//    A8_landscape("A8", PageOrientation.LANDSCAPE, 74, 52),
//    A9_landscape("A9", PageOrientation.LANDSCAPE, 52, 37),
//    A10_landscape("A10", PageOrientation.LANDSCAPE, 37, 26);

    private final int width;
    private final int height;
    private final PageOrientation pageOrientation;
    private final String name;

    private PageSize(final String name, final PageOrientation pageOrientation, final int width, final int height) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.pageOrientation = pageOrientation;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public PageOrientation getPageOrientation() {
        return pageOrientation;
    }

    public String getPageOrientationName() {
        return pageOrientation.toString();
    }

    public String getName() {
        return name;
    }

    private enum PageOrientation {
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
