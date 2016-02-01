package com.esterlorente.jediapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esterlorente.jediapp.data.LoginHelper;

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
    private LoginHelper loginHelper;

    private TextView textAttempts;

    private int numCards = 2;
    private ArrayList<Integer> idsCards;
    private ArrayList<CardMemory> listCards;
    private Integer selCard;
    private int numPairsLeft = numCards * numCards / 2;

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
        textAttempts = (TextView) rootView.findViewById(R.id.textAttempt);

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
            selCard = -1;
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
        //Log.e(TAG, "Click en carta " + id);

        switch (cardMemory.getStateCard()) {
            case R.drawable.cover:
                cardMemory.setStateCard(cardMemory.getAsignCard());
                //Log.e(TAG, "Carta " + pos + " - imagen " + cardMemory.getStateCard());

                flipCard(cardMemory, context.getDrawable(cardMemory.getStateCard()), 0);
                listCards.set(pos, cardMemory);

                if (selCard != -1) {
                    // hay dos cartas bocarriba
                    if (cardMemory.getStateCard() == listCards.get(selCard).getStateCard()) {
                        // son pareja, eliminarlas a los 2s
                        Log.e(TAG, "Pareja!");
                        deleteCard(cardMemory, 2000);
                        deleteCard(listCards.get(selCard), 2000);

                        listCards.get(pos).setStateCard(0);
                        listCards.get(selCard).setStateCard(0);

                        numPairsLeft = numPairsLeft - 1;
                        textAttempts.setText(Integer.toString(Integer.parseInt(textAttempts.getText().toString()) + 1));

                        if (numPairsLeft == 0) {
                            Log.e(TAG, "Final del juego!!");
                            int score = Integer.parseInt(textAttempts.getText().toString());

                            SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
                            final SharedPreferences.Editor editor = pref.edit();
                            String username = pref.getString("key_username", null);

                            loginHelper = new LoginHelper(context);
                            Cursor cursor = loginHelper.getUserScoreByName(username, numCards);
                            if (cursor.moveToFirst()) {
                                int minAttemps = cursor.getInt(cursor.getColumnIndex(loginHelper.SCORE));
                                Log.e(TAG, "MinAttemps " + minAttemps);
                                if (minAttemps > score) {
                                    Log.e(TAG, "Mejora de marca!");
                                    ContentValues valuesToStore = new ContentValues();
                                    valuesToStore.put(loginHelper.SCORE, score);
                                    loginHelper.updateScoreByName(valuesToStore, username, numCards);
                                } else Log.e(TAG, "No mejora :(");
                            } else {
                                Log.e(TAG, "Primera marca! :D");
                                ContentValues valuesToStore = new ContentValues();
                                valuesToStore.put(loginHelper.USERNAME, username);
                                valuesToStore.put(loginHelper.SCORE, score);
                                valuesToStore.put(loginHelper.NUMCARDS, numCards);
                                loginHelper.createScore(valuesToStore);
                            }
                        }
                    } else {
                        // no son pareja, voltearlas
                        Log.e(TAG, "No son pareja :(");
                        flipCard(cardMemory, context.getDrawable(R.drawable.cover), 2000);
                        flipCard(listCards.get(selCard), context.getDrawable(R.drawable.cover), 2000);

                        cardMemory.setStateCard(R.drawable.cover);
                        listCards.get(selCard).setStateCard(R.drawable.cover);
                        textAttempts.setText(Integer.toString(Integer.parseInt(textAttempts.getText().toString()) + 1));
                    }

                    selCard = -1;
                } else {
                    // es la primera carta bocarriba
                    selCard = pos;
                }

                break;
            default:
                Log.e(TAG, "No deberia pasar");
                break;
        }
    }

    private void flipCard(final CardMemory cardMemory, final Drawable newImage, int time) {
        /*
        new Thread(new Runnable() {
            public void run() {
                cardMemory.flipCard(context, newImage);
            }
        }).start();
        */

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cardMemory.flipCard(context, newImage);
            }
        }, time);
    }

    private void deleteCard(final CardMemory cardMemory, int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cardMemory.getCard().setVisibility(View.INVISIBLE);
            }
        }, time);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        Log.e(TAG, "Guardando datos");

        outstate.putIntegerArrayList("idsCards", idsCards);
        outstate.putParcelableArrayList("listCards", listCards);
        outstate.putInt("selCard", selCard);
        outstate.putInt("numPairsLeft", numPairsLeft);
        outstate.putString("numAttempts", textAttempts.getText().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e(TAG, "Restaurando datos");

            idsCards = (ArrayList) savedInstanceState.getIntegerArrayList("idsCards");
            listCards = (ArrayList) savedInstanceState.getParcelableArrayList("listCards");
            selCard = savedInstanceState.getInt("selCard");
            numPairsLeft = savedInstanceState.getInt("numPairsLeft");
            textAttempts.setText(savedInstanceState.getString("numAttempts"));

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
