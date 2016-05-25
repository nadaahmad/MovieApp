package com.example.sara.movies.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sara on 04-Apr-16.
 */
public class ImageAdapter extends BaseAdapter{
    List<String> poster_paths=new ArrayList<>();
    Context context;

    public ImageAdapter(Context context,ArrayList<String> poster_paths){
        this.poster_paths=poster_paths;
        this.context=context;
    }

    @Override
    public int getCount() {
        return poster_paths.size();
    }

    @Override
    public String getItem(int i) {
        return poster_paths.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder=new ViewHolder();
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.imageee,null);
            holder.imageView=(ImageView)convertView.findViewById(R.id.imageee_item);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder)convertView.getTag();
        }

        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+getItem(i)).into(holder.imageView);

        return convertView;
    }
    class ViewHolder{
        ImageView imageView;
    }
}
