package com.crisnello.moviedb.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.crisnello.moviedb.R;
import com.crisnello.moviedb.entitie.Movie;

import java.util.ArrayList;


public class AdapterListView extends BaseAdapter
{
    private LayoutInflater mInflater;
    private ArrayList<Movie> itens;

    public AdapterListView(Context context, ArrayList<Movie> itens)
    {
        //Itens que preencheram o listview
        this.itens = itens;
        //responsavel por pegar o Layout do item.
        mInflater = LayoutInflater.from(context);
    }


    public int getCount()
    {
        return itens.size();
    }


    public Movie getItem(int position)
    {
        return itens.get(position);
    }


    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent)
    {

        Movie item = itens.get(position);

        view = mInflater.inflate(R.layout.movie_listview, null);

        ((TextView) view.findViewById(R.id.text)).setText(item.toString());


        return view;
    }
}