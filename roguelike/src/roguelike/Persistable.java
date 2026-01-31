package roguelike;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public interface Persistable<T> {

	public void save(ObjectOutput argOutput) throws IOException;

	public T load(ObjectInput argInput) throws ClassNotFoundException, IOException;

}
