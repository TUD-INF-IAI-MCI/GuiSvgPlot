package application.service;

import de.tudresden.inf.mci.brailleplot.configparser.*;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvOrientation;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvParser;
import de.tudresden.inf.mci.brailleplot.csvparser.CsvType;
import de.tudresden.inf.mci.brailleplot.csvparser.MalformedCsvException;
import de.tudresden.inf.mci.brailleplot.datacontainers.CategoricalPointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointList;
import de.tudresden.inf.mci.brailleplot.datacontainers.PointListContainer;
import de.tudresden.inf.mci.brailleplot.datacontainers.SimpleCategoricalPointListContainerImpl;
import de.tudresden.inf.mci.brailleplot.diagrams.CategoricalBarChart;
import de.tudresden.inf.mci.brailleplot.diagrams.Diagram;
import de.tudresden.inf.mci.brailleplot.diagrams.LineChart;
import de.tudresden.inf.mci.brailleplot.diagrams.ScatterPlot;
import de.tudresden.inf.mci.brailleplot.layout.AbstractCanvas;
import de.tudresden.inf.mci.brailleplot.layout.InsufficientRenderingAreaException;
import de.tudresden.inf.mci.brailleplot.layout.PlotCanvas;
import de.tudresden.inf.mci.brailleplot.layout.RasterCanvas;
import de.tudresden.inf.mci.brailleplot.printabledata.PrintableData;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrintDirector;
import de.tudresden.inf.mci.brailleplot.printerbackend.PrinterCapability;
import de.tudresden.inf.mci.brailleplot.rendering.LiblouisBrailleTextRasterizer;
import de.tudresden.inf.mci.brailleplot.rendering.MasterRenderer;
import de.tudresden.inf.mci.brailleplot.rendering.Renderable;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolFloatingPointDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.BoolMatrixDataSvgExporter;
import de.tudresden.inf.mci.brailleplot.svgexporter.SvgExporter;
import de.tudresden.inf.mci.brailleplot.util.GeneralResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.options.SvgPlotOptions;

import java.io.*;
import java.util.*;

public class BraillePlotService {

    private String mConfigPath;
    private String mDefaultConfigPath = "/config/default.properties";
    private String mSemanticTablePath = GeneralResource.getOrExportResourceFile("mapping/kurzschrift_DE.properties").getAbsolutePath();
    private String mBrailleTablePath = GeneralResource.getOrExportResourceFile("mapping/eurobraille.properties").getAbsolutePath();
    private JavaPropertiesConfigurationParser mConfigParser;
    Printer mPrinter;
    Representation mRepresentation;
    Format mFormat;
    PrinterCapability mMode;
    Format mDefaultFormat;
    MasterRenderer mRenderer;
    AbstractCanvas<? extends PrintableData> mCanvas;
    SvgExporter<? extends AbstractCanvas> mSvgExporter;
    PrintDirector mPrintDirector;
    private boolean mInitialized = false;

    private static final Logger mLogger = LoggerFactory.getLogger(BraillePlotService.class);


    public BraillePlotService() {
    }

    public void initialize() throws LiblouisBrailleTextRasterizer.LibLouisLibraryMissingException {

        /*
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

         */

        LiblouisBrailleTextRasterizer.initModule();

        mInitialized = true;
    }

    public void configure(String configPath) throws ConfigurationParsingException, ConfigurationValidationException {
        mLogger.info("Configuring brailleplot: " + configPath);
        mConfigPath = configPath;
        mConfigParser = new JavaPropertiesConfigurationParser(new File(configPath).toPath(), getClass().getResource(mDefaultConfigPath));
        mPrinter = mConfigParser.getPrinter();
        mPrinter.override(new PrinterProperty("semantictable", mSemanticTablePath));
        mPrinter.override(new PrinterProperty("brailletable", mBrailleTablePath));
        mRepresentation = mConfigParser.getRepresentation();
        mDefaultFormat = mConfigParser.getFormat("default");
        mMode = PrinterCapability.valueOf(mPrinter.getProperty("mode").toString().toUpperCase());
        mPrintDirector = new PrintDirector(mMode, mPrinter);
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
        mRenderer = new MasterRenderer(mPrinter, mRepresentation, mFormat);

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

        /*
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
         */

        switch (type) {
            case ScatterPlot:
                PointListContainer<PointList> scatterPlotContainer = csvParser.parse(CsvType.DOTS, CsvOrientation.HORIZONTAL);
                diagram = new ScatterPlot(scatterPlotContainer);
                break;
            case LineChart:
                PointListContainer<PointList> lineChartContainer = csvParser.parse(CsvType.DOTS, CsvOrientation.HORIZONTAL);
                diagram = new LineChart(lineChartContainer);
                break;
            case BarChart:
                CategoricalPointListContainer<PointList> barChartContainer;
                try { // first try to parse as regular bar chart and convert to single category bar cart.
                    barChartContainer = new SimpleCategoricalPointListContainerImpl(csvParser.parse(CsvType.X_ALIGNED, CsvOrientation.HORIZONTAL));
                } catch (MalformedCsvException e) { // else parse as categorical bar chart
                    barChartContainer = csvParser.parse(CsvType.X_ALIGNED_CATEGORIES, CsvOrientation.HORIZONTAL);
                }
                diagram = new CategoricalBarChart(barChartContainer);
                break;
            default: throw new IllegalStateException("Unknown diagram type: " + type);
        }

        diagram.setTitle(Objects.requireNonNullElse(input.getTitle(), " "));
        diagram.setXAxisName(Objects.requireNonNullElse(input.getxUnit(), " ")); // the methods have strange names they do actually
        diagram.setYAxisName(Objects.requireNonNullElse(input.getyUnit(), " ")); // return the axis Name + Unit

        return diagram;
    }

