package ch.sebi.acmf.utils;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.sebi.acmf.ui.ACMFMainController;

/**
 * an exception handler for any uncaught exception which might occur
 * @author sebi
 *
 */
public class ExceptionHandler implements UncaughtExceptionHandler {
	/**
	 * the logger
	 */
	private Logger logger = LogManager.getLogger(getClass().getName());

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		logger.error("An uncaught exceptio occured: ", e);
		ACMFMainController.showErrorDialog(e);
	}

}
