package roguelike;

public class DialogResult<T> {

    private boolean canceled;
    private T item;

    private DialogResult(T item, boolean closed) {
        this.item = item;
        this.canceled = closed;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public T item() {
        return item;
    }

    public static <T> DialogResult<T> ok(T item) {
        return new DialogResult<>(item, false);
    }

    public static <T> DialogResult<T> cancel() {
        return new DialogResult<>(null, true);
    }
}
