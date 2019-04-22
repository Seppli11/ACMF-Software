package ch.sebi.acmf.undomanager;

import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.sebi.acmf.ui.ACMFMainController;
import javafx.application.Platform;

/**
 * Created by Sebastian on 11.11.2017.
 */
public class UndoManager {
	public final static UndoManager MAIN = new UndoManager(ACMFMainController.acmfMainController);

	private Logger logger = LogManager.getLogger(getClass().getName());

	private Stack<Action> undoActionStack = new Stack<>();
	private Stack<Action> redoActionStack = new Stack<>();

	private ACMFMainController acmfMainController;

	private boolean isDoing = false;

	public UndoManager(ACMFMainController acmfMainController) {
		this.acmfMainController = acmfMainController;
	}

	public void add(Action a) {
		if(isDoing()) {
			logger.info("Undomanager is doing something! add(Action) will be skipped!");
			return;
		}
		logger.info("Added action: " + a);
		a.setUndoManager(this);
		undoActionStack.push(a);
		redoActionStack.clear();
	}

	public void undo() {
		if(undoActionStack.empty()) {
			return;
		}
		isDoing = true;
		Action a = undoActionStack.pop();
		a.undo();
		redoActionStack.push(a);
		Platform.runLater(() -> isDoing = false);
	}

	public void redo() {
		if(redoActionStack.empty()) {
			return;
		}
		isDoing = true;
		Action a = redoActionStack.pop();
		a.redo();
		undoActionStack.push(a);
		Platform.runLater(() -> isDoing = false);
	}

	public boolean isDoing() {
		return isDoing;
	}

	public ACMFMainController getAcmfMainController() {
		return acmfMainController;
	}
}
