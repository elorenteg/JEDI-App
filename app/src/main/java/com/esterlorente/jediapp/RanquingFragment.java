package com.esterlorente.jediapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.esterlorente.jediapp.adapters.MyCustomAdapter;
import com.esterlorente.jediapp.data.LoginHelper;
import com.esterlorente.jediapp.utils.Score;

import java.util.ArrayList;


public class RanquingFragment extends Fragment {
    private String TAG = "RANQUING_FRAGMENT";
    private Context context;
    private View rootView;
    private LoginHelper loginHelper;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayout;
    private MyCustomAdapter adapter;

    private int numCards = 4;

    public RanquingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ranquing, container, false);
        context = getActivity();
        loginHelper = new LoginHelper(context);
        setHasOptionsMenu(true);

        return rootView;
    }

    private ArrayList<Score> getScoresByCard(int numCard) {
        ArrayList<Score> scores = new ArrayList();

        Cursor cursor = loginHelper.getAllScoresByNumcard(numCard);
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(loginHelper.USERNAME));
                int score = cursor.getInt(cursor.getColumnIndex(loginHelper.SCORE));
                Score scoreOBJ = new Score(username, score);
                scores.add(scoreOBJ);
            } while (cursor.moveToNext());
        }

        Log.e(TAG, "Scores " + scores.size());

        return scores;
    }

    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ranquing, menu);
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ArrayList<Score> scores;

        switch (id) {
            case R.id.settings:
                Log.e(TAG, "ranquing");
                return true;
            case R.id.num2:
                Log.e(TAG, "ranquing");
                scores = getScoresByCard(2);
                adapter.changeData(scores);
                return true;
            case R.id.num4:
                Log.e(TAG, "ranquing");
                scores = getScoresByCard(4);
                adapter.changeData(scores);
                return true;
            case R.id.num6:
                Log.e(TAG, "ranquing");
                scores = getScoresByCard(6);
                adapter.changeData(scores);
                return true;
            case R.id.num8:
                Log.e(TAG, "ranquing");
                scores = getScoresByCard(8);
                adapter.changeData(scores);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        Log.e(TAG, "Guardando datos");

        outstate.putInt("numCards", numCards);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e(TAG, "Restaurando datos");

            numCards = savedInstanceState.getInt("numCards");
        }
        initRanquing();
    }

    private void initRanquing() {
        //findViewById del layout activity_main
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        //LinearLayoutManager necesita el contexto de la Activity.
        //El LayoutManager se encarga de posicionar los items dentro del recyclerview
        //Y de definir la politica de reciclaje de los items no visibles.
        mLinearLayout = new LinearLayoutManager(context);

        //Asignamos el LinearLayoutManager al recycler:
        mRecyclerView.setLayoutManager(mLinearLayout);

        //El adapter se encarga de  adaptar un objeto definido en el c�digo a una vista en xml
        //seg�n la estructura definida.
        //Asignamos nuestro custom Adapter
        ArrayList<Score> scores = getScoresByCard(numCards);
        adapter = new MyCustomAdapter(scores);
        mRecyclerView.setAdapter(adapter);
    }


}
