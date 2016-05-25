package com.example.sara.movies.app;

import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
public class DetailFragment extends Fragment {

    public DetailFragment() {

    }
    HttpURLConnection urlConnection =null,urlConnection2 =null;
    int idInt,count=0,position,id;
    String jsonString="",reviewJson="",trailerJson="";
    Uri builtUri,builtUri2;
    ListView listView,listView2;
    ArrayAdapter<String> arrayAdapter,arrayAdapter2;
    BufferedReader reader = null,reader2 = null;
    View rootView;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ImageButton star;
    String chooseSorting;
    String poster_path,title,date,overview;
    double vote;
    @Override
    public void setArguments(Bundle args) {

        super.setArguments(args);
        position = args.getInt("position");
        jsonString=args.getString("jsonString");
        id=args.getInt("id");
        vote=args.getDouble("vote");
        poster_path=args.getString("poster_path");
        title=args.getString("title");
        date=args.getString("date");
        overview=args.getString("overview");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        chooseSorting=sharedPreferences.getString(getString(R.string.pref_appearance_key),getString(R.string.pref_appearance_default));
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        listView = (ListView)rootView.findViewById(R.id.listview_reviews);
        listView.setAdapter(arrayAdapter);
        listView2 = (ListView)rootView.findViewById(R.id.listview_trailers);
        listView2.setAdapter(arrayAdapter2);
        editor = sharedPreferences.edit();
        star=(ImageButton) rootView.findViewById(R.id.favorite);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star.setImageResource(R.drawable.fullstar);
                try{
                    int count=sharedPreferences.getInt("counter", 0)+1;
                    String posterPath=sharedPreferences.getString("poster_path","")+"&"+getStringFromJson(jsonString, position).get(2);
                    String idStr=sharedPreferences.getString("id", "")+"&"+getStringFromJson(jsonString, position).get(6);
                    String titlePref=sharedPreferences.getString("title", "")+"&"+getStringFromJson(jsonString, position).get(1);
                    String datePref=sharedPreferences.getString("date", "")+"&"+getStringFromJson(jsonString, position).get(4);
                    String overviewPref=sharedPreferences.getString("overview", "")+"&"+getStringFromJson(jsonString, position).get(0);
                    String votePref=sharedPreferences.getString("vote", "")+"&"+getStringFromJson(jsonString, position).get(5);
                    editor.putString("poster_path", posterPath);
                    editor.putInt("counter", count);
                    editor.putString("id", idStr);
                    editor.putString("title", titlePref);
                    editor.putString("date", datePref);
                    editor.putString("overview",overviewPref);
                    editor.putString("vote",votePref);
                    editor.commit();
                }catch (JSONException e){
                    Log.i("error",e.getMessage());
                }


            }
        });

