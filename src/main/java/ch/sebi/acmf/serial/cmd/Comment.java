package ch.sebi.acmf.serial.cmd;

import static ch.sebi.acmf.utils.ByteUtils.B;
import static ch.sebi.acmf.utils.ByteUtils.b;

import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import ch.sebi.acmf.utils.SettingsManager;

/**
 * Created by Sebastian on 15.08.2017.
 */
public class Comment extends Command {
	public static final byte COMMENT = 0x10;

	static JFrame jframe;
	static JTextPane console;
	static {
		jframe = new JFrame("Comments");
		if (SettingsManager.DEBUG) {
			console = new JTextPane();
			console.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(console);
			scrollPane.setAutoscrolls(true);
			jframe.add(scrollPane);
			jframe.pack();
			jframe.setVisible(true);
		}
	}

	private String msg;

	public Comment() {
		msg = "";
	}

	public Comment(String msg) {
		this.msg = msg;
	}

	@Override
	public Byte getId() {
		return COMMENT;
	}

	@Override
	public Byte getPackageCounter() {
		return (byte) msg.length();
	}

	@Override
	public Byte[] toBytes() {
		Byte[] bytes = B(msg.getBytes());
		return bytes;
	}



	@Override
	public CommandHandler getCommandHandler() {
		return new CommandHandler(this) {
			@Override
			public Command commandUsable(byte id, Byte[] bytes) {
				if(id == getId()) {
					//System.out.println("Comment: " + new String(b(bytes)));
					console.setText(new Date().toString() + ": " + new String(b(bytes)) + "\n" + console.getText());
					return new Comment(new String(b(bytes)));
				}
				return null;
			}

			@Override
			public boolean isCommandUsable(Command cmd) {
				return cmd.getId() == getId();
			}
		};


	}
}
