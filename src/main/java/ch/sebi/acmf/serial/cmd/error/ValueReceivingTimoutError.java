package ch.sebi.acmf.serial.cmd.error;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ch.sebi.acmf.serial.cmd.Command;
import ch.sebi.acmf.serial.cmd.CommandHandler;

/**
 * Created by Sebastian on 08.08.2017.
 */
public class ValueReceivingTimoutError extends Command {
	public static final byte ERROR_OCCURED = 0xD;
	public static final byte VALUE_RECEIVING_TIMOUT_ERROR_PACKAGE_COUNTER = 2;

	public static final byte VALUE_RECEIVING_TIMOUT_ERROR_CODE = 0x3;

	private Logger logger = LogManager.getLogger(getClass().getName());

	private byte cmd;

	public ValueReceivingTimoutError(byte cmd) {
		this.cmd = cmd;
	}

	public ValueReceivingTimoutError() {
		this.cmd = 0;
	}

	@Override
	public Byte getId() {
		return ERROR_OCCURED;
	}

	@Override
	public Byte getPackageCounter() {
		return VALUE_RECEIVING_TIMOUT_ERROR_PACKAGE_COUNTER;
	}

	@Override
	public Byte[] toBytes() {
		Byte[] bytes = getTemplate();
		bytes[3] = VALUE_RECEIVING_TIMOUT_ERROR_CODE;
		bytes[4] = cmd;
		return bytes;
	}

	@Override
	public CommandHandler getCommandHandler() {
		return new CommandHandler(this) {
			@Override
			public Command commandUsable(byte id, Byte[] bytes) {
				if(id == getId()) {
					if(bytes.length >=2 && bytes[0] == VALUE_RECEIVING_TIMOUT_ERROR_CODE) {
						logger.error(ValueReceivingTimoutError.class.getSimpleName() + ": cmd: " + cmd);
						return new ValueReceivingTimoutError(bytes[1]);
					}
				}
				return null;
			}
		};
	}
}
