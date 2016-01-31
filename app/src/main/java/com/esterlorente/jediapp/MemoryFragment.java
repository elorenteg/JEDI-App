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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * A placeholder fragment containing a simple view.
 */
public class MemoryFragment extends Fragment implements View.OnClickListener {
    private String TAG = "MEMORY_FRAGMENT";
    private Context context;
    private View rootView;

    private int numCards = 4;
    ArrayList<Integer> idsCards;
    private ArrayList<CardMemory> listCards;

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
        boolean initialize = listCards == null;
        if (initialize) {
            idsCards = new ArrayList();
            listCards = new ArrayList();
        }
        int id = 0;

        for (int i = 0; i < numCards; ++i) {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            for (int j = 0; j < numCards; ++j) {
                CardMemory cardMemory;
                ImageView card = new ImageView(context);
                if (initialize) {
                    cardMemory = new CardMemory();
                    cardMemory.setStateCard(R.drawable.cover);

                    card.setImageResource(R.drawable.cover);

                    // Asignacion dinamica de IDs a los ImageView
                    View v = (View) rootView.findViewById(id);
                    while (v != null || idsCards.indexOf(id) != -1) {
                        v = rootView.findViewById(++id);
                    }
                    card.setId(id);
                    idsCards.add(id);
                } else {
                    cardMemory = listCards.get(i * numCards + j);

                    card.setImageResource(listCards.get(i * numCards + j).getStateCard());
                    card.setId(idsCards.get(i * numCards + j));
                }

                int w = width / numCards;
                int h = height / numCards;
                if (h > 1.3 * w) h = (int) 1.3f * w;
                else if (h < 1.3 * w) w = Integer.valueOf(Math.round(h / 1.3f));
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(w, h);
                card.setLayoutParams(parms);
                card.setOnClickListener(this);
                cardMemory.setCard(card);

                listCards.add(cardMemory);
                rowLayout.addView(card);
            }
            linearLayout.addView(rowLayout);
        }

        if (initialize) assignCards(idsCards);
    }

    private void assignCards(ArrayList<Integer> idsCards) {
        ArrayList<Integer> possibleCards = new ArrayList();
        possibleCards.addAll(Arrays.asList(R.drawable.carta1, R.drawable.carta2, R.drawable.carta3, R.drawable.carta4, R.drawable.carta1, R.drawable.carta2, R.drawable.carta3, R.drawable.carta4));

        ArrayList<Integer> randIds = new ArrayList(idsCards);
        Collections.shuffle(randIds);

        for (int i = 0; i < possibleCards.size() && i * 2 + 1 < randIds.size(); ++i) {
            int rand1 = randIds.get(i * 2);
            int rand2 = randIds.get(i * 2 + 1);

            int pos1 = idsCards.indexOf(rand1);
            int pos2 = idsCards.indexOf(rand2);

            CardMemory card1 = listCards.get(pos1);
            CardMemory card2 = listCards.get(pos2);

            card1.setAsignCard(possibleCards.get(i));
            card2.setAsignCard(possibleCards.get(i));

            listCards.set(pos1, card1);
            listCards.set(pos2, card2);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int pos = idsCards.indexOf(id);

        CardMemory cardMemory = listCards.get(pos);
        Log.e(TAG, "Click en carta " + id);

        switch (cardMemory.getStateCard()) {
            case R.drawable.cover:
                cardMemory.setStateCard(cardMemory.getAsignCard());
                listCards.set(pos, cardMemory);

                Log.e(TAG, "Carta " + pos + " - imagen " + cardMemory.getStateCard());

                coolImageFlipper.flipImage(context.getDrawable(cardMemory.getStateCard()), cardMemory.getCard());
                break;
            default:
                cardMemory.setStateCard(R.drawable.cover);
                listCards.set(pos, cardMemory);

                Log.e(TAG, "Carta " + pos + " - imagen " + cardMemory.getStateCard());

                coolImageFlipper.flipImage(context.getDrawable(R.drawable.cover), cardMemory.getCard());
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        Log.e(TAG, "Guardando datos");

        outstate.putIntegerArrayList("idsCards", idsCards);
        outstate.putParcelableArrayList("listCards", listCards);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e(TAG, "Restaurando datos");

            idsCards = (ArrayList) savedInstanceState.getIntegerArrayList("idsCards");
            listCards = (ArrayList) savedInstanceState.getParcelableArrayList("listCards");

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
