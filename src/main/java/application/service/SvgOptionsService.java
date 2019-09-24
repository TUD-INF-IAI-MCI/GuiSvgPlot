package application.service;

import application.GuiSvgPlott;
import application.model.GuiSvgOptions;
import application.model.OutputGenerator;
import de.tudresden.inf.mci.brailleplot.GeneralResource;
import de.tudresden.inf.mci.brailleplot.configparser.ConfigurationParsingException;
import de.tudresden.inf.mci.brailleplot.configparser.ConfigurationValidationException;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;
import javafx.scene.control.Alert;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.SvgOptions;
import tud.tangram.svgplot.options.SvgPlotOptions;
import tud.tangram.svgplot.plotting.point.PointSymbol;
import tud.tangram.svgplot.svgcreator.SvgCreator;

import javax.xml.bind.ValidationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class provides methods for {@link SvgPlotOptions}.
 *
 * @author Emma Müller
 */
public class SvgOptionsService {
    private static final Logger logger = LoggerFactory.getLogger(SvgOptionsService.class);
    private static final SvgOptionsService INSTANCE = new SvgOptionsService();
    private static final BraillePlotService brlplot = new BraillePlotService();
    private ResourceBundle bundle;
    private boolean webViewZoomUpdated = false;

    private SvgOptionsService() {
    }

    public static SvgOptionsService getInstance() {
        return INSTANCE;
    }


