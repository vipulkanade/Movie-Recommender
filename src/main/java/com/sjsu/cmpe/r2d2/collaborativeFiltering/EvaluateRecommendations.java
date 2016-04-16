package main.java.com.sjsu.cmpe.r2d2.collaborativeFiltering;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * Created by ashish on 4/8/16.
 */
public class EvaluateRecommendations {


    static GenericUserBasedRecommender recommender = null;
    static DataModel dataModel = null;
    static double threshold = 0.1;
    static double sum =0;
    static String typeOfRecommendation = "ub";

    public static GenericUserBasedRecommender getRecommender() {
        return recommender;
    }

    public static void setRecommender(GenericUserBasedRecommender newrecommender) {
        recommender = newrecommender;
    }

    public static DataModel getDataModel() {
        return dataModel;
    }

    public static void setDataModel(DataModel dataModel) {
        EvaluateRecommendations.dataModel = dataModel;
    }

    public static String getTypeOfRecommendation() {
        return typeOfRecommendation;
    }

    public static void setTypeOfRecommendation(String typeOfRecommendation) {
        EvaluateRecommendations.typeOfRecommendation = typeOfRecommendation;
    }

    public void evalRecommender(){

        if(typeOfRecommendation.equalsIgnoreCase("ub")){

            RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
            RecommenderBuilder recommenderBuilder = new UserRecommenderBuilder();

            while(threshold<1){
                try {
                    System.out.println("RMSE : " + evaluator.evaluate(recommenderBuilder,null,dataModel,0.9,1));
                    threshold+=0.1;
                } catch (TasteException e) {
                    e.printStackTrace();
                }
            }

        }else if(typeOfRecommendation.equalsIgnoreCase("ib")){

            RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
            RecommenderBuilder recommenderBuilder = new ItemRecommenderBuilder();

            // evaluate RMSE for 500
            for(int i = 0;i<500;i++){
                try {
                    sum+=evaluator.evaluate(recommenderBuilder,null,dataModel,0.9,1);
                } catch (TasteException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(" RMSE :" + sum/500);
        }

    }

    class ItemRecommenderBuilder implements RecommenderBuilder {
        public Recommender buildRecommender(DataModel model) throws TasteException {
            ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
            Recommender recommender = new GenericItemBasedRecommender(model, similarity);
            Recommender cachingRecommender = new CachingRecommender(recommender);
            return cachingRecommender;
        }
    }

    class UserRecommenderBuilder implements RecommenderBuilder {
        public Recommender buildRecommender(DataModel model) throws TasteException {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(threshold, similarity, model);
            return new GenericUserBasedRecommender(model, neighborhood, similarity);
        }
    }

}
