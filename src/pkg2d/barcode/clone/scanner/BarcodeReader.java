package pkg2d.barcode.clone.scanner;

import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import java.awt.image.*;
import java.awt.image.renderable.ParameterBlock;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.*;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.swing.JOptionPane;
import tasman.bars.BarCode;
import tasman.bars.BarReader;
import tasman.bars.ReadOptions;


public class BarcodeReader {

            static Connection conn;

	    //Define var for dpi properties
	    static int XRES_TAG = 282;
	    static int YRES_TAG = 283;
	    static long[] resolution = { 300, 1 };
	    
	    static int successCounter;
	    int numberOfTIFFiles;
	    
	    static String fileLocation;
            static int fileInList;
		 
	    static String[] docID = new String[500];
	    static String[] spajNo = new String[500];
	    static String[] quotationValue = new String[500];
	    static String[] riskprofValue = new String[500];
	    static String[] others = new String[500];
            static String[] docSKPKK = new String[500];
            static String[] docSKPRA = new String[500];
            static String[] docSKPRB = new String[500];
	    static String[] SSEP = new String[500];
	    static String[] datafileRequirementFields = new String[500];
//	    static String[] barcodeValueFromDB = new String[500];
		
	    static String[] SPAJImages = new String[500];
	    static String[] SPAJImagesSuccess = new String[500];
	    static String[] quotationImages = new String[500];
	    static String[] riskprofImages = new String[500];
            static String[] othersImages = new String[500];
            static String[] SKPKKImages = new String[500];
            static String[] SKPRAImages = new String[500];
            static String[] SKPRBImages = new String[500];
	    static String[] SSEPImages = new String[500];
            
            int totalFound = 0; //Update Sigit
            int totalDoc = 0;
            String catatan = "";
            String namaFile = "";
            StringBuilder message = new StringBuilder();

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

		public void start(String fileLocation){
                        initializeConnection("Ticketing");
			boolean scanResult = false;

                        String barcodeFolder = "";
			String barcodeValue = "";
                        docID = new String[500];
                        spajNo = new String[500];
                        SPAJImages = new String[500];
                        quotationImages = new String[500];
                        riskprofImages = new String[500];
                        othersImages = new String[500];
                        SSEPImages = new String[500];
                        quotationValue = new String[500];
                        riskprofValue = new String[500];
                        others = new String[500];
                        docSKPKK = new String[500];
                        docSKPRA = new String[500];
                        docSKPRB = new String[500];
                        SKPKKImages = new String[500];
                        SKPRAImages = new String[500];
                        SKPRBImages = new String[500];

                        message.append("Processing SPAJ --> " + fileLocation + System.lineSeparator());

			System.out.println("Processing SPAJ --> " + fileLocation);

			File folder = new File(new File(fileLocation).toPath().toString());
			File[] files = folder.listFiles(); 				
			
			long start = System.currentTimeMillis(); //start timer
                        numberOfTIFFiles = 0;
                        successCounter = 0;
			for (int j = 0; j < files.length; j++) {  //looping all images within folder
				if(files[j].toString().endsWith(".tif")) {	//filter file search with TIFs
					numberOfTIFFiles++;
					readBarcode(files[j].toString(), scanResult, "", j); //read from folder
				}
			}
			
			System.out.println("Finished reading barcodes on "+fileLocation);
			System.out.println("Result: " + successCounter + " success out of " + numberOfTIFFiles + " files");
			//JOptionPane.showMessageDialog(null, "Total Documents: "+totalDoc, "Information", JOptionPane.INFORMATION_MESSAGE);
                        message.append("Finished reading barcodes on "+fileLocation + System.lineSeparator());
                        message.append("Result: " + successCounter + " success out of " + numberOfTIFFiles + " files" + System.lineSeparator());

                    long elapsedTimeMillis = System.currentTimeMillis()-start;
		    System.out.println("Processing time: " + elapsedTimeMillis+" ms"+System.lineSeparator());	//print timer
//                    logs.info("Processing time: " + elapsedTimeMillis+" ms");
                    
		    DATWriter writer = new DATWriter(); //generate .dat file
		    writer.generateDAT(fileLocation, barcodeValue);
		}

