package ch.sebi.acmf.undomanager;

import ch.sebi.acmf.data.Song;
import ch.sebi.acmf.data.Template;

/**
 * Created by Sebastian on 11.11.2017.
 */
public abstract class Action {
	private Song song;
	private Template template;
	private UndoManager undoManager;

	public Action() {
	}

	public Action(Song song) {
		this.song = song;
	}

	public Action(Song song, Template template) {
		this.song = song;
		this.template = template;
	}

	public abstract void undo();
	public abstract void redo();


	public Song getSong() {
		return song;
	}

	public Template getTemplate() {
		return template;
	}

	public UndoManager getUndoManager() {
		return undoManager;
	}

	protected void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}
}
