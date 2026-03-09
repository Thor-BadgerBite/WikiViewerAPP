/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package view;

import java.awt.Image;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.Article;
import model.Category;
import wikiviewer.DatabaseUse;
import wikiviewer.Helpers;

/**
 * Φόρμα προβολής επιλεγμένου άρθρου.
 */
public class ArticleDetailsForm extends JFrame {

    // Δήλωση έτσι ώστε να ξέρουμε από ποια φόρμα άνοιξε
    private final JFrame parentForm;

    // Το άρθρο που επιλέχτηκε
    private Article selectedArticle;

    // Το άρθρο το οποίο αποθηκεύτηκε
    private Article storedArticle;

    // Λίστα με τις κατηγορίες των άρθρων
    private final List<Category> categories;

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
     * Constructor της φόρμας προβολής άρθρου.
     * 
     * @param selectedArticle Το επιλεγμένο άρθρο προς προβολή.
     * @param parentForm      Η φόρμα από την οποία άνοιξε.
     */
    public ArticleDetailsForm(Article selectedArticle, JFrame parentForm) {
        initComponents();

        // Αρχικοποιήσεις
        Image pageIcon = new ImageIcon(MainForm.class.getResource("/resources/wikilogo.png")).getImage();
        setIconImage(pageIcon);
        this.parentForm = parentForm;
        this.selectedArticle = selectedArticle;
        this.storedArticle = DatabaseUse.getArticle(selectedArticle.getPageid());
        this.categories = DatabaseUse.getCategories();
        jTextPane1.setText(selectedArticle.getContent());
        jTextPane1.setCaretPosition(0);
        jTextPane1.setEditable(false);
        jButton1.setText("Αποθήκευση");
        jButton1.setIcon(scaledIcon("/resources/disk.png", 16, 16));
        jButton2.setText("Επιστροφή");
        jButton2.setIcon(scaledIcon("/resources/arrow-left.png", 16, 16));

        jLabel1.setText("Βαθμολογία");
        jLabel2.setText("Κατηγορία");
        jLabel3.setText("Σχόλια");
        jLabel4.setText("");

        // Βάζω στον comboBox της βαθμολογίας. της τιμές που θέλω να μπορεί να βάλει ο
        // χρήστης
        jComboBox1.removeAllItems();
        jComboBox1.addItem("-");
        for (int i = 1; i <= 5; i++) {
            jComboBox1.addItem(String.valueOf(i));
        }

        // Βάζω στο comboBox τις κατηγορίες που έχω στην DataBase
        jComboBox2.removeAllItems();
        jComboBox2.addItem("-");
        for (Category category : categories) {
            jComboBox2.addItem(category.getName());
        }

        // Εφόσον το επιλεγμένο άρθρο είναι αποθηκευμένο ενημερώνουμε τα στοιχεία της
        // φόρμας (σχόλια, βαθμολογία, κατηγορία, ημερομηνία/ώρα αποθήκευσης)
        if (storedArticle != null) {
            updateFormWithStoredArticle();
        }

        // Τίτλος φόρμας
        setTitle("Άρθρο " + selectedArticle.getPageid() + " - " + selectedArticle.getTitle() + " ("
                + Helpers.getFormattedTimestamp(selectedArticle.getTimestamp()) + ")");
        // Κεντράρισμα της φόρμας
        setLocationRelativeTo(null);
        // Η φόρμα δεν μπορεί να αλλάξει μέγεθος έτσι ώστε να μην χαλάει η γεωμετρία της
        setResizable(false);
        // Επειδή η φόρμα ανοίγει μέσα από την κεντρική φόρμα, απενεργοποιούμε την
        // λειτουργία του Χ στο παράθυρο έτσι ώστε να χρησιμοποιείται μόνο το κουμπί
        // επιστροφή και
        // να κλείνει το πρόγραμμα μόνο από την κετρική φόρμα
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Ενημερώνει το άρθρο με τα επιπλέον στοιχεία της φόρμας (σχόλια, βαθμολογία,
     * κατηγορία).
     * 
     * @param article Το άρθρο προς ενημέρωση.
     */
    public void updateArticleWithFormValues(Article article) {
        // Ενημέρωση σχολίων (μέχρι 1000 χαρακτήρες)
        String comments = Helpers.getString(jTextArea1.getText(), 1000);
        article.setComments(comments);

        // Ενημέρωση βαθμολογίας
        Integer selectedRating = (jComboBox1.getSelectedIndex() > 0)
                ? jComboBox1.getSelectedIndex()
                : null;
        article.setRating(selectedRating);

        // Ενημέρωση κατηγορίας
        String selectedCategoryName = (jComboBox2.getSelectedIndex() > 0)
                ? (String) jComboBox2.getSelectedItem()
                : null;
        Category selectedCategory = DatabaseUse.getCategory(selectedCategoryName);
        article.setCategoryid(selectedCategory);
    }

    /**
     * Ενημερώνει τα στοιχεία της φόρμας από τα στοιχεία του αποθηκευμένου άρθρου.
     */
    private void updateFormWithStoredArticle() {
        // Ενημέρωση του επιλεγμενου άρθρου
        selectedArticle.setComments(storedArticle.getComments());
        selectedArticle.setCategoryid(storedArticle.getCategoryid());
        selectedArticle.setRating(storedArticle.getRating());

        // Ενημέρωση σχολίων
        jTextArea1.setText(storedArticle.getComments());

        // Ενημέρωση βαθμολογίας
        int storedRatingIndex = (storedArticle.getRating() != null)
                ? storedArticle.getRating()
                : 0;

        // Ενημέρωση κατηγορίας
        jComboBox1.setSelectedIndex(storedRatingIndex);
        if (storedArticle.getCategoryid() == null) {
            jComboBox2.setSelectedIndex(0);
        } else {
            jComboBox2.setSelectedItem(storedArticle.getCategoryid().getName());
        }

        // Ενημέρωση ετικέτας ημερομηνίας/ώρας αποθήκευσης
        updateSavedAt();
    }

    // Ενημέρωση της ετικέτας με την ημερομηνία/ώρα αποθήκευσης του άρθρου
    private void updateSavedAt() {
        jLabel4.setText(
                "Αποθηκευμένο στη βάση δεδομένων: " + Helpers.getFormattedTimestamp(storedArticle.getSavedat()));
    }

    // Αποθηκεύει το άρθρο
    public void storeArticle() {
        // Επιλογή του άρθρου που θα αποθηκευτεί (αν υπάρχει ήδη στη βάση επιλέγουμε
        // αυτό)
        Article articleToStore = (storedArticle != null)
                ? storedArticle
                : selectedArticle;

        // Ενημέρωση του άρθρου με τα επιπλέον στοιχεία από την φόρμα (βαθμολογία,
        // κατηγορία, σχόλια)
        updateArticleWithFormValues(articleToStore);

        // Αποθηκεύουμε το άρθρο
        storedArticle = DatabaseUse.storeArticle(articleToStore);
        // Ενημερώνουμε την ετικέτα με την ημερομηνία/ώρα αποθήκευσης του άρθρου
        updateSavedAt();

        // Εμφάνιση μηνύματος αποθήκευσης άρθρου
        JOptionPane.showMessageDialog(
                this,
                "Το άρθρο αποθηκεύτηκε",
                "Αποθήκευση άρθρου",
                JOptionPane.INFORMATION_MESSAGE);
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

        jScrollPane2 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane2.setViewportView(jTextPane1);

        jButton1.setText("jButton1");
        jButton1.setMaximumSize(new java.awt.Dimension(160, 23));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("jButton2");
        jButton2.setMaximumSize(new java.awt.Dimension(160, 23));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setText("jLabel3");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setText("jLabel1");

        jComboBox1.setModel(
                new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("jLabel2");

        jComboBox2.setModel(
                new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("jLabel4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 160,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(17, 17, 17)
                                                        .addGroup(layout
                                                                .createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addGroup(layout.createSequentialGroup()
                                                                        .addGroup(layout.createParallelGroup(
                                                                                javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jLabel3,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                        53,
                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGroup(layout.createSequentialGroup()
                                                                                        .addComponent(jLabel1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                87,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addPreferredGap(
                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                        .addComponent(jComboBox1,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                        .addGap(46, 46, 46)
                                                                        .addComponent(jLabel2,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                72,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                        .addComponent(jComboBox2,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                113,
                                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addComponent(jScrollPane2,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 983,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jButton1,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 160,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addComponent(jLabel4,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 399,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(88, 88, 88)
                                                        .addComponent(jScrollPane1,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 912,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(17, Short.MAX_VALUE)));

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] { jButton1, jButton2 });

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 478,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2)
                                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addGap(0, 68, Short.MAX_VALUE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0,
                                                Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(33, 33, 33)));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Αποθήκευση άρθρου
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        storeArticle();
    }// GEN-LAST:event_jButton1ActionPerformed

    // Μετάβαση στην προηγούμενη φόρμα
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
        this.dispose();
        parentForm.setVisible(true);
    }// GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