		public void readBarcode(String fileFolder, boolean scanResult, String barcodeValue, int counter){
			//System.out.println(counter + ". Current Image --> "+ new File(fileLocation).toPath().getFileName().toString());
			
                            BufferedImage myImage = null;
                            try {
                                myImage = ImageIO.read( new File(fileFolder));
                            } catch (IOException ex) {
                                System.out.println("Error IOException: " + Arrays.deepToString(ex.getStackTrace()));
//                                logs.warning("Error IOException: "+Arrays.deepToString(ex.getStackTrace()));
                            }

			    BarReader barcodeReader = new BarReader(myImage);	// 1: Create BarcodeReader for specified image...
			    ReadOptions options = new ReadOptions();  		// 2: Create a ReadOptions.
			    options.readEast = true;                  		// 3: Read left to right.
			    options.readWest = true;                  		// 3: Read right to left.
			    options.code39 = true;				// 4: Set barcode reading format to Code39 for SPAJ.
			    options.codePDF417 = true;				// 5: Set barcode reading format to PDF417 for Quotation.
                            options.scanInterval = 20;
			    options.xOptions = new Properties();			// 6: set Properties
			    options.xOptions.put( "bitonal", Boolean.TRUE );
                            options.scanBarsToRead = 4;
//                            options.xOptions.put( "code39NoGuard", Boolean.TRUE);
			    options.xOptions.put( "threads", "4" );
//                            options.xOptions.put("smallQuietZone", Boolean.TRUE);
//                            options.xOptions.put("tinyQuietZone", Boolean.TRUE);
//                            options.xOptions.put("embossed", Boolean.TRUE);
			    BarCode[] bars = barcodeReader.readBars( options ); // 7: Read the barcodes contained in the image.
			    myImage.flush();
			    
			    if ( bars.length == 0 ) {               		// Process the results:
			     scanResult = false;					  		// If no barcode found
//                            options.readEast = false;              // No barcodes read. Maybe
//                            options.readWest = true;               // the image was scanned
//                            bars = barcodeReader.readBars( options );         // upside down. Let's see.			     
			/**     File oldFile = new File(fileLocation);
				 File newFile = new File(fileLocation.toString().substring(0, fileLocation.length()-4)+"-FAIL.tif");
				       
				 oldFile.renameTo(newFile);   **/
			     myImage.flush();
			    }
			    
			    else {
                             myImage.flush();
			     successCounter++;
			     scanResult = true;
			     int bundle = 0;
			     for ( int i = 0; i < bars.length; i++ )   {
			        barcodeValue = barcodeValue + bars[ i ].getString(); 
                                String regex = "\\d-";
                                Pattern p = Pattern.compile(regex);
                                Matcher m = p.matcher(barcodeValue);
                                String OneBarcode = m.replaceAll("");
                                String tes = m.toString();
//                                logs.info("Found barcode --> " + barcodeValue);
                                System.out.println("Found barcode --> " + barcodeValue);
                                if(barcodeValue.startsWith("1-")){
                                    if(checkSPAJ(OneBarcode) == true) { //Update Sigit
                                        if(OneBarcode.equals("14390101")){
                                            others[counter] = OneBarcode;
                                            othersImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected OthersID ==> " + others[counter] + " added on " + counter + " with Others Image value --> " + othersImages[counter]);
                                        }else if(OneBarcode.equals("14390101")){
                                            docID[counter] = "11011502";
                                            SPAJImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected DocID ==> " + docID[counter] + " added on " + counter + " with SPAJ Image value --> " + SPAJImages[counter]);
                                        }else{
                                            docID[counter] = OneBarcode;
                                            SPAJImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected DocID ==> " + docID[counter] + " added on " + counter + " with SPAJ Image value --> " + SPAJImages[counter]);
                                        }
                                    }
                                    else if(checkSKPKK(OneBarcode) == true){
                                            docSKPKK[counter] = OneBarcode;
                                            SKPKKImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected SKPKK ==> " + docSKPKK[counter] + " added on " + counter + " with SKPKK Image value --> " + SKPKKImages[counter]);
                                    }
                                    else if(checkSKPRA(OneBarcode) == true){
                                            docSKPRA[counter] = OneBarcode;
                                            SKPRAImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected SKPRA ==> " + docSKPRA[counter] + " added on " + counter + " with SKPRA Image value --> " + SKPRAImages[counter]);
                                    }
                                    else if(checkSKPRB(OneBarcode) == true){
                                            docSKPRB[counter] = OneBarcode;
                                            SKPRBImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected SKPRB ==> " + docSKPRB[counter] + " added on " + counter + " with SKPRB Image value --> " + SKPRBImages[counter]);
                                    }
                                    else if(checkOthers(OneBarcode) == true) {
                                            others[counter] = OneBarcode;
                                            othersImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected OthersID ==> " + others[counter] + " added on " + counter + " with Others Image value --> " + othersImages[counter]);
                                    }
                                    else if((OneBarcode.length() == 8 || OneBarcode.length() == 9)){
                                            spajNo[counter] = OneBarcode;
                                            System.out.println("Detected SPAJ No ==> " + spajNo[counter]+" with IMAGES -->"+new File(fileFolder).toPath().getFileName().toString());
                                    }
                                }
                                else if(!barcodeValue.startsWith("1-")){
                                    if(checkSPAJ(barcodeValue) == true) { //Update Sigit
                                        if(barcodeValue.equals("11010823")){
                                            others[counter] = barcodeValue;
                                            othersImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected OthersID ==> " + others[counter] + " added on " + counter + " with Others Image value --> " + othersImages[counter]);
                                        }else if(OneBarcode.equals("14390101")){
                                            docID[counter] = "11011502";
                                            SPAJImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected DocID ==> " + docID[counter] + " added on " + counter + " with SPAJ Image value --> " + SPAJImages[counter]);
                                        }else{
                                            docID[counter] = barcodeValue;
                                            SPAJImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected DocID ==> " + docID[counter] + " added on " + counter + " with SPAJ Image value --> " + SPAJImages[counter]);
                                        }
                                    }
                                    else if(checkSKPKK(barcodeValue) == true){
                                            docSKPKK[counter] = barcodeValue;
                                            SKPKKImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected SKPKK ==> " + docSKPKK[counter] + " added on " + counter + " with SKPKK Image value --> " + SKPKKImages[counter]);
                                    }
                                    else if(checkSKPRA(barcodeValue) == true){
                                            docSKPRA[counter] = barcodeValue;
                                            SKPRAImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected SKPRA ==> " + docSKPRA[counter] + " added on " + counter + " with SKPRA Image value --> " + SKPRAImages[counter]);
                                    }
                                    else if(checkSKPRB(barcodeValue) == true){
                                            docSKPRB[counter] = barcodeValue;
                                            SKPRBImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected SKPRB ==> " + docSKPRB[counter] + " added on " + counter + " with SKPRB Image value --> " + SKPRBImages[counter]);
                                    }
                                    else if(checkOthers(barcodeValue) == true) {
                                            others[counter] = barcodeValue;
                                            othersImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                            System.out.println("Detected OthersID ==> " + others[counter] + " added on " + counter + " with Others Image value --> " + othersImages[counter]);
                                    }
                                    else if((barcodeValue.length() == 8 || barcodeValue.length() == 9)){
                                            spajNo[counter] = barcodeValue;
                                            System.out.println("Detected SPAJ No ==> " + spajNo[counter]+ " with IMAGES -->"+new File(fileFolder).toPath().getFileName().toString());
                                    }
                                    else if(barcodeValue.contains(";") == true) {
                                            //Check and read barcode value *Sigit
                                            if(sanityCheck(barcodeValue,";") == 146 || sanityCheck(barcodeValue,";") == 156 
                                                    || sanityCheck(barcodeValue,";") == 154 || sanityCheck(barcodeValue,";") == 166 
                                                    || sanityCheck(barcodeValue,";") == 170 || sanityCheck(barcodeValue,";") == 172 
                                                    || sanityCheck(barcodeValue,";") == 173 || sanityCheck(barcodeValue,";") == 175 ) {
                                                    quotationValue[counter] = barcodeValue;
                                                    quotationImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                                    System.out.println("Detected 2D barcode with Image value --> " + quotationImages[counter]);
                                                    insertBarcodeValueIntoDatabase(barcodeValue, new File(fileFolder).getParent().toString(), fileFolder); //convert ; to | to avoid error on SQL syntax

//                                                    totalFound = totalFound + 1; //Update Sigit
//
//                                                    String[] isi = barcodeValue.split(";");
//                                                    String SQS = "";
//                                                    if(isi.length==157){
//                                                        SQS = "157";
//                                                    }else if(isi.length==167){
//                                                        SQS = "158";
//                                                    }else if(isi.length==171){
//                                                        SQS = "158";
//                                                    }else if(isi.length==173){
//                                                        SQS = "159";
//                                                    }else if(isi.length==174){
//                                                        SQS = "160";
//                                                    }else if(isi.length==147){
//                                                        SQS = "161";
//                                                    }else if(isi.length < 147){
//                                                        continue;
//                                                    }
//
//                                                    BufferedReader sqsReader = null;
////                                                    String SQSFile = "\\\\10.0.0.1\\data\\template\\SQS\\" + SQS + ".csv"; //Network
//                                                    String SQSFile = "D:\\template\\SQS\\" + SQS + ".csv"; //Local
//                                                    try {
//                                                        sqsReader = new BufferedReader(new FileReader(SQSFile));
//
//                                                    String[] SQSfield = new String[250];
//
//                                                    String Life_Name = "";
//                                                    int UsiaCust = 0;
//
//                                                        for(int pin=0; pin < isi.length; pin++){
//                                                            SQSfield[pin] = sqsReader.readLine();
//                                                            if(SQSfield[pin].equals("LIFE_LSURNAME_1")){
//                                                                Life_Name = isi[pin];
//                                                            }else if(SQSfield[pin].equals("LIFE_CLTDOB_1")){
//                                                                UsiaCust = Integer.parseInt(isi[pin].substring(4));
//                                                            }
//                                                        };
//
//                                                        namaFile = namaFile +"-LIFE NAME_"+ totalFound +": " + Life_Name + System.lineSeparator();
//                                                    } catch (FileNotFoundException ex) {
//                                                        System.out.println("Error FileNotFoundException: " + Arrays.deepToString(ex.getStackTrace()));
////                                                        logs.warning("Error FileNotFoundException: "+Arrays.deepToString(ex.getStackTrace()));
//                                                    } catch (IOException ex) {
//                                                        System.out.println("Error IOException: " + Arrays.deepToString(ex.getStackTrace()));
////                                                        logs.warning("Error IOException: "+Arrays.deepToString(ex.getStackTrace()));
//                                                    }
                                                    //Watermark should be after parsing
//                                                    ImageWatermark watermark = new ImageWatermark(); 
//                                                        watermark.watermarkImage(new File(fileFolder).toString(), scanResult);   //watermark image  		 
                                            }
                                            else if(sanityCheck(barcodeValue,";") == 70){ // Read RiskProfile
                                                riskprofValue[counter] = barcodeValue;
                                                riskprofImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                                System.out.println("Detected Risk Profile barcode with Image value --> " + riskprofImages[counter]);
                                                insertBarcodeValueIntoDatabase("RiskProfile"+barcodeValue, new File(fileFolder).getParent().toString(), fileFolder);
                                            }
                                    }
                                            else if(barcodeValue.equals("ESEP") || barcodeValue.equals("SSEP")) {
                                                    SSEP[counter] = barcodeValue;
                                                    SSEPImages[counter] = new File(fileFolder).toPath().getFileName().toString();
                                                    bundle = bundle + counter;
                                            }
                                }
			        
			        barcodeValue = "";
			      }	     
                                myImage.flush();
                             
			    }
			    
			    //resizeImage(fileLocation);
			    myImage.flush();
			    
		}

		
		private static void insertBarcodeValueIntoDatabase(String barcodeValue, String folder, String imageValue) {
//                System.out.println("Debug Insert DB Barcode !!");
			try {
                            PreparedStatement ps = conn.prepareStatement("INSERT INTO TICKETDB.dbo.Barcode values(?,?,?,?)");
                            ps.setString(1, barcodeValue);
                            ps.setString(2, folder);
                            ps.setString(3, "OK");
                            ps.setString(4, imageValue);
                            ps.executeUpdate();
			} catch (SQLException e) {
                            System.out.println("ERROR: " + e.getMessage());
			}
		}
		
