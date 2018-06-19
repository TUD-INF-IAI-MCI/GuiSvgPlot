package application.service;

import application.GuiSvgPlott;
import javafx.collections.FXCollections;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.StringConverter;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.svgcreator.SvgCreator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.scene.control.Label;

/**
 * This class provides methods for {@link SvgPlotOptions}.
 */
public class SvgOptionsService {

    private static final SvgOptionsService INSTANCE = new SvgOptionsService();
    private ResourceBundle bundle;

    private SvgOptionsService() {
    }

    public static SvgOptionsService getInstance() {
        return INSTANCE;
    }

    /**
     * Builds a {@link StringConverter} which converts a {@link DiagramType} to a string from language bundle.
     * @return the {@link StringConverter}
     */
    public StringConverter<DiagramType> getDiagramTypConverter() {
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

    /**
     * Builds the Svg.
     * @param svgPlotOptions the {@link SvgPlotOptions}
     */
    public void buildSVG(SvgPlotOptions svgPlotOptions) {
        svgPlotOptions.finalizeOptions();

        SvgCreator creator = svgPlotOptions.getDiagramType().getInstance(svgPlotOptions);
        try {
            creator.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Build Svg and load it into {@link WebView}.
     * @param svgPlotOptions the {@link SvgPlotOptions}
     * @param webView_svg the {@link WebView}
     */
    public void buildSVG(SvgPlotOptions svgPlotOptions, WebView webView_svg) {
        File svg = svgPlotOptions.getOutput();

        if (svg == null) {
            Path svgPath = Paths.get(System.getProperty("user.home") + "/svgPlot/svg.svg");

            svg = new File(svgPath.toString());
            svgPlotOptions.setOutput(svg);
        }
        svgPlotOptions.finalizeOptions();

        try {
            SvgCreator creator = svgPlotOptions.getDiagramType().getInstance(svgPlotOptions);
            creator.run();

            /* show created svg */
            BufferedReader br = new BufferedReader(new FileReader(svg));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            WebEngine webEngine = webView_svg.getEngine();
            webEngine.loadContent(sb.toString());

            // accessibility
            Path descPath = Paths.get(svg.getParentFile().getPath() + "/" + svg.getName().replace(".svg", "_desc.html"));
            String description = loadDescription(descPath.toString());
            webView_svg.setAccessibleHelp(bundle.getString("preview") + ": " + description);

        } catch (Exception e) {
            Label label =GuiSvgPlott.getInstance().getRootFrameController().label_message;
            label.setText("Fehler beim Erstellen der Vorschau!");
            label.getStyleClass().add("warn");
            label.setVisible(true);
            e.printStackTrace();
        }
    }

    /**
     * Loads description file and and builds a String.
     *
     * @param path Path to the description HTML document
     * @return the description as string
     */
    private String loadDescription(String path) {
        String desc = "";

        try {
            File descFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();
            Document doc = documentBuilder.parse(descFile);

            NodeList divs = doc.getElementsByTagName("div");
            for (int temp = 0; temp < divs.getLength(); temp++) {
                desc = desc.concat(divs.item(temp).getTextContent());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return desc;
    }

    /**
     * Sets the {@link ResourceBundle}
     * @param bundle the {@link ResourceBundle}
     */
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }
}
