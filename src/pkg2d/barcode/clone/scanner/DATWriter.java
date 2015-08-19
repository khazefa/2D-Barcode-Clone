package pkg2d.barcode.clone.scanner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.swing.JOptionPane;

public class DATWriter extends BarcodeReader {

	String fileLocation;
	String barcodeValue;
        String[] stampOtherImg = new String[500];
	
	public void generateDAT(String fileLocation, String barcodeValue)  {
		File folder = new File(new File(fileLocation).toPath().toString());
		File[] files = folder.listFiles();
                stampOtherImg = new String[500];
		try {
			FileWriter outFile = new FileWriter(new File(fileLocation) + "\\TES_" + new File(fileLocation).toPath().getFileName() + ".DAT");
			PrintWriter out = new PrintWriter(outFile);
			
			//System.out.println("Write .DAT file at " + new File(fileLocation) + "\\" + new File(fileLocation).toPath().getFileName() + ".DAT");
			
			for (int fileInList = 0; fileInList < files.length; fileInList++) {
				String currentFile = files[fileInList].getName().toString();
				if(files[fileInList].toString().endsWith("tif")) {	//filter file search with TIFs
                                        //Sementara hilangkan fungsi printout SPAJ
					if (Arrays.asList(SPAJImages).contains(currentFile)) {	//write SPAJ line on specified TIF
						out.println("File_Name: " + new File(files[fileInList].toString()).getName().toString());
						out.println("FORM_ID:" + docID[fileInList]);
						if(spajNo[fileInList] == null) {
							out.println("SPAJ_TTMPRCNO:");
						}
						else {
							out.println("SPAJ_TTMPRCNO:"+ spajNo[fileInList]);
						}
						out.println("Vert_Doc_Type: SPAJ_" + docID[fileInList]);
						out.println();
					}
                                        else if (Arrays.asList(quotationImages).contains(currentFile)) {	//write dummy QUOTATION line on specified TIF
						out.println("File_Name: " + new File(files[fileInList].toString()).getName().toString());
                                                String nmFile = new File(files[fileInList].toString()).getName().toString().replace(".tif", "").trim();
						out.println("Vert_Doc_Type: BARCODE_2D_"+nmFile);
						out.println();
                                                stampOtherImg[fileInList+1] = new File(files[fileInList+1].toString()).getName().toString();
					}
					else if (Arrays.asList(stampOtherImg).contains(currentFile)) {	//write OTHERS line on specified TIF
						out.println("File_Name: " + new File(files[fileInList].toString()).getName().toString());
						out.println("Vert_Doc_Type: OTHERS");
						out.println();
					}
                                        else if (Arrays.asList(riskprofImages).contains(currentFile)) {	//write dummy RiskProfile line on specified TIF
						out.println("File_Name: " + new File(files[fileInList].toString()).getName().toString());
                                                String nmFile = new File(files[fileInList].toString()).getName().toString().replace(".tif", "").trim();
						out.println("Vert_Doc_Type: RiskProfile_"+nmFile);
						out.println();
					}
                                        else if (Arrays.asList(SKPKKImages).contains(currentFile)) {	//write SKPKK line on specified TIF
						out.println("File_Name: " + new File(files[fileInList].toString()).getName().toString());
						out.println("FORM_ID:" + docSKPKK[fileInList]);
						out.println("SPAJ_TTMPRCNO:");
						out.println("Vert_Doc_Type: SKPKK");
						out.println();
					}
                                        else if (Arrays.asList(SKPRAImages).contains(currentFile)) {	//write SKPKK line on specified TIF
						out.println("File_Name: " + new File(files[fileInList].toString()).getName().toString());
						out.println("FORM_ID:" + docSKPRA[fileInList]);
						out.println("SPAJ_TTMPRCNO:");
						out.println("Vert_Doc_Type: SKPRA");
						out.println();
					}
                                        else if (Arrays.asList(SKPRBImages).contains(currentFile)) {	//write SKPKK line on specified TIF
						out.println("File_Name: " + new File(files[fileInList].toString()).getName().toString());
						out.println("FORM_ID:" + docSKPRB[fileInList]);
						out.println("SPAJ_TTMPRCNO:");
						out.println("Vert_Doc_Type: SKPRB");
						out.println();
					}
					else if (Arrays.asList(othersImages).contains(currentFile)) {	//write OTHERS line on specified TIF
						out.println("File_Name: " + new File(files[fileInList].toString()).getName().toString());
						out.println("FORM_ID:" + others[fileInList]);
						out.println("Vert_Doc_Type: OTHERS");
						out.println();
					}
					else if (Arrays.asList(SSEPImages).contains(currentFile)) {	//write SSEP line on specified TIF
						out.println("File_Name: " + new File(files[fileInList].toString()).getName().toString());
						out.println("DOCNAME:SSEP");
						out.println("Vert_Doc_Type: SSEP");
						out.println();
					}
					else {							//MISC
						out.println("File_Name: " + currentFile);
						out.println("Vert_Doc_Type: MISC");
						out.println();
					}
				}
			}
			
			out.close();
			
		} catch (IOException e){
//		    JOptionPane.showMessageDialog(null, e.toString(), "Error on generating .DAT file!",JOptionPane.ERROR_MESSAGE);
                    System.out.println("Error on generating .DAT file! "+Arrays.deepToString(e.getStackTrace()));
		    }
	}
}