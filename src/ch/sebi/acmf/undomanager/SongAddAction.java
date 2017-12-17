package ch.sebi.acmf.undomanager;

import ch.sebi.acmf.data.Song;
import javafx.scene.control.ListView;

/**
 * Created by Sebastian on 11.11.2017.
 */
public class SongAddAction extends Action {
	private Song addedSong;
	private ListView<Song> songListView;

	public SongAddAction(Song addedSong, ListView<Song> songListView) {
		super(addedSong);
		this.addedSong = addedSong;
		this.songListView = songListView;
	}


	@Override
	public void undo() {
		songListView.getItems().remove(addedSong);
	}

	@Override
	public void redo() {
		songListView.getItems().add(addedSong);
		//songListView.getSelectionModel().select(addedSong);
		getUndoManager().getAcmfMainController().getSong_listview().getSelectionModel().select(addedSong);
	}
}
