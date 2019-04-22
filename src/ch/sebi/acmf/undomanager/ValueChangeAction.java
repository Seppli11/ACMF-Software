package ch.sebi.acmf.undomanager;

import ch.sebi.acmf.data.Song;
import ch.sebi.acmf.data.Template;
import ch.sebi.acmf.data.Value;
import ch.sebi.acmf.deviceconfiguration.DeviceConfiguration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Sebastian on 19.11.2017.
 */
public class ValueChangeAction extends Action {
	private BooleanProperty undefinedProperty;
	private int oldValue, newValue;
	private IntegerProperty value;
	private int index;
	private boolean midi1;

	public ValueChangeAction(Song song, Template template, IntegerProperty value, BooleanProperty undefinedProperty) {
		super(song, template);
		this.value = value;
		this.oldValue = value.get();
		this.undefinedProperty = undefinedProperty;
	}

	@Override
	public void undo() {
//		getUndoManager().getAcmfMainController().getTimeline_panel()
		newValue = value.get();
		value.set(oldValue);
		if(oldValue == -1) {
			undefinedProperty.set(true);
		}
	}

	@Override
	public void redo() {
		value.set(newValue);
		if(newValue == -1) {
			undefinedProperty.set(true);
		}
	}
}
