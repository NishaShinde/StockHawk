package com.sam_chordas.android.stockhawk.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by shani on 4/18/16.
 * Usage : Model class for storing quote details like date,close. Helpful for drawing graph of stock price over time.
 */

public class Quote implements Parcelable{

    String date;
    float close;


    public Quote(){}

    protected Quote(Parcel in) {
        date = in.readString();
        close = in.readFloat();
    }

    public static final Creator<Quote> CREATOR = new Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getClose() {
        return close;
    }

    public void setClose(float close) {
        this.close = close;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeFloat(close);
    }
}
