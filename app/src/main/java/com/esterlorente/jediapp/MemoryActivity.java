package com.esterlorente.jediapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.material.joanbarroso.flipper.CoolImageFlipper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MemoryActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = "MEMORY_ACTIVITY";
    private Context context;

    private int numCards = 4;
    private ArrayList<ImageView> listCards;
    private ArrayList<Integer> idsCards;
    private Map<Integer, Integer> asignCards;
    CoolImageFlipper coolImageFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            numCards = bundle.getInt("editNumCards");
            Log.e(TAG, "Paso NumCards: " + numCards);
        } else Log.e(TAG, "Intent - No hay valor");

        Context context = getApplicationContext();
        coolImageFlipper = new CoolImageFlipper(context);

        initCards();
    }

    @Override
    protected void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);


    }

    private void initCards() {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutCards);
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
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layoutCards);
        listCards = new ArrayList();
        idsCards = new ArrayList();
        int id = 0;

        for (int i = 0; i < numCards; ++i) {
            LinearLayout rowLayout = new LinearLayout(MemoryActivity.this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            for (int j = 0; j < numCards; ++j) {
                ImageView card = new ImageView(MemoryActivity.this);
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
                View v = (View) findViewById(id);
                while (v != null || idsCards.indexOf(id) != -1) {
                    v = findViewById(++id);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_memory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int pos = idsCards.indexOf(view.getId());
        int id = view.getId();
        ImageView card = (ImageView) findViewById(id);
        Log.e(TAG, "Click en carta " + id);

        switch ((Integer) card.getTag()) {
            case R.drawable.cover:
                card.setTag(asignCards.get(pos));

                Log.e(TAG, "Carta " + pos + " - imagen " + asignCards.get(pos));

                coolImageFlipper.flipImage(getDrawable(asignCards.get(pos)), card);
                //card.setTag(R.drawable.carta2);
                //coolImageFlipper.flipImage(getDrawable(R.drawable.carta2), card);
                break;
            default:
                card.setTag(R.drawable.cover);

                Log.e(TAG, "Carta " + pos + " - imagen " + asignCards.get(pos));

                coolImageFlipper.flipImage(getDrawable(R.drawable.cover), card);
                break;
        }
    }
}