		private static boolean checkSPAJ(String barcodeValue) {
			// TODO --> 1. Check SPAJ_List.csv for existing barcode value 2. return true if found

			String line = "";
			String csvSplitBy = "	";
			
			try {
//				BufferedReader br = new BufferedReader(new FileReader("\\\\10.0.0.1\\data\\template\\formid-spaj_phs.csv")); //Server
				BufferedReader br = new BufferedReader(new FileReader("D:\\template\\formid-spaj_phs.csv")); //Local
				
				try {
					while ((line = br.readLine()) != null) {					
						String[] value = line.split(csvSplitBy);
                                                    if(value[1].equals(barcodeValue) && value[0].contains("SPAJ") && !value[0].contains("Amandemen")) {
                                                            System.out.println("found SPAJ ==> " + value[0] + " match with --> " + barcodeValue); 
                                                            br.close();
                                                            return true;
                                                    }
						
					}
				} catch (IOException ex) {
                                System.out.println("Error IOException: " + Arrays.deepToString(ex.getStackTrace()));
//                                    logs.warning("Error IOException: "+Arrays.deepToString(e.getStackTrace()));
				}
			} catch (FileNotFoundException ex) {
                                System.out.println("Error FileNotFoundException: " + Arrays.deepToString(ex.getStackTrace()));
//                                 logs.warning("Error FileNotFoundException: "+Arrays.deepToString(e.getStackTrace()));
			}	
			return false;
		}
                
