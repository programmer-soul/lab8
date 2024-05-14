package main;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class VisualCanvas extends Canvas {
    private PersonTableModel personModel;
    private LinkedHashMap<Integer,java.awt.Color> userColors=new LinkedHashMap<>();
    private LinkedHashMap<Integer,Rectangle> userRectangles=new LinkedHashMap<>();
    private int currentAvColor=0;
    private java.awt.Color[] availableColors = new java.awt.Color[] {java.awt.Color.blue,java.awt.Color.green, java.awt.Color.red, java.awt.Color.cyan, java.awt.Color.black, java.awt.Color.yellow,java.awt.Color.pink,java.awt.Color.orange};

    public VisualCanvas(PersonTableModel personModel){
        super();
        this.personModel=personModel;
    }
    public int getPersonIDByPoint(int x,int y){
        Set<Map.Entry<Integer, Rectangle>> set = userRectangles.entrySet();//Получаем набор прямоугольников
        for (Map.Entry<Integer, Rectangle> me : set) {
            Rectangle rect = me.getValue();
            if(x>=rect.x && x<=rect.width && y>=rect.y && y<=rect.height){
                return me.getKey();
            }
        }
        return 0;
    }

    public void paint(Graphics g) {
        userRectangles.clear();
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        for(int i=0;i<personModel.getRowCount();i++){
            Double xd=personModel.get(i).getCoordX();
            Integer x=xd.intValue();
            Integer y=personModel.get(i).getCoordY();
            Long h=personModel.get(i).height;
            int userID=personModel.get(i).userID;
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
            int maxHeight=this.getHeight();
            int maxWidth=this.getWidth();
            if (h>200){h=200L;}
            if (h<30){h=30L;}
            int r=Math.round((float)h/4);
            if (x>maxWidth){x=maxWidth-r*2;}
            if (y>maxHeight){y=maxHeight-h.intValue();}
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
            Rectangle rect=new Rectangle(x1,y1,x2,y2);
            userRectangles.put(personModel.get(i).id,rect);
            //g.setColor(java.awt.Color.yellow);
            //g.fillRect(x1,y1,x2-x1,y2-y1);

            g.setColor(userColor);
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

        }

    }
}
