package application.service;

import application.infrastructure.JacksonDeserializer.FunctionDeserializer;
import application.infrastructure.JacksonDeserializer.IntegralPlotSettingsDeserializer;
import application.infrastructure.JacksonDeserializer.PointDeserializer;
import application.infrastructure.JacksonDeserializer.RangeDeserializer;
import application.model.Preset;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.coordinatesystem.Range;
import tud.tangram.svgplot.data.Point;
import tud.tangram.svgplot.options.DiagramType;
import tud.tangram.svgplot.plotting.Function;
import tud.tangram.svgplot.plotting.IntegralPlotSettings;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The Preset Service.
 * This Singleton Class creates, updates, deletes and loads {@link Preset}s and stores it into an json-File on
 * path: System.getProperty("user.home") + "/svgPlot/presets.json".
 *
 * @author Emma Müller
 */
public class PresetService {
    private static final Logger logger = LoggerFactory.getLogger(PresetService.class);

    private static final PresetService INSTANCE = new PresetService();
    private ResourceBundle bundle;

    private Path path;
    private ObjectMapper mapper;


    private PresetService() {
        path = Paths.get(System.getProperty("user.home") + "/svgPlot/presets.json");
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Point.class, new PointDeserializer());
        module.addDeserializer(Range.class, new RangeDeserializer());
        module.addDeserializer(IntegralPlotSettings.class, new IntegralPlotSettingsDeserializer());
        module.addDeserializer(Function.class, new FunctionDeserializer());
        mapper.registerModule(module);
    }

    public static PresetService getInstance() {
        return INSTANCE;
    }

    /**
     * Creates the given {@link Preset} and adds it to the preset.json.
     * @param preset the {@link Preset} to create.
     */
    public void create(final Preset preset){
        try {
            if (!Files.exists(path))
                Files.createFile(path);

            List<Preset> presetList = getAll();
            presetList.add(preset);

            mapper.writeValue(path.toFile(), presetList);
        } catch (Exception e) {
            logger.error(bundle.getString("save_preset_error") + " " + e.getMessage());
        }
    }

    /**
     * Creates the given {@link Preset}s and adds them to the preset.json.
     * @param presets the {@link Preset}s to create.
     */
    public void createAll(final List<Preset> presets){
        try {
            if (!Files.exists(path))
                Files.createFile(path);

            List<Preset> presetList = getAll();
            presetList.addAll(presets);

            mapper.writeValue(path.toFile(), presetList);
        } catch (Exception e) {
            logger.error(bundle.getString("save_presets_error") + " " + e.getMessage());
        }
    }

    /**
     * Gets all saved {@link Preset}s.
     * @return the {@link Preset}s
     */
    public List<Preset> getAll() {
        List<Preset> presetList = new ArrayList<>();
        try {
            presetList = mapper.readValue(path.toFile(), new TypeReference<List<Preset>>() {});
        } catch (Exception e) {
            logger.error(bundle.getString("load_presets_error") + " " + e.getMessage());
            e.printStackTrace();
        }
        return presetList;
    }

    /**
     * Gets all saved {@link Preset}s with {@link DiagramType} not FunctionPlot.
     * @return the chart-{@link Preset}s
     */
    public List<Preset> getAllCharts() {
        List<Preset> presetList = new ArrayList<>();
        try {
            List<Preset> presets = mapper.readValue(path.toFile(), new TypeReference<List<Preset>>() {});
            for (Preset preset: presets ) {
                if (!preset.getDiagramType().equals(DiagramType.FunctionPlot)){
                    presetList.add(preset);
                }
            }
        } catch (Exception e) {
            logger.error(bundle.getString("load_presets_error") + " " + e.getMessage());
            e.printStackTrace();
        }
        return presetList;
    }

    /**
     * Gets all saved {@link Preset}s with {@link DiagramType} FunctionPlot.
     * @return the function-{@link Preset}s
     */
    public List<Preset> getAllFunctions() {
        List<Preset> presetList = new ArrayList<>();
        try {
            List<Preset> presets = mapper.readValue(path.toFile(), new TypeReference<List<Preset>>() {});
            for (Preset preset: presets ) {
                if (preset.getDiagramType().equals(DiagramType.FunctionPlot)){
                    presetList.add(preset);
                }
            }
        } catch (Exception e) {
            logger.error(bundle.getString("load_presets_error") + " " + e.getMessage());
            e.printStackTrace();
        }
        return presetList;
    }


    /**
     * Deletes the given {@link Preset} from json-file.
     * @param preset the {@link Preset} to delete.
     */
    public void delete(final Preset preset) {
        try {
            Preset listElement = null;
            List<Preset> presetList = getAll();
            for(Preset savedPreset: presetList){
                if (savedPreset.equals(preset)){
                    listElement = savedPreset;
                }
            }
            if (listElement != null){
                presetList.remove(listElement);
            }
            mapper.writeValue(path.toFile(), presetList);
        } catch (Exception e) {
            logger.error(bundle.getString("delete_preset_error") + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates the given {@link Preset}.
     * @param preset the {@link Preset} to save.
     */
    public void save(final Preset preset) {
        try {
            Preset listElement = new Preset(preset);
            System.out.println(preset.getOptions().getLineStyles().size());
            List<Preset> presetList = getAll();
            for(Preset savedPreset: presetList){
                if (savedPreset.equals(preset)){
                    listElement = savedPreset;
                }
            }
            presetList.remove(listElement);
            presetList.add(preset);
            mapper.writeValue(path.toFile(), presetList);
        } catch (Exception e) {
            logger.error(bundle.getString("delete_preset_error") + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Preset> findByName(final String name){
        List<Preset> resultList = new ArrayList<>();
        try {
            List<Preset> presetList = getAll();
            for(Preset savedPreset: presetList){
                if (savedPreset.getName().equals(name)){
                    resultList.add(savedPreset);
                }
            }
        } catch (Exception e) {
            logger.error(bundle.getString("find_preset_error") + " " + e.getMessage());
        }
        return resultList;
    }

    public void setBundle(final ResourceBundle bundle){
        this.bundle = bundle;
    }

}
