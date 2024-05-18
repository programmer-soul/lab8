package main;
import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Canvas;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

public class VisualCanvas extends Canvas {
    private PersonTableModel personModel;
    private LinkedHashMap<Integer,java.awt.Color> userColors=new LinkedHashMap<>();
    private LinkedHashMap<Integer,CanvasPerson> canvasPersons=new LinkedHashMap<>();
    private List<Integer> arrayForRemoving=new ArrayList<Integer>();
    private int currentAvColor=0;

    private java.awt.Color[] availableColors = new java.awt.Color[] {java.awt.Color.blue,java.awt.Color.green, java.awt.Color.red, java.awt.Color.cyan, java.awt.Color.black, java.awt.Color.yellow,java.awt.Color.pink,java.awt.Color.orange};

    public VisualCanvas(PersonTableModel personModel){
        super();
        this.personModel=personModel;
    }
    public int getPersonIDByPoint(int x,int y){
        Set<Map.Entry<Integer, CanvasPerson>> set = canvasPersons.entrySet();//Получаем набор прямоугольников
        for (Map.Entry<Integer, CanvasPerson> me : set) {
            CanvasPerson rect = me.getValue();
            if(x>=rect.x1 && x<=rect.x2 && y>=rect.y1 && y<=rect.y2){
                return me.getKey();
            }
        }
        return 0;
    }

    public CanvasPerson drawPerson(Graphics g,int x,int y, int h,java.awt.Color color){
        int r=Math.round((float)h/4);
        int r15=Math.round((float)r*3/2);
        int rd2=Math.round((float)r/2);
        int rd3=Math.round((float)r/3);
        int rd4=Math.round((float)r/4);
        int rd8=Math.round((float)r/8);
        int rd16=Math.round((float)r/16);
        int rd32=Math.round((float)r/32);
        int x1=x+rd2-r;
        int x2=x+rd2+r;
        int y1=y;
        int y2=y+3*r+r15-rd8;
        //g.setColor(java.awt.Color.yellow);
        //g.fillRect(x1,y1,x2-x1,y2-y1);

        g.setColor(color);
        g.fillOval(x, y, r, r);//голова
        g.fillOval(x, y+r, r, r*2);//туловище
        //g.fillRect(x-r15, y+r, r*2,rd3);//рука лев 90 градуссв
        for(int j=0;j<r;j++){//рука лев 45 градуссв
            g.fillRect(x+rd2-j, y+r-rd4+j, 1,rd3);
        }
        //g.fillRect(x+rd2, y+r, r*2,rd3);//рука прав 90 градуссв
        for(int j=0;j<r;j++){//рука прав 45 градуссв
            g.fillRect(x+rd2+j, y+r-rd4+j, 1,rd3);
        }
        g.fillRect(x-rd32, y+r+r15-rd8, rd3,r*2);//нога лев
        g.fillRect(x+rd2+6*rd32, y+r+r15-rd8, rd3,r*2);//нога правая

        return new CanvasPerson(x,y,h,x1,y1,x2,y2);
    }
    public synchronized void paint(Graphics g) {
        //Очистим удалённые
        Set<Map.Entry<Integer, CanvasPerson>> set = canvasPersons.entrySet();// Получаем набор элементов
        for (Map.Entry<Integer, CanvasPerson> me : set) {
            Integer id = me.getKey();
            ResponsePerson per= personModel.getByID(id);
            if (per==null){
                CanvasPerson rect = me.getValue();
                drawPerson(g,rect.x,rect.y,rect.h,java.awt.Color.WHITE);
                arrayForRemoving.add(id);
            }
        }
        for(int i=0;i<arrayForRemoving.size();i++) {
            canvasPersons.remove(arrayForRemoving.get(i));
        }

        for(int i=0;i<personModel.getRowCount();i++){
            Double xd=personModel.get(i).getCoordX();
            int x=xd.intValue();
            int y=personModel.get(i).getCoordY();
            Long hl=personModel.get(i).height;
            int h=hl.intValue();
            int userID=personModel.get(i).userID;
            int id=personModel.get(i).id;
            if (h>200){h=200;}
            if (h<30){h=30;}
            if (x>this.getWidth()){x=this.getWidth()-h/4;}
            if (y>this.getHeight()){y=this.getHeight()-h;}
            CanvasPerson prev=new CanvasPerson();
            if (canvasPersons.containsKey(id)){
                prev=canvasPersons.get(id);
                if (prev.isNew){
                    prev.newCounter++;
                    if (prev.newCounter<10){
                        prev.heightNew=prev.heightNew+prev.increment;
                        h=Math.round(prev.heightNew);
                    }
                    else if (prev.newCounter==10){
                        prev.heightNew=prev.heightMax;
                        h=Math.round(prev.heightNew);
                    }
                    else if (prev.newCounter<20){
                        prev.heightNew=prev.heightNew-prev.increment;
                        h=Math.round(prev.heightNew);
                    }
                    else{
                        prev.isNew=false;
                    }
                }
            }
            else{//Новый человек!
                prev.isNew=true;
                prev.heightNew=h;
                prev.heightMax=h*3;
                prev.newCounter=0;
                prev.increment=(prev.heightMax-h)/10;
            }
            java.awt.Color userColor;
            if (userColors.containsKey(userID)){
                userColor=userColors.get(userID);
            }
            else{
                userColor=availableColors[currentAvColor];
                userColors.put(userID,userColor);
                currentAvColor++;
                if (currentAvColor>=availableColors.length){currentAvColor=0;}
            }
            CanvasPerson rect=drawPerson(g,x,y,h,userColor);
            rect.isNew=prev.isNew;
            rect.heightMax=prev.heightMax;
            rect.heightNew=prev.heightNew;
            rect.newCounter=prev.newCounter;
            rect.increment=prev.increment;
            canvasPersons.put(personModel.get(i).id,rect);
        }

    }
}
