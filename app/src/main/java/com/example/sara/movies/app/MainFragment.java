package com.example.sara.movies.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {
    public MainFragment() {

    }
    GridView gridView;
    ImageAdapter imageAdapter;
    View rootView ;
    ArrayList<String> resultStrs;
    String jsonFileString,chooseSorting;
    SetMovieName setMovieName;
    ArrayList<String> posterPathesList=new ArrayList<>();
    ArrayList<String> idList=new ArrayList<>();
    ArrayList<String> titleList=new ArrayList<>();
    ArrayList<String> dateList=new ArrayList<>();
    ArrayList<String> overviewList=new ArrayList<>();
    ArrayList<String> voteList=new ArrayList<>();
   // ArrayList<String> adultList=new ArrayList<>();

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setMovieName = (SetMovieName) activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        gridView.setAdapter(null);
        SharedPreferences sharedPrefs= PreferenceManager.getDefaultSharedPreferences(getActivity());
        chooseSorting=sharedPrefs.getString(getString(R.string.pref_appearance_key),getString(R.string.pref_appearance_default));
        if(!(chooseSorting.equals("favourite"))) {
            new MovieTask().execute(chooseSorting);
        }
        else{
            int numPathes=sharedPrefs.getInt("counter",0);
            posterPathesList.clear();
            String posterPathesArr[];
            String posterPathes=sharedPrefs.getString("poster_path", "");
            posterPathesArr=posterPathes.substring(1).split("&",numPathes);
            for(int i=0;i<posterPathesArr.length;i++)
                if((!posterPathesList.equals(null)) &&(!(posterPathesList.contains(posterPathesArr[i]))))
                    posterPathesList.add(posterPathesArr[i]);

            String idArr[];
            String ids=sharedPrefs.getString("id","");
            idArr=ids.substring(1).split("&",numPathes);
            for(int i=0;i<idArr.length;i++){ idList.add(idArr[i]);}

            String titleArr[];
            String titles=sharedPrefs.getString("title","");
            titleArr=titles.substring(1).split("&",numPathes);
            for(int i=0;i<titleArr.length;i++){ titleList.add(titleArr[i]);}

            String dateArr[];
            String dates=sharedPrefs.getString("date","");
            dateArr=dates.substring(1).split("&",numPathes);
            for(int i=0;i<dateArr.length;i++){ dateList.add(dateArr[i]);}

            String overviewArr[];
            String overviews=sharedPrefs.getString("overview","");
            overviewArr=overviews.substring(1).split("&",numPathes);
            for(int i=0;i<overviewArr.length;i++){ overviewList.add(overviewArr[i]);}

            String voteArr[];
            String votes=sharedPrefs.getString("vote","");
            voteArr=votes.substring(1).split("&",numPathes);
            for(int i=0;i<voteArr.length;i++){ voteList.add(voteArr[i]);}

//            String adultArr[];
//            String adults=sharedPrefs.getString("adult","");
//            Log.i("tttt",adults);
//            adultArr=adults.substring(1).split("-",numPathes);
//            for(int i=0;i<adultArr.length;i++){Log.i("onstartLoop",i+adultArr[i]); adultList.add(adultArr[i]);}
            gridView.setAdapter(null);

                   // gridView.clearChoices();
            imageAdapter=new ImageAdapter(getActivity(),posterPathesList);
            gridView.setAdapter(imageAdapter);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_main, container, false);
        gridView= (GridView) rootView.findViewById(R.id.posterrr);
        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if(!(chooseSorting.equals("favourite"))) {
                            setMovieName.setName(i, jsonFileString);
                        }
                        else{
                            setMovieName.setNameFavouriteCase(Integer.parseInt(idList.get(i)),Double.parseDouble(voteList.get(i)),posterPathesList.get(i)
                                    ,titleList.get(i),overviewList.get(i),dateList.get(i)/*,adultList.get(i)*/);
                        }
                    }
                });
        return rootView;
    }

    public void setMovieListener(SetMovieName MovieName) {
        setMovieName = MovieName;
    }

    public class MovieTask extends AsyncTask<String,Void ,ArrayList<String>> {
        @Override
        protected void onPostExecute(ArrayList<String> paths) {
            imageAdapter=new ImageAdapter(getActivity(),paths);
            gridView.setAdapter(imageAdapter);
            super.onPostExecute(paths);
        }

        @Override
        protected ArrayList<String> doInBackground(String[] strings) {
            if(strings.length==0) return null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


            try{
                //URL url=new URL("https://api.themoviedb.org/3/movie/popular?api_key=30e0eb4c9a04152e0620151e5d67943c");
                final String MOVIE_BASE_URL="https://api.themoviedb.org/3/movie/";
                final String MOVIE_POPULAR_URL="popular?";
                final String MOVIE_RATED_URL="top_rated?";
                final String APPID_PARAM="api_key";

                Uri builtUri;

                if(strings[0].equals("popular")){
                    builtUri=Uri.parse(MOVIE_BASE_URL + MOVIE_POPULAR_URL).buildUpon()
                            .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_MOVIE_AAP_API_KEY)
                            .build();
                }else {
                    builtUri=Uri.parse(MOVIE_BASE_URL + MOVIE_RATED_URL).buildUpon()
                            .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_MOVIE_AAP_API_KEY)
                            .build();
                }

                URL url=new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET"); //get,update,post,delete+
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) return null;

                jsonFileString = buffer.toString();

            } catch (IOException e) {
                Log.e("error", e.getMessage());
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("error", "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(jsonFileString);
            } catch (JSONException e) {
                Log.e("error", e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
        private ArrayList getMovieDataFromJson(String jsonFileString)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_RESULTS = "results";
            final String MDB_POSTER_PATH = "poster_path";

            JSONObject jsonFileObject = new JSONObject(jsonFileString);
            JSONArray MoviesArray = jsonFileObject.getJSONArray(MDB_RESULTS);

            resultStrs = new ArrayList<>();
            for(int i = 0; i < MoviesArray.length(); i++) {
                JSONObject aMovie = MoviesArray.getJSONObject(i);
                String posterPath = aMovie.getString(MDB_POSTER_PATH);
                resultStrs.add(posterPath);
            }
            return resultStrs;
        }

    }

}