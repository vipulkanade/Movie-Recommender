package main.java.com.sjsu.cmpe.r2d2.collaborativeFiltering;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.util.List;

/*
*   Source: https://mahout.apache.org/users/recommender/userbased-5-minutes.html
*   Created by Ashish 04/01/2016
*   User Based Recommendation
* */

public class UserBasedRecommendation {

    static long userID = 0;
    static int noOfItemsRecommendations = 1;
    static UserBasedRecommender movieRecommender = null;
    static DataModel movieModel = null;

    public static void buildRecommender(String csvFile) {

        try {

            //get data-model from movieCSV File
            movieModel = new FileDataModel(new File(csvFile));

            // build customerSimilarity coeffient
            // we will use pearson correlation similarity.
            UserSimilarity customerSimilarity = new PearsonCorrelationSimilarity(movieModel);

            // We will use all users who have similarity greater than 0.1
            UserNeighborhood userNeighborhood = new ThresholdUserNeighborhood(0.1, customerSimilarity, movieModel);

            // Finally we will create our moive recommender.
            movieRecommender = new GenericUserBasedRecommender(movieModel, userNeighborhood, customerSimilarity);

            //Step 4:- Create object of UserBasedRecommender or ItemBasedRecommender


        } catch (Exception e) {
            System.out.println("There was an error.");
            e.printStackTrace();
        }


    }

    public void showRecommendations() {

        List<RecommendedItem> recommendations = null;
        try {
            recommendations = movieRecommender.recommend(userID, noOfItemsRecommendations);
            for (RecommendedItem recommendation : recommendations) {
                System.out.println("Recommendation : [ Movie: "  + recommendation.getItemID()+", SimValue: "+recommendation.getValue()+" ]");
            }
        } catch (TasteException e) {
            System.out.println("Taste Exception in recommender");
            e.printStackTrace();
        }


    }



    public  DataModel getMovieModel() {
        return movieModel;
    }


    public  UserBasedRecommender getMovieRecommender() {
        return movieRecommender;
    }

    public  void setMovieRecommender(String csvFile) {
        buildRecommender(csvFile);
    }


    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        UserBasedRecommendation.userID = userID;
    }

    public int getNoOfItemsRecommendations() {
        return noOfItemsRecommendations;
    }

    public void setNoOfItemsRecommendations(int noOfItemsRecommendations) {
        UserBasedRecommendation.noOfItemsRecommendations = noOfItemsRecommendations;
    }


}