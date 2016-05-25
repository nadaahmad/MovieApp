package com.example.sara.movies.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements SetMovieName{

    boolean tablet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tablet= findViewById(R.id.detail_container) !=null;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(savedInstanceState==null){
            MainFragment mainFragment =new MainFragment();
            mainFragment.setMovieListener(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, mainFragment).commit();
        }
    }

    @Override
    public void setName(int i,String jsonFileString) {
        if(tablet) {
            DetailFragment detailFragment =new DetailFragment();
            Bundle bundle=new Bundle();
            bundle.putInt("position",i);
            bundle.putString("jsonString", jsonFileString);
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, detailFragment).commit();
        }else{
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra("position",i)
                    .putExtra("jsonString",jsonFileString);
            startActivity(intent);
            }
        }

    public void setNameFavouriteCase(int id,double vote,String posterPath,String title
            ,String overview,String date/*,String adult*/) {
        if(tablet) {
            DetailFragment detailFragment =new DetailFragment();
            Bundle bundle=new Bundle();
            bundle.putInt("id",id);
            bundle.putDouble("vote", vote);
            bundle.putString("poster_path", posterPath);
            bundle.putString("title",title);
            bundle.putString("overview",overview);
            bundle.putString("date",date);
         //   bundle.putString("adult",adult);
            detailFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.detail_container, detailFragment).commit();
        }else{
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra("id", id)
                    .putExtra("vote", vote)
                    .putExtra("poster_path",posterPath)
                    .putExtra("title",title)
                    .putExtra("overview",overview)
                    .putExtra("date",date);
                //    .putExtra("adult",adult);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.title_activity_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}
