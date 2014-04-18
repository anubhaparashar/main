
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.lang.StringBuffer;
import java.io.IOException;
import java.io.*;
import java.sql.*;  

public class PhoneBookDataBase extends JFrame
{
   private DataPanel myDataPanel;
   private Connection dbconn;
   private static int numPeople=0;
   private static String info;
   private static JTextArea txtInfo=new JTextArea( 8, 40 ); //8 rows 40 cols needs to be here to speak
                                                            //across classes
       public PhoneBookDataBase()
       {
              super("This is my Phone Book which calls MS ACCESS database");
              GridLayout myGridLayout= new GridLayout(3,1); //3 rows 1 col allows 3 panels
              Container p = getContentPane();
              myDataPanel=new DataPanel();
               p.add(myDataPanel);
               myDataPanel.setLayout(myGridLayout);
        
        try
         {
            String url = "jdbc:odbc:myAddressBook";  
                                                     
            Class.forName( "sun.jdbc.odbc.JdbcOdbcDriver" );
            dbconn = DriverManager.getConnection( url );
            info="Connection successful\n";
      }
      catch ( ClassNotFoundException cnfex )           //yes 3 catches
      {
            cnfex.printStackTrace();
            info=info+"Connection unsuccessful\n" + cnfex.toString();
      }
      catch ( SQLException sqlex )
      {
            sqlex.printStackTrace();
            info=info+"Connection unsuccessful\n" +sqlex.toString();
      }
      catch ( Exception excp )
      {
            excp.printStackTrace();
            info=info+excp.toString();
      }
      //**********************************

        txtInfo.setText(info);   //sets connection information
              setSize(500,290);
              setVisible(true);
       }
       public static void main(String args[])
       {
              PhoneBookDataBase myPhoneBookDataBase= new PhoneBookDataBase();
              myPhoneBookDataBase.addWindowListener
              (
                     new WindowAdapter()
                     {
                           public void windowClosing(WindowEvent e)
                           {
                                  System.exit(0);
                           }
                     }
              );
       }
   //*******************************
   class DataPanel extends JPanel implements ActionListener
   {
              JLabel lblIDCap= new   JLabel("Record Number");
              JLabel lblFirst=new JLabel("Last Name");
              JLabel lblLast=new JLabel("First Name ");
              JLabel lblPhone=new JLabel("Phone Number");
              //JTextArea txtInfo=new JTextArea();
              JLabel lblID=new JLabel("         "); //10 spaces
              JTextField  txtLast=new JTextField(10);
              JTextField  txtFirst=new JTextField(10);
              JTextField  txtPhone=new JTextField(10);

              JButton btnAdd=new JButton("Add Record");
              JButton btnFind=new JButton("Find Record");
              JButton btnDelete=new JButton("Delete Record");
              JButton btnUpdate=new JButton("Update Record");        //**
              JButton btnClear=new JButton("Clear");
              JButton btnExit=new JButton("Exit");

