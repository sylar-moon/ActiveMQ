package my.group;

import com.opencsv.CSVWriter;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class WriterToCSV {
    private final Logger LOGGER = new MyLogger().getLogger();
    String pathToCSV ;
    private final CSVWriter writer;

    WriterToCSV(String pathToCSV) throws IOException {
        this.pathToCSV=pathToCSV;
        writer= new CSVWriter(new FileWriter(pathToCSV));
    }


    public void WriteMessageToCSV(String[] message) throws FileFormatException {
        if (!pathToCSV.toLowerCase(Locale.ROOT).substring(pathToCSV.lastIndexOf(".")).contains("csv")) {
            throw new FileFormatException("The format of this file is not csv");
        }
            writer.writeNext(message);
            LOGGER.info("Data has been written to {} ", pathToCSV);
    }
    public void stopWriteCsv(){
        try {
            writer.close();
        } catch (IOException e) {
            LOGGER.error("It is not possible to stop CsvWriter",e);
        }
    }
}