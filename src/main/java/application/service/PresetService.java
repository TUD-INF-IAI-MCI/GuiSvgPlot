package application.service;

import application.model.Preset;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PresetService {
    private static final Logger logger = LoggerFactory.getLogger(PresetService.class);

    private static final PresetService INSTANCE = new PresetService();
    private ResourceBundle bundle;

    private Path path;
    private ObjectMapper mapper;

    private PresetService() {
        path = Paths.get(System.getProperty("user.home") + "/svgPlot/presets.json");
        mapper = new ObjectMapper();
    }

    public static PresetService getInstance() {
        return INSTANCE;
    }

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

    public List<Preset> getAll() {
        List<Preset> presetList = new ArrayList<>();
        try {
            presetList = mapper.readValue(path.toFile(), new TypeReference<List<Preset>>() {});
        } catch (IOException e) {
            logger.error(bundle.getString("load_presets_error") + " " + e.getMessage());
        }
        return presetList;
    }

    public void delete(final Preset preset) {
        try {
            Preset listElement = new Preset();
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

    public void save(final Preset preset) {
        try {
            List<Preset> presetList = getAll();
            for(Preset savedPreset: presetList){
                if (savedPreset.equals(preset)){
                   savedPreset.update(preset);
                }
            }

            mapper.writeValue(path.toFile(), presetList);
        } catch (Exception e) {
            logger.error(bundle.getString("delete_preset_error") + " " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setBundle(final ResourceBundle bundle){
        this.bundle = bundle;
    }

}
