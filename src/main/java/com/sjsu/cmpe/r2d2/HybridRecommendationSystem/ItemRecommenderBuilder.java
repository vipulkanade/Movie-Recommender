package main.java.com.sjsu.cmpe.r2d2.HybridRecommendationSystem;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;

/**
 * Created by vipulkanade on 4/12/16.
 */
public class ItemRecommenderBuilder implements RecommenderBuilder {

    private DataModel similarityDataModel;

    public ItemRecommenderBuilder(DataModel similarityDataModel) {
        this.similarityDataModel = similarityDataModel;
    }

    public Recommender buildRecommender(DataModel model) throws TasteException {
        ItemSimilarity similarity = new LogLikelihoodSimilarity(similarityDataModel);
        Recommender recommender = new GenericItemBasedRecommender(model, similarity);
        Recommender cachingRecommender = new CachingRecommender(recommender);
        return cachingRecommender;
    }
}
