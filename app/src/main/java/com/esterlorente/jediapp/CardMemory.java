package com.esterlorente.jediapp;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
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

    public void flipCard(final Context context, final Drawable newImage) {
        ObjectAnimator animCardStart = (ObjectAnimator) AnimatorInflater.loadAnimator(
                context, R.animator.memory_cardflip);
        animCardStart.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator animCardEnd = (ObjectAnimator) AnimatorInflater.loadAnimator(
                        context, R.animator.memory_cardflipend);
                card.setImageDrawable(newImage);
                animCardEnd.setTarget(card);
                animCardEnd.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animCardStart.setTarget(card);
        animCardStart.start();
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