                private static boolean checkSKPKK(String barcodeValue) {
			// TODO --> 1. Check SPAJ_List.csv for existing barcode value 2. return true if found

			String line = "";
			String csvSplitBy = "	";
			
			try {
//				BufferedReader br = new BufferedReader(new FileReader("\\\\10.0.0.1\\data\\template\\formid-spaj_phs.csv")); //Server
				BufferedReader br = new BufferedReader(new FileReader("D:\\template\\formid-spaj_phs.csv")); //Local
				
				try {
					while ((line = br.readLine()) != null) {					
						String[] value = line.split(csvSplitBy);
                                                    if(value[1].equals(barcodeValue) && value[0].contains("SKPKK")) {
                                                            System.out.println("found SKPKK ==> " + value[0] + " match with --> " + barcodeValue); 
                                                            br.close();
                                                            return true;
                                                    }
						
					}
				} catch (IOException ex) {
                                System.out.println("Error IOException: " + Arrays.deepToString(ex.getStackTrace()));
//                                    logs.warning("Error IOException: "+Arrays.deepToString(e.getStackTrace()));
				}
			} catch (FileNotFoundException ex) {
                                System.out.println("Error FileNotFoundException: " + Arrays.deepToString(ex.getStackTrace()));
//                                 logs.warning("Error FileNotFoundException: "+Arrays.deepToString(e.getStackTrace()));
			}	
			return false;
		}
                
