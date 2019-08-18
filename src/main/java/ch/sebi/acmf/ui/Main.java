package ch.sebi.acmf.ui;

import java.io.IOException;

import ch.sebi.acmf.utils.ExceptionHandler;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler());
		ACMFStarter.startApplication(args);
	}
}