// The detail Activity called via intent.  Inspect the intent for forecast data.

            try {
                if(!(chooseSorting.equals("favourite"))) {
                    Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + getStringFromJson(jsonString, position).get(2)).into(((ImageView) rootView.findViewById(R.id.image)));
                    idInt = (int) getStringFromJson(jsonString, position).get(6);
                    String isAdult = (String) getStringFromJson(jsonString, position).get(3);
                    ((TextView) rootView.findViewById(R.id.title)).setText((String) getStringFromJson(jsonString, position).get(1));
                    ((TextView) rootView.findViewById(R.id.releasedate)).setText((String) getStringFromJson(jsonString, position).get(4));
                    ((TextView) rootView.findViewById(R.id.overview)).setText((String) getStringFromJson(jsonString, position).get(0));
                    ((TextView) rootView.findViewById(R.id.voteaverage)).setText("" + getStringFromJson(jsonString, position).get(5));
                  //  ((TextView) rootView.findViewById(R.id.adult)).setText((isAdult == "true") ? "+18" : "");

                    new MovieReviews().execute(idInt);
                    new MovieTrailers().execute(idInt);
                }else{
                    star.setImageResource(R.drawable.fullstar);
                    Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" +poster_path).into(((ImageView) rootView.findViewById(R.id.image)));
                    ((TextView) rootView.findViewById(R.id.title)).setText(title);
                    ((TextView) rootView.findViewById(R.id.releasedate)).setText(date);
                    ((TextView) rootView.findViewById(R.id.overview)).setText(overview);
                    ((TextView) rootView.findViewById(R.id.voteaverage)).setText("" +vote);
                 //   ((TextView) rootView.findViewById(R.id.adult)).setText(adult);
                    new MovieReviews().execute(id);
                    new MovieTrailers().execute(id);
                }
            }catch (JSONException e) {
                Log.e("error", e.getMessage(), e);
                e.printStackTrace();
            }

        setRetainInstance(true);
        return rootView;
    }

    private ArrayList<Object> getStringFromJson(String jsonFileString,int position)
            throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String MDB_OVERVIEW = "overview";
        final String MDB_TITLE = "title";
        final String MDB_POSTER_PATH = "poster_path";
        final String MDB_VOTE_AVERAGE = "vote_average";
        final String MDB_RELEASE_DATE = "release_date";
        final String MDB_ADULT = "adult";
        final String MDB_ID = "id";


        ArrayList<Object> requiredDetails=new ArrayList<>();
        final String MDB_RESULTS = "results";
        JSONObject jsonFileObject = new JSONObject(jsonFileString);
        JSONArray MoviesArray = jsonFileObject.getJSONArray(MDB_RESULTS);
        JSONObject aMovie = MoviesArray.getJSONObject(position);

        String overview = aMovie.getString(MDB_OVERVIEW);
        String title = aMovie.getString(MDB_TITLE);
        String poster_path = aMovie.getString(MDB_POSTER_PATH);
        String adult=aMovie.getString(MDB_ADULT);
        String release_date=aMovie.getString(MDB_RELEASE_DATE);
        double vote_average=aMovie.getDouble(MDB_VOTE_AVERAGE);
        int id = aMovie.getInt(MDB_ID);

        requiredDetails.add(overview);
        requiredDetails.add(title);
        requiredDetails.add(poster_path);
        requiredDetails.add(adult);
        requiredDetails.add(release_date);
        requiredDetails.add(vote_average);
        requiredDetails.add(id);

        return requiredDetails;
    }

    public class MovieReviews extends AsyncTask<Integer, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Integer[] params) {
            if (params.length == 0) return null;
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String MOVIE_REVIEWS_URL = "/reviews?";
            final String APPID_PARAM = "api_key";

            try{
                builtUri = Uri.parse(MOVIE_BASE_URL+params[0]+MOVIE_REVIEWS_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_MOVIE_AAP_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
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

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                reviewJson = buffer.toString();
            } catch (IOException e) {
                Log.e("error", e.getMessage(), e);
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
                return getReviews(reviewJson);
            }catch(JSONException e) {
                Log.e("error", e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if(result !=null){
                arrayAdapter = new ArrayAdapter<>(
                        getActivity(),
                        R.layout.list_reviews,
                        R.id.list_reviews_text, //problem here
                        result
                );

            }
            listView.setAdapter(arrayAdapter);
            setListViewHeightBasedOnChildren(listView);
            //super.onPostExecute(result);
        }

        private ArrayList getReviews(String jsonFileString)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_AUTHOR = "author";
            final String MDB_CONTENT = "content";
            final String MDB_RESULTS = "results";

            JSONObject jsonFileObject = new JSONObject(jsonFileString);
            JSONArray MoviesArray = jsonFileObject.getJSONArray(MDB_RESULTS);
            String review="";
            ArrayList<String> resultStrs = new ArrayList<>();

            for (int i = 0; i < MoviesArray.length(); i++) {
                JSONObject aMovie = MoviesArray.getJSONObject(i);
                 review = aMovie.getString(MDB_AUTHOR) + ":\n" + aMovie.getString(MDB_CONTENT) + "\n\n";

                resultStrs.add(review);
            }

            return resultStrs;
        }
    }

    public class MovieTrailers extends AsyncTask<Integer, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Integer[] params) {
            if (params.length == 0) return null;
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
            final String MOVIE_VIDEOS_URL = "/videos?";
            final String APPID_PARAM = "api_key";

            try{
                builtUri2 = Uri.parse(MOVIE_BASE_URL+params[0]+MOVIE_VIDEOS_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_MOVIE_AAP_API_KEY)
                        .build();

                URL url = new URL(builtUri2.toString());
                urlConnection2 = (HttpURLConnection) url.openConnection();
                urlConnection2.setRequestMethod("GET"); //get,update,post,delete+
                urlConnection2.connect();

                InputStream inputStream = urlConnection2.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader2 = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader2.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                trailerJson = buffer.toString();
            } catch (IOException e) {
                Log.e("error", e.getMessage(), e);
                return null;
            } finally{
                if (urlConnection2 != null) {
                    urlConnection2.disconnect();
                }
                if (reader2 != null) {
                    try {
                        reader2.close();
                    } catch (final IOException e) {
                        Log.e("error", "Error closing stream", e);
                    }
                }
            }
            try {
                return getTrailersKeys(trailerJson);
            }catch(JSONException e) {
                Log.e("error", e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(final ArrayList<String> result) {
            ArrayList<String> trailersNamesList=new ArrayList<>();
            try{
                trailersNamesList= getTrailersNames(trailerJson);
            }catch(JSONException e) {
            Log.e("error", e.getMessage(), e);
            e.printStackTrace();
            }

            arrayAdapter2 = new ArrayAdapter<>(
                    getActivity(),
                    R.layout.list_trailers,
                    R.id.list_trailers_text, //problem here
                    trailersNamesList
            );
            listView2.setAdapter(arrayAdapter2);
            setListViewHeightBasedOnChildren(listView2);

            if(result !=null){
                final Intent intent=new Intent(Intent.ACTION_VIEW);
                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        intent.setData(Uri.parse(result.get(i)));
                        startActivity(intent);
                    }
                });
            }
            super.onPostExecute(result);
        }

        private ArrayList getTrailersNames(String jsonFileString)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_NAME = "name";
            final String MDB_RESULTS = "results";

            JSONObject jsonFileObject = new JSONObject(jsonFileString);
            JSONArray MoviesArray = jsonFileObject.getJSONArray(MDB_RESULTS);
            String name="";
            ArrayList<String> resultStrs = new ArrayList<>();

            for (int i = 0; i < MoviesArray.length(); i++) {
                JSONObject aMovie = MoviesArray.getJSONObject(i);
                name = aMovie.getString(MDB_NAME);

                resultStrs.add(name);
            }

            return resultStrs;
        }

        private ArrayList getTrailersKeys(String jsonFileString)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String MDB_KEY = "key";
            final String MDB_RESULTS = "results";

            JSONObject jsonFileObject = new JSONObject(jsonFileString);
            JSONArray MoviesArray = jsonFileObject.getJSONArray(MDB_RESULTS);
            String videoUrl="";
            ArrayList<String> resultStrs = new ArrayList<>();

            for (int i = 0; i < MoviesArray.length(); i++) {
                JSONObject aMovie = MoviesArray.getJSONObject(i);
                videoUrl = "https://www.youtube.com/watch?v=" +aMovie.getString(MDB_KEY);

                resultStrs.add(videoUrl);
            }

            return resultStrs;
        }

    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = ListView.MeasureSpec.makeMeasureSpec(listView.getWidth(), ListView.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, ListView.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
    }

}
