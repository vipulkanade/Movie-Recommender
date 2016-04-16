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
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.SparkConf;

import scala.Tuple2;

/**
 * Created by ashish on 4/8/16.
 */
public class EvaluateRecommendations {


    static GenericUserBasedRecommender recommender = null;
    static DataModel dataModel = null;
    static double threshold = 0.1;
    static double sum =0;
    static String typeOfRecommendation = "ub";
    private final static int RANK = 5;
    private final static int NUMBER_ITERATIONS = 8;
    private static double lambda;

    public static void setRecommender(GenericUserBasedRecommender newrecommender) {
        recommender = newrecommender;
    }

    public static void setDataModel(DataModel dataModel) {
        EvaluateRecommendations.dataModel = dataModel;
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
        } else if(typeOfRecommendation.equalsIgnoreCase("matrix")){
            SparkConf spark_conf = new SparkConf().setAppName("Matrix Factor Evaluation").setMaster("local[2]");
            JavaSparkContext java_spark_cont = new JavaSparkContext(spark_conf);
            JavaRDD<String> file_data = java_spark_cont.textFile("src/main/resources/output/output.csv");
            JavaRDD<Rating> rating = file_data.map(
                    new Function<String, Rating>() {
                        public Rating call(String s) {
                            String[] sarray = s.split(",");
                            return new Rating(Integer.parseInt(sarray[0]), Integer.parseInt(sarray[1]),
                                    Double.parseDouble(sarray[2]));
                        }
                    }
            );

            //Test the relation between RMSE and lambda
            lambda = 0.01;
            while (lambda < 1) {
                System.out.println("lambda => " + lambda + ", corresponding value of RMSE => " + getRMSE(rating));
                lambda += 0.1;
            }
        }
    }

    public static double getRMSE(JavaRDD<Rating> ratings) {
        JavaRDD<Rating>[] splits = ratings.randomSplit(new double[]{0.9, 0.1}, 1000);
        JavaRDD<Rating> test = splits[1];

        MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(splits[0]), RANK, NUMBER_ITERATIONS, lambda, 1, 2000);

        JavaRDD<Tuple2<Object, Object>> userProducts = test.map(
                new Function<Rating, Tuple2<Object, Object>>() {
                    public Tuple2<Object, Object> call(Rating r) {
                        return new Tuple2<Object, Object>(r.user(), r.product());
                    }
                }
        );

        JavaPairRDD<Tuple2<Integer, Integer>, Double> predictions = JavaPairRDD.fromJavaRDD(
                model.predict(JavaRDD.toRDD(userProducts)).toJavaRDD().map(
                        new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
                            public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r){
                                return new Tuple2<Tuple2<Integer, Integer>, Double>(new Tuple2<Integer, Integer>(r.user(), r.product()), r.rating());
                            }
                        }
                ));
        JavaRDD<Tuple2<Double, Double>> ratesAndPreds =
                JavaPairRDD.fromJavaRDD(test.map(
                        new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
                            public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r){
                                return new Tuple2<Tuple2<Integer, Integer>, Double>(new Tuple2<Integer, Integer>(r.user(), r.product()), r.rating());
                            }
                        }
                )).join(predictions).values();
        double RMSE = JavaDoubleRDD.fromRDD(ratesAndPreds.map(
                new Function<Tuple2<Double, Double>, Object>() {
                    public Object call(Tuple2<Double, Double> pair) {
                        Double err = pair._1() - pair._2();
                        return err * err;
                    }
                }
        ).rdd()).mean();
        return Math.sqrt(RMSE);
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
