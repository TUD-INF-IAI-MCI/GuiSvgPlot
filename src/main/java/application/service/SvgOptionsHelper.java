package application.service;

import javafx.collections.FXCollections;
import javafx.util.StringConverter;
import tud.tangram.svgplot.options.DiagramType;

import java.util.ResourceBundle;

public class SvgOptionsHelper {

    private static final SvgOptionsHelper INSTANCE = new SvgOptionsHelper();

    private SvgOptionsHelper() {
    }

    public static SvgOptionsHelper getInstance() {
        return INSTANCE;
    }

    public StringConverter<DiagramType> getDiagramTypConverter(ResourceBundle bundle) {
        return new StringConverter<DiagramType>() {
            @Override
            public String toString(DiagramType diagramType) {
                return bundle.getString(diagramType.toString());
            }

            @Override
            public DiagramType fromString(String string) {
                DiagramType diagramType = DiagramType.FunctionPlot;
                for (DiagramType dt : FXCollections.observableArrayList(DiagramType.values())) {
                    if (this.toString(dt).equals(string)) {
                        diagramType = dt;
                    }
                }
                return diagramType;
            }
        };
    }
}