              public DataPanel()
              {
                     JPanel myPanel = new JPanel();
                     JPanel myPanel2 = new JPanel();
                     JPanel myPanel3 =new JPanel();                     //**
                     myPanel.setLayout(new GridLayout (4,2)); //4 rows 2 cols
                     myPanel2.setLayout(new GridLayout (2,3)); //2 rows 3 cols
                     myPanel3.setLayout(new GridLayout(1,1)); //1 row 1 col
                      add(myPanel);
                      add(myPanel2);
                      add(myPanel3);                                     //**
                     myPanel.add(lblIDCap);
                     myPanel.add(lblID);
                     myPanel.add(lblLast);
                     myPanel.add(txtLast);
                     myPanel.add(lblFirst);
                     myPanel.add(txtFirst);
                     myPanel.add(lblPhone);
                     myPanel.add(txtPhone);
                     myPanel2.add(btnAdd);
                     myPanel2.add(btnFind);
                     myPanel2.add(btnDelete);
                     myPanel2.add(btnUpdate);
                     myPanel2.add(btnClear);
                     myPanel2.add(btnExit);
                     myPanel3.add( new JScrollPane(txtInfo));           //**
                     //puts txtInfo on application and allows it to scroll
                     btnAdd.addActionListener(this);
                     btnFind.addActionListener(this);
                     btnUpdate.addActionListener(this);
                     btnClear.addActionListener(this);
                     btnExit.addActionListener(this);
                     btnDelete.addActionListener(this);
              }
              public void actionPerformed(ActionEvent event)
              {
                     String ID="";     //must initialize to ""
                     String Last="";
                     String First="";
                     String Phone="";
                     Object source=event.getSource();
                     ID=lblID.getText().trim();
                     lblID.setText(ID);
                     Last=txtLast.getText().trim();  //removes additional characters
                     txtLast.setText(Last);          //sets fields in their places
                     First=txtFirst.getText().trim();
                     txtFirst.setText(First);
                     Phone=txtPhone.getText().trim();
                     txtPhone.setText(Phone);
                     if (source.equals(btnAdd))
                     {
                       //********************************
                  try {
                        Statement statement = dbconn.createStatement();
                        if ( !Last.equals( "" ) &&
                              !First.equals( "" ) &&
                                    !Phone.equals("") )
                                    {
                               String temp = "INSERT INTO AddressTable (" +
                                  "Last, First, Phone" +
                                  ") VALUES ('" +
                                  Last   + "', '" +
                                  First  + "', '" +
                                  Phone  +
                                  "')";
                                  txtInfo.append( "\nInserting: " +
                                  dbconn.nativeSQL( temp ) + "\n" );
                            int result = statement.executeUpdate( temp );
                            if ( result == 1 )
                            { //confirming insertion
                                 //txtInfo.append("\nInsertion successful\n");
                                 String query="";
                                 try
                                 {
                                   query = "SELECT * FROM AddressTable WHERE First='" +
                                   First + "' AND Last= '" + Last + "'";
                                   ResultSet rs = statement.executeQuery( query );
                                  rs.next();
                                    lblID.setText(String.valueOf(rs.getInt(1)));
                                 }
                                catch ( SQLException sqlex )
                                {
                                   txtInfo.append( sqlex.toString() );
                                }
                           }
                           else
                           {
                               txtInfo.append( "\nInsertion failed\n" );
                               txtFirst.setText( "" );
                               txtLast.setText( "" );
                               txtPhone.setText( "" );
                            }
                          }
                          else
                            txtInfo.append( "\nEnter last, first, " +
                               "phone and address, then press Add\n" );
                         statement.close();
                      }
                      catch ( SQLException sqlex )
                      {
                         txtInfo.append( sqlex.toString() );
                            txtFirst.setText("Entry already exists -- re-enter");
                      }
                  }
                //****************************
              if (source.equals(btnFind))
              {
                     try
                     {
                       if ( !Last.equals("") && !First.equals(""))
                       {
						      txtPhone.setText("Not Found");

                              Statement statement =dbconn.createStatement();
                              String query = "SELECT * FROM AddressTable " +
                                 "WHERE First = '" +
                                 First + "'"+
                                 " AND Last = '" +
                                 Last + "'";
                                 txtInfo.append( "\nSending query: "   +
                                    dbconn.nativeSQL( query ) + "\n" );
                                 ResultSet rs = statement.executeQuery( query );
                                 display( rs );
                                 statement.close();
                        }
                        else
                          txtLast.setText("Enter last name and First name"+
                                           " then press Find" );
                     }
                     catch ( SQLException sqlex )
                     {
                             txtInfo.append( sqlex.toString() + sqlex.getMessage() );
                     }
               }
   //******************************************
    if (source.equals(btnUpdate))
    {
              try
              {
                  Statement statement = dbconn.createStatement();
                  if ( ! lblID.getText().equals(""))
                  {
                           String temp = "UPDATE AddressTable SET " +
                             "First='" + txtFirst.getText() +
                             "', Last='" + txtLast.getText() +
                             "', Phone='" + txtPhone.getText() +
                             "' WHERE id=" + lblID.getText();
                             txtInfo.append( "\nUpdating: " +
                                dbconn.nativeSQL( temp ) + "\n" );
                                 int result = statement.executeUpdate( temp );
                                 if ( result == 1 )
                             txtInfo.append( "\nUpdate successful\n" );
                          else {
                             txtInfo.append( "\nUpdate failed\n" );
                             txtFirst.setText( "" );
                             txtLast.setText( "" );
                             txtPhone.setText( "" );
                          }
                        statement.close();
                       }
                       else
                          txtInfo.append( "\nYou may only update an " +
                                         "existing record. Use Find to " +
                                         "\nlocate the record, then " +
                                         "modify the information and " +
                                         "\npress Update.\n" );
                    }
                    catch ( SQLException sqlex ) {
                         txtInfo.append( sqlex.toString() );
             }
         }
         //********************************************
         if (source.equals(btnDelete))
         {
              try
              {
                         Statement statement = dbconn.createStatement();
                         if ( ! lblID.getText().equals(""))
                         {
                                    System.out.print(lblID.getText());
                                     String temp = "DELETE from AddressTable " +
                                               " WHERE id=" + lblID.getText();
                                               txtInfo.append( "\nDeleting: " +
                                               dbconn.nativeSQL( temp ) + "\n" );

                                  int result = statement.executeUpdate( temp );
                                   if ( result == 1 )
                                 {
                                         txtInfo.append( "\nDeletion successful\n" );
                               }
                               else
                            {
                                  txtInfo.append( "\nDeletion failed\n" );
                                  txtFirst.setText( "" );
                                  txtLast.setText( "" );
                                   txtPhone.setText( "" );
                             }
                             statement.close();
                      }
                       else
                        txtInfo.append( "\nYou may only delete an " +
                        "existing record. Use Find to " +
                        "\nlocate the record, then " +
                       "press delete.\n" );
            }
             catch ( SQLException sqlex )
             {
                       txtInfo.append( sqlex.toString() );
             }
          }
           //********************************************
          if (source.equals(btnClear))
          {
              txtLast.setText("");
              txtFirst.setText("");
              txtPhone.setText("");
              lblID.setText("");
          }
           //********************************************
           if (source.equals(btnExit))
           {
                           System.exit(0);
           }
          }
              //********************************************
          public void display( ResultSet rs )
          {
             try
             {
                rs.next();
                int recordNumber = rs.getInt( 1 );
                if ( recordNumber != 0 )
                {
                   lblID.setText( String.valueOf(recordNumber) );
                   txtLast.setText( rs.getString( 2 ) ); //2 is second column in database
                   txtFirst.setText( rs.getString( 3 ) ); //3 is third column in database
                   txtPhone.setText( rs.getString( 4 ) );
                }
                else
                {
                        txtInfo.append( "\nNo record found\n" );
                }
            }
            catch ( SQLException sqlex )
            {
                     txtInfo.append( "\n*** Information Not In Database ***\n" );
            }
         }
   }
}