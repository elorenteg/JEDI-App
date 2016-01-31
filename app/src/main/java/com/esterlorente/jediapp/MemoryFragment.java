package com.esterlorente.jediapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.material.joanbarroso.flipper.CoolImageFlipper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MemoryFragment extends Fragment implements View.OnClickListener {
    private String TAG = "MEMORY_FRAGMENT";
    private Context context;
    private View rootView;

    private int numCards = 4;

    private ArrayList<ImageView> listCards;
    private ArrayList<Integer> idsCards;
    private List<Integer> asignCards;
    private List<Integer> stateCards;

    CoolImageFlipper coolImageFlipper;

    public MemoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_memory, container, false);

        context = getContext();
        coolImageFlipper = new CoolImageFlipper(context);

        return rootView;
    }

    private void initCards() {
        final LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutCards);
        linearLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = linearLayout.getWidth();
                int height = linearLayout.getHeight();
                Log.e(TAG, width + " " + height);

                drawCards(width, height);
            }
        });
    }

    private void drawCards(int width, int height) {
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutCards);
        boolean initialize = idsCards == null;
        if (initialize) {
            idsCards = new ArrayList();
            stateCards = new ArrayList(Collections.nCopies(numCards * numCards, R.drawable.cover));
        }
        listCards = new ArrayList();
        int id = 0;

        for (int i = 0; i < numCards; ++i) {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            for (int j = 0; j < numCards; ++j) {
                ImageView card = new ImageView(context);
                if (initialize) {
                    card.setImageResource(R.drawable.cover);
                } else {
                    card.setImageResource(stateCards.get(i * numCards + j));
                }

                int w = width / numCards;
                int h = height / numCards;
                if (h > 1.3 * w) h = (int) 1.3f * w;
                else if (h < 1.3 * w) w = Integer.valueOf(Math.round(h / 1.3f));
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(w, h);
                card.setLayoutParams(parms);
                card.setOnClickListener(this);

                if (initialize) {
                    // Asignacion dinamica de IDs a los ImageView
                    View v = (View) rootView.findViewById(id);
                    while (v != null || idsCards.indexOf(id) != -1) {
                        v = rootView.findViewById(++id);
                    }
                    card.setId(id);
                    card.setTag(R.drawable.cover);
                    idsCards.add(card.getId());
                } else {
                    card.setId(idsCards.get(i * numCards + j));
                    card.setTag(stateCards.get(i * numCards + j));
                }

                listCards.add(card);
                rowLayout.addView(card);
            }
            linearLayout.addView(rowLayout);
        }

        if (initialize) assignCards();
    }

    private void assignCards() {
        ArrayList<Integer> possibleCards = new ArrayList();
        possibleCards.addAll(Arrays.asList(R.drawable.carta1, R.drawable.carta2, R.drawable.carta3, R.drawable.carta4, R.drawable.carta1, R.drawable.carta2, R.drawable.carta3, R.drawable.carta4));
        asignCards = Arrays.asList(new Integer[numCards * numCards]);

        ArrayList<Integer> randIds = new ArrayList(idsCards);
        Collections.shuffle(randIds);

        for (int i = 0; i < possibleCards.size() && i * 2 + 1 < randIds.size(); ++i) {
            int rand1 = randIds.get(i * 2);
            int rand2 = randIds.get(i * 2 + 1);

            int pos1 = idsCards.indexOf(rand1);
            int pos2 = idsCards.indexOf(rand2);

            asignCards.set(pos1, possibleCards.get(i));
            asignCards.set(pos2, possibleCards.get(i));
        }
    }

    @Override
    public void onClick(View view) {
        int pos = idsCards.indexOf(view.getId());
        int id = view.getId();
        ImageView card = (ImageView) rootView.findViewById(id);
        Log.e(TAG, "Click en carta " + id);

        switch ((Integer) card.getTag()) {
            case R.drawable.cover:
                stateCards.set(pos, asignCards.get(pos));
                card.setTag(stateCards.get(pos));

                Log.e(TAG, "Carta " + pos + " - imagen " + stateCards.get(pos));

                coolImageFlipper.flipImage(context.getDrawable(stateCards.get(pos)), card);
                break;
            default:
                stateCards.set(pos, R.drawable.cover);
                card.setTag(R.drawable.cover);

                Log.e(TAG, "Carta " + pos + " - imagen " + stateCards.get(pos));

                coolImageFlipper.flipImage(context.getDrawable(R.drawable.cover), card);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        Log.e(TAG, "Guardando datos");

        outstate.putSerializable("idsCards", (Serializable) idsCards);
        outstate.putSerializable("asignCards", (Serializable) asignCards);
        outstate.putSerializable("stateCards", (Serializable) stateCards);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e(TAG, "Restaurando datos");

            idsCards = (ArrayList) savedInstanceState.getSerializable("idsCards");
            asignCards = (List) savedInstanceState.getSerializable("asignCards");
            stateCards = (List) savedInstanceState.getSerializable("stateCards");

            initCards();
        } else {
            if (listCards != null) {
                Log.e(TAG, "Datos no-null");
            } else {
                Log.e(TAG, "Datos null, inicializando");
                initCards();
            }
        }
    }

}
