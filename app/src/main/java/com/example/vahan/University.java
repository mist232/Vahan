package com.example.vahan;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class University  implements Parcelable {

        private String name;
        private String country;
        private List<String> web_pages;
        private String website;


        protected University(Parcel in) {
                name = in.readString();
                country = in.readString();
                web_pages = in.createStringArrayList();
                website = in.readString();
        }



        public String getCountry() {
                return country;
        }

        public String getName() {
                return name;
        }
        public List<String> getWebpages() {
                return web_pages;
        }
        public void setWebsite(String web) {
                this.website=web;
        }

        // Parcelable implementation


        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        @Override
        public int describeContents() {
                return 0;
        }

        public static final Creator<University> CREATOR = new Creator<University>() {
                @Override
                public University createFromParcel(Parcel in) {
                        return new University(in);
                }

                @Override
                public University[] newArray(int size) {
                        return new University[size];
                }
        };
}
