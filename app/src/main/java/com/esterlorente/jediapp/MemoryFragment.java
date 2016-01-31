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
import java.util.HashMap;
import java.util.Map;

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
    private Map<Integer, Integer> asignCards;
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

        initCards();

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
        listCards = new ArrayList();
        idsCards = new ArrayList();
        int id = 0;

        for (int i = 0; i < numCards; ++i) {
            LinearLayout rowLayout = new LinearLayout(context);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            for (int j = 0; j < numCards; ++j) {
                ImageView card = new ImageView(context);
                card.setImageResource(R.drawable.cover);

                int w = width / numCards;
                int h = height / numCards;
                if (h > 1.3 * w) h = (int) 1.3f * w;
                else if (h < 1.3 * w) w = Integer.valueOf(Math.round(h / 1.3f));

                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(w, h);
                card.setLayoutParams(parms);
                card.setOnClickListener(this);
                rowLayout.addView(card);

                // Asignacion dinamica de IDs a los ImageView
                View v = (View) rootView.findViewById(id);
                while (v != null || idsCards.indexOf(id) != -1) {
                    v = rootView.findViewById(++id);
                }
                card.setId(id);
                card.setTag(R.drawable.cover);
                idsCards.add(card.getId());
                listCards.add(card);
            }
            linearLayout.addView(rowLayout);
        }

        assignCards();
    }

    private void assignCards() {
        ArrayList<Integer> possibleCards = new ArrayList();
        possibleCards.addAll(Arrays.asList(R.drawable.carta1, R.drawable.carta2, R.drawable.carta3, R.drawable.carta4, R.drawable.carta1, R.drawable.carta2, R.drawable.carta3, R.drawable.carta4));
        asignCards = new HashMap<Integer, Integer>();

        ArrayList<Integer> randIds = new ArrayList(idsCards);
        Collections.shuffle(randIds);

        for (int i = 0; i < possibleCards.size() && i * 2 + 1 < randIds.size(); ++i) {
            int rand1 = randIds.get(i * 2);
            int rand2 = randIds.get(i * 2 + 1);

            int pos1 = idsCards.indexOf(rand1);
            int pos2 = idsCards.indexOf(rand2);

            asignCards.put(pos1, possibleCards.get(i));
            asignCards.put(pos2, possibleCards.get(i));
            Log.e(TAG, "Carta " + pos1 + " - imagen " + possibleCards.get(i));
            Log.e(TAG, "Carta " + pos2 + " - imagen " + possibleCards.get(i));
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
                card.setTag(asignCards.get(pos));

                Log.e(TAG, "Carta " + pos + " - imagen " + asignCards.get(pos));

                coolImageFlipper.flipImage(context.getDrawable(asignCards.get(pos)), card);
                break;
            default:
                card.setTag(R.drawable.cover);

                Log.e(TAG, "Carta " + pos + " - imagen " + asignCards.get(pos));

                coolImageFlipper.flipImage(context.getDrawable(R.drawable.cover), card);
                break;
        }
    }
}
