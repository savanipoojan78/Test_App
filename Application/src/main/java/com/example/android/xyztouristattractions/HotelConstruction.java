package com.example.android.xyztouristattractions;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class HotelConstruction {

    public String name;
    public String description;
    public String longDescription;
    public Uri imageUrl;
    public Uri secondaryImageUrl;
    public LatLng location;
    public String city;
    public String rating;
    public String Rupee;

    public Bitmap image;
    public Bitmap secondaryImage;
    public String distance;

    public HotelConstruction(String name, String description, String longDescription, Uri imageUrl,
                      Uri secondaryImageUrl, LatLng location, String city,String rating,String Rupee) {
        this.name = name;
        this.description = description;
        this.longDescription = longDescription;
        this.imageUrl = imageUrl;
        this.secondaryImageUrl = secondaryImageUrl;
        this.location = location;
        this.city = city;
        this.rating=rating;
        this.Rupee=Rupee;
    }
}
