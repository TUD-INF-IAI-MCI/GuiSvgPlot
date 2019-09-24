package application.service;

import de.tudresden.inf.mci.brailleplot.GeneralResource;
import de.tudresden.inf.mci.brailleplot.configparser.*;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvOrientation;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvParser;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvType;
import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.printabledata.MatrixData;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrintDirector;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrinterCapability;
import de.tudresden.inf.mci.brailleplot.rendering.MasterRenderer;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolMatrixDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.SvgExporter;
import org.liblouis.Louis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class BraillePlotService {

    private String mConfigPath;
    private String mDefaultConfigPath = "/config/default.properties";
    private String mSemanticTablePath = GeneralResource.getOrExportResourceFile("mapping/kurzschrift_DE.properties").getAbsolutePath();
    private String mBrailleTablePath = GeneralResource.getOrExportResourceFile("mapping/eurobraille.properties").getAbsolutePath();
    private JavaPropertiesConfigurationParser mConfigParser;
    Printer mPrinter;
    Format mFormat;
    Format mDefaultFormat;
    MasterRenderer mRenderer;
    RasterCanvas mCanvas;
    private boolean mInitialized = false;

    private static final Logger mLogger = LoggerFactory.getLogger(BraillePlotService.class);


    public BraillePlotService() {
    }

    public void initialize() throws LibLouisLibraryMissingException {


        // JNA Lib Path for liblouis
        Path libPath = Paths.get(System.getProperty("user.home") + "/svgPlot/braillePlot/");
        System.setProperty("jna.library.path", libPath.toAbsolutePath().toString());
        try
        {
            // Simple test if lib is available.
            Louis.getVersion();
        }
        catch(java.lang.UnsatisfiedLinkError e) {
            throw new LibLouisLibraryMissingException(e);
        }

        mInitialized = true;
    }

    public void configure(String configPath) throws ConfigurationParsingException, ConfigurationValidationException {
        mLogger.info("Configuring brailleplot: " + configPath);
        mConfigPath = configPath;
        mConfigParser = new JavaPropertiesConfigurationParser(mConfigPath, mDefaultConfigPath);
        mPrinter = mConfigParser.getPrinter();
        mPrinter.override(new PrinterProperty("semantictable", mSemanticTablePath));
        mPrinter.override(new PrinterProperty("brailletable", mBrailleTablePath));
        mDefaultFormat = mConfigParser.getFormat("default");
    }

    public void processInput(SvgPlotOptions input) throws IOException, InsufficientRenderingAreaException {

        mLogger.info("Setting up Format");
        String formatName = "external";
        Point formatSize = input.getSize();
        List<FormatProperty> formatProperties = new ArrayList<>();
        formatProperties.add(new FormatProperty(formatName, "page.width", Integer.toString((int) formatSize.getX())));
        formatProperties.add(new FormatProperty(formatName, "page.height", Integer.toString((int) formatSize.getY())));
        mFormat = new Format(formatProperties, formatName);
        mFormat.setFallback(mDefaultFormat);

        mLogger.info("Setting up Master Renderer");
        mRenderer = new MasterRenderer(mPrinter, mFormat);

        mLogger.info("Creating diagram");
        Diagram diagram = createRenderableDiagramFromCSV(input);

        mLogger.info("Rendering");
        render(diagram);
    }

    public Diagram createRenderableDiagramFromCSV(SvgPlotOptions input) throws IOException {

        // Parse csv data
        String csvPath = input.getCsvPath();
        DiagramType type = input.getDiagramType();
        InputStream csvStream = new FileInputStream(csvPath);
        Reader csvReader = new BufferedReader(new InputStreamReader(csvStream));
        CsvParser csvParser = new CsvParser(csvReader, ',', '\"');

        Diagram diagram;

        switch (type) {
            case ScatterPlot:
                throw new UnsupportedOperationException("Diagram type currently not implemented: " + type);
            case LineChart:
                throw new UnsupportedOperationException("Diagram type currently not implemented: " + type);
            case BarChart:
                CategoricalPointListContainer<PointList> container = csvParser.parse(CsvType.X_ALIGNED_CATEGORIES, CsvOrientation.HORIZONTAL);
                diagram = new CategoricalBarChart(container);
                break;
            default:
                throw new IllegalArgumentException("Unsupported diagram type: " + type);
        }

        diagram.setTitle(Objects.requireNonNullElse(input.getTitle(), ""));
        diagram.setXAxisName(Objects.requireNonNullElse(input.getxUnit(), "")); // the methods have strange names they do actually
        diagram.setYAxisName(Objects.requireNonNullElse(input.getyUnit(), "")); // return the axis Name + Unit

        return diagram;
    }

    public void render(Renderable data) throws InsufficientRenderingAreaException {
        // Render diagram
        mCanvas = mRenderer.rasterize(data);
    }

    public String print() throws NoPrintServiceException, PrinterNotInstalledException {
        // Check if some SpoolerService/Printservice exists
        if (!PrintDirector.isPrintServiceOn()) {
            throw new NoPrintServiceException();
        }
        // Printing
        String printerName = mPrinter.getProperty("name").toString();
        String printerMode = mPrinter.getProperty("mode").toString().toUpperCase();
        PrintDirector printD = new PrintDirector(PrinterCapability.valueOf(printerMode), mPrinter);
        Iterator<MatrixData<Boolean>> pages = mCanvas.getPageIterator();
        try {
            while (pages.hasNext()) {
                printD.print(pages.next());
            }
        } catch (RuntimeException e) {
            throw new PrinterNotInstalledException(printerName);
        }
        return printerName;

    }

    public void textDump(String txtPath, String txtLegendPath) {
    }

    public void svgExport(String svgPath, String svgLegendPath) throws IOException {
        SvgExporter<RasterCanvas> svgExporter = new BoolMatrixDataSvgExporter(mCanvas);
        svgExporter.render();
        svgExporter.dump(svgPath, 0);
        svgExporter.dump(svgLegendPath, 1);
    }


    public boolean isInitialized() {
        return mInitialized;
    }

    class LibLouisLibraryMissingException extends Exception {
        LibLouisLibraryMissingException(Throwable cause) {
            super(cause);
        }
    }

    class NoPrintServiceException extends Exception {
        NoPrintServiceException() {

        }
    }

    class PrinterNotInstalledException extends Exception {
        private String mDeviceName;
        PrinterNotInstalledException(String deviceName) {
            mDeviceName = deviceName;
        }

        String getDeviceName() {
            return mDeviceName;
        }
    }
}
