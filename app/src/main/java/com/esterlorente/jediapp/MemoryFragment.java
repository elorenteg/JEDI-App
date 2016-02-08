package com.esterlorente.jediapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esterlorente.jediapp.data.LoginHelper;
import com.esterlorente.jediapp.utils.CardMemory;

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

    public String username;

    public TextView textAttempts;

    private int numCards;
    private int numPairsLeft;
    private ArrayList<Integer> idsCards;
    private ArrayList<CardMemory> listCards;
    private Integer selCard;

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

        setHasOptionsMenu(true);

        Bundle args = this.getArguments();
        if (args != null) {
            username = args.getString("username");
        }

        context = getContext();
        textAttempts = (TextView) rootView.findViewById(R.id.textAttempt);

        if (savedInstanceState == null && args != null && args.getBoolean("first")) {
            Log.e(TAG, "Inicializando datos");
            numCards = 4;
            restartMemory(numCards);
        }

        return rootView;
    }

    private void initCards() {
        final LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutCards);
        linearLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = linearLayout.getWidth();
                int height = linearLayout.getHeight();

                drawCards(width, height);
            }
        });
    }

    private void drawCards(int width, int height) {
        boolean initialize = listCards == null;
        LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.layoutCards);
        if (initialize) {
            linearLayout.removeAllViews();
            idsCards = new ArrayList();
            listCards = new ArrayList();
            selCard = -1;
        }
        int id = 0;
        int paddingLeft = 10;
        int paddingRight = 10;
        int paddingTop = 10;
        int paddingBottom = 10;

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

                int w = (width - numCards * (paddingLeft + paddingRight)) / numCards;
                int h = (height - numCards * (paddingTop + paddingBottom)) / numCards;
                if (h > 1.3 * w) h = (int) 1.3f * w;
                else if (h < 1.3 * w) w = Integer.valueOf(Math.round(h / 1.3f));
                LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(w, h);
                parms.setMargins(paddingLeft, paddingTop, paddingRight, paddingBottom);
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

            CardMemory card1 = listCards.get(idsCards.indexOf(rand1));
            CardMemory card2 = listCards.get(idsCards.indexOf(rand2));

            card1.setAsignCard(possibleCards.get(i));
            card2.setAsignCard(possibleCards.get(i));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int pos = idsCards.indexOf(id);

        CardMemory cardMemory = listCards.get(pos);

        switch (cardMemory.getStateCard()) {
            case R.drawable.cover:
                cardMemory.setStateCard(cardMemory.getAsignCard());

                flipCard(cardMemory, cardMemory.getStateCard(), 0);

                if (selCard != -1) {
                    // hay dos cartas bocarriba
                    int score = Integer.parseInt(textAttempts.getText().toString()) + 1;
                    textAttempts.setText(Integer.toString(score));

                    if (cardMemory.getStateCard() == listCards.get(selCard).getStateCard()) {
                        // son pareja, eliminarlas a los 2s
                        Log.e(TAG, "Pareja!");
                        deleteCards(cardMemory, listCards.get(selCard), 2000);

                        numPairsLeft = numPairsLeft - 1;

                        if (numPairsLeft == 0) {
                            Log.e(TAG, "Final del juego!!");

                            loginHelper = new LoginHelper(context);
                            Cursor cursor = loginHelper.getUserScoreByName(username, numCards);
                            if (cursor.moveToFirst()) {
                                int minAttemps = cursor.getInt(cursor.getColumnIndex(loginHelper.SCORE));

                                if (minAttemps > score) {
                                    Log.e(TAG, "Mejora de marca!");
                                    updateScore(username, numCards, score);
                                    //notifyDataSetChanged();
                                } else {
                                    Log.e(TAG, "No mejora :(");
                                }
                            } else {
                                Log.e(TAG, "Primera marca! :D");
                                createScore(username, numCards, score);
                            }
                            showEndGame();
                        }
                    } else {
                        // no son pareja, voltearlas
                        Log.e(TAG, "No son pareja :(");
                        flipCard(cardMemory, R.drawable.cover, 2000);
                        flipCard(listCards.get(selCard), R.drawable.cover, 2000);
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

    private void showEndGame() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(getString(R.string.complete)).setIcon(R.drawable.gato)
                .setMessage(getString(R.string.alert_mss) + " " + textAttempts.getText() + "\n" +
                        getString(R.string.newGame))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        restartMemory(numCards);
                    }
                })
                .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setCancelable(false);

        alertDialog.create();
        alertDialog.show();
    }

    private void createScore(String username, int numCards, int score) {
        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.USERNAME, username);
        valuesToStore.put(loginHelper.SCORE, score);
        valuesToStore.put(loginHelper.NUMCARDS, numCards);
        loginHelper.createScore(valuesToStore);
    }

    private void updateScore(String username, int numCards, int score) {
        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.SCORE, score);
        loginHelper.updateScoreByName(valuesToStore, username, numCards);
    }

    private void flipCard(final CardMemory cardMemory, final int newImage, int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cardMemory.flipCard(context, newImage);
            }
        }, time);
    }

    private void deleteCards(final CardMemory cardMemory1, final CardMemory cardMemory2, int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cardMemory1.setInvisible();
                cardMemory2.setInvisible();
            }
        }, time);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        //super.onSaveInstanceState(outstate);

        Log.e(TAG, "Guardando datos");

        outstate.putInt("numCards", numCards);
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

            numCards = savedInstanceState.getInt("numCards");
            idsCards = (ArrayList) savedInstanceState.getIntegerArrayList("idsCards");
            listCards = (ArrayList) savedInstanceState.getParcelableArrayList("listCards");
            selCard = savedInstanceState.getInt("selCard");
            numPairsLeft = savedInstanceState.getInt("numPairsLeft");
            textAttempts.setText(savedInstanceState.getString("numAttempts"));

            initCards();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.restart:
                restartMemory(numCards);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void restartMemory(int numCards) {
        this.numCards = numCards;
        numPairsLeft = numCards * numCards / 2;
        textAttempts.setText("0");
        listCards = null;
        initCards();
    }
}
