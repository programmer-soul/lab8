import main.*;
import main.Color;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.Timer;
/**
 * Программа PersonLab Client
 * @author Matvei Baranov
 * @version 5.0
 */
public class Main {
    public static Logger logger = Logger.getLogger("ClientLog");
    public static Commands commands = new Commands();
    public static Localizator localizator;
    public static PersonTableModel personModel;
    public static JFrame mainFrame;
    public static JFrame loginFrame;
    public static JFrame messageFrame;
    public static JFrame personFrame;
    public static JFrame setIDFrame;
    public static JFrame visualFrame;
    public static JLabel messageLabel;
    public static JTextField username;
    public static JTable personTable;
    public static JPasswordField userpassword;
    public static JTextField personName;
    public static JTextField personHeight;
    public static JTextField personWeight;
    public static JTextField personPassport;
    public static JComboBox personEyeColor;
    public static JTextField personCoordinateX;
    public static JTextField personCoordinateY;
    public static JTextField personX;
    public static JTextField personY;
    public static JTextField personZ;
    public static JTextField personSetID;
    public static JButton loginButton;
    public static JButton setIDButton;
    public static int FrameMode=1;
    public static int LoginMode=1;
    public static int SetIDMode=1;
    public static int personID;
    public static int setID;
    public static int userID=0;
    public static String userName;
    public static String prevUsername="";
    public static String prevPassword="";
    public static String prevLanguage="ru";
    public static String prevCountry="RU";
    public static boolean auth=false;
    public static UDPDatagramClient client=null;
    public static VisualCanvas canvas;
    public static JButton editButton;
    public static JButton addButton;
    public static JButton removeButton;
    public static JButton visualButton;
    public static JMenu menu1;
    public static JMenu menu2;
    public static JMenu menu3;
    public static JMenuItem menu1Item1;
    public static JMenuItem menu1Item2;
    public static JMenuItem menu1Item3;
    public static JMenuItem menu1Item4;
    public static JMenuItem menu1Item5;
    public static JMenuItem menu1Item6;
    public static JMenuItem menu1Item7;
    public static JMenuItem menu1Item8;
    public static JMenuItem menu1Item9;
    public static JMenuItem menu2Item1;
    public static JMenuItem menu2Item2;
    public static JMenuItem menu2Item3;
    public static JMenuItem menu2Item4;
    public static JMenuItem menu2Item5;
    public static JMenuItem menu2Item6;
    public static JMenuItem menu2Item7;
    public static JMenuItem menu3Item1;
    public static JMenuItem menu3Item2;
    public static JMenuItem menu3Item3;
    public static JMenuItem menu3Item4;
    public static JLabel personNameLabel;
    public static JLabel personHeightLabel;
    public static JLabel personWeightLabel;
    public static JLabel personEyeColorLabel;
    public static JLabel personCoordinateXLabel;
    public static JLabel personCoordinateYLabel;
    public static JLabel personSetIDLabel;
    public static JLabel personPassportLabel;
    public static JLabel personXLabel;
    public static JLabel personYLabel;
    public static JLabel personZLabel;
    public static JLabel usernameLabel;
    public static JLabel passwordLabel;
    public static String StrError="Ошибка";
    public static String StrInformation="Информация";
    public static String StrRegister="Регистрация";
    public static String StrServerConnectionError="Невозможно подключиться к серверу!";
    public static String StrYouCanNotRemoveObject="Вы не можете удалить объект, созданный не вами!";
    public static String StrErrorWithID="Ошибка поля id";
    public static String StrErrorWithName="Ошибка поля Имени";
    public static String StrErrorWithPassport="Ошибка поля Пасспорт";
    public static String StrErrorWithCoordinateX="Ошибка поля Координата X";
    public static String StrErrorWithCoordinateY="Ошибка поля Координата Y";
    public static String StrErrorWithHeight="Ошибка поля рост";
    public static String StrErrorWithWeight="Ошибка поля вес";
    public static String StrErrorWithLocationX="Ошибка поля локация X";
    public static String StrErrorWithLocationY="Ошибка поля локация Y";
    public static String StrErrorWithLocationZ="Ошибка поля локация Z";
    public static String StrLogin="Авторизация";
    public static String StrMaxId="Максимальным id";
    public static String StrAverageWeight="Средний вес";
    public static String StrSumOfHeight="Сумма роста всех людей";

