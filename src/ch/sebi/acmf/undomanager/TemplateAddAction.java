package ch.sebi.acmf.undomanager;

import ch.sebi.acmf.data.Song;
import ch.sebi.acmf.data.Template;
import ch.sebi.acmf.ui.TimelinePane;
import javafx.scene.control.ListView;

/**
 * Created by Sebastian on 11.11.2017.
 */
public class TemplateAddAction extends Action {
	private Template addedTemplate;

	public TemplateAddAction(Song song, Template addedTemplate) {
		super(song);
		this.addedTemplate = addedTemplate;
	}

	@Override
	public void undo() {
		getUndoManager().getAcmfMainController().getSong_listview().getSelectionModel().select(getSong());
		getUndoManager().getAcmfMainController().getTimeline_panel().getTemplates().remove(addedTemplate);
	}

	@Override
	public void redo() {
		getUndoManager().getAcmfMainController().getSong_listview().getSelectionModel().select(getSong());
		getUndoManager().getAcmfMainController().getTimeline_panel().getTemplates().add(addedTemplate);
	}
}
