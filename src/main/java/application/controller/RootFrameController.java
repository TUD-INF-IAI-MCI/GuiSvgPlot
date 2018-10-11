package application.controller;

import application.GuiSvgPlott;
import application.controller.wizard.SVGWizardController;
import application.controller.wizard.chart.ChartWizardFrameController;
import application.model.Preset;
import application.model.Settings;
import application.service.PresetService;
import application.util.DialogUtil;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Robert Schlegel, Emma Müller, Constantin Amend
 */
public class RootFrameController implements Initializable {

	@FXML
	public AnchorPane anchorPane_loading;

	@FXML
	public BorderPane borderPane_Content;

	@FXML
	private Label label_Headline;

	@FXML
	public Button button_StartDiagram;

	@FXML
	public Button button_StartFunction;

	@FXML
	public AnchorPane anchorPain_main;

	@FXML
	private MenuItem menuItem_About;

	@FXML
	private MenuItem menuItem_csvHelp;

	@FXML
	private MenuItem menuItem_settings;
	@FXML
	public Menu menu_Presets = new Menu();
	@FXML
	public MenuItem menuItem_Save_Preset;
	@FXML
	public MenuItem menuItem_Preset_Editor;

	@FXML
	public ScrollPane scrollPane_message;

	@FXML
	public VBox vBox_messages;

	private Node center;

	private ResourceBundle bundle;

	public SVGWizardController svgWizardController;

	public PresetsController presetsController = PresetsController.getInstance();

	private PresetService presetService = PresetService.getInstance();

	private DialogUtil dialogUtil = DialogUtil.getInstance();