    public static boolean login(String username,String password){
        if (Objects.equals(username, prevUsername) && Objects.equals(password, prevPassword)){
            showMessageFrame(StrError,commands.getMessage());
            return false;
        }
        prevUsername=username;
        prevPassword=password;
        logger.info("Login "+username+" "+password);
        auth=false;
        try {
            if (client==null){
                client = new UDPDatagramClient(new InetSocketAddress(InetAddress.getLocalHost(), 25555),logger);
            }
            commands.setUserAuth(username,password);
            auth=commands.execute(client,"authenticate onstart",false);
            if (auth){
                userID=commands.getUserID();
                userName=username;
                mainFrame.setTitle("PersonLab Client 5.0 ( "+userName+" id#"+userID+")");
                commands.execute(client,"show",false);
                personModel.setPersons(commands.getList());
            }
            else{
                showMessageFrame(StrError,commands.getMessage());
            }
            return auth;
        } catch (IOException e) {
            logger.log(Level.SEVERE,StrServerConnectionError, e);
            showMessageFrame(StrError,StrServerConnectionError);
            //JOptionPane.showMessageDialog(loginFrame, StrServerConnectionError);
            return false;
        }
    }
    public static boolean executeCommand(String command, String parametr,ResponsePerson person){
        try {
            if (client==null){
                client = new UDPDatagramClient(new InetSocketAddress(InetAddress.getLocalHost(), 25555),logger);
            }
            if (Objects.equals(command, "insert") || Objects.equals(command, "update") || Objects.equals(command, "replace_if_lower") ){
                commands.sendCommand(command,parametr,client,person);
            }
            else{
                commands.execute(client,command,false);
            }
            return true;
        } catch (IOException e) {
            logger.log(Level.SEVERE,StrServerConnectionError, e);
            showMessageFrame(StrError,StrServerConnectionError);
            //JOptionPane.showMessageDialog(loginFrame, StrServerConnectionError);
            return false;
        }
    }

