package ch.sebi.acmf.serial.cmd;

/**
 * Created by Sebastian on 05.11.2017.
 */
public class FinishedTransmittingData extends Command {
	public static final byte FINISHED_TRANSMITTING_DATA = 0x11;
	public static final byte FINISHED_TRANSMITTING_DATA_PACKAGE_COUNTER = 0;


	@Override
	public Byte getId() {
		return FINISHED_TRANSMITTING_DATA;
	}

	@Override
	public Byte getPackageCounter() {
		return FINISHED_TRANSMITTING_DATA_PACKAGE_COUNTER;
	}

	@Override
	public Byte[] toBytes() {
		return getTemplate();
	}


}
