package com.esterlorente.jediapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class MyCustomAdapter extends RecyclerView.Adapter<MyCustomAdapter.AdapterViewHolder> {

    ArrayList<Score> scores;

    MyCustomAdapter(ArrayList<Score> scores) {
        Log.e("TAG", "MyCustomAdapter");
        this.scores = scores;
    }


    @Override
    public MyCustomAdapter.AdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.e("TAG", "onCreateViewHolder");
        //Instancia un layout XML en la correspondiente vista.
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        //Inflamos en la vista el layout para cada elemento
        View view = inflater.inflate(R.layout.row_ranquing, viewGroup, false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyCustomAdapter.AdapterViewHolder adapterViewholder, int position) {
        Log.e("TAG", "onBindViewHolder " + adapterViewholder.username.getText());
        //Dependiendo del entero se asignará una imagen u otra
        adapterViewholder.username.setText(scores.get(position).getUsername());
        adapterViewholder.score.setText(String.valueOf(scores.get(position).getScore()));
    }

    @Override
    public int getItemCount() {
        Log.e("TAG", "getItemCount " + scores.size());
        //Debemos retornar el tamaño de todos los elementos contenidos en el viewholder
        //Por defecto es return 0 --> No se mostrará nada.
        return scores.size();
    }


    // Definimos una clase viewholder que funciona como adapter para
    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        /*
        *  Mantener una referencia a los elementos de nuestro ListView mientras el usuario realiza
        *  scrolling en nuestra aplicación. Así que cada vez que obtenemos la vista de un item,
        *  evitamos las frecuentes llamadas a findViewById, la cuál se realizaría únicamente la primera vez y el resto
        *  llamaríamos a la referencia en el ViewHolder, ahorrándonos procesamiento.
        */

        public TextView username;
        public TextView score;
        public View v;

        public AdapterViewHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            this.username = (TextView) itemView.findViewById(R.id.textUser);
            this.score = (TextView) itemView.findViewById(R.id.textScore);
        }
    }
}

