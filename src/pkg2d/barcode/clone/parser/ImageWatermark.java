package pkg2d.barcode.clone.parser;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.mortennobel.imagescaling.ResampleOp;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;


public class ImageWatermark{
	
    //Define var for dpi properties
    static int XRES_TAG = 282;
    static int YRES_TAG = 283;
    static long[] resolution = { 300, 1 };
	
	public void watermarkImage(String fileLocation, boolean scanResult) {
		try {
			
			File file = new File(fileLocation);
			
            if (!file.exists()) {
                    System.out.println("File not Found");			//file not found exception
            } 	
            
            ImageIcon icon = new ImageIcon(file.getPath());
            BufferedImage bufferedImage = ImageIO.read(file);

            Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
            g2d.drawImage(icon.getImage(), 1, 1, null);
            g2d.setColor(Color.BLACK);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                           RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setFont(new Font("Courier New", Font.BOLD, 70));

            String watermark;
    		if(scanResult==true){
    			watermark = "OK";   //draw check mark if success.

                FontMetrics fontMetrics = g2d.getFontMetrics();
                Rectangle2D rect = fontMetrics.getStringBounds(watermark, g2d);
                g2d.drawString(watermark, (icon.getIconWidth() - (int) rect
                                .getWidth()) / 2, (icon.getIconHeight() - (int) rect
                                .getHeight()) / 2);
               
                g2d.drawString(watermark, icon.getIconWidth()+40,icon.getIconHeight()+90);
                
                TIFFField xRes = new TIFFField(XRES_TAG,
                TIFFField.TIFF_RATIONAL, 1, new long[][] { resolution });
                TIFFField yRes = new TIFFField(YRES_TAG,
                TIFFField.TIFF_RATIONAL, 1, new long[][] { resolution });
                TIFFEncodeParam param = new TIFFEncodeParam();
                
                param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);
                param.setExtraFields(new TIFFField[]{xRes, yRes});
                
                JAI.create("filestore",bufferedImage,fileLocation,"TIFF",param);
                
    		}
    		else {
    			watermark = "";
    		}

		}
		catch (IOException ioe) {
		    JOptionPane.showMessageDialog(null, ioe.toString(), "Error on watermarking image!",
                    JOptionPane.ERROR_MESSAGE);
        }
    
}
}