    public static void updatePerson(){
        int row=personTable.getSelectedRow();
        if (row>=0){
            FrameMode=1;
            personID=(int)personTable.getValueAt(row,0);
            personFrame.setTitle(editButton.getText()+" id "+personID);
            personName.setText((String)personTable.getValueAt(row,1));
            personPassport.setText((String)personTable.getValueAt(row,2));
            personCoordinateX.setText(Double.toString((double)personTable.getValueAt(row,3)));
            personCoordinateY.setText(Integer.toString((Integer)personTable.getValueAt(row,4)));
            personHeight.setText(Long.toString((long)personTable.getValueAt(row,6)));
            personWeight.setText(Integer.toString((Integer)personTable.getValueAt(row,7)));
            Color color=(Color)personTable.getValueAt(row,8);
            personEyeColor.setSelectedIndex(color.ordinal());
            personX.setText(Double.toString((double)personTable.getValueAt(row,9)));
            personY.setText(Float.toString((float)personTable.getValueAt(row,10)));
            personZ.setText(Double.toString((double)personTable.getValueAt(row,11)));
            personFrame.setVisible(true);
        }
    }
    public static void updatePersonFromVisualArea(int id){
        if (id>0){
            FrameMode=1;
            personID=id;
            ResponsePerson person=personModel.getByID(id);
            personFrame.setTitle(editButton.getText()+" id "+personID);
            if (person != null) {
                personName.setText(person.name);
                personPassport.setText(person.passportID);
                personCoordinateX.setText(Double.toString(person.coordinates.getX()));
                personCoordinateY.setText(Integer.toString(person.coordinates.getY()));
                personHeight.setText(Long.toString(person.height));
                personWeight.setText(Integer.toString(person.weight));
                personEyeColor.setSelectedIndex(person.eyeColor.ordinal());
                personX.setText(Double.toString(person.location.getX()));
                personY.setText(Float.toString(person.location.getY()));
                personZ.setText(Double.toString(person.location.getZ()));
            }
            personFrame.setVisible(true);
        }
    }
    public static void visualPerson(){
        visualFrame.setVisible(true);
        //visualFrame.
        visualFrame.repaint();
    }
    public static void addPerson(){
        FrameMode=2;
        personID=0;
        personFrame.setTitle(addButton.getText());
        personName.setText("");
        personPassport.setText("");
        personCoordinateX.setText("");
        personCoordinateY.setText("");
        personHeight.setText("");
        personWeight.setText("");
        personX.setText("");
        personY.setText("");
        personZ.setText("");
        personFrame.setVisible(true);
    }
    public static void removePerson(){
        int row=personTable.getSelectedRow();
        if (row>=0){
            if (personModel.getUserID(row)==userID){
                executeCommand("remove_key "+personTable.getValueAt(row,0),"",null);
            }
            else{
                showMessageFrame(StrError,StrYouCanNotRemoveObject);
            }
        }
    }
    public static void submitSetID(){
        if (Person.validateWeight(personSetID.getText())){
            setID=Integer.parseInt(personSetID.getText());
        }
        else{
            showMessageFrame(StrError,StrErrorWithID);
            return;
        }
        if(SetIDMode==1){//Удалить элементы, ключ которых превышает заданный
            executeCommand("remove_greater_key "+setID,"",null);
            showMessageFrame(StrInformation,commands.getMessage());
            setIDFrame.setVisible(false);
        }
        else{//Заменить значения, ключ которых меньше заданного
            FrameMode=3;
            personID=setID;
            personFrame.setTitle(localizator.getKeyString("ReplaceIfKeyLower") + setID);
            personName.setText("");
            personPassport.setText("");
            personCoordinateX.setText("");
            personCoordinateY.setText("");
            personHeight.setText("");
            personWeight.setText("");
            personX.setText("");
            personY.setText("");
            personZ.setText("");
            personFrame.setVisible(true);
            setIDFrame.setVisible(false);
        }
    }
    public static void submitPerson(){
        String name=personName.getText();
        String passportID=personPassport.getText();
        Color eyeColor=Color.values()[personEyeColor.getSelectedIndex()];
        long height;
        Integer weight;
        double X;
        int Y;
        double locationX;
        float locationY;
        double locationZ;

        if (!Person.validateName(name)){
            showMessageFrame(StrError,StrErrorWithName);
            return;
        }
        if (!Person.validatePassportID(passportID)){
            showMessageFrame(StrError,StrErrorWithPassport);
            return;
        }
        if (Coordinates.validateX(personCoordinateX.getText())){
            X=Double.parseDouble(personCoordinateX.getText());
        }
        else{
            showMessageFrame(StrError,StrErrorWithCoordinateX);
            return;
        }
        if (Coordinates.validateY(personCoordinateY.getText())){
            Y=Integer.parseInt(personCoordinateY.getText());
        }
        else{
            showMessageFrame(StrError,StrErrorWithCoordinateY);
            return;
        }
        if (Person.validateHeight(personHeight.getText())){
            height=Long.parseLong(personHeight.getText());
        }
        else{
            showMessageFrame(StrError,StrErrorWithHeight);
            return;
        }
        if (Person.validateWeight(personWeight.getText())){
            weight=Integer.parseInt(personWeight.getText());
        }
        else{
            showMessageFrame(StrError,StrErrorWithWeight);
            return;
        }
        if (Location.validateX(personX.getText())){
            locationX=Double.parseDouble(personX.getText());
        }
        else{
            showMessageFrame(StrError,StrErrorWithLocationX);
            return;
        }
        if (Location.validateY(personY.getText())){
            locationY=Float.parseFloat(personY.getText());
        }
        else{
            showMessageFrame(StrError,StrErrorWithLocationY);
            return;
        }
        if (Location.validateZ(personZ.getText())){
            locationZ=Double.parseDouble(personZ.getText());
        }
        else{
            showMessageFrame(StrError,StrErrorWithLocationZ);
            return;
        }
        Coordinates coordinates=new Coordinates(X,Y);
        Location location=new Location(locationX,locationY,locationZ);
        ZonedDateTime creationDate = ZonedDateTime.now(ZoneOffset.systemDefault());
        ResponsePerson per= new ResponsePerson(personID,userID,name,coordinates,creationDate,height,weight,passportID,eyeColor,location);
        if (FrameMode==1){//Редактировать
            executeCommand("update","",per);
        }
        else
        if (FrameMode==2){//Добавить
            executeCommand("insert","",per);
        }
        else{//Заменить значения, ключ которых меньше заданного
            executeCommand("replace_if_lower", String.valueOf(personID),per);
        }
        personFrame.setVisible(false);
    }
    public static void showMessageFrame(String title,String message){
        messageFrame.setTitle(title);
        messageLabel.setText(message);
        messageFrame.setVisible(true);
    }
    public static void changeLocalizator(String language,String country){
        if (Objects.equals(prevLanguage, language) &&  Objects.equals(prevCountry,country) ){return;}
        localizator = new Localizator(ResourceBundle.getBundle("locales/gui", new Locale(language, country)));
        prevLanguage = language;
        prevCountry = country;

        menu1.setText(localizator.getKeyString("MainCommands"));
        menu1Item1.setText(localizator.getKeyString("Sort"));
        menu1Item2.setText(localizator.getKeyString("DisableSort"));
        menu1Item3.setText(localizator.getKeyString("Filter"));
        menu1Item4.setText(localizator.getKeyString("DisableFilter"));
        menu1Item5.setText(localizator.getKeyString("Edit"));
        editButton.setText(localizator.getKeyString("Edit"));
        menu1Item6.setText(localizator.getKeyString("Add"));
        addButton.setText(localizator.getKeyString("Add"));
        menu1Item7.setText(localizator.getKeyString("Remove"));
        removeButton.setText(localizator.getKeyString("Remove"));
        menu1Item8.setText(localizator.getKeyString("VisualArea"));
        visualButton.setText(localizator.getKeyString("VisualArea"));
        visualFrame.setTitle(localizator.getKeyString("VisualArea"));
        menu1Item9.setText(localizator.getKeyString("Exit"));

        menu2.setText(localizator.getKeyString("OtherCommands"));
        menu2Item1.setText(localizator.getKeyString("ShowSumOfHeight"));
        menu2Item2.setText(localizator.getKeyString("ShowAverageWeight"));
        menu2Item3.setText(localizator.getKeyString("RemoveIfKeyGreater"));
        menu2Item4.setText(localizator.getKeyString("ReplaceIfKeyLower"));
        menu2Item5.setText(localizator.getKeyString("RegisterNewUserOnServer"));
        menu2Item6.setText(localizator.getKeyString("ShowMaxById"));
        menu2Item7.setText(localizator.getKeyString("Clear"));

        menu3.setText(localizator.getKeyString("ChangeLanguage"));
        menu3Item1.setText(localizator.getKeyString("Russian"));
        menu3Item2.setText(localizator.getKeyString("English"));
        menu3Item3.setText(localizator.getKeyString("Slovenian"));
        menu3Item4.setText(localizator.getKeyString("Polish"));

        personNameLabel.setText(localizator.getKeyString("Name"));
        personHeightLabel.setText(localizator.getKeyString("Height"));
        personWeightLabel.setText(localizator.getKeyString("Weight"));
        personPassportLabel.setText(localizator.getKeyString("Passport"));
        personEyeColorLabel.setText(localizator.getKeyString("EyeColor"));
        personCoordinateXLabel.setText(localizator.getKeyString("CoordinateX"));
        personCoordinateYLabel.setText(localizator.getKeyString("CoordinateY"));
        personXLabel.setText(localizator.getKeyString("LocationX"));
        personYLabel.setText(localizator.getKeyString("LocationY"));
        personZLabel.setText(localizator.getKeyString("LocationZ"));

        personSetIDLabel.setText(localizator.getKeyString("EnterID"));
        setIDFrame.setTitle(localizator.getKeyString("EnterID"));
        StrError=localizator.getKeyString("Error");
        StrInformation=localizator.getKeyString("Information");
        StrServerConnectionError=localizator.getKeyString("ServerConnectionError");
        StrYouCanNotRemoveObject=localizator.getKeyString("YouCanNotRemoveObject");
        StrRegister=localizator.getKeyString("Register");

        usernameLabel.setText(localizator.getKeyString("Username"));
        passwordLabel.setText(localizator.getKeyString("Password"));
        StrLogin=localizator.getKeyString("Login");
        StrMaxId=localizator.getKeyString("MaxId");
        StrAverageWeight=localizator.getKeyString("AverageWeight");
        StrSumOfHeight=localizator.getKeyString("SumOfHeight");

        StrErrorWithID=localizator.getKeyString("ErrorWithID");
        StrErrorWithName=localizator.getKeyString("ErrorWithName");
        StrErrorWithPassport=localizator.getKeyString("ErrorWithPassport");
        StrErrorWithCoordinateX=localizator.getKeyString("ErrorWithCoordinateX");
        StrErrorWithCoordinateY=localizator.getKeyString("ErrorWithCoordinateY");
        StrErrorWithHeight=localizator.getKeyString("ErrorWithHeight");
        StrErrorWithWeight=localizator.getKeyString("ErrorWithWeight");
        StrErrorWithLocationX=localizator.getKeyString("ErrorWithLocationX");
        StrErrorWithLocationY=localizator.getKeyString("ErrorWithLocationY");
        StrErrorWithLocationZ=localizator.getKeyString("ErrorWithLocationZ");

        personModel.SetLocalizator(localizator);
        TableColumnModel tcm = personTable.getTableHeader().getColumnModel();
        for(int i=0;i<personModel.getColumnCount();i++){
            tcm.getColumn(i).setHeaderValue(personModel.getColumnName(i));
        }
        mainFrame.repaint();
    }
    public static void main(String[] args) throws InterruptedException {
        localizator = new Localizator(ResourceBundle.getBundle("locales/gui", new Locale(prevLanguage, prevCountry)));
        personModel = new PersonTableModel(localizator);
        mainFrame = new JFrame("PersonLab Client 5.0");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800,600);
        mainFrame.setLocationRelativeTo(null);
        personTable= new JTable(personModel);
        personTable.setColumnSelectionAllowed(true);
        personTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//.SINGLE_SELECTION

