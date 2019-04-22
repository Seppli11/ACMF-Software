package ch.sebi.acmf.undomanager;

import ch.sebi.acmf.data.Song;
import ch.sebi.acmf.data.Template;
import javafx.beans.property.BooleanProperty;

/**
 * Created by Sebastian on 10.12.2017.
 */
public class ValueToUndefinedAction extends Action {
	private BooleanProperty undefinedProperty;
	public ValueToUndefinedAction(Song song, Template template, BooleanProperty undefinedProperty) {
		super(song, template);
		this.undefinedProperty = undefinedProperty;
	}

	@Override
	public void undo() {
		this.undefinedProperty.set(true);
	}

	@Override
	public void redo() {
		this.undefinedProperty.set(false);
	}
}
