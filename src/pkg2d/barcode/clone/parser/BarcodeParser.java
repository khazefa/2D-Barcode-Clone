package pkg2d.barcode.clone.parser;

import pkg2d.barcode.clone.scanner.GetBirth;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader; 
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import static pkg2d.barcode.clone.parser.Executor.conn;
import static pkg2d.barcode.clone.parser.Executor.initializeConnection;

public class BarcodeParser extends Executor{

	String fileLocation;
	String barcodeValue;
	String SQSVersion;
	String[] DocIDs;
	String[] SQS;
	
	public BarcodeParser() {
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
                            String line = "";
                            String csvSplitBy = "   ";
                
                            try {
                            BufferedReader barcodeIDFetcher = new BufferedReader(new FileReader("D:\\template\\BARCODE_ID.csv")); //Local

                            while ((line = barcodeIDFetcher.readLine()) != null) {

                                    String[] value = line.split(csvSplitBy);
                                        if(barcodeValueFromDB[j].contains(value[0])) {
                                                System.out.println("Barcode has DocID " + value[0] + " therefore will be processed as SQS " + value[1]);
                                            //Update sigit proccess barcode with each images value
                                                    if(barcodeValueFromDB[j].startsWith("RiskProfile")){ //Parse RiskProfile
                                                        //code for risk profile
                                                        String barc = barcodeValueFromDB[j].replaceFirst("RiskProfile", "");
                                                        parseRPBarcode(barc, barcodeFolderFromDB[j], imageValueFromDB[j]);
                                                    }else{
                                                        processBarcode(barcodeValueFromDB[j], value[1], barcodeFolderFromDB[j], imageValueFromDB[j]);
                                                    }
                                        }
                            }

                            barcodeIDFetcher.close();

                            } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                            } catch (IOException e) {
                                    e.printStackTrace();
                            }
                            
//                            parser.processBarcode(barcodeValueFromDB[j], "161", barcodeFolderFromDB[j], imageValueFromDB[j]);
//                            System.out.println("FOLDER: "+barcodeFolderFromDB[j]+" VALUE: "+barcodeValueFromDB[j]+" IMAGE: "+imageValueFromDB[j]);
                            
                    }            
            } catch (SQLException ex) {
                Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
                System.out.println("ERROR: " + ex.getMessage());
            }
        }
	
	public void parseBarcode(String barcodeFolder, String barcodeValue, String imageValues) {
		//TODO:
		//- load reference table -- ok
		//- search doc id and match it against SQS version -- ok
		//- parse 
		//- upload parse result to temporary database
		
		//System.out.println("Tes");
		String line = "";
		String csvSplitBy = "   ";
		
		try {
//			BufferedReader barcodeIDFetcher = new BufferedReader(new FileReader("\\\\10.0.0.1\\data\\template\\BARCODE_ID.csv")); //Server
			BufferedReader barcodeIDFetcher = new BufferedReader(new FileReader("D:\\template\\BARCODE_ID.csv")); //Local
			
			while ((line = barcodeIDFetcher.readLine()) != null) {
				
				String[] value = line.split(csvSplitBy);
                                //if(!value[0].isEmpty()) {
                                    //System.out.println("DEBUG #1: " + barcodeValue);
                                    //System.out.println("DEBUG #2: " + value[0]);
                                    if(barcodeValue.contains(value[0])) {
                                            System.out.println("Barcode has DocID " + value[0] + " therefore will be processed as SQS " + value[1]);
                                        //Update sigit proccess barcode with each images value
                                                processBarcode(barcodeValue, value[1], barcodeFolder, imageValues);                       
                                            
                                    }
//                                    else {
//                                            System.out.println("Found undefined SQS, skip this barcode");
//                                    }	
                                //}
			}
			
			barcodeIDFetcher.close();
			
		} catch (FileNotFoundException e) {
                        e.printStackTrace();
		} catch (IOException e) {
                        e.printStackTrace();
		}
                
//                    String barcodeFolderFromDB = "";
//                    String barcodeValueFromDB = "";
//                    String imageValueFromDB = "";
//
//                    Statement statement; 
//                    String queryString;
//
//                   queryString = "SELECT BarcodeValue, Folder, ImageValue from TICKETDB.dbo.Barcode where BarcodeValue ='"+barcodeValue+"'";
//                       try {
//                               statement = conn.createStatement();
//
//                      ResultSet rs = statement.executeQuery(queryString);
//                          while (rs.next()) {
//                             barcodeValueFromDB = rs.getString(1);
//                             barcodeFolderFromDB = rs.getString(2);
//                             imageValueFromDB = rs.getString(3);
//                          }
//                       } catch (SQLException e) {
//                               logs.warning("Error SQLException: "+Arrays.deepToString(e.getStackTrace()));
//                       }    		    
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
                    e.printStackTrace();
            }
            try {
                umur = GetBirth.getAge(tglLahir, tglQuot);

            } catch (Exception e) {
                    e.printStackTrace();
            }
            return umur;

        }
        
	private void processBarcode(String barcodeValue, String SQS, String barcodeFolder, String imageValue){  
            try {
                //method used to "filter" written parsed barcode value so it can only contains value in the datafile requirement txt file

                System.out.println("Parse Barcode..");
                BufferedReader sqsReader = null;
//                String SQSFile = "\\\\10.0.0.1\\data\\template\\SQS\\" + SQS + ".csv"; //Server
                String SQSFile = "D:\\template\\SQS\\" + SQS + ".csv"; //Local
                String datFile = barcodeFolder + "\\TES_" + new File(barcodeFolder).getName().toString() + ".DAT";

                sqsReader = new BufferedReader(new FileReader(SQSFile));
                System.out.println("Reading SQS file from " + SQS + System.lineSeparator());

                String[] SQSfield = new String[250];
                String[] value = new String[250];
                String tempIns = "";
                String tempFund = "";
                String writeToDAT = "";
                String dateQ = "";
                String lifeName1 = "";
                String ownerOccTmp = "";
                String payorOccTmp = "";
                String ownerOcc = "";
                String payorOcc = "";
                String getTahun = "";
                
                String regex = "\\d+";
                int signIns=0;
                int signFund=0;
                boolean isPIAorPSIA = false;
                
                
                String[] strHeadIns = new String[5];
                String[] strIns = new String[5];
                ArrayList<String> headsIns = new ArrayList<String>();
                ArrayList<String> valsIns = new ArrayList<String>();
                
                String[] strHeadFund = new String[5];
                String[] strFund = new String[5];
                ArrayList<String> headsFund = new ArrayList<String>();
                ArrayList<String> valsFund = new ArrayList<String>();
                value = barcodeValue.split(";");
                for(int i = 0; i < value.length; i++) {
                   SQSfield[i] = sqsReader.readLine();
                   if(SQSfield[i].equals("SQS_Barcode_Date_Time_Created_")){				
                        String txt = value[i];
                        String mName[] = new String[]{"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                        String dayQ = txt.substring(0, 2);
                        String monthQName = txt.substring(3, 6);
                        String monthQ = "";
                        String yearQ = txt.substring(7);
                        for(int q = 0; q < mName.length; q++) {
                        if(mName[q].equals(monthQName)){
                            if(q < 10){
                            monthQ = "0" + String.valueOf(q);
                            }else{
                            monthQ = String.valueOf(q);
                            }
                        }
                        }
                        dateQ = dayQ + "/" + monthQ + "/" + yearQ;                              

                    writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                    }
                    else if(SQSfield[i].equals("FORM_ID")){
                    //if(SQSfield[i].equals("FORM_ID")){
                    value[i] = value[i].toUpperCase();
                    if(checkDocId(value[i]) == true){
                        isPIAorPSIA = true;
                    }
                    //System.out.println("DEBUG Form ID: " + i + ". FIELD " + SQSfield[i] + " : " + value[i]);	
                    writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                    }
                    else if(SQSfield[i].startsWith("INSURANCE_")) {		//populate INSURANCE_TYPE_* for rider.csv
                        if(SQSfield[i].contains("LIFE")){
                            if(value[i+2].isEmpty() && value[i+3].isEmpty()){
                                value[i] = "";
                                value[i+1] = "";
                                value[i+2] = "";
                                value[i+3] = "";
                                value[i+4] = "";
                            }
                        }                        
                        if(SQSfield[i].contains("CRTABLE")){
                            if(!value[i].isEmpty()){
                                    if(value[i].equals("W1XR") || value[i].equals("W1YR")){ // Filter Kode Produk *sigit
                                        value[i+1] = "";
                                        value[i+2] = "";
                                    }
                                    if(value[i+1].isEmpty()){
                                        value[i+1] = "0";
                                    }
                                    if(value[i+2].isEmpty()){
                                        value[i+2] = "0";
                                    }                   
                            }
                        }
                        headsIns.add(SQSfield[i]);
                        valsIns.add(value[i]);
                        if(signIns == 0){
                            writeToDAT = writeToDAT + "ISI_INSURANCE";
                            ++signIns;
                        }else{
                            continue;
                        }
                    value[i] = value[i].toUpperCase();
                    }
                    else if(SQSfield[i].equals("LIFE_LSURNAME_1")) {	//Ambil Nama Tertanggung
                        lifeName1 = value[i];
                        ownerOccTmp = value[i+2];
                        payorOccTmp = value[i+2];
                        writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                    }
                    else if(SQSfield[i].equals("LIFE_CLTDOB_1")) {	//Get cust birthday
                        String dayB = value[i].substring(6);
                        String monthB = value[i].substring(4,6);
                        String yearB = value[i].substring(0,4);

                        String birthDay = dayB + "/" + monthB + "/" + yearB;
                        int ageCust = getUmur(birthDay, dateQ);
                        getTahun = "USIA_TAHUN_BERIKUT:" + ageCust + System.lineSeparator();
                        writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                    }
                    else if(SQSfield[i].startsWith("FUND_")){
                        if(SQSfield[i].contains("UALFND")){
                            if(value[i+1].isEmpty() || value[i+1].equals("0")){
                                value[i] = "N";
                            }
                        }
                        if(SQSfield[i].contains("UALPRC")){
                            if(value[i].isEmpty() || value[i].equals("0")){
                                value[i] = "000";
                            }else{
                                if(value[i].length()==1){
                                    value[i] = "00"+value[i];
                                }else if(value[i].length()==2){
                                    value[i] = "0"+value[i];
                                }
                            }
                        }
                        headsFund.add(SQSfield[i]);
                        valsFund.add(value[i]);
                        if(signFund == 0){
                            writeToDAT = writeToDAT + "ISI_FUND";
                            ++signFund;
                        }else{
                            continue;
                        }
                    value[i] = value[i].toUpperCase();
//                    //System.out.println("DEBUG Requirement: " + i + ". FIELD " + SQSfield[i] + " : " + value[i]);
//                    writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                    }
                    else if(SQSfield[i].equals("OWNER_LSURNAME")) {	//Ambil Nama Owner
                        if(!value[i].isEmpty() && value[i].equals(lifeName1)){ //Jika Owner/Payor sama dengan tertanggung, maka pekerjaan owner/payor mengikuti tertanggung
                            ownerOcc = ownerOccTmp;
                        }else{
                            ownerOcc = "";
                        }
                        writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                    }
                    else if(SQSfield[i].equals("OWNER_OCCPCODE")){
                        int sqsver = Integer.parseInt(SQS);
                        if(sqsver == 160){
                            if(value[i].matches("^\\d{1}$|^\\d{2}$|^\\d{3}$") && !value[i].equals("0")){
//                                if(!ownerOcc.isEmpty()){
//                                    value[i] = ownerOcc;
//                                }else{
//                                    value[i] = "ADMN";
//                                }
                                value[i] = "";
                                writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                            }
                        }else{
                            if(value[i].matches("^\\d{1}$|^\\d{2}$|^\\d{3}$") && !value[i].equals("0")){
                                if(!ownerOcc.isEmpty()){
                                    value[i] = ownerOcc;
                                }else{
                                    value[i] = "";
                                }
                                writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                            }
                        }
                    } 
                    else if(SQSfield[i].equals("PAYOR_LSURNAME")) {	//Ambil Nama Payor
                        if(!value[i].isEmpty() && value[i].equals(lifeName1)){
                            payorOcc = payorOccTmp;
                        }else{
                            payorOcc = "";
                        }
                        writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                    }
                    else if(SQSfield[i].equals("PAYOR_OCCPCODE")){
                        int sqsver = Integer.parseInt(SQS);
                        if(sqsver == 160){
                            if(value[i].matches("^\\d{1}$|^\\d{2}$|^\\d{3}$") && !value[i].equals("0")){
//                                if(!payorOcc.isEmpty()){
//                                    value[i] = payorOcc;
//                                }else{
//                                    value[i] = "ADMN";
//                                }
                                value[i] = "";
                                writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                            }
                        }else{
                            if(value[i].matches("^\\d{1}$|^\\d{2}$|^\\d{3}$") && !value[i].equals("0")){
                                if(!payorOcc.isEmpty()){
                                    value[i] = payorOcc;
                                }else{
                                    value[i] = "";
                                }
                                writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                            }
                        }
                    }
//                    else if(SQSfield[i].equals("OWNER_OCCPCODE") || SQSfield[i].equals("PAYOR_OCCPCODE")){
//                        if(value[i].matches("^\\d{1}$|^\\d{2}$|^\\d{3}$") && !value[i].equals("0")){
//                            value[i] = "ADMN";
//                            writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
//                        }
//                    }  
                    else{
                        writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                    }
                }

                strHeadIns = headsIns.toArray(new String[headsIns.size()]); 
                strIns = valsIns.toArray(new String[valsIns.size()]);
//                System.out.println("PANJANG INS: " + strIns.length);
                for(int i = strIns.length-5; i >= 0; i--) {
                    for(int j = 0; j < i;) {
                            if(strIns[j].equals("")){
                                String temp = strIns[j];
                                strIns[j] = strIns[j + 5];
                                strIns[j + 5] = temp;
                            }
                        ++j;
//                            System.out.println("DEBUG INS: "+strIns[j]);
                        }
                }

//                String regexUnit = "\\d{1,2}";
                String regexPack = "[A-Z]";
                for(int i = 0; i < strHeadIns.length; ++i) {
                //    System.out.println(strHead[i] + ":" + strIns[i] +" ");
                    if(strHeadIns[i].contains("INSTPREM")){
                        if(strIns[i].matches("^\\d{1}$|^\\d{2}$") && !strIns[i].equals("0")){
                            strHeadIns[i] = "INSURANCE_UNITPRUMED_"+strHeadIns[i].substring(19);
                            if(strIns[i].length() < 2){
                                strIns[i] = "0" + strIns[i];
                            }
                        }else if(strIns[i].matches(regexPack)){
                            strHeadIns[i] = "INSURANCE_PACKAGE_"+strHeadIns[i].substring(19);
                        }
                        if(isPIAorPSIA==true){ // Condition if Premi zero and Sumins not zero then Value of Sumins fill Premi Value
                            String temp = strIns[i];
                            strIns[i] = strIns[i + 1];
//                            strIns[i + 1] = temp.replace("0", "");  
//                            strIns[i + 1] = strIns[i];               
                        }
                    }
                    if(strHeadIns[i].contains("SUMINS")){
                        if(strIns[i].matches("^\\d{1}$|^\\d{2}$") && !strIns[i].equals("0")){
                            strHeadIns[i] = "INSURANCE_UNITPRUMED_"+strHeadIns[i].substring(17);
                            if(strIns[i].length() < 2){
                                strIns[i] = "0" + strIns[i];
                            }
                        }else if(strIns[i].matches(regexPack)){
                            strHeadIns[i] = "INSURANCE_PACKAGE_"+strHeadIns[i].substring(17);
                        }
                    }
                    tempIns = tempIns + strHeadIns[i] + ":" + strIns[i] + System.lineSeparator();
                }
                
                strHeadFund = headsFund.toArray(new String[headsFund.size()]); 
                strFund = valsFund.toArray(new String[valsFund.size()]);

                for(int i = strFund.length-2; i >= 0; i--) {
                    for(int j = 0; j < i;) {
                        if(strFund[j].equals("N")) {
                            String temp = strFund[j];
                            strFund[j] = strFund[j + 2];
                            strFund[j + 2] = temp;
                        }if(strFund[j].equals("000")) {
                            String temp = strFund[j];
                            strFund[j] = strFund[j + 2];
                            strFund[j + 2] = temp;
                        }
                        ++j;
                    }
                }

                for(int i = 0; i < strHeadFund.length; ++i) {
                //    System.out.println(strHead[i] + ":" + strFund[i] +" ");
                    tempFund = tempFund + strHeadFund[i] + ":" + strFund[i] + System.lineSeparator();
                }
                
                writeToDAT = writeToDAT.replaceFirst("ISI_INSURANCE", tempIns);
                writeToDAT = writeToDAT.replaceFirst("ISI_FUND", tempFund);
                writeToDAT = writeToDAT + "FLAG_SQS_VER:"+ SQS + System.lineSeparator();
                writeToDAT = writeToDAT + getTahun;
                writeToDAT = writeToDAT + "TEMP_FLAG_3:1" + System.lineSeparator();
                writeToDAT = writeToDAT + "Vert_Doc_Type: QUOTATION_BARCODE";

//                System.out.println("==========================");
//                System.out.println(writeToDAT);
                sqsReader.close();

                Path path = Paths.get(datFile);
                Charset charset = StandardCharsets.UTF_8;

                String content = new String(Files.readAllBytes(path), charset);
                String nmFile = new File(imageValue).getName().toString().replace(".tif", "").trim();// Update Sigit get FileName for BARCODE_2D
                content = content.replaceFirst("Vert_Doc_Type: BARCODE_2D_"+nmFile, writeToDAT);
                Files.write(path, content.getBytes(charset));
                boolean replaceOK = content.contains(writeToDAT);
                if(replaceOK==true){
                    ImageWatermark watermark = new ImageWatermark(); 
                    watermark.watermarkImage(new File(imageValue).toString(), true);   //watermark image
                }
                
                deleteBarcodeDataInDB(barcodeFolder); //Delete data on table Barcode
            } catch (FileNotFoundException ex) {
                    System.out.println("Error FileNotFoundException: " + Arrays.deepToString(ex.getStackTrace()));
//                    logs.warning("Error FileNotFoundException: "+Arrays.deepToString(ex.getStackTrace()));
            } catch (IOException ex) {
                    System.out.println("Error IOException: " + Arrays.deepToString(ex.getStackTrace()));
//                    logs.warning("Error IOException: "+Arrays.deepToString(ex.getStackTrace()));
            }
	}
        
        public void parseRPBarcode(String barcodeValue, String barcodeFolder, String imageValue){
        try {
            System.out.println("Parse Risk Pofile Barcode..");
            BufferedReader sqsReader = null;
            String SQSFile = "D:\\template\\Risk_Profile\\template.csv"; //Local
            String datFile = barcodeFolder + "\\TES_" + new File(barcodeFolder).getName().toString() + ".DAT";
            sqsReader = new BufferedReader(new FileReader(SQSFile));
//            System.out.println("Reading SQS file from " + SQS + System.lineSeparator());
            
            String[] SQSfield = new String[250];
            String[] value = new String[250];
            String tempRisk1 = "";
            String tempRisk2 = "";
            String writeToDAT = "";
            String dateQ = "";
            String getTahun = "";
            
            int signRisk1=0;
            int signRisk2=0;
            
            String[] strHeadRisk1 = new String[5];
            String[] strRisk1 = new String[5];
            ArrayList<String> headsRisk1 = new ArrayList<String>();
            ArrayList<String> valsRisk1 = new ArrayList<String>();
            
            
            String[] strHeadRisk2 = new String[5];
            String[] strRisk2 = new String[5];
            ArrayList<String> headsRisk2 = new ArrayList<String>();
            ArrayList<String> valsRisk2 = new ArrayList<String>();
            
            value = barcodeValue.split(";");
            for(int i = 0; i < value.length; i++) {
                SQSfield[i] = sqsReader.readLine();
                if(SQSfield[i].equals("Risk_Barcode_Date_Time_Created_")){				
                     String txt = value[i];
                     String mName[] = new String[]{"","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                     String dayQ = txt.substring(0, 2);
                     String monthQName = txt.substring(3, 6);
                     String monthQ = "";
                     String yearQ = txt.substring(7,11);
                     for(int q = 0; q < mName.length; q++) {
                     if(mName[q].equals(monthQName)){
                         if(q < 10){
                         monthQ = "0" + String.valueOf(q);
                         }else{
                         monthQ = String.valueOf(q);
                         }
                     }
                     }
                     dateQ = yearQ + monthQ + dayQ;

                 writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                 }                
                else if(SQSfield[i].contains("OWNER_MTHINCID")){
                    if(value[i].isEmpty() || value[i].equals("0")){
                        value[i] = "";
                    }
                    headsRisk1.add(SQSfield[i]);
                    valsRisk1.add(value[i]);
                    if(signRisk1 == 0){
                        writeToDAT = writeToDAT + "ISI_RISK1";
                        ++signRisk1;
                    }else{
                        continue;
                    }
                value[i] = value[i].toUpperCase();                        
                }
                else if(SQSfield[i].contains("OWNER_YRINCID")){
                    if(value[i].isEmpty() || value[i].equals("0")){
                        value[i] = "";
                    }
                    headsRisk2.add(SQSfield[i]);
                    valsRisk2.add(value[i]);
                    if(signRisk2 == 0){
                        writeToDAT = writeToDAT + "ISI_RISK2";
                        ++signRisk2;
                    }else{
                        continue;
                    }
                value[i] = value[i].toUpperCase();
                }
                else if(SQSfield[i].equals("RISKPROFILE_RESULT")){
                    writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                    getTahun = "RISKPROFILE_SIGNDATE:" + dateQ + System.lineSeparator();
                    writeToDAT = writeToDAT + getTahun;                    
                }
                else{
//                    getTahun = "RISKPROFILE_SIGNDATE:" + dateQ + System.lineSeparator();
                    writeToDAT = writeToDAT + SQSfield[i] + ":" + value[i] + System.lineSeparator();
                }
            }
            
            strHeadRisk1 = headsRisk1.toArray(new String[headsRisk1.size()]); 
            strRisk1 = valsRisk1.toArray(new String[valsRisk1.size()]);

            for(int i = strRisk1.length-1; i >= 0; i--) {
                for(int j = 0; j < i;) {
                    if(strRisk1[j].equals("")) {
                        String temp = "";
                        strRisk1[j] = strRisk1[j + 1];
                        strRisk1[j + 1] = temp;
                    }
                    ++j;
                }
            }

            for(int i = 0; i < strHeadRisk1.length; ++i) {
            //    System.out.println(strHead[i] + ":" + strFund[i] +" ");
                tempRisk1 = tempRisk1 + strHeadRisk1[i] + ":" + strRisk1[i] + System.lineSeparator();
            }
            
            strHeadRisk2 = headsRisk2.toArray(new String[headsRisk2.size()]); 
            strRisk2 = valsRisk2.toArray(new String[valsRisk2.size()]);

            for(int i = strRisk2.length-1; i >= 0; i--) {
                for(int j = 0; j < i;) {
                    if(strRisk2[j].equals("")) {
                        String temp = "";
                        strRisk2[j] = strRisk2[j + 1];
                        strRisk2[j + 1] = temp;
                    }
                    ++j;
                }
            }

            for(int i = 0; i < strHeadRisk2.length; ++i) {
            //    System.out.println(strHead[i] + ":" + strFund[i] +" ");
                tempRisk2 = tempRisk2 + strHeadRisk2[i] + ":" + strRisk2[i] + System.lineSeparator();
            }            

            writeToDAT = writeToDAT.replaceFirst("ISI_RISK1", tempRisk1);
            writeToDAT = writeToDAT.replaceFirst("ISI_RISK2", tempRisk2);
//            writeToDAT = writeToDAT + getTahun;
//            writeToDAT = writeToDAT + getTahun;
//            writeToDAT = writeToDAT + "TEMP_FLAG_3:1" + System.lineSeparator();
            writeToDAT = writeToDAT + "Vert_Doc_Type: PROFIL_NASABAH_BARCODE";

//            System.out.println("==========================");
//            System.out.println(writeToDAT);
            sqsReader.close();
            
            Path path = Paths.get(datFile);
            Charset charset = StandardCharsets.UTF_8;

            String content = new String(Files.readAllBytes(path), charset);
            String nmFile = new File(imageValue).getName().toString().replace(".tif", "").trim();// Update Sigit get FileName for BARCODE_2D
            content = content.replaceFirst("Vert_Doc_Type: RiskProfile_"+nmFile, writeToDAT);
            Files.write(path, content.getBytes(charset));
            boolean replaceOK = content.contains(writeToDAT);
            if(replaceOK==true){
                System.out.println("REPLACED PROFIL OK !");
                System.out.println();
            }            
        } catch (FileNotFoundException ex) {
                    System.out.println("Error FileNotFoundException: " + Arrays.deepToString(ex.getStackTrace()));
//            logs.warning("Error FileNotFoundException: "+Arrays.deepToString(ex.getStackTrace()));
        } catch (IOException ex) {
                    System.out.println("Error IOException: " + Arrays.deepToString(ex.getStackTrace()));
//            logs.warning("Error IOException: "+Arrays.deepToString(ex.getStackTrace()));
        }
    }

        private static boolean checkDocId(String docid) {
            String line = "";
            BufferedReader docReader = null;
            String DocFile = "D:\\template\\Doc_PIA_PSIA.csv"; //Local
            try {
                docReader = new BufferedReader(new FileReader(DocFile));
                while ((line = docReader.readLine()) != null) {
                        if(docid.equals(line)){
                            return true;
                        }
                }
                docReader.close();                
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
//            String[] arrDocId = {"11012019","11012020","11012021","11012033","11012034","11012035"}; //Old Way
//            for(String list:arrDocId){
//                if(docid.equals(list)){
//                    return true;
//                }
//            }
            return false;
        }         
        
	private static void updateTicket(String ticketNumber) {
		// TODO Auto-generated method stub
        Statement statement;
			try {
				statement = conn.createStatement();
			    String queryString = "UPDATE TICKETDB.dbo.ControlForm SET Status ='3' WHERE ID = '" + ticketNumber + "'";
			    int updateQuery = statement.executeUpdate(queryString);
				} catch (SQLException e) {
//				    JOptionPane.showMessageDialog(null, e.toString(), "Error on updating SQL!",JOptionPane.ERROR_MESSAGE);
                                    e.printStackTrace();
			}
		}


	private void deleteBarcodeDataInDB(String barcodeFolder) {
		// TODO Auto-generated method stub
		try {
                    Statement statement;
                    statement = conn.createStatement();
		    String queryString = "DELETE FROM TICKETDB.dbo.Barcode WHERE Folder = '" + barcodeFolder + "'";
		    int updateQuery = statement.executeUpdate(queryString);
//                    PreparedStatement ps = conn.prepareStatement("delete from TICKETDB.dbo.Barcode where Folder=?");
//                    ps.setString(1, barcodeFolder);
//                    ps.executeUpdate();
		} catch (SQLException e) {
//			    JOptionPane.showMessageDialog(null, e.toString(), "Error on updating SQL!",JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
		}
	}
	
	private boolean checkQuotationIndex(String quotationField) {
		try {
//			BufferedReader barcodeIndexReader = new BufferedReader(new FileReader("\\\\10.0.0.1\\data\\template\\SQS\\QUOTATION_INDEX.csv")); //Server
			BufferedReader barcodeIndexReader = new BufferedReader(new FileReader("D:\\template\\SQS\\QUOTATION_INDEX.csv")); //Local
			String line = "";
			String csvSplitBy = "	";
			
			while ((line = barcodeIndexReader.readLine()) != null) {
				
				String[] value = line.split(csvSplitBy);

				if(quotationField.contains(value[0])) {
					return true;
				}
				
			}
			
			barcodeIndexReader.close();
			
		} catch (FileNotFoundException e) {
                    e.printStackTrace();
		} catch (IOException e) {
                    e.printStackTrace();
		}
		return false;

	}

	private static boolean checkDatafileRequirementFields(String fields) {
		//System.out.println(fields);
		//return false;
		
		String line = "";
		String csvSplitBy = "	";
		String linebarcode = "";
		
		try {
//			BufferedReader datafileReader = new BufferedReader(new FileReader("\\\\10.0.0.1\\data\\template\\datafile-requirement.txt")); //Server
			BufferedReader datafileReader = new BufferedReader(new FileReader("D:\\template\\datafile-requirement.txt")); //Local
			
			try {
				while ((line = datafileReader.readLine()) != null) {
					//System.out.println(line);
					String[] value = line.split(csvSplitBy);
                                        //System.out.println(value[3]);
                                        if(fields.equals(value[3])) {
                                        //if(value[3].contains(fields)){
						datafileReader.close();
						return true;
					}
				}
				datafileReader.close();
			} catch (IOException e) {
                                e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
                            e.printStackTrace();
		}	
		return false;
	}	
}