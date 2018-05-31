package com.bro.entity;


import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Entity;
import org.bson.types.ObjectId;


import java.util.Date;
import java.util.Objects;
import java.util.zip.DataFormatException;

/** Latitude et doubleitude quelque part sur Terre, où se trouve ton/ta bro avec une précision variable. */
@Entity("geolocation")
public class Geolocation {

    @Id
    private ObjectId id;

    private double lat;

    private double lng;

    @Reference
    private User user;

    private Date timestamp;


    /**
     * constructeur vide pour le dao
     * @param timestamp
     */

    /**
     * @param lat
     * @param lng
     */
    public Geolocation(double lat, double lng, User user, Date timestamp) {
        this.lat = lat;
        this.lng = lng;
        this.user = user;
        this.timestamp = timestamp;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public void updateTimestamp(){
        this.timestamp = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Geolocation geolocation = (Geolocation) o;
        return Double.compare(geolocation.lat, lat) == 0 && Double.compare(geolocation.lng, lng) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }

}