package main.java.com.sjsu.cmpe.r2d2.app;

import main.java.com.sjsu.cmpe.r2d2.collaborativeFiltering.UserBasedRecommendation;
import main.java.com.sjsu.cmpe.r2d2.collaborativeFiltering.EvaluateRecommendations;
import main.java.com.sjsu.cmpe.r2d2.collaborativeFiltering.ItemBasedRecommendation;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;

/**
 * Created by R2D2 on 4/10/16.
 */
public class Application {
    //csv file after data preprocessing.
    static final String INPUT_CSV_DATASET = "src/main/resources/output/output.csv";


    public static void main(String[] args) {



        System.out.println(" \n ---- [UserbBased Recommendation]  ---- \n ");

        UserBasedRecommendation customUserRecommender = new UserBasedRecommendation();
        // set userID
        customUserRecommender.setUserID(204);
        // set noOfRecommendations
        customUserRecommender.setNoOfItemsRecommendations(3);
        customUserRecommender.setMovieRecommender(INPUT_CSV_DATASET);
        customUserRecommender.showRecommendations();

        System.out.println(" \n ---- [Item Based Recommendation]  ---- \n ");

        ItemBasedRecommendation customItemBasedRecommender = new ItemBasedRecommendation();
        customItemBasedRecommender.setUserID(204);
        customItemBasedRecommender.setNoOfItemsRecommendations(3);
        customItemBasedRecommender.setItemRecommender(INPUT_CSV_DATASET);
        customItemBasedRecommender.showRecommendations();


        System.out.println(" \n ---- [UserbBased Recommendation] Evaluation Results ---- \n ");
        EvaluateRecommendations userRecoEval = new EvaluateRecommendations();
        EvaluateRecommendations.setTypeOfRecommendation("ub");
        EvaluateRecommendations.setRecommender((GenericUserBasedRecommender) customUserRecommender.getMovieRecommender());
        EvaluateRecommendations.setDataModel(customUserRecommender.getMovieModel());
        userRecoEval.evalRecommender();


        System.out.println(" \n ---- [ItemBased Recommendation] Evaluation Results ---- \n ");
        EvaluateRecommendations itemRecoEval = new EvaluateRecommendations();
        EvaluateRecommendations.setTypeOfRecommendation("ib");
        EvaluateRecommendations.setRecommender((GenericUserBasedRecommender) customUserRecommender.getMovieRecommender());
        EvaluateRecommendations.setDataModel(customItemBasedRecommender.getMovieModel());
        itemRecoEval.evalRecommender();
    }
}