    public void render(Renderable data) throws InsufficientRenderingAreaException {
        // Render diagram
        switch (mMode) { // Decide on correct rendering mode to apply
            case NORMALPRINTER:
                RasterCanvas rasterCanvas = mRenderer.rasterize(data);
                mSvgExporter = new BoolMatrixDataSvgExporter(rasterCanvas);
                mCanvas = rasterCanvas;
                break;
            case INDEX_EVEREST_D_V4_FLOATINGDOT_PRINTER:
                PlotCanvas plotCanvas = mRenderer.plot(data);
                mSvgExporter = new BoolFloatingPointDataSvgExporter(plotCanvas);
                mCanvas = plotCanvas;
                break;
            default: throw new UnsupportedOperationException("Mode not supported: " + mMode);
        }
    }

    public String print() throws NoPrintServiceException, PrinterNotInstalledException {
        /*
        // Check if some SpoolerService/Printservice exists
        if (!PrintDirector.isPrintServiceOn()) {
            throw new NoPrintServiceException();
        }
        // Printing
        String printerName = mPrinter.getProperty("name").toString();
        String printerMode = mPrinter.getProperty("mode").toString().toUpperCase();
        PrintDirector mPrintDirector = new PrintDirector(PrinterCapability.valueOf(printerMode), mPrinter);
        ListIterator<? extends PrintableData> pages = mCanvas.getPageIterator();
        try {
            while (pages.hasNext()) {
                mPrintDirector.print(pages.next());
            }
        } catch (RuntimeException e) {
            throw new PrinterNotInstalledException(printerName);
        }
        return printerName;
        */

        String printerName = mPrinter.getProperty("name").toString();
        if (!PrintDirector.isPrintServiceOn()) { // Check for running spooler or print service
            throw new NoPrintServiceException();
        }
        ListIterator<? extends PrintableData> outputPages = mCanvas.getPageIterator();
        try {
            while (outputPages.hasNext()) { // Iterate pages
                PrintableData page = outputPages.next();
                mPrintDirector.print(page);
            }
        } catch (RuntimeException e) {
            throw new PrinterNotInstalledException(printerName);
        }
        return printerName;
    }

    public void textDump(String binPath, String binLegendPath) {
        File binFile = new File(binPath);
        String binParent = binFile.getParentFile().getAbsolutePath();
        String[] binSegments = binFile.getName().split("\\.", 2);
        String binName = binSegments[0];
        String binExtension;
        if (binSegments.length > 1) {
            binExtension = binSegments[1];
        } else {
            binExtension = "bin";
        }

        int pageNumber = 0;
        ListIterator<? extends PrintableData> outputPages = mCanvas.getPageIterator();
        while (outputPages.hasNext()) { // Iterate pages
            PrintableData page = outputPages.next();
            String outputPath;
            if (outputPages.hasNext()) {
                outputPath = binParent + File.separator + binName + ((pageNumber > 0) ? (pageNumber + 1) : "") + "." + binExtension;
            } else {
                outputPath = new File(binLegendPath).getAbsolutePath();
            }
            try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                outputStream.write(mPrintDirector.byteDump(page));
            } catch (IOException ex) {
                // Inform user, but do not stop execution
                mLogger.error("An error occured while creating byte dump", ex);
                throw new RuntimeException();
            }
            pageNumber++;
        }
    }

    public void svgExport(String svgPath, String svgLegendPath) throws IOException {
        File svgFile = new File(svgPath);
        String svgParent = svgFile.getParentFile().getAbsolutePath();
        String[] svgSegments = svgFile.getName().split("\\.", 2);
        String svgName = svgSegments[0];
        String svgExtension;
        if (svgSegments.length > 1) {
            svgExtension = svgSegments[1];
        } else {
            svgExtension = "svg";
        }

        mSvgExporter.render();
        int pageNumber = 0;
        ListIterator<? extends PrintableData> outputPages = mCanvas.getPageIterator();
        while (outputPages.hasNext()) { // Iterate pages
            outputPages.next();
            String outputPath;
            if (outputPages.hasNext()) {
                outputPath = svgParent + File.separator + svgName + ((pageNumber > 0) ? (pageNumber + 1) : "") + "." + svgExtension;
            } else {
                outputPath = new File(svgLegendPath).getAbsolutePath();
            }
            mSvgExporter.dump(outputPath, pageNumber);
            pageNumber++;
        }
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
