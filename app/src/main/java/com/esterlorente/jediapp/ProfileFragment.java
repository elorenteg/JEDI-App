package com.esterlorente.jediapp;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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
import android.widget.Toast;

import com.esterlorente.jediapp.data.LoginHelper;
import com.esterlorente.jediapp.utils.ImageParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


public class ProfileFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private String TAG = "PROFILE_FRAGMENT";
    private static final int CAMERA_REQUEST = 1888;
    private static final int GALLERY_REQUEST = 100;
    private static final int PIC_CROP = 1;

    private String username;

    private View rootview;
    private Context context;
    private LoginHelper loginHelper;

    private ImageView imageProfile;
    private Button buttonStreet;
    private ImageButton imageCamera, imageGallery, imageGPS;
    private EditText editStreet;
    private TextView textUsername, textStreet, textLatitude, textLongitude, text2Cards, text4Cards, text6Cards, text8Cards;

    private List<Address> l;
    private LocationManager lm;
    private LocationListener lis;

    public ProfileFragment() {
    }

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

        Bundle args = this.getArguments();
        if (args != null) {
            username = args.getString("username");
        }

        initButtons();
        initProfile();

        return rootview;
    }

    private void initProfile() {
        textUsername.setText(username);

        Cursor cursor1 = loginHelper.getUserImageByName(username);
        if (cursor1.moveToFirst()) {
            byte[] image = cursor1.getBlob(cursor1.getColumnIndex(loginHelper.IMAGE));
            if (image != null) {
                imageProfile.setImageBitmap(ImageParser.byteArrayToBitmap(image));
            }
        }

        Cursor cursor2 = loginHelper.getScoresByName(username);
        if (cursor2.moveToFirst()) {
            do {
                int score = cursor2.getInt(cursor2.getColumnIndex(loginHelper.SCORE));
                int numCards = cursor2.getInt(cursor2.getColumnIndex(loginHelper.NUMCARDS));

                switch (numCards) {
                    case 2:
                        text2Cards.setText(String.valueOf(score));
                        break;
                    case 4:
                        text4Cards.setText(String.valueOf(score));
                        break;
                    case 6:
                        text6Cards.setText(String.valueOf(score));
                        break;
                    case 8:
                        text8Cards.setText(String.valueOf(score));
                        break;
                }
            } while (cursor2.moveToNext());
        }
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
                makeImageCamera();
                break;
            case R.id.imageGPS:
                initGPS();
                break;
            case R.id.imageGallery:
                chooseImageFromGallery();
                break;
        }
    }

    private void chooseImageFromGallery() {
        Intent pickAnImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickAnImage.setType("image/*");

        startActivityForResult(pickAnImage, GALLERY_REQUEST);
    }

    private void makeImageCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void performCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (resultCode == getActivity().RESULT_OK) {
                    Uri selectedImage = data.getData();
                    performCrop(selectedImage);
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == getActivity().RESULT_OK) {
                    Uri selectedImage = data.getData();
                    performCrop(selectedImage);
                }
                break;
            case PIC_CROP:
                if (data != null) {
                    Log.e(TAG, "Crop != null");
                    Bundle extras = data.getExtras();
                    Bitmap selectedBitmap = extras.getParcelable("data");

                    imageProfile.setImageBitmap(selectedBitmap);
                    updateImageByName(selectedBitmap);
                } else
                    Log.e(TAG, "Crop == null");
                break;
        }
    }

    private void updateImageByName(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();

        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.IMAGE, image);
        loginHelper.updateImageByName(valuesToStore, textUsername.getText().toString());
    }

    private void changeVisibility() {
        if (editStreet.getVisibility() == View.GONE) {
            editStreet.setVisibility(View.VISIBLE);
            textStreet.setVisibility(View.GONE);
        } else {
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

        outstate.putByteArray("imageProfile", ImageParser.bitmapToByteArray(((BitmapDrawable) imageProfile.getDrawable()).getBitmap()));
        outstate.putString("username", textUsername.getText().toString());
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

            textUsername.setText(savedInstanceState.getString("username"));
            imageProfile.setImageBitmap(ImageParser.byteArrayToBitmap(savedInstanceState.getByteArray("imageProfile")));
        }
    }

    private void initGPS() {
        l = null;
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        lis = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
                Geocoder gc = new Geocoder(getActivity().getApplicationContext());
                try {
                    l = gc.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < l.size(); ++i) {
                    Log.v("LOG", l.get(i).getAddressLine(0).toString());

                    if (i == 0) textLongitude.setText("");
                    textLongitude.setText(textLongitude.getText() + "\n" + l.get(i).getAddressLine(0).toString());
                }
                Log.v("LOG", ((Double) location.getLatitude()).toString());
            }
        };

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, lis);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lis);
    }
}