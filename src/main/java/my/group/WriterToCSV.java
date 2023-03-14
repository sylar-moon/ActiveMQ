package my.group;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class WriterToCSV {
    private final Logger LOGGER = new MyLogger().getLogger();
    String pathToCSV = "fdsf";
    private final CSVWriter writer;

    WriterToCSV(String pathToCSV) throws IOException {
        writer= new CSVWriter(new FileWriter(pathToCSV));
    }


    public void WriteMessageToCSV(String[] message) throws FileFormatException {
        if (!pathToCSV.toLowerCase(Locale.ROOT).substring(pathToCSV.lastIndexOf(".")).contains("csv")) {
            throw new FileFormatException("The format of this file is not csv");
        }
        try (CSVWriter writer = new CSVWriter(new FileWriter(pathToCSV))) {

            writer.writeNext(message);

            LOGGER.info("Data has been written to {} ", pathToCSV);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
    public void stopWriteCsv(CSVWriter writer){
        try {
            writer.close();
        } catch (IOException e) {
            LOGGER.error("It is not possible to stop CsvWriter",e);
        }
    }
}