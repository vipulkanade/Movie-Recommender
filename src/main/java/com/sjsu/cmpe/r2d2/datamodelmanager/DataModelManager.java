package main.java.com.sjsu.cmpe.r2d2.datamodelmanager;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by vipulkanade on 4/6/16.
 */
public class DataModelManager {
    private static DataModelManager mInstance;

    private List<String> mMoviesList = new ArrayList<String>();
    private static final Object obj = new Object();

    private DataModelManager(){}

    public static DataModelManager getInstance() {
        synchronized (obj) {
            if(mInstance == null)
                mInstance = new DataModelManager();
        }
        return mInstance;
    }

    public List<String> getMoviesList() {
        return mMoviesList;
    }

    public void setmMoviesList(List<String> mMoviesList) {
        this.mMoviesList = mMoviesList;
    }


}
