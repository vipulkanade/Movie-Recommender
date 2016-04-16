package main.java.com.sjsu.cmpe.r2d2.contentbasedrecommendation;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;

import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.commons.io.FileUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;

import java.util.List;

import main.java.com.sjsu.cmpe.r2d2.contentparser.MovieListParser;
import main.java.com.sjsu.cmpe.r2d2.datamodelmanager.DataModelManager;

/**
 * Created by vipulkanade on 4/6/16.
 */
public class ContentBasedRecommendation {
    private static final int RECOMMENDED_ITEM_NUM = 3;
    private static final String NL = System.getProperty("line.separator");
    private static String file_path = "src/main/resources/input/";
    private static final String source_file = "src/main/resources/output/movies-genres-processed.csv";

    static  int[] movie_genre_831 = {1,2,3,6,7};
    static  int[] movie_genre_224 = {1,3,4};
    public static void main(String[] args) {
        try {
            userBase(831, 3423, movie_genre_831,  "3/1/2016  19:05:00,831,20-30,Male");
            userBase(224, 1356, movie_genre_224,"3/1/2016  19:05:00,224,20-30,Male");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void generateUserBasedMovieGenreData (int sid, int userId, int[] movie_genre_user) throws IOException {
        File src_file = new File(source_file);
        String dest_file_path = file_path + "movies-genres-processed-" + sid + ".csv";
        File dest_file = new File(dest_file_path);

        FileUtils.copyFile(src_file,dest_file);

        processForUser(dest_file, movie_genre_user, userId);

    }

    public static void processForUser(File file, int[] movie_genre_user, int userID) throws IOException{
        CSVFormat outputFormat = CSVFormat.DEFAULT.withDelimiter(',').withRecordSeparator(NL);
        FileWriter fileWriter = new FileWriter(file, true);

        CSVPrinter printer = new CSVPrinter(fileWriter,outputFormat);

        for (int i = 0; i < movie_genre_user.length; i++) {
            printer.print(userID);
            printer.print(movie_genre_user[i]);
            printer.print(1);
            printer.println();
        }
        printer.close();
    }

    private static void userBase(int sid, int userId, int[] movie_genre_user, String user) throws IOException {
        generateUserBasedMovieGenreData(sid, userId, movie_genre_user);
        MovieListParser.getMovieList();
        List<String> movies = DataModelManager.getInstance().getMoviesList();
        DataModel model = null;
        try {
            model = new FileDataModel(new File("src/main/resources/input/movies-genres-processed-" + sid + ".csv"));
            UserSimilarity similarity = new LogLikelihoodSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            long[] items = recommender.mostSimilarUserIDs(userId, 3);
            System.out.println("<-------- " + user + " ---------->");
            for (long m : items) {
                System.out.println("Movie : " + m + " -> "+ movies.get((int)m));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
