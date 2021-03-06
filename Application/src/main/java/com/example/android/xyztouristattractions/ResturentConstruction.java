package com.example.android.xyztouristattractions;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class ResturentConstruction {
    public String name;
    public String description;
    public String longDescription;
    public Uri imageUrl;
    public Uri secondaryImageUrl;
    public LatLng location;
    public String city;

    public Bitmap image;
    public Bitmap secondaryImage;
    public String distance;

    public ResturentConstruction(String name, String description, String longDescription, Uri imageUrl,
                      Uri secondaryImageUrl, LatLng location, String city) {
        this.name = name;
        this.description = description;
        this.longDescription = longDescription;
        this.imageUrl = imageUrl;
        this.secondaryImageUrl = secondaryImageUrl;
        this.location = location;
        this.city = city;
    }
}
