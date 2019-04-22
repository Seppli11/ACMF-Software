package ch.sebi.acmf.undomanager;

import ch.sebi.acmf.data.Song;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Created by Sebastian on 19.11.2017.
 */
public class SongChangeOrderAction extends Action {
	private ObservableList<Song> oldList, newList;

	public SongChangeOrderAction(Song song, ObservableList<Song> oldList) {
		super(song);
		this.oldList = FXCollections.observableArrayList(oldList);
	}

	@Override
	public void undo() {
		newList = FXCollections.observableArrayList(getUndoManager().getAcmfMainController().getSong_listview().getItems());
		getUndoManager().getAcmfMainController().getSong_listview().getItems().setAll(oldList);
		getUndoManager().getAcmfMainController().getSong_listview().getSelectionModel().select(getSong());
	}

	@Override
	public void redo() {
		getUndoManager().getAcmfMainController().getSong_listview().getItems().setAll(newList);
		getUndoManager().getAcmfMainController().getSong_listview().getSelectionModel().select(getSong());
	}
}
