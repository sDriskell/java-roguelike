package roguelike.ui;

import roguelike.MessageDisplayProperties;
import roguelike.MessageLog;
import roguelike.ui.windows.TerminalBase;
import roguelike.util.StringEx;
import squidpony.squidcolor.SColor;
import squidpony.squidcolor.SColorFactory;

public class MessageDisplay {
	private TerminalBase terminal;
	private int numLines;
	private MessageLog messages;

	public MessageDisplay(MessageLog messages, TerminalBase terminal, int numLines) {
		this.terminal = terminal;
		this.numLines = numLines;
		this.messages = messages;
	}

	public void display(String message) {
		display(new MessageDisplayProperties(message));
	}

	public void display(String message, SColor color) {
		display(new MessageDisplayProperties(message, color));
	}

	public void display(MessageDisplayProperties message) {

		messages.add(message);
	}

	public void draw() {
		terminal.fill(0, 0, terminal.size().width, terminal.size().height, ' ');
		int msgCount = 0;
		int maxSize = messages.size(numLines);
		for (int x = 0; x < maxSize; x++) {
			if (msgCount >= maxSize)
				break;

			MessageDisplayProperties props = messages.get(x);
			StringEx[] lines = props.getText().wordWrap(terminal.size().width - 6);
			TerminalBase colorTerm = terminal.withColor(SColorFactory.blend(props.getColor(), SColor.BLACK_CHESTNUT_OAK, (x / (float) numLines)));
			String prefix = "> ";
			for (int i = 0; (i < lines.length) && (msgCount < maxSize); i++) {
				if (i > 0)
					prefix = "";

				colorTerm.write(0, msgCount, prefix + lines[i].toString());
				msgCount++;
			}
		}
	}
}
