package main.java.com.sjsu.cmpe.r2d2.contentbasedrecommendation;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;

import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import main.java.com.sjsu.cmpe.r2d2.contentparser.MovieListParser;
import main.java.com.sjsu.cmpe.r2d2.datamodelmanager.DataModelManager;

/**
 * Created by vipulkanade on 4/6/16.
 */
public class ContentBasedRecommendation {
    private static final int RECOMMENDED_ITEM_NUM = 3;
    public static void main(String[] args) {
        userBase(831, 3423, "3/1/2016  19:05:00,831,20-30,Male");
        userBase(224, 1356, "3/1/2016  19:05:00,224,20-30,Male");
    }

    private static void userBase(int sid, int userId, String user) {
        MovieListParser.getMovieList();
        List<String> movies = DataModelManager.getInstance().getMoviesList();
        DataModel model = null;
        try {
            model = new FileDataModel(new File("src/main/resources/input/movie-night-content-base-" + sid + ".csv"));
            UserSimilarity similarity = new LogLikelihoodSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            long[] items = recommender.mostSimilarUserIDs(userId, 3);
            System.out.println("<-------- " + user + " ---------->");
            System.out.println("movies: " + Arrays.toString(items));
            for (long m : items) {
                System.out.println(movies.get((int)m));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
