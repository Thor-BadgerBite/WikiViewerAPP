package view;

/**
 * Splash Screen της εφαρμογής.
 * Εμφανίζεται για 4 δευτερόλεπτα κατά την εκκίνηση.
 */
public class SplashForm extends javax.swing.JFrame {

        public SplashForm() {
                initComponents();
                setSize(600, 335); // Ρυθμίζουμε το μέγεθος (επειδή βάλαμε Null Layout)
                setLocationRelativeTo(null); // Κεντράρισμα ΑΦΟΥ έχει πάρει το σωστό μέγεθος
        }

        @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
        // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();

        jLabel4.setText("jLabel4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImages(null);
        setUndecorated(true);
        getContentPane().setLayout(null);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("<html><center><h1 style=\"color: white; margin-bottom: 0px;\">WikiViewer</h1><h3 style=\"color: white; margin-top: 5px;\">Η εγκυκλοπαίδεια στον υπολογιστή σας</h3></center></html> ");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(10, 10, 580, 70);

        jLabel2.setText("<html><ul style=\"color: white; font-size: 12px; font-weight: bold;\"><li>Ανδριόπουλος Χρήστος</li><li>Καραγιάννης Ιωάννης</li><li>Δεμισαρλής Θωμάς</li></ul></html> ");
        jLabel2.setToolTipText("");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(-10, 190, 300, 110);

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("<html><i style=\"color: white;\">Φόρτωση δεδομένων...</i></html> ");
        jLabel3.setToolTipText("");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(10, 300, 580, 16);

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/splash.jpg"))); // NOI18N
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        getContentPane().add(jLabel6);
        jLabel6.setBounds(0, 0, 600, 340);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    // End of variables declaration//GEN-END:variables
}
