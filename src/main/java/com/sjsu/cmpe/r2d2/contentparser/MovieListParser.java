package main.java.com.sjsu.cmpe.r2d2.contentparser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import main.java.com.sjsu.cmpe.r2d2.datamodelmanager.DataModelManager;
/**
 * Created by vipulkanade on 4/9/16.
 */
public class MovieListParser {

    public static void getMovieList() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader("src/main/resources/input/movie-list.txt"));
            String s = in.readLine();
            List<String> list = Arrays.asList(s.split(","));
            DataModelManager.getInstance().setmMoviesList(list);
        } catch (Exception e) {

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
