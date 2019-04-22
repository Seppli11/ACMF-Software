package ch.sebi.acmf.undomanager;

import ch.sebi.acmf.data.Song;
import javafx.scene.control.ListView;

/**
 * Created by Sebastian on 11.11.2017.
 */
public class SongRemoveAction extends Action {
	private Song removedSong;

	public SongRemoveAction(Song removedSong) {
		super(removedSong);
		this.removedSong = removedSong;
	}


	@Override
	public void undo() {
		getUndoManager().getAcmfMainController().getSong_listview().getItems().add(removedSong);
		getUndoManager().getAcmfMainController().getSong_listview().getSelectionModel().select(getSong());
	}

	@Override
	public void redo() {
		getUndoManager().getAcmfMainController().getSong_listview().getItems().remove(removedSong);
	}
}
