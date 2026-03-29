package roguelike.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jsefa.Deserializer;
import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;

import roguelike.screens.MainScreen;

/**
 * 
 */
public class FileUtils {

  private FileUtils() {
    // Utility class
  }

  /**
   * 
   * @param argPath
   * @param argEncoding
   * @return
   * @throws IOException
   */
  static String readFile(String argPath, Charset argEncoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(argPath));
    return new String(encoded, argEncoding);
  }

  /**
   * 
   * @param argStream
   * @return
   * @throws IOException
   */
  public static String readFile(InputStream argStream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(argStream));
    StringBuilder out = new StringBuilder();
    String line;

    while ((line = reader.readLine()) != null) {
      out.append(line);
      out.append("\n");
    }

    reader.close();
    return out.toString();
  }

  /**
   * 
   * @param <T>
   * @param argPath
   * @param argType
   * @return
   */
  public static <T> List<T> recordsFromCsv(String argPath, Class<T> argType) {
    String csv;

    try {
      csv = FileUtils.readFile(MainScreen.class.getResourceAsStream(argPath));
    }
    catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    ArrayList<T> items = new ArrayList<>();
    CsvConfiguration csvConfiguration = new CsvConfiguration();
    csvConfiguration.setFieldDelimiter(',');
    csvConfiguration.setLineFilter(new HeaderAndFooterFilter(1, false, false));

    Deserializer deserializer = CsvIOFactory
        .createFactory(csvConfiguration, argType).createDeserializer();

    deserializer.open(new StringReader(csv));

    while (deserializer.hasNext()) {
      T row = deserializer.next();
      items.add(row);
    }

    deserializer.close(true);

    System.out
        .println("Read " + items.size() + " of type " + argType.getName() + " from " + argPath);

    return items;
  }
}
