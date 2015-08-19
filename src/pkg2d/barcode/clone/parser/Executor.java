package pkg2d.barcode.clone.parser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;


public class Executor {

	public static Connection conn;
	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws SQLException 
	 */
        public static void main(String[] args){
            Executor e = new Executor();
            e.start("Z:\\011_SIGIT\\SCANNER 1\\C 0 T 100");
        }
        
        public void start(String fileLocation){
            try {
                
                int barcodeCounter = 0;
                String[] barcodeFolderFromDB = new String[500];
                String[] barcodeValueFromDB = new String[500];
                String[] imageValueFromDB = new String[500];
                
                initializeConnection("Ticketing");
                String queryString = "SELECT BarcodeValue, Folder, ImageValue FROM TICKETDB.dbo.Barcode WHERE Folder ='"+fileLocation+"'";
                Statement statement = conn.createStatement();

                ResultSet rs = statement.executeQuery(queryString);
                int x = 0;
                 while(rs.next()) {
                    barcodeCounter++;
                    barcodeValueFromDB[x] = rs.getString("BarcodeValue");
                    barcodeFolderFromDB[x] = rs.getString("Folder");
                    imageValueFromDB[x] = rs.getString("ImageValue");
                    x++; 
                 }

                    for(int j = 0; j < barcodeCounter; j++) {
                            BarcodeParser parser = new BarcodeParser();
                            // Update sigit parse Barcode with each images Value
                            parser.parseBarcode(barcodeValueFromDB[j], barcodeFolderFromDB[j], imageValueFromDB[j]);
                            System.out.println("FOLDER: "+barcodeFolderFromDB[j]+" VALUE: "+barcodeValueFromDB[j]+" IMAGE: "+imageValueFromDB[j]);
                            System.out.println("RUN PARSING");
                    }            
            } catch (SQLException ex) {
                Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                System.out.println("ERROR: " + ex.getMessage());
            }
        }

	public static void initializeConnection(String connectionType) {
            try {
                if(connectionType.equals("Ticketing")) {
                //	          	 String db_connect_string="jdbc:sqlserver://10.1.1.5:19422"; //SERVER
                        String db_connect_string="jdbc:sqlserver://JKTWEB\\SQLEXPRESS"; //UAT
                        String db_userid="sa";
                        String db_password="ticket";

                    Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                    conn = DriverManager.getConnection(db_connect_string,
                             db_userid, db_password
                                );
                    System.out.println("Connected to Ticketing Database");	 
                }
            } catch (Exception e) {
                     JOptionPane.showMessageDialog(null, e.toString(), "Error on connecting to SQL database!",
                 JOptionPane.ERROR_MESSAGE);
            }
	}



	 
}
