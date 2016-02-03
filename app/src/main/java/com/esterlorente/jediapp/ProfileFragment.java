package com.esterlorente.jediapp;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.esterlorente.jediapp.data.LoginHelper;

import java.util.ArrayList;


public class ProfileFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private String TAG = "PROFILE_FRAGMENT";

    private View rootview;
    private Context context;
    private LoginHelper loginHelper;

    private ImageView imageProfile;
    private Button buttonStreet;
    private ImageButton imageCamera, imageGallery, imageGPS;
    private EditText editStreet;
    private TextView textUsername, textStreet, textLatitude, textLongitude, text2Cards, text4Cards, text6Cards, text8Cards;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_profile, container, false);

        context = getActivity();
        loginHelper = new LoginHelper(context);
        setHasOptionsMenu(true);

        initButtons();

        return rootview;
    }

    private void initButtons() {
        imageProfile = (ImageView) rootview.findViewById(R.id.imageProfile);
        buttonStreet = (Button) rootview.findViewById(R.id.buttonStreet);
        imageCamera = (ImageButton) rootview.findViewById(R.id.imageCamera);
        imageGPS = (ImageButton) rootview.findViewById(R.id.imageGPS);
        imageGallery = (ImageButton) rootview.findViewById(R.id.imageGallery);
        editStreet = (EditText) rootview.findViewById(R.id.editStreet);
        textStreet = (TextView) rootview.findViewById(R.id.textStreet);
        textUsername = (TextView) rootview.findViewById(R.id.textName);
        textLatitude = (TextView) rootview.findViewById(R.id.textLatitude);
        textLongitude = (TextView) rootview.findViewById(R.id.textLongitude);
        text2Cards = (TextView) rootview.findViewById(R.id.text_dosCards);
        text4Cards = (TextView) rootview.findViewById(R.id.text_cuaCards);
        text6Cards = (TextView) rootview.findViewById(R.id.text_sisCards);
        text8Cards = (TextView) rootview.findViewById(R.id.text_vuiCards);

        buttonStreet.setOnClickListener(this);
        imageCamera.setOnClickListener(this);
        imageGPS.setOnClickListener(this);
        imageGallery.setOnClickListener(this);

        editStreet.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStreet:
                changeVisibility();
                break;
            case R.id.imageCamera:
                break;
            case R.id.imageGPS:
                break;
            case R.id.imageGallery:
                break;
        }
    }

    private void changeVisibility() {
        if (editStreet.getVisibility() == View.GONE) {
            editStreet.setVisibility(View.VISIBLE);
            textStreet.setVisibility(View.GONE);
        }
        else {
            textStreet.setText(editStreet.getText().toString());
            editStreet.setVisibility(View.GONE);
            textStreet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        Log.e(TAG, "Guardando datos");

        outstate.putString("textStreet", textStreet.getText().toString());
        outstate.putString("editStreet", editStreet.getText().toString());
        outstate.putBoolean("stateEdits", editStreet.getVisibility() == View.VISIBLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Log.e(TAG, "Restaurando datos");

            textStreet.setText(savedInstanceState.getString("textStreet"));
            editStreet.setText(savedInstanceState.getString("editStreet"));
            boolean stateEdits = savedInstanceState.getBoolean("stateEdits");
            if (stateEdits) {
                textStreet.setVisibility(View.GONE);
                editStreet.setVisibility(View.VISIBLE);
            }

        }
    }
}