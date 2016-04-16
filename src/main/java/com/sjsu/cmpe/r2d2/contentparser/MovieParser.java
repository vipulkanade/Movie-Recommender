package main.java.com.sjsu.cmpe.r2d2.contentparser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
/**
 * Created by vipulkanade on 4/10/16.
 */
public class MovieParser {

    private static final String inputCSV = "src/main/resources/input/movies-corrsponding-genres.csv";
    private static final String outputCSV = "src/main/resources/output/movies-genres-processed.csv";

    private static final String NL = System.getProperty("line.separator");
    private static int userID;
    private static int itemID;
    private static String rating;

    public static void parseCSV() throws IOException{
        CSVParser parser = new CSVParser(new FileReader(inputCSV), CSVFormat.DEFAULT.withHeader());
        CSVFormat outputFormat = CSVFormat.DEFAULT.withDelimiter(',').withRecordSeparator(NL);

        FileWriter fileWriter = new FileWriter(outputCSV);
        CSVPrinter printer = new CSVPrinter(fileWriter,outputFormat);

        for (CSVRecord record : parser) {
            System.out.println(record.getRecordNumber());
            for(int i=1;i<record.size();i++){
                String str = record.get(i);
                if (str.equalsIgnoreCase("x")) {
                    printer.print(record.getRecordNumber());
                    printer.print(i);
                    printer.print(1);
                    printer.println();
                }
            }
        }
        printer.close();
        parser.close();
    }

    public static void main(String[]args){

        try {
            parseCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
