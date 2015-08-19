/*
 * Good code is its own best documentation.
 * As you're about to add a comment, ask yourself,
 * "How can I improve the code so that this comment isn't needed?"
 * Improve the code and then document it to make it even clearer.
 * -Steve McConnell 
 */
package pkg2d.barcode.clone;

import java.io.File;
import javax.swing.JFileChooser;
import pkg2d.barcode.clone.parser.BarcodeParser;
import pkg2d.barcode.clone.parser.Executor;
import pkg2d.barcode.clone.scanner.BarcodeReader;

/**
 *
 * @author Sigit P
 */
public class MainView extends javax.swing.JFrame {
JFileChooser chooser;
String choosertitle;
File locationNow;
private static final boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );
    /**
     * Creates new form MainView
     */
    public MainView() {
        initComponents();
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        bScan = new javax.swing.JButton();
        bParse = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("2D BARCODE MANUAL");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pkg2d/barcode/clone/logobar.jpg"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Monospaced", 1, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("2D BARCODE MANUAL");
        jLabel2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        bScan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pkg2d/barcode/clone/barscanner.png"))); // NOI18N
        bScan.setText("SCAN");
        bScan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bScan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bScanActionPerformed(evt);
            }
        });

        bParse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pkg2d/barcode/clone/barparser.png"))); // NOI18N
        bParse.setText("PARSE");
        bParse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bParse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bParse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bParseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(bScan)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bParse))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jLabel1)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bScan)
                    .addComponent(bParse))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bScanActionPerformed
        // TODO add your handling code here:
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle(choosertitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //    
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
            System.out.println("getCurrentDirectory(): "
               +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " 
               +  chooser.getSelectedFile());
            String location = chooser.getSelectedFile().getPath().toString();
            locationNow = chooser.getSelectedFile();
            BarcodeReader reader = new BarcodeReader();
            reader.start(location);
          }
        else {
            System.out.println("No Selection ");
          }
    }//GEN-LAST:event_bScanActionPerformed

    private void bParseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bParseActionPerformed
        // TODO add your handling code here:
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(locationNow);
        chooser.setDialogTitle(choosertitle);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //    
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
            System.out.println("getCurrentDirectory(): "
               +  chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : " 
               +  chooser.getSelectedFile());
            String location = chooser.getSelectedFile().getPath().toString();
//            String location = chooser.getCurrentDirectory().getPath().toString();
            BarcodeParser parser = new BarcodeParser();
            parser.start(location);
          }
        else {
            System.out.println("No Selection ");
          }        
    }//GEN-LAST:event_bParseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainView.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainView().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bParse;
    private javax.swing.JButton bScan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