                private static boolean checkSKPRA(String barcodeValue) {
			// TODO --> 1. Check SPAJ_List.csv for existing barcode value 2. return true if found

			String line = "";
			String csvSplitBy = "	";
			
			try {
//				BufferedReader br = new BufferedReader(new FileReader("\\\\10.0.0.1\\data\\template\\formid-spaj_phs.csv")); //Server
				BufferedReader br = new BufferedReader(new FileReader("D:\\template\\formid-spaj_phs.csv")); //Local
				
				try {
					while ((line = br.readLine()) != null) {					
						String[] value = line.split(csvSplitBy);
                                                    if(value[1].equals(barcodeValue) && value[0].contains("SKPR type A")) {
                                                            System.out.println("found SKPRA ==> " + value[0] + " match with --> " + barcodeValue); 
                                                            br.close();
                                                            return true;
                                                    }
						
					}
				} catch (IOException ex) {
                                System.out.println("Error IOException: " + Arrays.deepToString(ex.getStackTrace()));
//                                    logs.warning("Error IOException: "+Arrays.deepToString(e.getStackTrace()));
				}
			} catch (FileNotFoundException ex) {
                                System.out.println("Error FileNotFoundException: " + Arrays.deepToString(ex.getStackTrace()));
//                                 logs.warning("Error FileNotFoundException: "+Arrays.deepToString(e.getStackTrace()));
			}	
			return false;
		}
                