        Box contents = new Box(BoxLayout.Y_AXIS);
        contents.add(new JScrollPane(personTable));
        mainFrame.getContentPane().add(contents);

        JPanel buttons = new JPanel();
        editButton = new JButton("Редактировать");
        editButton.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     updatePerson();
                                                 }
                                             });
                                         }
                                     }
        );
        addButton = new JButton("Добавить");
        addButton.addActionListener(new ActionListener(){
                                        public void actionPerformed(ActionEvent e){
                                            SwingUtilities.invokeLater(new Runnable()
                                            {
                                                public void run()
                                                {
                                                    addPerson();
                                                }
                                            });
                                        }
                                    }
        );
        removeButton = new JButton("Удалить");
        removeButton.addActionListener(new ActionListener(){
                                           public void actionPerformed(ActionEvent e){
                                               SwingUtilities.invokeLater(new Runnable()
                                               {
                                                   public void run()
                                                   {
                                                       removePerson();
                                                   }
                                               });
                                           }
                                       }
        );
        visualButton = new JButton("Визуализация");
        visualButton.addActionListener(new ActionListener(){
                                           public void actionPerformed(ActionEvent e){
                                               SwingUtilities.invokeLater(new Runnable()
                                               {
                                                   public void run()
                                                   {
                                                       visualPerson();
                                                   }
                                               });
                                           }
                                       }
        );

        buttons.add(editButton);
        buttons.add(addButton);
        buttons.add(removeButton);
        buttons.add(visualButton);

        JMenuBar menuBar= new JMenuBar();
        menu1 = new JMenu("Основные команды");

        menu1Item1 = new JMenuItem("Сортировка");
        menu1Item1.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     int col=personTable.getSelectedColumn();
                                                     if (col>=0){
                                                         personModel.sort(col);
                                                         personModel.fireTableDataChanged();
                                                     }

                                                 }
                                             });
                                         }
                                     }
        );
        menu1Item2 = new JMenuItem("Отключить Сортировку");
        menu1Item2.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     personModel.disableSort();
                                                 }
                                             });
                                         }
                                     }
        );
        menu1Item3 = new JMenuItem("Фильтр");
        menu1Item3.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     int row=personTable.getSelectedRow();
                                                     int col=personTable.getSelectedColumn();
                                                     if (row>=0 && col>=0){
                                                         personModel.filter(row,col);
                                                         personModel.fireTableDataChanged();
                                                     }

                                                 }
                                             });
                                         }
                                     }
        );
        menu1Item4 = new JMenuItem("Отключить Фильтр");
        menu1Item4.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     personModel.disableFilter();
                                                 }
                                             });
                                         }
                                     }
        );


        menu1Item5 = new JMenuItem("Редактировать");
        menu1Item5.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     updatePerson();
                                                 }
                                             });
                                         }
                                     }
        );
        menu1Item6 = new JMenuItem("Добавить");
        menu1Item6.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     addPerson();
                                                 }
                                             });
                                         }
                                     }
        );
        menu1Item7 = new JMenuItem("Удалить");
        menu1Item7.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     removePerson();
                                                 }
                                             });
                                         }
                                     }
        );
        menu1Item8 = new JMenuItem("Визуализация");
        menu1Item8.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     visualPerson();
                                                 }
                                             });
                                         }
                                     }
        );
        menu1Item9 = new JMenuItem("Выход");
        menu1Item9.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
                                                 }
                                             });
                                         }
                                     }
        );

        menu1.add(menu1Item1);
        menu1.add(menu1Item2);
        menu1.add(menu1Item3);
        menu1.add(menu1Item4);
        menu1.addSeparator();
        menu1.add(menu1Item5);
        menu1.add(menu1Item6);
        menu1.add(menu1Item7);
        menu1.add(menu1Item8);
        menu1.addSeparator();
        menu1.add(menu1Item9);
        menuBar.add(menu1);

        menu2 = new JMenu("Другие команды");
        menu2Item1 = new JMenuItem("Вывести сумму значения поля Рост для всех элементов");
        menu2Item1.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     executeCommand("sum_of_height","",null);
                                                     showMessageFrame(StrInformation,StrSumOfHeight+" "+commands.getValue());
                                                 }
                                             });
                                         }
                                     }
        );
        menu2Item2 = new JMenuItem("Вывести среднее значение поля Вес для всех элементов");
        menu2Item2.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     executeCommand("average_of_weight","",null);
                                                     showMessageFrame(StrInformation,StrAverageWeight+" "+commands.getValue());
                                                 }
                                             });
                                         }
                                     }
        );
        menu2Item3 = new JMenuItem("Удалить элементы, ключ которых превышает заданный");
        menu2Item3.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     SetIDMode=1;
                                                     setIDFrame.setVisible(true);
                                                 }
                                             });
                                         }
                                     }
        );
        menu2Item4 = new JMenuItem("Заменить значения, ключ которых меньше заданного");
        menu2Item4.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     SetIDMode=2;
                                                     setIDFrame.setVisible(true);
                                                 }
                                             });
                                         }
                                     }
        );
        menu2Item5 = new JMenuItem("Регистрация нового пользователя на сервере");
        menu2Item5.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     LoginMode=2;
                                                     loginFrame.setTitle(menu2Item5.getText());
                                                     loginButton.setText(StrRegister);
                                                     loginFrame.setVisible(true);
                                                 }
                                             });
                                         }
                                     }
        );
        menu2Item6 = new JMenuItem("Вывести максимальным id");
        menu2Item6.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     executeCommand("max_by_id","",null);
                                                     showMessageFrame(StrInformation,StrMaxId+" "+commands.getMessage());
                                                 }
                                             });
                                         }
                                     }
        );
        menu2Item7 = new JMenuItem("Очистить");
        menu2Item7.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     executeCommand("clear","",null);
                                                 }
                                             });
                                         }
                                     }
        );

        menu2.add(menu2Item1);
        menu2.add(menu2Item2);
        menu2.add(menu2Item3);
        menu2.add(menu2Item4);
        menu2.add(menu2Item5);
        menu2.add(menu2Item6);
        menu2.add(menu2Item7);
        menuBar.add(menu2);

        menu3 = new JMenu("Сменить язык");
        menu3Item1 = new JMenuItem("Русский");
        menu3Item1.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     changeLocalizator("ru","RU");
                                                 }
                                             });
                                         }
                                     }
        );
        menu3Item2 = new JMenuItem("Английский");
        menu3Item2.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     changeLocalizator("en","AU");
                                                 }
                                             });
                                         }
                                     }
        );
        menu3Item3 = new JMenuItem("Словенский");
        menu3Item3.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     changeLocalizator("sl","SI");
                                                 }
                                             });
                                         }
                                     }
        );
        menu3Item4 = new JMenuItem("Польский");
        menu3Item4.addActionListener(new ActionListener(){
                                         public void actionPerformed(ActionEvent e){
                                             SwingUtilities.invokeLater(new Runnable()
                                             {
                                                 public void run()
                                                 {
                                                     changeLocalizator("pl","PL");
                                                 }
                                             });
                                         }
                                     }
        );
        menu3.add(menu3Item1);
        menu3.add(menu3Item2);
        menu3.add(menu3Item3);
        menu3.add(menu3Item4);
        menuBar.add(menu3);

        mainFrame.getContentPane().add(buttons, "South");
        mainFrame.setJMenuBar(menuBar);
        mainFrame.setVisible(false);

        setIDFrame = new JFrame("Задать ID");
        setIDFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setIDFrame.setSize(400,150);
        setIDFrame.setLocationRelativeTo(null);
        setIDFrame.setLayout(new FlowLayout());
        personSetIDLabel = new JLabel("Задать ID");
        personSetIDLabel.setPreferredSize(new Dimension(100, 30));
        setIDFrame.add(personSetIDLabel);
        personSetID= new JTextField(30);
        setIDFrame.add(personSetID);

        setIDButton = new JButton("OK");
        setIDButton.addActionListener(new ActionListener(){
                                          public void actionPerformed(ActionEvent ae){
                                              SwingUtilities.invokeLater(new Runnable()
                                              {
                                                  public void run()
                                                  {
                                                      submitSetID();
                                                  }
                                              });
                                          }
                                      }
        );
        setIDFrame.add(setIDButton);
        setIDFrame.setVisible(false);

        personFrame = new JFrame("");
        personFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        personFrame.setSize(550,450);
        personFrame.setLocationRelativeTo(null);
        personFrame.setLayout(new FlowLayout());

        personNameLabel = new JLabel("ФИО");
        personNameLabel.setPreferredSize(new Dimension(100, 30));
        personFrame.add(personNameLabel);
        personName= new JTextField(30);
        personFrame.add(personName);

        personHeightLabel = new JLabel("Рост");
        personHeightLabel.setPreferredSize(new Dimension(100, 30));
        personFrame.add(personHeightLabel);
        personHeight= new JTextField(30);
        personFrame.add(personHeight);

        personWeightLabel = new JLabel("Вес");
        personWeightLabel.setPreferredSize(new Dimension(100, 30));
        personFrame.add(personWeightLabel);
        personWeight= new JTextField(30);
        personFrame.add(personWeight);

        personPassportLabel = new JLabel("Пасспорт");
        personPassportLabel.setPreferredSize(new Dimension(100, 30));
        personFrame.add(personPassportLabel);
        personPassport= new JTextField(30);
        personFrame.add(personPassport);

        personEyeColorLabel = new JLabel("Цвет глаз");
        personEyeColorLabel.setPreferredSize(new Dimension(100, 30));
        personFrame.add(personEyeColorLabel);
        String[] colorItems = {"BLACK","BLUE","ORANGE","WHITE"};
        personEyeColor= new JComboBox(colorItems);
        personEyeColor.setPreferredSize(new Dimension(333, 30));
        personEyeColor.setSize(60,30);
        personFrame.add(personEyeColor);

        personCoordinateXLabel = new JLabel("Координата X");
        personCoordinateXLabel.setPreferredSize(new Dimension(100, 30));
        personFrame.add(personCoordinateXLabel);
        personCoordinateX= new JTextField(30);
        personFrame.add(personCoordinateX);

        personCoordinateYLabel = new JLabel("Координата Y");
        personCoordinateYLabel.setPreferredSize(new Dimension(100, 30));
        personFrame.add(personCoordinateYLabel);
        personCoordinateY= new JTextField(30);
        personFrame.add(personCoordinateY);

        personXLabel = new JLabel("X");
        personXLabel.setPreferredSize(new Dimension(100, 30));
        personFrame.add(personXLabel);
        personX= new JTextField(30);
        personFrame.add(personX);

        personYLabel = new JLabel("Y");
        personYLabel.setPreferredSize(new Dimension(100, 30));
        personFrame.add(personYLabel);
        personY= new JTextField(30);
        personFrame.add(personY);

        personZLabel = new JLabel("Z");
        personZLabel.setPreferredSize(new Dimension(100, 30));
        personFrame.add(personZLabel);
        personZ= new JTextField(30);
        personFrame.add(personZ);

        JButton submitButton = new JButton("OK");
        submitButton.setPreferredSize(new Dimension(100, 30));
        submitButton.addActionListener(new ActionListener(){
                                           public void actionPerformed(ActionEvent e){
                                               SwingUtilities.invokeLater(new Runnable()
                                               {
                                                   public void run()
                                                   {
                                                       submitPerson();
                                                   }
                                               });
                                           }
                                       }
        );
        submitButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        personFrame.add(submitButton);
        personFrame.setVisible(false);


        messageFrame = new JFrame(StrError);
        messageFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        messageFrame.setSize(500,150);
        messageFrame.setLocationRelativeTo(null);
        Box messageBox = Box.createVerticalBox();
        messageLabel = new JLabel("");
        messageLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        messageBox.add(Box.createVerticalStrut(30));
        messageBox.add(messageLabel);
        messageBox.add(Box.createVerticalGlue());
        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(new ActionListener(){
                                          public void actionPerformed(ActionEvent e){
                                              messageFrame.setVisible(false);
                                          }
                                      }
        );
        closeButton.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        messageBox.add(closeButton);
        messageBox.add(Box.createVerticalStrut(30));
        messageFrame.setContentPane(messageBox);
        messageFrame.setVisible(false);

        visualFrame = new JFrame("Визуализация объектов");
        visualFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        visualFrame.setSize(1200,800);
        visualFrame.setLocationRelativeTo(null);
        visualFrame.setLayout(new FlowLayout());
        canvas = new VisualCanvas(personModel);
        canvas.setPreferredSize(new Dimension(1200,800));
        visualFrame.add(canvas);
        visualFrame.setVisible(false);
        canvas.addMouseListener(new MouseInputAdapter(){
                                    public void mousePressed(MouseEvent e){
                                        SwingUtilities.invokeLater(new Runnable()
                                        {
                                            public void run()
                                            {
                                                updatePersonFromVisualArea(canvas.getPersonIDByPoint(e.getX(),e.getY()));
                                            }
                                        });
                                    }
                                }
        );

        loginFrame = new JFrame("PersonLab. Введите имя пользователя и пароль.");
        loginFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        loginFrame.setSize(430,150);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new FlowLayout());

        usernameLabel = new JLabel("Имя");
        usernameLabel.setPreferredSize(new Dimension(60, 30));
        loginFrame.add(usernameLabel);

        username= new JTextField(30);
        username.setText("testuser");
        loginFrame.add(username);

        passwordLabel = new JLabel("Пароль");
        passwordLabel.setPreferredSize(new Dimension(60, 30));
        loginFrame.add(passwordLabel);

        userpassword= new JPasswordField(30);
        userpassword.setText("test");
        loginFrame.add(userpassword);

        loginButton = new JButton(StrLogin);
        loginButton.addActionListener(new ActionListener(){
                                          public void actionPerformed(ActionEvent ae){
                                              SwingUtilities.invokeLater(new Runnable()
                                              {
                                                  public void run()
                                                  {
                                                      String passtext=new String(userpassword.getPassword());
                                                      if (username.getText().isEmpty()){
                                                          username.requestFocusInWindow();
                                                      }
                                                      else if(passtext.isEmpty()){
                                                          userpassword.requestFocusInWindow();
                                                      }
                                                      else{
                                                          if (LoginMode==1){
                                                              if (login(username.getText(),passtext)){
                                                                  mainFrame.setVisible(true);
                                                                  loginFrame.setVisible(false);
                                                              }
                                                          }
                                                          else{
                                                              executeCommand("register "+username.getText()+" "+passtext,"",null);
                                                              showMessageFrame(StrInformation,commands.getMessage());
                                                          }
                                                      }
                                                  }
                                              });
                                          }
                                      }
        );
        loginFrame.add(loginButton);
        loginFrame.setVisible(true);
        while(loginFrame.isVisible()){
            Thread.sleep(500);
        }
        if (auth){
            try {
                commands.SetTableParams(personModel,visualFrame,canvas);
                UDPDatagramServer server = new UDPDatagramServer(InetAddress.getLocalHost(), 30200, commands, logger);
                server.run();
                Timer timer = new Timer(100,
                        new ActionListener(){
                            public void actionPerformed(ActionEvent e){
                                SwingUtilities.invokeLater(new Runnable()
                                {
                                    public void run()
                                    {
                                        if (!mainFrame.isVisible()){Thread.currentThread().interrupt();}
                                        if (visualFrame.isVisible()){canvas.repaint();}
                                    }
                                });
                            }
                        }
                );
                timer.start();
            }
            catch (UnknownHostException e) {
                logger.log(Level.SEVERE,"Ошибка создания сервера для приёма объектов", e);
            }
        }
        else
        {
            loginFrame.dispose();
            messageFrame.dispose();
            personFrame.dispose();
            visualFrame.dispose();
            mainFrame.dispose();
        }

    }
}

