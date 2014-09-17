package roguelike.ui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class Menu<T> {

	private ArrayList<T> items;
	private int activeIndex;
	private int currentPage;
	private int pageCount;
	private int pageSize;
	private int pageIndex;

	public Menu(List<T> items) {
		this(items, 26);
	}

	public Menu(List<T> items, int pageSize) {
		this.items = new ArrayList<T>(items);
		this.activeIndex = 0;
		this.currentPage = 1;
		this.pageSize = pageSize;

		pageCount = (int) Math.ceil(items.size() / (float) pageSize);
	}

	public void processKey(KeyEvent key) {
		int maxItems = Math.min(items.size(), pageSize);

		if (items.size() > 0) {

			switch (key.getKeyCode()) {
			case KeyEvent.VK_UP:
				pageIndex = Math.max(0, pageIndex - 1);
				break;
			case KeyEvent.VK_DOWN:
				pageIndex = Math.min(maxItems - 1, pageIndex + 1);
				break;

			case KeyEvent.VK_LEFT:
				currentPage = Math.max(currentPage - 1, 1);
				break;

			case KeyEvent.VK_RIGHT:
				currentPage = Math.min(currentPage + 1, pageCount);
				break;

			default:
				pageIndex = Math.min(maxItems - 1, Math.max(0, getIndexOfChar(key)));
			}

			activeIndex = getPageOffset(pageIndex);
		}
	}

	public T getActiveItem() {
		if (activeIndex >= 0)
			return items.get(activeIndex);

		return null;
	}

	public T getItemAt(int index) {
		if (index >= 0)
			return items.get(index);

		return null;
	}

	public int getActiveItemIndex() {
		return activeIndex;
	}

	public int getFirstItemIndex() {
		return getPageOffset(0);
	}

	public int getLastItemIndex() {
		return getFirstItemIndex() + Math.min(pageSize, items.size());
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getPageCount() {
		return pageCount;
	}

	public int size() {
		return items.size();
	}

	private int getPageOffset(int index) {
		return ((currentPage - 1) * pageSize) + index;
	}

	private int getIndexOfChar(KeyEvent key) {
		char keyChar = key.getKeyChar();
		if (keyChar >= 97 && keyChar <= 122)
			return keyChar - 97; // a-z, returns 0-26
		if (keyChar >= 65 && keyChar <= 90)
			return keyChar - 65; // A-Z, returns 0-26

		return -1; // invalid character pressed
	}
}