                private static boolean checkSKPRB(String barcodeValue) {
			// TODO --> 1. Check SPAJ_List.csv for existing barcode value 2. return true if found

			String line = "";
			String csvSplitBy = "	";
			
			try {
//				BufferedReader br = new BufferedReader(new FileReader("\\\\10.0.0.1\\data\\template\\formid-spaj_phs.csv")); //Server
				BufferedReader br = new BufferedReader(new FileReader("D:\\template\\formid-spaj_phs.csv")); //Local
				
				try {
					while ((line = br.readLine()) != null) {					
						String[] value = line.split(csvSplitBy);
                                                    if(value[1].equals(barcodeValue) && value[0].contains("SKPR type B")) {
                                                            System.out.println("found SKPRB ==> " + value[0] + " match with --> " + barcodeValue); 
                                                            br.close();
                                                            return true;
                                                    }
						
					}
				} catch (IOException ex) {
                                System.out.println("Error IOException: " + Arrays.deepToString(ex.getStackTrace()));
//                                    logs.warning("Error IOException: "+Arrays.deepToString(e.getStackTrace()));
				}
			} catch (FileNotFoundException ex) {
                                System.out.println("Error FileNotFoundException: " + Arrays.deepToString(ex.getStackTrace()));
//                                 logs.warning("Error FileNotFoundException: "+Arrays.deepToString(e.getStackTrace()));
			}	
			return false;
		}

		private static boolean checkOthers(String barcodeValue) {
			// TODO --> 1. Check SPAJ_List.csv for existing barcode value 2. return true if found

			String line = "";
			String csvSplitBy = "	";
			
			try {
//				BufferedReader br = new BufferedReader(new FileReader("\\\\10.0.0.1\\data\\template\\formid-spaj_phs.csv")); //Server
				BufferedReader br = new BufferedReader(new FileReader("D:\\template\\formid-spaj_phs.csv")); //Local
				
				try {
					while ((line = br.readLine()) != null) {					
						String[] value = line.split(csvSplitBy);
						if((value[1].equals(barcodeValue) && !value[0].contains("SPAJ")) || (value[1].equals(barcodeValue) && value[0].contains("Amandemen"))) { //Use Amandemen
							System.out.println("found OTHERS ==> " + value[0] + " match with --> " + barcodeValue); 
							br.close();
							return true;
						}
					}
				} catch (IOException ex) {
                                System.out.println("Error IOException: " + Arrays.deepToString(ex.getStackTrace()));
//                                    logs.warning("Error IOException: "+Arrays.deepToString(e.getStackTrace()));
				}
			} catch (FileNotFoundException ex) {
                                System.out.println("Error FileNotFoundException: " + Arrays.deepToString(ex.getStackTrace()));
//                                 logs.warning("Error FileNotFoundException: "+Arrays.deepToString(e.getStackTrace()));
			}	
			return false;
		}
		
