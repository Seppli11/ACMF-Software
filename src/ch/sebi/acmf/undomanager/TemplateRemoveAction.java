package ch.sebi.acmf.undomanager;

import ch.sebi.acmf.data.Song;
import ch.sebi.acmf.data.Template;
import ch.sebi.acmf.ui.TimelinePane;
import javafx.scene.control.ListView;

/**
 * Created by Sebastian on 11.11.2017.
 */
public class TemplateRemoveAction extends Action {
	private Template removedTemplate;

	public TemplateRemoveAction(Song song, Template removedTemplate) {
		super(song);
		this.removedTemplate = removedTemplate;
	}

	@Override
	public void undo() {
		getUndoManager().getAcmfMainController().getSong_listview().getSelectionModel().select(getSong());
		getUndoManager().getAcmfMainController().getTimeline_panel().getTemplates().add(removedTemplate);
	}

	@Override
	public void redo() {
		getUndoManager().getAcmfMainController().getSong_listview().getSelectionModel().select(getSong());
		getUndoManager().getAcmfMainController().getTimeline_panel().getTemplates().remove(removedTemplate);
	}
}
