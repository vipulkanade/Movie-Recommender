package main.java.com.sjsu.cmpe.r2d2.contentparser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by ashish on 4/8/16.
 */
public class DataProcessing {

    private static final String inputCSV = "src/main/resources/input/movieDataSet.csv";
    private static final String outputCSV = "src/main/resources/output/output.csv";
    private static final String matrixFileCSV = "src/main/resources/output/matrixFactorData.csv";

    //private static final Object[] OUTPUT_FILE_HEADER = {"UserID","ItemID","Ratings"};
    private static final String NL = System.getProperty("line.separator");
    private static int userID;
    private static int itemID;
    private static String rating;

    // private static String []outputString;



    public static void parseCSV() throws IOException{
        CSVParser parser = new CSVParser(new FileReader(inputCSV), CSVFormat.DEFAULT.withHeader());
        CSVFormat outputFormat = CSVFormat.DEFAULT.withDelimiter(',').withRecordSeparator(NL);
        CSVFormat matrixFormat = CSVFormat.DEFAULT.withDelimiter(',').withRecordSeparator(NL);

        FileWriter fileWriter = new FileWriter(outputCSV);
        FileWriter matrixFileWriter = new FileWriter(matrixFileCSV);
        CSVPrinter printer = new CSVPrinter(fileWriter,outputFormat);
        CSVPrinter matrixPrinter = new CSVPrinter(matrixFileWriter,matrixFormat);


        //  printer.printRecord(OUTPUT_FILE_HEADER);
        for (CSVRecord record : parser) {
            userID = Integer.parseInt(record.get("ID"));
            itemID=1;
            for(int i=5;i<record.size();i++){
                rating = record.get(i);
                if(!rating.equalsIgnoreCase("N/A")){
                    //rating="0";
                    matrixPrinter.print(userID);
                    matrixPrinter.print(itemID);
                    matrixPrinter.print(rating);
                    matrixPrinter.print(generateRandomNumber());
                    matrixPrinter.println();
                    //matrixPrinter.printRecords(matrixOutPut);
                    printer.print(userID);
                    printer.print(itemID);
                    printer.print(rating);
                    printer.println();
                }
                /*String output=userID+"\t"+itemID+"\t"+rating;
                String matrixOutPut =userID+"\t"+itemID+"\t"+rating+"\t"+generateRandomNumber();
*/
                //String output=userID+","+itemID+","+rating;
                //String matrixOutPut =userID+","+itemID+","+rating+","+generateRandomNumber();



                //output="";
                //printer.printRecords(userID,itemID,Integer.parseInt(rating));
                // printer.print('\n');
                System.out.println("UserID: "+userID+" itemID: "+ (itemID++)+" Rating: "+rating);
                //outputString = new String[]{userID,String.valueOf(itemID),rating};
                //printer.println();
            }
        }
        printer.close();
        parser.close();
        matrixPrinter.close();
    }

    public static long generateRandomNumber(){
        Random r = new Random();
        long unixTime = (long) (1293861599+r.nextDouble()*60*60*24*365);
        return unixTime;
    }
    public static void main(String[]args){

        try {
            parseCSV();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
