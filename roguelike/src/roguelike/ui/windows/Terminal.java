package roguelike.ui.windows;

import java.awt.Point;
import java.awt.Rectangle;

import squidpony.squidcolor.SColor;

public abstract class Terminal {

	protected CharEx[][] data;
	protected Rectangle size;
	protected ColorPair colors;
	protected TerminalCursor cursor;

	protected TerminalChangeNotification terminalChanged;

	protected Terminal(TerminalChangeNotification terminalChanged) {
		setTerminalChanged(terminalChanged);
	}

	public void setTerminalChanged(TerminalChangeNotification terminalChanged) {
		if (terminalChanged == null)
			throw new IllegalArgumentException("terminal changed notification is null");

		this.terminalChanged = terminalChanged;
	}

	public Point location() {
		return this.size.getLocation();
	}

	public Rectangle size() {
		return this.size;
	}

	public abstract Terminal getWindow(int x, int y, int width, int height);

	public abstract Terminal withColor(SColor color);

	public abstract Terminal withColor(SColor foreground, SColor background);

	public Terminal write(int x, int y, String text) {
		return write(x, y, new StringEx(text, colors.foreground(), colors.background()));
	}

	public Terminal write(int x, int y, StringEx text) {
		CharEx[][] temp = new CharEx[text.size()][1];
		for (int i = 0; i < text.size(); i++) {
			temp[i][0] = text.get(i);
		}
		put(x, y, temp);
		return this;
	}

	public Terminal put(int x, int y, CharEx[][] c) {
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c[0].length; j++) {
				put(x + i, y + j, c[i][j]);
			}
		}
		return this;
	}

	public Terminal put(int x, int y, CharEx c) {
		if (cursor.put(x, y, c)) {
			terminalChanged.onChanged(x + size.x, y + size.y, c);
		}
		return this;
	}

	public Terminal put(int x, int y, char c) {
		CharEx ch = new CharEx(c, colors.foreground(), colors.background());
		put(x, y, ch);
		return this;
	}

	public Terminal fill(int x, int y, int width, int height, char c) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				put(i + x, j + y, new CharEx(c));
			}
		}
		return this;
	}

	public abstract Terminal fill(int x, int y, int width, int height);

}
