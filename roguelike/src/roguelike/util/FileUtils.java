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
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsefa.Deserializer;
import org.jsefa.common.lowlevel.filter.HeaderAndFooterFilter;
import org.jsefa.csv.CsvIOFactory;
import org.jsefa.csv.config.CsvConfiguration;

import roguelike.screens.MainScreen;

public class FileUtils {
    private static final Logger LOG = LogManager.getLogger(FileUtils.class);

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static String readFile(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder out = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            out.append(line);
            out.append("\n");
        }

        reader.close();
        return out.toString();
    }

    public static <T> List<T> recordsFromCsv(String path, Class<T> typeClass) {
        String csv;
        
        try {
            csv = FileUtils.readFile(MainScreen.class.getResourceAsStream(path));
        }
        catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

        ArrayList<T> items = new ArrayList<>();
        
        CsvConfiguration csvConfiguration = new CsvConfiguration();
        csvConfiguration.setFieldDelimiter(',');
        csvConfiguration.setLineFilter(new HeaderAndFooterFilter(1, false, false));

        Deserializer deserializer =
                CsvIOFactory.createFactory(csvConfiguration, typeClass).createDeserializer();

        deserializer.open(new StringReader(csv));
        while (deserializer.hasNext()) {
            T row = deserializer.next();
            items.add(row);
        }
        deserializer.close(true);

        LOG.info("Read {} of type {} from {}", items.size(), typeClass.getName(), path);
        return items;
    }
}
