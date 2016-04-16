package main.java.com.sjsu.cmpe.r2d2.HybridRecommendationSystem;


import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

import java.io.File;
/**
 * Created by vipulkanade on 4/12/16.
 */
public class HybridRecommendation {

    // Number of tests
    static final int n = 1000;

    public static void main(String[] args) {

        hybridUserBasedRecommendation();
        hybridItemRecommendation();

    }

    private static void hybridUserBasedRecommendation() {
        DataModel model = null;
        try {
            model = new FileDataModel(new File("src/main/resources/output/output.csv"));
            RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
            DataModel similarityDataModel = new FileDataModel((new File("src/main/resources/output/movies-genres-processed.csv")));
            RecommenderBuilder builder = new UserBasedRecommenderBuilder(similarityDataModel);
            double dSum = 0;
            for (int i = 0; i < n; i++) {
                dSum += evaluator.evaluate(builder, null, model, 0.9, 1.0);
            }
            System.out.println("RMSE for item based recommendation: " + dSum / n);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void hybridItemRecommendation() {
        DataModel model = null;
        try {
            model = new FileDataModel(new File("src/main/resources/output/output.csv"));
            RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
            DataModel similarityDataModel = new FileDataModel((new File("src/main/resources/output/movies-genres-processed.csv")));
            RecommenderBuilder builder = new ItemRecommenderBuilder(similarityDataModel);
            double dSum = 0;

            for (int i = 0; i < n; i++) {
                dSum += evaluator.evaluate(builder, null, model, 0.9, 1.0);
            }
            System.out.println("RMSE for item based recommendation: " + dSum / n);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
