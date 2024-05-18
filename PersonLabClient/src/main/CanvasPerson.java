package main;

public class CanvasPerson {
    public int x;
    public int y;
    public int h;
    public int x1;
    public int x2;
    public int y1;
    public int y2;
    public boolean isNew=false;
    public int heightMax=0;
    public float heightNew=0;
    public int newCounter=0;
    public float increment=1;
    public CanvasPerson(){
    }
    public CanvasPerson(int x,int y,int h,int x1,int y1,int x2,int y2){
        this.x=x;
        this.y=y;
        this.h=h;
        this.x1=x1;
        this.y1=y1;
        this.x2=x2;
        this.y2=y2;
    }
}