	public SimpleBooleanProperty loading;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.bundle = resources;
	}

	public void init() {
		this.presetService.setBundle(bundle);
		loading = new SimpleBooleanProperty(false);
		anchorPane_loading.visibleProperty().bindBidirectional(loading);
		/*
		 * loading.addListener((observable, oldValue, newValue) -> {
		 * System.out.println(newValue); anchorPane_loading.setVisible(newValue); }); //
		 * loading.set(false);
		 */

		this.button_StartDiagram.setOnAction(this::startDiagram);
		this.button_StartFunction.setOnAction(event -> startFunction(true));

		this.menuItem_settings.setOnAction(event -> GuiSvgPlott.getInstance().setSettingsDialog());
		this.menuItem_Save_Preset.setDisable(true);
		this.menuItem_Save_Preset.setOnAction(event -> {
			saveAsPreset();
		});

		this.menuItem_About.setOnAction(event -> {
			Alert alert = dialogUtil.alert(Alert.AlertType.INFORMATION, "menu_help_about_title",
					"menu_help_about_content", "menu_help_about_content");
			alert.setHeaderText(null);
			alert.setResizable(true);
			alert.getDialogPane().setMinSize(500, 250);
			dialogUtil.styleDialog(alert);

			alert.showAndWait();
		});

		this.menuItem_csvHelp.setOnAction(event -> {
			showCsvHelper();
		});

		this.menuItem_About.setOnAction(event -> {
			Alert alert = dialogUtil.alert(Alert.AlertType.INFORMATION, "menu_help_about_title",
					"menu_help_about_content", "menu_help_about_content");
			alert.setHeaderText(null);
			alert.setResizable(true);
			alert.getDialogPane().setMinSize(500, 250);
			alert.showAndWait();
		});

		this.menuItem_csvHelp.setOnAction(event -> {
			showCsvHelper();
		});

	}

	public void saveAsPreset() {
		if (svgWizardController != null) {
			Preset savedPreset = new Preset(svgWizardController.getGuiSvgOptions(), "tempName",
					svgWizardController.getGuiSvgOptions().getDiagramType());
			TextInputDialog nameDialogue = dialogUtil.textInputDialog("prompt_preset_name_title",
					"prompt_preset_name_header", "prompt_preset_name_content");
			String headerText = bundle.getString("prompt_preset_name_header");
			if (svgWizardController instanceof ChartWizardFrameController) {
				headerText = headerText.concat("\n" + this.bundle.getString("prompt_preset_name_header_sub"));
			}
			nameDialogue.setHeaderText(headerText);
			dialogUtil.styleDialog(nameDialogue);

			Optional<String> result = nameDialogue.showAndWait();
			if (result.isPresent() && result.get().equals("")) {
				showErrorAlert(bundle.getString("alert_preset_empty_title"),
						bundle.getString("alert_preset_empty_header"), bundle.getString("alert_preset_empty_content"));
			} else if (result.isPresent() && presetService.findByName(result.get()).size() == 0) {
				savedPreset.setName(result.get());
				presetService.create(savedPreset);
				Alert alert = dialogUtil.alert(Alert.AlertType.CONFIRMATION, "alert_preset_created_title",
						"alert_preset_created_header", "alert_preset_created_content");
				alert.getDialogPane().setMinSize(500, 150);
				alert.showAndWait();
			} else if (result.isPresent()) {
				String header = bundle.getString("alert_preset_duplicate_header1") + result.get()
						+ bundle.getString("alert_preset_duplicate_header2");
				showErrorAlert(bundle.getString("alert_preset_duplicate_title"), header,
						bundle.getString("alert_preset_duplicate_content"));
			}
		}
	}

	private void showCsvHelper() {

		FXMLLoader loader = new FXMLLoader();
		loader.setResources(bundle);
		loader.setLocation((GuiSvgPlott.CsvFormatHelper));
		try {

			Stage stage = new Stage();
			stage.setTitle(this.bundle.getString("menu_help_csv"));
			stage.getIcons().add(Settings.getInstance().favicon);
			Scene s = new Scene(loader.load());
			stage.setScene(s);

			((Button) stage.getScene().lookup("#button_close")).setOnAction(event -> {
				stage.close();
			});

			stage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void closeWizard() {
		this.setSceneTitle("application_title");
		this.label_Headline.setText(this.bundle.getString("application_title"));
		borderPane_Content.setCenter(center);
	}

	private void startFunction(boolean isExtended) {
		this.setSceneTitle("application_create_function_title");
		this.label_Headline.setText(this.bundle.getString("headline_function"));
		startWizard(GuiSvgPlott.FunctionWizardFrame, isExtended);
	}

	private void startDiagram(ActionEvent event) {
		this.setSceneTitle("application_create_chart_title");
		this.label_Headline.setText(this.bundle.getString("headline_chart"));
		startWizard(GuiSvgPlott.ChartWizardFrame, false);
	}

	@FXML
	private void closeButtonAction() {
		Alert alert = dialogUtil.alert(Alert.AlertType.CONFIRMATION, "alert_exit_title", "alert_exit_header",
				"alert_exit_content");
		alert.setResizable(true);
		alert.getDialogPane().setMinWidth(400);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// ... user chose OK
			GuiSvgPlott.getInstance().getPrimaryStage().close();
		} else {
			// ... user chose CANCEL or closed the dialog
		}
	}

	@FXML
	private void startDiagramDefaultPreset() {
		this.setSceneTitle("application_create_chart_title");
		this.label_Headline.setText(this.bundle.getString("headline_chart"));
		startWizard(GuiSvgPlott.ChartWizardFrame, false);
	}

	@FXML
	private void startFunctionDefaultPreset() {
		this.setSceneTitle("application_create_function_title");
		this.label_Headline.setText(this.bundle.getString("headline_function"));
		startWizard(GuiSvgPlott.FunctionWizardFrame, false);
	}

	/**
	 * handles the open preset editor onclick
	 */
	@FXML
	public void startPresetOverview() {
		// checks whether the PresetOverview window has been opened from within the
		// Function/Chartwizard Frame -> fixes #24
		if (!menuItem_Save_Preset.disableProperty().get()) {
			GuiSvgPlott.getInstance().getRootFrameController().scrollPane_message.setVisible(false);
			if (GuiSvgPlott.getInstance().closeWizard(false)) {
				menuItem_Preset_Editor.setDisable(true);
				menuItem_Save_Preset.setDisable(true);
				this.setSceneTitle("application_preset_overview");
				this.label_Headline.setText(this.bundle.getString("headline_presets"));

			}
		}

		FXMLLoader loader = new FXMLLoader();
		loader.setResources(bundle);
		loader.setLocation((GuiSvgPlott.PresetOverviewFrame));
		try {
			center = borderPane_Content.getCenter();
			borderPane_Content.setCenter(loader.load());
			if (presetsController == null) {
				presetsController = loader.getController();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void startWizard(URL fxmlPath, boolean isExtended) {
		this.clearMessageLabel();
		FXMLLoader loader = new FXMLLoader();
		loader.setResources(bundle);
		loader.setLocation((fxmlPath));
		menuItem_Save_Preset.setDisable(false);

		try {
			center = borderPane_Content.getCenter();
			borderPane_Content.setCenter(loader.load());
			svgWizardController = ((SVGWizardController) loader.getController());
			svgWizardController.setExtended(isExtended);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clearMessageLabel() {
//        this.scrollPane_message.setContent(new Text());
//        this.scrollPane_message.setVisible(false);
//        this.scrollPane_message.getStyleClass().clear();
		this.scrollPane_message.setAccessibleText(bundle.getString("no_error_messages"));
		this.vBox_messages.getChildren().clear();

		try {
			this.svgWizardController.vBox_infos.getChildren().clear();
			this.svgWizardController.vBox_warnings.getChildren().clear();
			this.svgWizardController.button_Warnings.setGraphic(this.svgWizardController.getWarnIcon());
			this.svgWizardController.button_Warnings.setDisable(true);
			this.svgWizardController.button_Infos.setGraphic(this.svgWizardController.getInfoIcon());
			this.svgWizardController.button_Infos.setDisable(true);
		} catch (Exception e) {
		}
	}

	public void setSceneTitle(final String messageCode) {
		Stage scene = GuiSvgPlott.getInstance().getPrimaryStage();
		scene.titleProperty().set(this.bundle.getString(messageCode));
	}

	private void showErrorAlert(String title, String header, String content) {
		Alert alert = dialogUtil.alertWithTexts(Alert.AlertType.ERROR, title, header, content);
		alert.showAndWait();
	}
}
