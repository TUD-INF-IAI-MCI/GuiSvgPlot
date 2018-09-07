package application.service;

import application.GuiSvgPlott;
import application.model.GuiSvgOptions;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.svgcreator.SvgCreator;

import javax.xml.bind.ValidationException;
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
     * Builds the Svg.
     *
     * @param svgPlotOptions the {@link SvgPlotOptions}
     */
    public void buildSVG(SvgPlotOptions svgPlotOptions) throws ValidationException {
        if (!this.isSvgPlottOptionsValid(svgPlotOptions)) {
            throw new ValidationException("The SvgPlottOptions are not valid!");
        }
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
     * @param guiSvgOptions the {@link GuiSvgOptions}
     * @param webView_svg   the {@link WebView}
     */
    public void buildPreviewSVG(GuiSvgOptions guiSvgOptions, WebView webView_svg) {
        SvgPlotOptions svgPlotOptions = guiSvgOptions.getOptions();

        if (!this.isSvgPlottOptionsValid(svgPlotOptions)) {
//            logger.error(this.bundle.getString("preview_load_error"));
            return;
        }

        GuiSvgPlott.getInstance().getRootFrameController().clearMessageLabel();
        Path svgPath = Paths.get(System.getProperty("user.home") + "/svgPlot/svg.svg");
        Path legendPath = Paths.get(System.getProperty("user.home") + "/svgPlot/svg_legend.svg");

        File svg = new File(svgPath.toString());
        svgPlotOptions.setOutput(svg);
        svgPlotOptions.finalizeOptions();

        try {
            SvgCreator creator = svgPlotOptions.getDiagramType().getInstance(svgPlotOptions);
            creator.run();

            guiSvgOptions.updatePointSpecificOptions();

            /* show created svg and legend. */
            BufferedReader br = new BufferedReader(new FileReader(svg));
            String line;
            StringBuilder sb = new StringBuilder();

            double width = svgPlotOptions.getSize().getX();
            double height = svgPlotOptions.getSize().getY();
            String svgSizeAttr;

            if (svgPlotOptions.getSize().getX() > svgPlotOptions.getSize().getY()) { // querformat
                height = height * 2;
                svgSizeAttr = "y=\"" + svgPlotOptions.getSize().getY() + "mm\">";
            } else {
                width = width * 2;
                svgSizeAttr = "x=\"" + svgPlotOptions.getSize().getX() + "mm\">";
            }

            while ((line = br.readLine()) != null) {
                if (line.contains("<svg") && line.contains("width=\"" + svgPlotOptions.getSize().x())) {
                    sb.append("<svg width=\"" + width + "mm\" height=\"" + height + "mm\" version=\"1.1\">");
                }
                sb.append(line.trim());
            }
            String line_legend;
            BufferedReader br_legend = new BufferedReader(new FileReader(legendPath.toString()));
            while ((line_legend = br_legend.readLine()) != null) {
                if (!line_legend.contains("<?xml") && !line_legend.contains("<!DOCTYPE svg")) {
                    String tempLine = line_legend;
                    if (line_legend.contains("<svg xmlns=")) {
                        tempLine = line_legend.split(">")[0] + svgSizeAttr;
                    }
                    sb.append(tempLine.trim());
                }
            }
            sb.append("</svg>");

            WebEngine webEngine = webView_svg.getEngine();
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED) {
                    adjustScaleOfWebView(webView_svg);
                }
            });

//            URL url = svg.toURI().toURL();
//            webEngine.load(url.toExternalForm());
//            webEngine.load("https://w>ww.google.com/maps/");
            webEngine.loadContent(sb.toString());
            webView_svg.setFocusTraversable(true);
//            webView_svg.requestFocus();

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
        } catch (Exception e) {
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
        //http://endmemo.com/sconvert/millimeterpixel.php
        double oneMMinPixel = 3.779528;
        String heightScript = "document.getElementsByTagName('svg')[1].getAttribute('height').split('mm')[0]";
        double docHeight = Double.parseDouble(webEngine.executeScript(heightScript).toString()) * oneMMinPixel;
        String widthScript = "document.getElementsByTagName('svg')[1].getAttribute('width').split('mm')[0]";
        double docWidth = Double.parseDouble(webEngine.executeScript(widthScript).toString()) * oneMMinPixel;

        double scrollBar = 20; // im not sure what unit is, but 20 seems to fit. (maybe pixels?)
        double webViewHeight = webView.getHeight() - scrollBar;
        double webViewWidth = webView.getWidth() - scrollBar;
        double zoom;
        if (docHeight > docWidth) {
            if (docHeight > webViewHeight) {
                zoom = webViewHeight / docHeight;
            } else {
                zoom = docHeight / webViewHeight;
            }
        } else {
            if (docWidth > webViewWidth) {
                zoom = webViewWidth / docWidth;
            } else {
                zoom = docWidth / webViewWidth;
            }
        }
        webView.setZoom(zoom);
    }

    public static String getLoggerName() {
        return logger.getName();
    }


    private boolean isSvgPlottOptionsValid(final SvgPlotOptions svgPlotOptions) {
        boolean hasErrorInXRange = svgPlotOptions.getxRange().getFrom() == svgPlotOptions.getxRange().getTo();
        boolean hasErrorInYRange = svgPlotOptions.getyRange().getFrom() == svgPlotOptions.getyRange().getTo();

        boolean hasErrorInCustomSizeWidth = svgPlotOptions.getSize().getX() < GuiSvgOptions.MINIMUM_PAGE_WIDTH;
        boolean hasErrorInCustomSizeHeight = svgPlotOptions.getSize().getY() < GuiSvgOptions.MINIMUM_PAGE_HEIGHT;

        boolean hasErrorInTextures = svgPlotOptions.getTextures().contains(null);

        boolean hasError = hasErrorInXRange || hasErrorInYRange || hasErrorInCustomSizeWidth || hasErrorInCustomSizeHeight || hasErrorInTextures;
        if (hasError) {
            GuiSvgPlott.getInstance().getRootFrameController().clearMessageLabel();
            if (hasErrorInXRange) {
                logger.error(this.bundle.getString("preview_load_xrange_error"));
            }
            if (hasErrorInYRange) {
                logger.error(this.bundle.getString("preview_load_yrange_error"));
            }
            if (hasErrorInCustomSizeWidth){
                logger.error(this.bundle.getString("preview_load_customWidth_error"));
            }
            if (hasErrorInCustomSizeHeight){
                logger.error(this.bundle.getString("preview_load_customHeight_error"));
            }
            if (hasErrorInTextures){
                logger.error(this.bundle.getString("preview_load_emptyTexture_error"));
            }
            return false;
        }


        return true;
    }
}
