package com.esterlorente.jediapp;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esterlorente.jediapp.data.LoginHelper;
import com.esterlorente.jediapp.utils.ImageParser;

import java.io.File;
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

    private LinearLayout imageBackground;
    private ImageView imageProfile;
    private ImageButton imageCamera, imageGallery, imageGPS, imageEdit;
    private EditText editStreet;
    private TextView textUsername, textEmail, textStreet, textLastNotif, textLatitude, textLongitude, text2Cards, text4Cards, text6Cards, text8Cards;

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

        Cursor cursor1 = loginHelper.getUserProfileByName(username);
        if (cursor1.moveToFirst()) {
            String image = cursor1.getString(cursor1.getColumnIndex(loginHelper.IMAGE));
            if (image != null) {
                Uri uri = Uri.parse(image);
                changeImageAndBackground(uri);
            }

            String email = cursor1.getString(cursor1.getColumnIndex(loginHelper.EMAIL));
            textEmail.setText(email);

            String lastNotif = cursor1.getString(cursor1.getColumnIndex(loginHelper.LAST_NOTIF));
            if (lastNotif != null) {
                textLastNotif.setText(lastNotif);
            }

            String street = cursor1.getString(cursor1.getColumnIndex(loginHelper.STREET));
            if (street != null) {
                textStreet.setText(street);
            }

            String latitude = cursor1.getString(cursor1.getColumnIndex(loginHelper.LATITUDE));
            String longitude = cursor1.getString(cursor1.getColumnIndex(loginHelper.LONGITUDE));
            if (latitude != null) {
                textLatitude.setText(latitude);
                textLongitude.setText(longitude);
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
        imageEdit = (ImageButton) rootview.findViewById(R.id.imageEdit);
        imageCamera = (ImageButton) rootview.findViewById(R.id.imageCamera);
        imageGPS = (ImageButton) rootview.findViewById(R.id.imageGPS);
        imageGallery = (ImageButton) rootview.findViewById(R.id.imageGallery);
        editStreet = (EditText) rootview.findViewById(R.id.editStreet);

        textUsername = (TextView) rootview.findViewById(R.id.textName);
        textEmail = (TextView) rootview.findViewById(R.id.text_email);
        textStreet = (TextView) rootview.findViewById(R.id.textStreet);
        textLastNotif = (TextView) rootview.findViewById(R.id.text_notification);
        textLatitude = (TextView) rootview.findViewById(R.id.textLatitude);
        textLongitude = (TextView) rootview.findViewById(R.id.textLongitude);
        text2Cards = (TextView) rootview.findViewById(R.id.text_dosCards);
        text4Cards = (TextView) rootview.findViewById(R.id.text_cuaCards);
        text6Cards = (TextView) rootview.findViewById(R.id.text_sisCards);
        text8Cards = (TextView) rootview.findViewById(R.id.text_vuiCards);

        imageEdit.setOnClickListener(this);
        imageCamera.setOnClickListener(this);
        imageGPS.setOnClickListener(this);
        imageGallery.setOnClickListener(this);

        imageBackground = (LinearLayout) rootview.findViewById(R.id.imageBackground);
        changeImageAndBackground(null);

        editStreet.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageEdit:
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
                    changeImageAndBackground(selectedImage);
                    //performCrop(selectedImage);
                }
                break;
            case GALLERY_REQUEST:
                if (resultCode == getActivity().RESULT_OK) {
                    Uri selectedImage = data.getData();
                    changeImageAndBackground(selectedImage);
                    //performCrop(selectedImage);
                }
                break;
        }
    }

    private void updateImageByName(Uri uri) {
        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.IMAGE, uri.toString());
        loginHelper.updateImageByName(valuesToStore, textUsername.getText().toString());
    }

    private void changeImageAndBackground(Uri uri) {
        boolean defaultImage = (uri == null);

        boolean imageExist = true;
        Bitmap bitmap = null;
        if (defaultImage) {
            int defaultDrawable = R.drawable.gato5;
            uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                    getResources().getResourcePackageName(defaultDrawable) + '/' +
                    getResources().getResourceTypeName(defaultDrawable) + '/' +
                    getResources().getResourceEntryName(defaultDrawable));

            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gato5);
        } else {
            if (!getRealPathFromURI(uri).equals("-1")) {
                File imgFile = new File(getRealPathFromURI(uri).toString());
                if (!imgFile.exists()) imageExist = false;
                else bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            } else {
                Toast.makeText(context, "Imagen borrada", Toast.LENGTH_SHORT).show();

                int defaultDrawable = R.drawable.gato5;
                uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                        getResources().getResourcePackageName(defaultDrawable) + '/' +
                        getResources().getResourceTypeName(defaultDrawable) + '/' +
                        getResources().getResourceEntryName(defaultDrawable));

                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.gato5);
            }
        }

        imageProfile.setTag(uri.toString());
        ImageParser.roundEffect(getActivity(), imageProfile, uri);
        updateImageByName(uri);

        if (imageExist) {
            final Bitmap bitmap1 = bitmap;
            imageBackground.post(new Runnable() {
                @Override
                public void run() {
                    int width = imageBackground.getWidth();
                    int height = imageBackground.getHeight();

                    Bitmap bitmap3 = Bitmap.createScaledBitmap(bitmap1, width, height, true);
                    bitmap3 = ImageParser.blurRenderScript(getActivity(), bitmap3, 25);
                    Drawable drawable = new BitmapDrawable(getResources(), bitmap3);
                    imageBackground.setBackground(drawable);

                    ((MainActivity) getActivity()).updateNavHeader(bitmap1);
                }
            });
        } else Log.e(TAG, "Uri no valida");
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

    private void changeVisibility() {
        if (editStreet.getVisibility() == View.GONE) {
            editStreet.setVisibility(View.VISIBLE);
            textStreet.setVisibility(View.GONE);
        } else {
            textStreet.setText(editStreet.getText().toString());
            editStreet.setVisibility(View.GONE);
            textStreet.setVisibility(View.VISIBLE);

            updateUserStreetByName();
        }
    }

    private void updateUserStreetByName() {
        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.STREET, textStreet.getText().toString());
        loginHelper.updateUserStreetByName(valuesToStore, textUsername.getText().toString());
    }

    private void updateCoordinatesByName() {
        ContentValues valuesToStore = new ContentValues();
        valuesToStore.put(loginHelper.LATITUDE, textLatitude.getText().toString());
        valuesToStore.put(loginHelper.LONGITUDE, textLongitude.getText().toString());
        loginHelper.updateUserCoordinatesByName(valuesToStore, textUsername.getText().toString());
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        //Log.e(TAG, "Guardando datos");

        outstate.putString("textStreet", textStreet.getText().toString());
        outstate.putString("editStreet", editStreet.getText().toString());
        outstate.putBoolean("stateEdits", editStreet.getVisibility() == View.VISIBLE);

        //outstate.putByteArray("imageProfile", ImageParser.bitmapToByteArray(((BitmapDrawable) imageProfile.getDrawable()).getBitmap()));
        outstate.putString("imageTag", imageProfile.getTag().toString());
        outstate.putString("username", textUsername.getText().toString());

        outstate.putString("latitude", textLatitude.getText().toString());
        outstate.putString("longitude", textLongitude.getText().toString());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            //Log.e(TAG, "Restaurando datos");

            textStreet.setText(savedInstanceState.getString("textStreet"));
            editStreet.setText(savedInstanceState.getString("editStreet"));
            boolean stateEdits = savedInstanceState.getBoolean("stateEdits");
            if (stateEdits) {
                textStreet.setVisibility(View.GONE);
                editStreet.setVisibility(View.VISIBLE);
            }

            textUsername.setText(savedInstanceState.getString("username"));

            Uri uri = Uri.parse(savedInstanceState.getString("imageTag"));
            ImageParser.roundEffect(getActivity(), imageProfile, uri);
            //imageProfile.setImageBitmap(ImageParser.byteArrayToBitmap(savedInstanceState.getByteArray("imageProfile")));

            textLatitude.setText(savedInstanceState.getString("latitude"));
            textLongitude.setText(savedInstanceState.getString("longitude"));

            updateCoordinatesByName();
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
                            location.getLongitude(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < l.size(); ++i) {
                    Log.v("LOG", l.get(i).getAddressLine(0).toString());

                    if (i == 0) textLongitude.setText("");
                    textLatitude.setText(String.valueOf(l.get(i).getLatitude()));
                    textLongitude.setText(String.valueOf(l.get(i).getLongitude()));

                    Log.e(TAG, "Lat " + textLatitude.getText().toString());
                    Log.e(TAG, "Long " + textLongitude.getText().toString());
                    //getFromLocation(l.get(i).getAddressLine(0).toString());

                    lm.removeUpdates(lis);
                    lm = null;
                }
                Log.v("LOG", ((Double) location.getLatitude()).toString());
            }
        };

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, lis);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, lis);
    }
}