		private static void resizeImage(String fileLocation) {

            BufferedImage bufferedImage = null;
			try {
				bufferedImage = ImageIO.read(new File(fileLocation));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		    float scale =0.75f;
            
            TIFFField xRes = new TIFFField(XRES_TAG,
            TIFFField.TIFF_RATIONAL, 1, new long[][] { resolution });
            TIFFField yRes = new TIFFField(YRES_TAG,
            TIFFField.TIFF_RATIONAL, 1, new long[][] { resolution });
            TIFFEncodeParam param = new TIFFEncodeParam();
            
            param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
            param.setExtraFields(new TIFFField[]{xRes, yRes});
		    
			//Set new size image parameter and Resize image based on scaling;
			ParameterBlock pb = new ParameterBlock();
			pb.addSource(bufferedImage);
			pb.add(scale);
			pb.add(scale);
			pb.add(0.0f);
			pb.add(0.0f);
			pb.add(new InterpolationNearest());
			PlanarImage scaledImage = JAI.create("scale", pb, null);
			
			
			JAI.create("filestore",scaledImage ,fileLocation,"TIFF",param);
		}

		public static int sanityCheck(String barcodeValue, String string)				//sanity check using ";" characters
		{
			int pos = barcodeValue.indexOf(string);
			return pos == -1 ? 0 : 1 + sanityCheck(barcodeValue.substring(pos+1),string);
		}

        public boolean createfolder()throws SQLException //automatic folder creation
        {             
            int flag = 0;  

            //MonthName
            SimpleDateFormat formatter=  new SimpleDateFormat("HHmmss");
            String timeNow = formatter.format(Calendar.getInstance().getTime()); //get current time
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int m =cal.get(Calendar.MONTH) +1;
            String month = ""+m;
            String day = ""+cal.get(Calendar.DAY_OF_MONTH);
            String[] monthName = {"JANUARI", "FEBRUARI",
                        "MARET", "APRIL", "MEI", "JUNI", "JULI",
                        "AGUSTUS", "SEPTEMBER", "OKTOBER", "NOVEMBER",
                        "DESEMBER"
                        };
            String namabulan = monthName[cal.get(Calendar.MONTH)];
            if(cal.get(Calendar.MONTH)<10)
            {
                month ="0"+m;
            } 
            if(cal.get(Calendar.DAY_OF_MONTH)<10)
            {
                day ="0"+cal.get(Calendar.DAY_OF_MONTH);  
            }
            String yymmdd = ""+year+""+month+""+day+"";

                 //File dir = new File("X://PRUDENTIAL OUTPUT//"+m_jcombobox2.getSelectedItem().toString()+"//"+month+"."+namabulan+"//"+yymmdd+"//"+m_jcombobox1.getSelectedItem().toString()+"//"+m_jtextfield3.getText()+"//00"+i+"");
                 //File dir = new File("X://PRUDENTIAL OUTPUT//BARU//"+HeadFolder+"//"+m_jcombobox2.getSelectedItem().toString()+"//"+month+"."+namabulan+"//"+yymmdd+"//"+m_jcombobox1.getSelectedItem().toString()+"//"+m_jtextfield3.getText()+"//00"+i+"");
                 File dir = new File("Z://dummy//2D_BARCODE_LOGS//"+year+"//"+namabulan+"//"+yymmdd+"");
                 boolean isDirCreated = dir.mkdirs();
                 if(isDirCreated)
                 {
                     flag=0;
                 }
                 else
                 {
                     flag=1;
                 }

             if(flag==0)
             {
                 System.out.println("Directory created along with required nonexistent parent directories");
                 System.out.println(dir);
                 System.out.println(dir.toPath().toString());
                 //txt(dir.toPath().toString()+"\\", message, String.valueOf(year), namabulan, yymmdd, timeNow);
                 return true;
             }
             else
             {
                 System.out.println("Directory already exist");
                 System.out.println(dir.toPath().toString());
                 //txt(dir.toPath().toString()+"\\", message, String.valueOf(year), namabulan, yymmdd, timeNow);
                 return false;
             }
        }
        
        public static int getUmur(String tgllahir, String tglquotation) {
            int umur = 0;
            Date tglLahir = null;
            Date tglQuot = null;
            long time, time2;
            try {
                time = new SimpleDateFormat("dd/MM/yyyy").parse(tgllahir).getTime();
                tglLahir = new Date(time);
                time2 = new SimpleDateFormat("dd/MM/yyyy").parse(tglquotation).getTime();
                tglQuot = new Date(time2);
            } catch (ParseException e) {
                    //logs.warning("Error ParseException: "+Arrays.deepToString(e.getStackTrace()));
                e.getStackTrace();
            }
            try {
                umur = GetBirth.getAge(tglLahir, tglQuot);

            } catch (Exception e) {
                    //logs.warning("Error Exception: "+Arrays.deepToString(e.getStackTrace()));
                e.getStackTrace();
            }
            return umur;
        }
}