package application.util;

import javafx.collections.FXCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tud.tangram.svgplot.options.DiagramType;

import java.util.ResourceBundle;

/**
 *
 * @author Emma Müller
 */
public class DiagramTypeUtil {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ChoiceBoxUtil.class);
	private static final DiagramTypeUtil INSTANCE = new DiagramTypeUtil();
	private ResourceBundle bundle;

	private DiagramTypeUtil() {
	}

	public static DiagramTypeUtil getInstance() {
		return INSTANCE;
	}

	public String toString(DiagramType diagramType) {
		return bundle.getString("diagramType_" + diagramType.toString().toLowerCase());
	}

	public DiagramType fromString(String string) {
		DiagramType diagramType = DiagramType.FunctionPlot;
		for (DiagramType dt : FXCollections.observableArrayList(DiagramType.values())) {
			if (this.toString(dt).equals(string)) {
				diagramType = dt;
			}
		}
		return diagramType;
	}

	/**
	 * Sets the {@link ResourceBundle}
	 *
	 * @param bundle the {@link ResourceBundle}
	 */
	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}
}
