package application.service;

import application.GuiSvgPlott;
import application.model.PageSize;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.svgcreator.SvgCreator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * This class provides methods for {@link SvgPlotOptions}.
 */
public class SvgOptionsService {
    private static final Logger logger = LoggerFactory.getLogger(SvgOptionsService.class);
    private static final SvgOptionsService INSTANCE = new SvgOptionsService();
    private ResourceBundle bundle;

    private SvgOptionsService() {
    }

    public static SvgOptionsService getInstance() {
        return INSTANCE;
    }

    /**
     * Builds a {@link StringConverter} which converts a {@link DiagramType} to a string from language bundle.
     *
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
     * Builds a {@link StringConverter} which converts a {@link PageSize} to a string from language bundle.
     *
     * @return the {@link StringConverter}
     */
    public StringConverter<PageSize> getPageSizeConverter() {
        return new StringConverter<PageSize>() {
            @Override
            public String toString(PageSize pageSize) {
                return pageSize.getName() + " " + bundle.getString(pageSize.getPageOrientationName().toLowerCase());
            }

            @Override
            public PageSize fromString(String string) {
                PageSize pageSize = PageSize.A4;
                for (PageSize ps : FXCollections.observableArrayList(PageSize.values())) {
                    if (this.toString(ps).equals(string)) {
                        pageSize = ps;
                    }
                }
                return pageSize;
            }
        };
    }


    /**
     * Builds the Svg.
     *
     * @param svgPlotOptions the {@link SvgPlotOptions}
     */
    public void buildSVG(SvgPlotOptions svgPlotOptions) {
        GuiSvgPlott.getInstance().getRootFrameController().clearMessageLabel();
        svgPlotOptions.finalizeOptions();

        try {
            SvgCreator creator = svgPlotOptions.getDiagramType().getInstance(svgPlotOptions);
            creator.run();
            logger.info(this.bundle.getString("chart_creation_success_message") + " " + svgPlotOptions.getOutput());
        } catch (Exception e) {
            logger.error(this.bundle.getString("chart_creation_error"));
            e.printStackTrace();
        }
    }

    /**
     * Build Svg and load it into {@link WebView}.
     *
     * @param svgPlotOptions the {@link SvgPlotOptions}
     * @param webView_svg    the {@link WebView}
     */
    public void buildPreviewSVG(SvgPlotOptions svgPlotOptions, WebView webView_svg) {
        GuiSvgPlott.getInstance().getRootFrameController().clearMessageLabel();
        Path svgPath = Paths.get(System.getProperty("user.home") + "/svgPlot/svg.svg");

        File svg = new File(svgPath.toString());
        svgPlotOptions.setOutput(svg);
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
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    adjustScaleOfWebView(webView_svg);
                }
            });
            webEngine.loadContent(sb.toString());

            // accessibility
            Path descPath = Paths.get(svg.getParentFile().getPath() + "/" + svg.getName().replace(".svg", "_desc.html"));
            String description = loadDescription(descPath.toString());
            webView_svg.setAccessibleHelp(bundle.getString("preview") + ": " + description);

            // reset zoom
            ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
                adjustScaleOfWebView(webView_svg);
            };
            Window window = webView_svg.getScene().getWindow();
            window.widthProperty().addListener(stageSizeListener);
            window.heightProperty().addListener(stageSizeListener);

        } catch (ClassCastException e) {
            logger.warn(this.bundle.getString("preview_pointlist_warning"));
//            e.printStackTrace();
        } catch (Exception e){
            logger.error(this.bundle.getString("preview_load_error"));
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
     *
     * @param bundle the {@link ResourceBundle}
     */
    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    /**
     * Sets the zoom of the WebView so that the content fits in without scrollbars.
     *
     * @param webView the {@link WebView}
     */
    private void adjustScaleOfWebView(WebView webView) {
        webView.setZoom(1.0);
        WebEngine webEngine = webView.getEngine();

        double docHeight = Double.parseDouble(webEngine.executeScript("document.height").toString());
        double docWidth = Double.parseDouble(webEngine.executeScript("document.width").toString());

        if (docHeight > docWidth) {
            double zoom = 1.0;
            if (docHeight > webView.getHeight()) {
                zoom = webView.getHeight() / docHeight;
                webView.setZoom(zoom);
            } else if (docHeight != webView.getHeight()) {
                zoom = docHeight / webView.getHeight();
                webView.setZoom(zoom);
            }
        } else {
            double zoom = 1.0;
            if (docWidth > webView.getWidth()) {
                zoom = webView.getWidth() / docWidth;
                webView.setZoom(zoom);
            } else if (docHeight != webView.getHeight()) {
                zoom = docWidth / webView.getWidth();
                webView.setZoom(zoom);
            }
        }
    }

    public static String getLoggerName() {
        return logger.getName();
    }
}
