package main;

import java.io.Serializable;
import java.time.ZonedDateTime;
/**
 * Класс Ответ от сервера со структурой Person для обмена данными между клиентом и сервером
 * @author Matvei Baranov
 */
public class ResponsePerson implements Serializable {
    public int id;
    public String name;
    public Coordinates coordinates;
    public ZonedDateTime creationDate;
    public long height;
    public Integer weight;
    public String passportID;
    public Color eyeColor;
    public Location location;
    public int userID;
    /**
     * Конструктор класса
     */
    public ResponsePerson(int id,int userID,String name, Coordinates coordinates, ZonedDateTime creationDate, long height, Integer weight, String passportID, Color eyeColor, Location location){
        this.id=id;
        this.userID=userID;
        this.name=name;
        this.coordinates=coordinates;
        this.creationDate=creationDate;
        this.height=height;
        this.weight=weight;
        this.passportID=passportID;
        this.eyeColor=eyeColor;
        this.location=location;
    }
    /**
     * @return возвращает Person
     */
    public Person toPerson(){
        return (new Person(id,userID,name,coordinates,creationDate,height,weight,passportID,eyeColor,location));
    }
    public int getUserID() {
        return userID;
    }
    public int getID() {
        return id;
    }
    public String getName() {
        return name;
    }
    public double getCoordX() {
        return coordinates.getX();
    }
    public Integer getCoordY() { return coordinates.getY();}
    public ZonedDateTime getCreationDate() { return creationDate;}
    public long getHeight() { return height;}
    public Integer getWeight() { return weight;}
    public String getPassportID() {
        return passportID;
    }
    public Color getEyeColor() {
        return eyeColor;
    }
    public double getX() {
        return location.getX();
    }
    public Float getY() {
        return location.getY();
    }
    public double getZ() { return location.getZ();}
    public void show(){
        System.out.println(id+":"+name+" "+passportID+" height:"+height+" weight:"+weight+" eye:"+eyeColor+" "+coordinates.toString()+" "+location.toString());
    }
    /**
     * @return возвращает строкой
     */
    public String toString() {
        return name;
    }
}