    /**
     * Builds the Svg.
     *
     * @param guiSvgOptions the {@link GuiSvgOptions}
     */
    public void buildSVG(GuiSvgOptions guiSvgOptions) throws ValidationException {
        SvgPlotOptions svgPlotOptions = guiSvgOptions.getOptions();
        if (!this.isSvgPlottOptionsValid(svgPlotOptions)) {
            throw new ValidationException("The SvgPlottOptions are not valid!");
        }
        GuiSvgPlott.getInstance().getRootFrameController().clearMessageLabel();
        svgPlotOptions.finalizeOptions();

        try {

            // If BraillePlot is selected as generator
            if (guiSvgOptions.getOutputGenerator().equals(OutputGenerator.BraillePlot)) {
                logger.info(bundle.getString("info_functionality_restricted"));

                if (!brlplot.isInitialized()) {
                    brlplot.initialize();
                }

                if (!guiSvgOptions.getCsvPath().isEmpty()) {
                    if (!guiSvgOptions.getBrlPlotPrint() && !guiSvgOptions.getBrlPlotSvgExport() && !guiSvgOptions.getBrlPlotTextDump()) {
                        Alert missingPrinter = new Alert(Alert.AlertType.WARNING);
                        missingPrinter.setTitle(this.bundle.getString("brailleplot_no_action"));
                        missingPrinter.setHeaderText(this.bundle.getString("brailleplot_no_output_generated"));
                        missingPrinter.setContentText(this.bundle.getString("brailleplot_please_select_action"));
                        missingPrinter.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
                        missingPrinter.showAndWait();
                        return;
                    }
                    brlplot.configure(guiSvgOptions.getBrlPlotConfig().getAbsolutePath());
                    brlplot.processInput(svgPlotOptions);
                    if (guiSvgOptions.getBrlPlotPrint()) {
                        String targetDeviceName = brlplot.print();
                        logger.info(this.bundle.getString("brailleplot_print_message") + ": " + targetDeviceName);
                    }
                    FileChooser fc = new FileChooser();
                    File initialDir = new File(System.getProperty("user.home"));
                    String initialName = (guiSvgOptions.getTitle().isEmpty() ? "untitled" : guiSvgOptions.getTitle()).toLowerCase();
                    if (guiSvgOptions.getBrlPlotSvgExport()) {
                        fc.setInitialDirectory(initialDir);
                        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Scalable Vector Graphics (SVG)", "*.svg"));
                        fc.setInitialFileName(initialName + ".svg");
                        File file = fc.showSaveDialog(GuiSvgPlott.getInstance().getPrimaryStage());
                        if (file != null) {
                            String svgPath = file.getAbsolutePath();
                            String svgLegendPath = svgPath.substring(0, svgPath.length() - 4) + "_legend.svg";
                            brlplot.svgExport(svgPath, svgLegendPath);
                            logger.info(this.bundle.getString("brailleplot_svg_export_message") + ": " + file.getAbsolutePath());
                            initialDir = file.getParentFile(); // save location for txt dump
                        }
                    }
                    if (guiSvgOptions.getBrlPlotTextDump()) {
                        fc.setInitialDirectory(initialDir);
                        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Dump", "*.bin"));
                        fc.setInitialFileName(initialName + ".txt");
                        File file = fc.showSaveDialog(GuiSvgPlott.getInstance().getPrimaryStage());
                        if (file != null) {
                            String txtPath = file.getAbsolutePath();
                            String txtLegendPath = txtPath.substring(0, txtPath.length() - 4) + "_legend.txt";
                            logger.info(this.bundle.getString("brailleplot_text_dump_message") + ": " + file.getAbsolutePath());
                            brlplot.textDump(txtPath, txtLegendPath);
                        }
                    }
                }
            }

            // If SvgPlot is selected as generator
            if (guiSvgOptions.getOutputGenerator().equals(OutputGenerator.SvgPlot)) {
                SvgCreator creator = svgPlotOptions.getDiagramType().getInstance(svgPlotOptions);
                creator.run();
                logger.info(this.bundle.getString("chart_creation_success_message") + " " + svgPlotOptions.getOutput());
            }
        } catch (BraillePlotService.PrinterNotInstalledException e) {
            Alert missingPrinter = new Alert(Alert.AlertType.ERROR);
            missingPrinter.setTitle(this.bundle.getString("brailleplot_printer_not_installed"));
            missingPrinter.setHeaderText(this.bundle.getString("brailleplot_no_printer_with_name") + ": '" + e.getDeviceName() + "'");
            missingPrinter.setContentText(this.bundle.getString("brailleplot_check_config_printer_name") + ": " + guiSvgOptions.getBrlPlotConfig().getAbsolutePath());
            missingPrinter.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            missingPrinter.showAndWait();
        } catch (Exception e) {
            logger.error(this.bundle.getString("chart_creation_error") + " " + e.getMessage());
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
        Path svgPath = Paths.get(System.getProperty("user.home") + "//svgPlot/svg.svg");
        Path legendPath = Paths.get(System.getProperty("user.home") + "//svgPlot/svg_legend.svg");

        svgPath.toFile().delete();
        legendPath.toFile().delete();

        File svg = new File(svgPath.toString());
        svgPlotOptions.setOutput(svg);
        svgPlotOptions.finalizeOptions();

        try {
            // If BraillePlot is selected as generator
            if (guiSvgOptions.getOutputGenerator().equals(OutputGenerator.BraillePlot)) {
                logger.info(bundle.getString("info_functionality_restricted"));

                if (!brlplot.isInitialized()) {
                    brlplot.initialize();
                }

                if (Objects.nonNull(guiSvgOptions.getCsvPath()) && !guiSvgOptions.getCsvPath().isEmpty()) {
                    brlplot.configure(guiSvgOptions.getBrlPlotConfig().getAbsolutePath());
                    brlplot.processInput(svgPlotOptions);
                    brlplot.svgExport(svgPath.toString(), legendPath.toString());
                }
            }

            // If SvgPlot is selected as generator
            if (guiSvgOptions.getOutputGenerator().equals(OutputGenerator.SvgPlot)) {
                SvgCreator creator = svgPlotOptions.getDiagramType().getInstance(svgPlotOptions);
                creator.run();
            }

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
            webViewZoomUpdated = false;

            WebEngine webEngine = webView_svg.getEngine();
            webEngine.getLoadWorker().stateProperty().addListener((observable, oldState, newState) -> {
                if (newState == Worker.State.SUCCEEDED && !webViewZoomUpdated) {
                    adjustScaleOfWebView(webView_svg);
                    webViewZoomUpdated = true;
                }
            });

            webEngine.loadContent(sb.toString());
            webView_svg.setFocusTraversable(true);
//            webView_svg.requestFocus();

            // accessibility
            if (guiSvgOptions.getOutputGenerator().equals(OutputGenerator.SvgPlot)) {
                Path descPath = Paths.get(svg.getParentFile().getPath() + "/" + svg.getName().replace(".svg", "_desc.html"));
                String description = loadDescription(descPath.toString());
                webView_svg.setAccessibleHelp(bundle.getString("preview") + ": " + description);
            } else {
                webView_svg.setAccessibleHelp(bundle.getString("preview"));
            }

            // reset zoom
            ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
                adjustScaleOfWebView(webView_svg);
            };
            Window window = webView_svg.getScene().getWindow();
            window.widthProperty().addListener(stageSizeListener);
            window.heightProperty().addListener(stageSizeListener);

        } catch (BraillePlotService.LibLouisLibraryMissingException e) {
            logger.error(this.bundle.getString("liblouis_missing") + " '" + System.getProperty("jna.library.path") + "'");
            e.printStackTrace();
        } catch (InsufficientRenderingAreaException e) {
            logger.error(this.bundle.getString("brailleplot_insufficient_area") + ": " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalStateException | ConfigurationValidationException | ConfigurationParsingException e) {
            logger.error(this.bundle.getString("brailleplot_config_error") + ": " + e.getMessage());
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            logger.warn(this.bundle.getString("no_output_generated"));
        } catch (ClassCastException e) {
            logger.warn(this.bundle.getString("preview_pointlist_warning"));
        } catch (NullPointerException e){
            if (svgPlotOptions.getDiagramType().equals(DiagramType.BarChart)){
                logger.error(this.bundle.getString("preview_barchart_error"));
                e.printStackTrace();
            } else {
                logger.error(this.bundle.getString("preview_load_error") + " " + e.getClass());
                e.printStackTrace();
            }
        } catch (Exception e) {
            logger.error(this.bundle.getString("preview_load_error") + " " + e.getClass());
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
        double currentZoom = webView.getZoom();
        //webView.setZoom(1.0); WHY???
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
        if (zoom != currentZoom) {
            webView.setZoom(zoom);
        }
    }

    public static String getLoggerName() {
        return logger.getName();
    }


    private boolean isSvgPlottOptionsValid(final SvgPlotOptions svgPlotOptions) {
        DiagramType diagramType = svgPlotOptions.getDiagramType();

        double xFrom = svgPlotOptions.getxRange() != null ? svgPlotOptions.getxRange().getFrom() : -8;
        double xTo = svgPlotOptions.getxRange() != null ? svgPlotOptions.getxRange().getTo() : 8;
        boolean hasErrorInXRange = svgPlotOptions.getxRange() != null && xFrom >= xTo;

        double yFrom = svgPlotOptions.getyRange() != null ? svgPlotOptions.getyRange().getFrom() : -8;
        double yTo = svgPlotOptions.getyRange() != null ? svgPlotOptions.getyRange().getTo() : 8;
        boolean hasErrorInYRange = svgPlotOptions.getyRange() != null && yFrom >= yTo;

        boolean hasErrorInCustomSizeWidth = svgPlotOptions.getSize().getX() < GuiSvgOptions.MINIMUM_PAGE_WIDTH;
        boolean hasErrorInCustomSizeHeight = svgPlotOptions.getSize().getY() < GuiSvgOptions.MINIMUM_PAGE_HEIGHT;

        boolean hasErrorInTextures = diagramType.equals(DiagramType.BarChart) && svgPlotOptions.getTextures().contains(null);
        boolean hasErrorInLineTypes = diagramType.equals(DiagramType.LineChart) && svgPlotOptions.getLineStyles().contains(null);

        List<PointSymbol> pointSymbols = svgPlotOptions.getPointSymbols();
        String showLinePoints = svgPlotOptions.getShowLinePoints();
        boolean hasErrorInPointSymbols = diagramType.equals(DiagramType.ScatterPlot) && pointSymbols.contains(null)
                || diagramType.equals(DiagramType.LineChart) && showLinePoints != null && showLinePoints.equals("on") && pointSymbols.contains(null);

        boolean hasErrorInColors = svgPlotOptions.getCustomColors() != null && svgPlotOptions.getCustomColors().contains(null);

        boolean hasError = hasErrorInXRange || hasErrorInYRange || hasErrorInCustomSizeWidth || hasErrorInCustomSizeHeight || hasErrorInTextures || hasErrorInLineTypes || hasErrorInPointSymbols || hasErrorInColors;
        if (hasError) {
            GuiSvgPlott.getInstance().getRootFrameController().clearMessageLabel();
            if (hasErrorInXRange) {
                if (xFrom == xTo) {
                    logger.error(this.bundle.getString("preview_load_xrange_equal_error"));
                }
                if (xFrom > xTo) {
                    logger.error(this.bundle.getString("preview_load_xrange_error"));
                }
            }
            if (hasErrorInYRange) {
                if (yFrom == yTo) {
                    logger.error(this.bundle.getString("preview_load_yrange_equal_error"));
                }
                if (yFrom > yTo) {
                    logger.error(this.bundle.getString("preview_load_yrange_error"));
                }

            }
            if (hasErrorInCustomSizeWidth) {
                logger.error(this.bundle.getString("preview_load_customWidth_error"));
            }
            if (hasErrorInCustomSizeHeight) {
                logger.error(this.bundle.getString("preview_load_customHeight_error"));
            }
            if (hasErrorInTextures) {
                logger.error(this.bundle.getString("preview_load_emptyTexture_error"));
            }
            if (hasErrorInLineTypes) {
                logger.error(this.bundle.getString("preview_load_emptyLineStyle_error"));
            }
            if (hasErrorInPointSymbols) {
                logger.error(this.bundle.getString("preview_load_emptyPointSymbol_error"));
            }
            if (hasErrorInColors) {
                logger.error(this.bundle.getString("preview_load_emptyColor_error"));
            }
            return false;
        }


        return true;
    }
}
