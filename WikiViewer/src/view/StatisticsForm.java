/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package view;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Search;
import wikiviewer.DatabaseUse;
import wikiviewer.WikiViewer;

/**
 * Φόρμα στατιστικών.
 */
public class StatisticsForm extends javax.swing.JFrame {

    // Τα μοντέλα των δύο πινάκων
    private DefaultTableModel categoriesTableModel;
    private DefaultTableModel searchesTableModel;

    // Οι αποθηκευμένες αναζητήσεις
    private List<Search> searches;

    // Τα στατιστικά κατηγοριών
    private List<Object[]> categoryStatistics;

    /**
     * Δημιουργεί και επιστρέφει ένα εικονίδιο με τις επιθυμητές διαστάσεις.
     * 
     * @param path Η διαδρομή του εικονιδίου.
     * @param w    Το πλάτος του εικονιδίου.
     * @param h    Το ύψος του εικονιδίου.
     * @return Το εικονίδιο με τις νέες διαστάσεις.
     */
    private ImageIcon scaledIcon(String path, int w, int h) {
        Image img = new ImageIcon(MainForm.class.getResource(path)).getImage();
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Constructor της φόρμας στατιστικών.
     */
    public StatisticsForm() {
        initComponents();

        // Αρχικοποιήσεις
        setTitle("Στατιστικά");
        Image pageIcon = new ImageIcon(MainForm.class.getResource("/resources/wikilogo.png")).getImage();
        setIconImage(pageIcon);
        jLabel1.setText("Αποθηκευμένα άρθρα ανά κατηγορία:");
        jLabel2.setText("Στατιστικά αναζητήσεων:");

        jButton1.setText("Δημιουργία PDF");
        jButton1.setIcon(scaledIcon("/resources/file-pdf.png", 16, 16));
        jButton2.setText("Επιστροφή");
        jButton2.setIcon(scaledIcon("/resources/arrow-left.png", 16, 16));
        jButton3.setText("Καθαρισμός στατιστικών αναζητήσεων");
        jButton3.setIcon(scaledIcon("/resources/sweep.png", 16, 16));

        // Δημιουργία μοντέλου για τον πίνακα με τα στατιστικά κατηγοριών
        categoriesTableModel = new DefaultTableModel() {
            // Κάνουμε override αυτή την μέθοδο ώστε να μην μπορούν να τροποποιηθούν τα
            // στοιχεία του πίνακα
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Ορίζουμε τις στήλες του μοντέλου
        categoriesTableModel.addColumn("Κατηγορία");
        categoriesTableModel.addColumn("Αποθηκευμένα άρθρα");

        // Βάζουμε το μοντέλο στον πρώτο πίνακα
        jTable1.setModel(categoriesTableModel);

        // Δημιουργία μοντέλου για τον πίνακα με τα στατιστικά αναζητήσεων
        searchesTableModel = new DefaultTableModel() {
            // Κάνουμε override αυτή την μέθοδο ώστε να μην μπορούν να τροποποιηθούν τα
            // στοιχεία του πίνακα
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Στήλε του μοντέλου
        searchesTableModel.addColumn("Λέξη-κλειδί");
        searchesTableModel.addColumn("Πλήθος αναζητήσεων");

        // Βάζουμε το μοντέλο στον δεύτερο πίνακα
        jTable2.setModel(searchesTableModel);

        // Κεντράρισμα της φόρμας
        setLocationRelativeTo(null);
        // Η φόρμα δεν μπορεί να αλλάξει μέγεθος
        setResizable(false);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    // Λαμβάνει από τη βάση τα στατιστικά κατηγοριών και ενημερώνει τον πρώτο πίνακα
    public void getCategoriesData() {
        categoriesTableModel.setRowCount(0);
        categoryStatistics = DatabaseUse.getCategoryStatistics();
        for (Object[] categoryStatistic : categoryStatistics) {
            categoriesTableModel.addRow(categoryStatistic);
        }
    }

    // Λαμβάνει από τη βάση τις αναζητήσεις και ενημερώνει τον δεύτερο πίνακα
    public void getSearchesData() {
        searchesTableModel.setRowCount(0);
        searches = DatabaseUse.getSearches();
        for (Search search : searches) {
            searchesTableModel.addRow(new Object[] { search.getSearchstring(), search.getNumberofsearches() });
        }
    }

    // Δημιουργεί το αρχείο pdf με τα στατιστικά
    public void createPDF() {
        OutputStream outputStream = null;
        Document document = null;

        try {
            // 1) Ζήτα από τον χρήστη πού να αποθηκευτεί
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Αποθήκευση PDF");
            fileChooser.setSelectedFile(new File("Statistics.pdf"));
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF files", "pdf"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                return; // ο χρήστης πάτησε Cancel
            }

            File pdfFile = fileChooser.getSelectedFile();

            // 2) Εξασφάλισε κατάληξη .pdf
            if (!pdfFile.getName().toLowerCase().endsWith(".pdf")) {
                pdfFile = new File(pdfFile.getAbsolutePath() + ".pdf");
            }

            // (προαιρετικό) Αν υπάρχει ήδη, ρώτα αν θέλει overwrite
            if (pdfFile.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(
                        this,
                        "Το αρχείο υπάρχει ήδη:\n" + pdfFile.getAbsolutePath() + "\n\nΘέλετε αντικατάσταση;",
                        "Επιβεβαίωση αντικατάστασης",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (overwrite != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // 3) Δημιουργία Document / Writer
            document = new Document();
            outputStream = new FileOutputStream(pdfFile);
            PdfWriter.getInstance(document, outputStream);

            // Άνοιγμα εγγράφου
            document.open();

            // Γραμματοσειρά για Ελληνικά
            FontFactory.register("arial.ttf", "Arial");
            Font font = FontFactory.getFont("Arial", "Cp1253", true);

            // Τίτλος 1
            Paragraph par = new Paragraph("Αποθηκευμένα άρθρα ανά κατηγορία:", font);
            par.setAlignment(Element.ALIGN_CENTER);
            document.add(par);
            document.add(new Paragraph("\n"));

            // Πίνακας 1
            PdfPTable table = new PdfPTable(2);
            table.addCell(new Paragraph("Κατηγορία", font));
            table.addCell(new Paragraph("Αποθηκευμένα άρθρα", font));

            for (Object[] categoryStatistic : categoryStatistics) {
                table.addCell(new Paragraph(String.valueOf(categoryStatistic[0]), font));
                table.addCell(new Paragraph(String.valueOf(categoryStatistic[1]), font));
            }
            document.add(table);

            document.add(new Paragraph("\n\n"));

            // Τίτλος 2
            par = new Paragraph("Στατιστικά αναζητήσεων:", font);
            par.setAlignment(Element.ALIGN_CENTER);
            document.add(par);
            document.add(new Paragraph("\n"));

            // Πίνακας 2
            table = new PdfPTable(2);
            table.addCell(new Paragraph("Λέξη-κλειδί", font));
            table.addCell(new Paragraph("Πλήθος αναζητήσεων", font));

            for (Search search : searches) {
                table.addCell(new Paragraph(search.getSearchstring(), font));
                table.addCell(new Paragraph(String.valueOf(search.getNumberofsearches()), font));
            }
            document.add(table);

            // Κλείσιμο
            document.close();
            document = null;

            outputStream.close();
            outputStream = null;

            JOptionPane.showMessageDialog(
                    this,
                    "Δημιουργήθηκε το αρχείο:\n" + pdfFile.getAbsolutePath(),
                    "Επιτυχία αποθήκευσης αρχείου",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Πρόβλημα στην δημιουργία του αρχείου Statistics.pdf\n" + e.getMessage(),
                    "Αποτυχία αποθήκευσης αρχείου",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            // safety close σε περίπτωση που σκάσει πριν το close
            try {
                if (document != null && document.isOpen())
                    document.close();
            } catch (Exception ignored) {
            }
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("jLabel1");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null }
                },
                new String[] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }));
        jTable1.setMaximumSize(new java.awt.Dimension(2147483647, 100));
        jScrollPane1.setViewportView(jTable1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][] {
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null },
                        { null, null, null, null }
                },
                new String[] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }));
        jScrollPane2.setViewportView(jTable2);

        jLabel2.setText("jLabel2");

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("jButton2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("jButton3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(12, 22, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jButton3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton2))
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 638,
                                                Short.MAX_VALUE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 354,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jScrollPane1)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING,
                                                javax.swing.GroupLayout.PREFERRED_SIZE, 220,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(21, 21, 21)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 184,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 184,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2)
                                        .addComponent(jButton3))
                                .addContainerGap(27, Short.MAX_VALUE)));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Δημιουργεί το αρχείο pdf
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        createPDF();
    }// GEN-LAST:event_jButton1ActionPerformed

    // Μετάβαση στην αρχική φόρμα
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
        WikiViewer.mainForm.setVisible(true);
        this.dispose();
    }// GEN-LAST:event_jButton2ActionPerformed
     // Καθαρισμός ΜΟΝΟ των αναζητήσεων

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton3ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Είστε σίγουροι ότι θέλετε μηδενίσετε τους μετρητές αναζήτησης;",
                "Επιβεβαίωση Μηδενισμού",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseUse.clearStatistics();
            getSearchesData();
            getCategoriesData();
        }
    }// GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
