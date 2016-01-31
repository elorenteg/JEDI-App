package com.esterlorente.jediapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

public class CardMemory implements Parcelable {
    private ImageView card;
    private int asignCard;
    private int stateCard;

    public CardMemory() {
    }

    protected CardMemory(Parcel in) {
        asignCard = in.readInt();
        stateCard = in.readInt();
    }

    public ImageView getCard() {
        return card;
    }

    public void setCard(ImageView card) {
        this.card = card;
    }

    public int getAsignCard() {
        return asignCard;
    }

    public void setAsignCard(int asignCard) {
        this.asignCard = asignCard;
    }

    public int getStateCard() {
        return stateCard;
    }

    public void setStateCard(int stateCard) {
        this.stateCard = stateCard;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(asignCard);
        parcel.writeInt(stateCard);
    }

    public static final Creator<CardMemory> CREATOR = new Creator<CardMemory>() {
        @Override
        public CardMemory createFromParcel(Parcel in) {
            return new CardMemory(in);
        }

        @Override
        public CardMemory[] newArray(int size) {
            return new CardMemory[size];
        }
    };
}
