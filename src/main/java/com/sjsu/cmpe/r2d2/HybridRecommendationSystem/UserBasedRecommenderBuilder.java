package main.java.com.sjsu.cmpe.r2d2.HybridRecommendationSystem;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
/**
 * Created by vipulkanade on 4/12/16.
 */
public class UserBasedRecommenderBuilder implements RecommenderBuilder {

    private DataModel similarityDataModel;

    public UserBasedRecommenderBuilder(DataModel similarityDataModel) {
        this.similarityDataModel = similarityDataModel;
    }

    public Recommender buildRecommender(DataModel model) throws TasteException {
        UserSimilarity similarity = new LogLikelihoodSimilarity(similarityDataModel);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        return recommender;
    }
}