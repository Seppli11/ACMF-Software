package ch.sebi.acmf.serial.cmd.error;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.sebi.acmf.serial.cmd.Command;
import ch.sebi.acmf.serial.cmd.CommandHandler;

/**
 * Created by Sebastian on 08.08.2017.
 */
public class DisplayWriteBufferError extends Command {
	public static final byte ERROR_OCCURED = 0xD;
	public static final byte DISPLAY_WRITE_BUFFER_ERROR_PACKAGE_COUNTER = 1;

	public static final byte DISPLAY_WRITE_BUFFER_ERROR_CODE = 0x4;

	private Logger logger = LogManager.getLogger(getClass().getName());

	public DisplayWriteBufferError() {
	}

	@Override
	public Byte getId() {
		return ERROR_OCCURED;
	}

	@Override
	public Byte getPackageCounter() {
		return DISPLAY_WRITE_BUFFER_ERROR_PACKAGE_COUNTER;
	}

	@Override
	public Byte[] toBytes() {
		Byte[] bytes = getTemplate();
		bytes[3] = DISPLAY_WRITE_BUFFER_ERROR_CODE;
		return bytes;
	}

	@Override
	public CommandHandler getCommandHandler() {
		return new CommandHandler(this) {
			@Override
			public Command commandUsable(byte id, Byte[] bytes) {
				if(id == getId()) {
					if(bytes.length >=1 && bytes[0] == DISPLAY_WRITE_BUFFER_ERROR_CODE) {
						logger.error(DisplayWriteBufferError.class.getSimpleName() + ": ");
						return new DisplayWriteBufferError();
					}
				}
				return null;
			}
		};
	}
}
