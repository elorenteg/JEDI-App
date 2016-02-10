package com.esterlorente.jediapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.esterlorente.jediapp.adapters.MyCustomAdapter;
import com.esterlorente.jediapp.data.LoginHelper;
import com.esterlorente.jediapp.utils.Score;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

        initRanquing();

        return rootView;
    }

    private ArrayList<Score> getScoresByCard(int numCard) {
        ArrayList<Score> scores = new ArrayList();

        Cursor cursor = loginHelper.getAllScoresAndImagesByNumcard(numCard);
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndex(loginHelper.USERNAME));
                int score = cursor.getInt(cursor.getColumnIndex(loginHelper.SCORE));
                String path = cursor.getString(cursor.getColumnIndex(loginHelper.IMAGE));
                byte[] image = null;
                if (path == null) {
                    Bitmap bitmap = drawableToBitmap(getActivity().getDrawable(R.drawable.gato5));
                    bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    image = stream.toByteArray();
                } else {
                    Uri uri = Uri.parse(path);

                    if (!getRealPathFromURI(uri).equals("-1")) {
                        File imgFile = new File(getRealPathFromURI(uri).toString());
                        if (imgFile.exists()) {
                            Bitmap bitmap1 = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            Bitmap bitmap3 = Bitmap.createScaledBitmap(bitmap1, 150, 150, true);
                            Drawable drawable = new BitmapDrawable(getResources(), bitmap3);

                            Bitmap bitmap = drawableToBitmap(drawable);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            image = stream.toByteArray();
                        } else {
                            Bitmap bitmap = drawableToBitmap(getActivity().getDrawable(R.drawable.gato5));
                            bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            image = stream.toByteArray();
                        }
                    }
                }
                Score scoreOBJ = new Score(image, username, score);
                scores.add(scoreOBJ);

            } while (cursor.moveToNext());
        }
        cursor.close();

        return scores;
    }

    private String getRealPathFromURI(Uri contentURI) {
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            if (cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                return cursor.getString(idx);
            }
            return "-1";
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        final int width = !drawable.getBounds().isEmpty() ? drawable
                .getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ? drawable
                .getBounds().height() : drawable.getIntrinsicHeight();

        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width,
                height <= 0 ? 1 : height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case R.id.restart:
                ArrayList<Score> emptyScores = new ArrayList();
                adapter.changeData(emptyScores);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        //Log.e(TAG, "Guardando datos");

        outstate.putInt("numCards", numCards);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //Log.e(TAG, "Restaurando datos");

            numCards = savedInstanceState.getInt("numCards");
            initRanquing();
        }
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
        //scores = filterPostDelete(scores, numCards);
        adapter = new MyCustomAdapter(scores);
        mRecyclerView.setAdapter(adapter);
    }

    private ArrayList<Score> filterPostDelete(ArrayList<Score> scores, int numCards) {
        /*
        SharedPreferences pref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String username = pref.getString("key_username", null);
        if (username != null) {
            loginUser(username);
        }

        ArrayList<Score> filteredScores = new ArrayList();
        for (int i = 0; i < scores.size(); ++i) {

        }
        */
        return null;
    }

    public void refresh(int numCards) {
        //Log.e(TAG, "Refresh");
        this.numCards = numCards;
        ArrayList<Score> scores = getScoresByCard(numCards);
        adapter.changeData(scores);
    }
}
