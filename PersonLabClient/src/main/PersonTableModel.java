package main;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.stream.*;
import java.util.List;
import java.util.*;
import java.time.ZonedDateTime;
/**
 * Класс оперирует таблицей с сортировкой и фильтром для отображения на форме
 * @author Matvei Baranov
 */
public class PersonTableModel extends AbstractTableModel {
    private List<ResponsePerson> persons = new ArrayList<ResponsePerson>();
    private List<ResponsePerson> filterPersons = new ArrayList<ResponsePerson>();
    private Localizator localizator;
    private Object filterObject=null;
    private int filterColumn=-1;
    private int sortingColumn=-1;

    private String StrName="ФИО";
    private String StrHeight="Рост";
    private String StrWeight="Вес";
    private String StrPassport="Пасспорт";
    private String StrEyeColor="Цвет глаз";
    private String StrCoordinateX="X координата";
    private String StrCoordinateY="Y координата";
    private String StrCreationDate="Дата создания";
    private String StrUserID="id пользователя";
    private String StrLocationX="X локация";
    private String StrLocationY="Y локация";
    private String StrLocationZ="Z локация";

    public void setPersons(List<ResponsePerson> persons){
        this.persons=persons;
        if (sortingColumn>=0){ filtering();}
        if (filterColumn>=0){ filtering();}
    }
    public void SetLocalizator(Localizator localizator){
        this.localizator=localizator;
        StrName=localizator.getKeyString("Name");
        StrHeight=localizator.getKeyString("Height");
        StrWeight=localizator.getKeyString("Weight");
        StrPassport=localizator.getKeyString("Passport");
        StrEyeColor=localizator.getKeyString("EyeColor");
        StrCoordinateX=localizator.getKeyString("CoordinateX");
        StrCoordinateY=localizator.getKeyString("CoordinateY");
        StrCreationDate=localizator.getKeyString("CreationDate");
        StrUserID=localizator.getKeyString("UserID");
        StrLocationX=localizator.getKeyString("LocationX");
        StrLocationY=localizator.getKeyString("LocationY");
        StrLocationZ=localizator.getKeyString("LocationZ");
    }
    public PersonTableModel(){}
    public PersonTableModel(Localizator localizator){
        SetLocalizator(localizator);
    }
    public ResponsePerson get(int i) {
        if (filterColumn>=0){
            return filterPersons.get(i);
        }else{
            return persons.get(i);
        }
    }
    public ResponsePerson getByID(int id) {
        for(int i=0;i<persons.size();i++) {
            if (persons.get(i).id == id) {
                return persons.get(i);
            }
        }
        return null;
    }
    @Override
    public int getRowCount() {
        if (filterColumn>=0){
            return filterPersons.size();
        }else{
            return persons.size();
        }
    }
    @Override
    public int getColumnCount() {
        return 13;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (filterColumn>=0){
            switch (columnIndex){
                case 0:return filterPersons.get(rowIndex).id;
                case 1:return filterPersons.get(rowIndex).name;
                case 2:return filterPersons.get(rowIndex).passportID;
                case 3:return filterPersons.get(rowIndex).coordinates.getX();
                case 4:return filterPersons.get(rowIndex).coordinates.getY();
                case 5:if (localizator==null){return filterPersons.get(rowIndex).creationDate;}
                else {return localizator.getDate(filterPersons.get(rowIndex).creationDate.toLocalDateTime());}
                case 6:return filterPersons.get(rowIndex).height;
                case 7:return filterPersons.get(rowIndex).weight;
                case 8:return filterPersons.get(rowIndex).eyeColor;
                case 9:return filterPersons.get(rowIndex).location.getX();
                case 10:return filterPersons.get(rowIndex).location.getY();
                case 11:return filterPersons.get(rowIndex).location.getZ();
                case 12:return filterPersons.get(rowIndex).userID;
            }
        }
        else{
            switch (columnIndex){
                case 0:return persons.get(rowIndex).id;
                case 1:return persons.get(rowIndex).name;
                case 2:return persons.get(rowIndex).passportID;
                case 3:return persons.get(rowIndex).coordinates.getX();
                case 4:return persons.get(rowIndex).coordinates.getY();
                case 5:if (localizator==null){return persons.get(rowIndex).creationDate;}
                else {return localizator.getDate(persons.get(rowIndex).creationDate.toLocalDateTime());}
                case 6:return persons.get(rowIndex).height;
                case 7:return persons.get(rowIndex).weight;
                case 8:return persons.get(rowIndex).eyeColor;
                case 9:return persons.get(rowIndex).location.getX();
                case 10:return persons.get(rowIndex).location.getY();
                case 11:return persons.get(rowIndex).location.getZ();
                case 12:return persons.get(rowIndex).userID;
            }
        }
        return null;
    }
    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex){
            case 0:return "id";
            case 1:return StrName;
            case 2:return StrPassport;
            case 3:return StrCoordinateX;
            case 4:return StrCoordinateY;
            case 5:return StrCreationDate;
            case 6:return StrHeight;
            case 7:return StrWeight;
            case 8:return StrEyeColor;
            case 9:return StrLocationX;
            case 10:return StrLocationY;
            case 11:return StrLocationZ;
            case 12:return StrUserID;
        }
        return "";
    }
    private void filtering(){
        switch (filterColumn){
            case 0:filterPersons = persons.stream().filter(p->p.id==(int)filterObject).collect(Collectors.toList());break;
            case 1:filterPersons = persons.stream().filter(p->p.name==(String)filterObject).collect(Collectors.toList());break;
            case 2:filterPersons = persons.stream().filter(p->p.passportID==(String)filterObject).collect(Collectors.toList());break;
            case 3:filterPersons = persons.stream().filter(p->p.getCoordX()==(double)filterObject).collect(Collectors.toList());break;
            case 4:filterPersons = persons.stream().filter(p->p.getCoordY()==(Integer)filterObject).collect(Collectors.toList());break;
            case 5:filterPersons = persons.stream().filter(p->p.creationDate==(ZonedDateTime)filterObject).collect(Collectors.toList());break;
            case 6:filterPersons = persons.stream().filter(p->p.height==(long)filterObject).collect(Collectors.toList());break;
            case 7:filterPersons = persons.stream().filter(p->p.weight==(Integer)filterObject).collect(Collectors.toList());break;
            case 8:filterPersons = persons.stream().filter(p->p.eyeColor==(Color)filterObject).collect(Collectors.toList());break;
            case 9:filterPersons = persons.stream().filter(p->p.getX()==(double)filterObject).collect(Collectors.toList());break;
            case 10:filterPersons = persons.stream().filter(p->p.getY()==(Float)filterObject).collect(Collectors.toList());break;
            case 11:filterPersons = persons.stream().filter(p->p.getZ()==(double)filterObject).collect(Collectors.toList());break;
            case 12:filterPersons = persons.stream().filter(p->p.userID==(int)filterObject).collect(Collectors.toList());break;
        }
    }
    private void sorting(){
        switch (sortingColumn){
            case 0:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getID)).collect(Collectors.toList());break;//getID).reversed()
            case 1:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getName)).collect(Collectors.toList());break;
            case 2:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getPassportID)).collect(Collectors.toList());break;
            case 3:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getCoordX)).collect(Collectors.toList());break;
            case 4:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getCoordY)).collect(Collectors.toList());break;
            case 5:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getCreationDate)).collect(Collectors.toList());break;
            case 6:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getHeight)).collect(Collectors.toList());break;
            case 7:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getWeight)).collect(Collectors.toList());break;
            case 8:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getEyeColor)).collect(Collectors.toList());break;
            case 9:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getX)).collect(Collectors.toList());break;
            case 10:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getY)).collect(Collectors.toList());break;
            case 11:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getZ)).collect(Collectors.toList());break;
            case 12:persons = persons.stream().sorted(Comparator.comparing(ResponsePerson::getUserID)).collect(Collectors.toList());break;
        }
    }
    public void filter(int rowIndex, int columnIndex){
        filterObject=getValueAt(rowIndex,columnIndex);
        filterColumn=columnIndex;
        filtering();
    }
    public void sort(int columnIndex){
        if (columnIndex>=0){
            sortingColumn=columnIndex;
            sorting();
            if (filterColumn>=0){ filtering();}
        }
    }
    public void disableFilter(){
        filterObject=null;
        filterColumn=-1;
    }
    public void disableSort(){
        sortingColumn=-1;
    }

    public int getUserID(int rowIndex) {
        if (rowIndex>=0){
            if (filterColumn>=0){
                return filterPersons.get(rowIndex).userID;
            }
            else{
                return persons.get(rowIndex).userID;
            }

        }else{
            return 0;
        }
    }
}
