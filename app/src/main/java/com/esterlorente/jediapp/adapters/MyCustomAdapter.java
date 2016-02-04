package com.esterlorente.jediapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.esterlorente.jediapp.R;
import com.esterlorente.jediapp.utils.ImageParser;
import com.esterlorente.jediapp.utils.Score;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class MyCustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    ArrayList<Score> scores;

    public MyCustomAdapter(ArrayList<Score> scores) {
        this.scores = scores;
    }

    public void changeData(ArrayList<Score> scores) {
        this.scores = scores;
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        //Instancia un layout XML en la correspondiente vista.
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        //Inflamos en la vista el layout para cada elemento
        //View view = inflater.inflate(R.layout.row_ranquing, viewGroup, false);
        //return new AdapterViewHolder(view);

        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.header_ranquing, viewGroup, false);
            HeaderViewHolder adapterViewHolder = new HeaderViewHolder(view);
            return adapterViewHolder;
        } else if (viewType == TYPE_ITEM) {
            View view = inflater.inflate(R.layout.row_ranquing, viewGroup, false);
            return new AdapterViewHolder(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    //@Override
    public void onBindViewHolder(RecyclerView.ViewHolder adapterViewholder, int position) {
        if (adapterViewholder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) adapterViewholder;
            headerHolder.username.setText("Username");
            headerHolder.score.setText("Score");
            headerHolder.image.setText("Image");
        } else if (adapterViewholder instanceof AdapterViewHolder) {
            AdapterViewHolder rowViewHolder = (AdapterViewHolder) adapterViewholder;
            Score score = getItem(position);
            rowViewHolder.username.setText(score.getUsername());
            rowViewHolder.score.setText(String.valueOf(score.getScore()));
            rowViewHolder.image.setImageBitmap(ImageParser.byteArrayToBitmap(score.getImage()));
        }
    }

    @Override
    public int getItemCount() {
        //Debemos retornar el tamaño de todos los elementos contenidos en el viewholder
        //Por defecto es return 0 --> No se mostrará nada.
        return scores.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private Score getItem(int position) {
        return scores.get(position - 1);
    }


    // Definimos una clase viewholder que funciona como adapter para
    public class AdapterViewHolder extends RecyclerView.ViewHolder {
        /*
        *  Mantener una referencia a los elementos de nuestro ListView mientras el usuario realiza
        *  scrolling en nuestra aplicación. Así que cada vez que obtenemos la vista de un item,
        *  evitamos las frecuentes llamadas a findViewById, la cuál se realizaría únicamente la primera vez y el resto
        *  llamaríamos a la referencia en el ViewHolder, ahorrándonos procesamiento.
        */

        public ImageView image;
        public TextView username;
        public TextView score;
        public View v;

        public AdapterViewHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            this.image = (ImageView) itemView.findViewById(R.id.imageUser);
            this.username = (TextView) itemView.findViewById(R.id.textUser);
            this.score = (TextView) itemView.findViewById(R.id.textScore);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        /*
        *  Mantener una referencia a los elementos de nuestro ListView mientras el usuario realiza
        *  scrolling en nuestra aplicación. Así que cada vez que obtenemos la vista de un item,
        *  evitamos las frecuentes llamadas a findViewById, la cuál se realizaría únicamente la primera vez y el resto
        *  llamaríamos a la referencia en el ViewHolder, ahorrándonos procesamiento.
        */

        public TextView image;
        public TextView username;
        public TextView score;
        public View v;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            this.v = itemView;
            this.image = (TextView) itemView.findViewById(R.id.headerImage);
            this.username = (TextView) itemView.findViewById(R.id.headerUser);
            this.score = (TextView) itemView.findViewById(R.id.headerScore);
        }
    }
}

