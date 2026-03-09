/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package view;

import java.awt.Image;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import wikiviewer.ApiData;
import model.Article;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import wikiviewer.DatabaseUse;
import wikiviewer.Helpers;
import wikiviewer.WikiViewer;

/**
 * Φόρμα αναζήτησης άρθρων στο API.
 */
public class SearchForm extends javax.swing.JFrame {

    // Ότι δεδομένα μας επιστρέφει το Api
    private ApiData apiData;

    // Δήλωση buffer για την προηγούμενη αναζήτηση
    private String previousSearchString;

    // Λίστα με την αναζήτηση
    private List<Article> currentPageArticles;

    // Το άρθρο που επιλέχτηκε από τον χρήστη
    private Article selectedArticle;

    // Το μοντέλο της λίστας
    private final DefaultListModel<String> listModel;

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
     * Constructor της φόρμας αναζήτησης.
     */
    public SearchForm() {
        initComponents();

        // Αρχικοποιήσεις
        // Βάζουμε αυτήν την γραμμή έτσι ώστε στην αναζήτηση αν κάποιος πατήσει και
        // enter να εκτελεί την αναζήτηση
        jTextField1.addActionListener(evt -> jButton1.doClick());
        Image pageIcon = new ImageIcon(MainForm.class.getResource("/resources/wikilogo.png")).getImage();
        setIconImage(pageIcon);
        setTitle("Αναζήτηση στο API");
        previousSearchString = "";
        apiData = null;
        selectedArticle = null;
        currentPageArticles = null;
        jLabel1.setText("Αναζήτηση για:");
        jLabel2.setText(null);
        jButton1.setText("Αναζήτηση");
        jButton1.setIcon(scaledIcon("/resources/search.png", 16, 16));
        jButton2.setText("Καθαρισμός");
        jButton2.setIcon(scaledIcon("/resources/sweep.png", 16, 16));
        jButton2.setHorizontalTextPosition(SwingConstants.RIGHT);
        jButton3.setText("Προηγούμενα");
        jButton3.setIcon(scaledIcon("/resources/angle-left.png", 16, 16));
        jButton3.setEnabled(false);
        jButton4.setText("Επόμενα");
        jButton4.setIcon(scaledIcon("/resources/angle-right.png", 16, 16));
        jButton4.setHorizontalTextPosition(SwingConstants.LEFT);
        jButton4.setEnabled(false);
        jButton5.setText("Προβολή επιλεγμένου άρθρου");
        jButton5.setIcon(scaledIcon("/resources/eye.png", 16, 16));
        jButton6.setText("Επιστροφή");
        jButton6.setIcon(scaledIcon("/resources/arrow-left.png", 16, 16));
        jTextField1.setText("");

        // Κεντράρισμα της φόρμας
        setLocationRelativeTo(null);
        // Η φόρμα δεν μπορεί να αλλάξει μέγεθος
        setResizable(false);
        // Η φόρμα δεν μπορεί να κλείσει ώστε να μην κλείνει η εφαρμογή
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Βάζουμε το μοντέλο στη λίστα
        listModel = new DefaultListModel<>();
        jList1.setModel(listModel);

        // Για να μπορεί ο χρήστης να επιλέγει μόνο ένα άρθρο την φορά
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Επιστρέφει την αναζήτηση που έχει εισάγει ο χρήστης στο jTextField
     * (χωρίς επιπλέον κενά και μέχρι 100 χαρακτήρες).
     * 
     * @return Η συμβολοσειρά αναζήτησης.
     */
    public String getSearchString() {
        // Αφαίρεση επιπλέον κενών με Regex
        String searchString = jTextField1.getText().trim().replaceAll("\\s+", " ");
        // Αποθηκεύουμε μόνο τους πρώτους 100 χαρακτήρες
        searchString = Helpers.getString(searchString, 100);
        // Ενημερώνουμε την συμβολοσειρά αναζήτησης και την επιστρέφουμε
        jTextField1.setText(searchString);
        return searchString;
    }

    /**
     * Εμφάνιση των άρθρων της τρέχουσας σελίδας.
     */
    private void showCurrentPageArticles() {
        // Βάζουμε στην λίστα τα αποτελέσματα της τρέχουσας σελίδας αποτελεσμάτων
        if (currentPageArticles != null && !currentPageArticles.isEmpty()) {
            setListArticles(currentPageArticles);
        } else {
            listModel.clear();
        }

        // Ενημερώνουμε την ετικέτα με τα αποτελέσματα της τρέχουσας σελίδας
        if (apiData.getCurrentPageResults() > 0) {
            int currentPageFrom = (apiData.getCurrentPage() - 1) * apiData.getLimit() + 1;
            int currentPageTo = currentPageFrom + apiData.getCurrentPageResults() - 1;
            int total = apiData.getTotalResults();
            jLabel2.setText("Αρθρα " + currentPageFrom + " - " + currentPageTo + " από " + total);
        } else {
            jLabel2.setText("Δεν βρέθηκαν άρθρα");
        }

        // Διαχείρηση κουμπιών Προηγούμενο/Επόμενο
        jButton3.setEnabled(apiData.hasPrevious());
        jButton4.setEnabled(apiData.hasNext());

        // Ενημέρωση της προηγούμενης συμβολοσειράς αναζήτησης
        previousSearchString = getSearchString();
    }

    /**
     * Πραγματοποιεί την αναζήτηση για την συγκεκριμένη συμβολοσειρά αναζήτησης.
     * 
     * @param searchString Η συμβολοσειρά αναζήτησης.
     */
    public void search(String searchString) {

        // Guard ώστε να μην γίνει αναζήτηση εάν το jTextField είναι κενό
        if (searchString.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Δεν έχετε επιλέξει λέξη-κλειδί για αναζήτηση",
                    "Αναζήτηση άρθρου",
                    JOptionPane.INFORMATION_MESSAGE);
            jTextField1.setText(previousSearchString);
            return;
        }
        if (searchString.equals(previousSearchString)) {
            return;
        }

        // Λαμβάνουμε την τρέχουσα σελίδα δεδομένων του API
        apiData = new ApiData(searchString);

        // Ενημερώνουμε τον πίνακα αναζητήσεων με την νέα αναζήτηση
        DatabaseUse.storeSearch(searchString);

        // Εμφανίζουμε τα άρθρα της τρέχουσας σελίδας
        currentPageArticles = apiData.getPageArticles();
        showCurrentPageArticles();
    }

    /**
     * Εμφάνιση των αποτελεσμάτων της προηγούμενης σελίδας.
     */
    public void previousPage() {
        if (apiData.hasPrevious()) {
            currentPageArticles = apiData.getPreviousPageArticles();
            showCurrentPageArticles();
        }
    }

    /**
     * Εμφάνιση των αποτελεσμάτων της επόμενης σελίδας.
     */
    public void nextPage() {
        if (apiData.hasNext()) {
            currentPageArticles = apiData.getNextPageArticles();
            showCurrentPageArticles();
        }
    }

    /**
     * Τοποθετεί στην λίστα τα δεδομένα των άρθρων της τρέχουσας σελίδας
     * αποτελεσμάτων.
     * 
     * @param articles Η λίστα άρθρων προς εμφάνιση.
     */
    public void setListArticles(List<Article> articles) {
        listModel.clear();
        for (Article article : articles) {
            listModel.addElement(article.toString());
        }
    }

    /**
     * Ανοίγει νέα φόρμα και συμπληρώνει τα στοιχεία του επιλεγμένου άρθρου.
     */
    public void showSelectedArticleDetails() {
        int indexSelected = jList1.getSelectedIndex();
        if (indexSelected != -1) {
            // Εφόσον έχει επιλεγεί άρθρο το λαμβάνουμε από το API
            selectedArticle = currentPageArticles.get(indexSelected);

            // Παίρνουμε και το πλήρες κείμενο του άρθρου από το API
            String content = apiData.getPageContents(selectedArticle.getPageid());
            selectedArticle.setContent(content);

            // Κλείσιμο της φόρμας αναζήτησης και εμφάνιση την φόρμας προβολής του
            // επιλεγμένου άρθρου
            this.dispose();
            ArticleDetailsForm articleDetailsForm = new ArticleDetailsForm(selectedArticle, this);
            articleDetailsForm.setVisible(true);

        } else {
            // Guard ώστε να μην γίνει αναζήτηση εάν δεν έχει επιλεγεί άρθρο
            JOptionPane.showMessageDialog(
                    this,
                    "Δεν έχετε επιλέξει άρθρο",
                    "Προβολή άρθρου",
                    JOptionPane.INFORMATION_MESSAGE);
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
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("jLabel1");

        jTextField1.setText("jTextField1");

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("jLabel2");

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };

            public int getSize() {
                return strings.length;
            }

            public String getElementAt(int i) {
                return strings[i];
            }
        });
        jScrollPane1.setViewportView(jList1);

        jButton2.setText("jButton2");
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
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

        jButton4.setText("jButton4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("jButton5");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("jButton6");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(59, 59, 59)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jButton3)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jLabel2,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE, 477,
                                                                        Short.MAX_VALUE)
                                                                .addGap(543, 543, 543)
                                                                .addComponent(jButton4))
                                                        .addComponent(jScrollPane1,
                                                                javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jButton5,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 260,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(jButton6,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 120,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(50, 50, 50))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 87,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 372,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 120,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 120,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 27,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton2)
                                        .addComponent(jButton1))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(jButton3)
                                        .addComponent(jButton4))
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton5)
                                        .addComponent(jButton6))
                                .addGap(46, 46, 46)));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Πλήκτρο αναζήτησης. Κάνει ανάκτηση της πρώτης σελίδας αποτελεσμάτων από το
    // API
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        String searchString = getSearchString();
        search(searchString);
    }// GEN-LAST:event_jButton1ActionPerformed

    // Καθαρισμός κειμένου αναζήτησης
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
        jTextField1.setText("");
    }// GEN-LAST:event_jButton2ActionPerformed

    // Μετάβαση στην προηγούμενη σελίδα αποτελεσμάτων
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton3ActionPerformed
        previousPage();
    }// GEN-LAST:event_jButton3ActionPerformed

    // Μετάβαση στην επόμενη σελίδα αποτελεσμάτων
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton4ActionPerformed
        nextPage();
    }// GEN-LAST:event_jButton4ActionPerformed

    // Μετάβαση στην φόρμα προβολής του επιλεγμένου άρθρου
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton5ActionPerformed
        showSelectedArticleDetails();
    }// GEN-LAST:event_jButton5ActionPerformed

    // Επιστροφή στην αρχική οθόνη
    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton6ActionPerformed
        this.dispose();
        WikiViewer.mainForm.setVisible(true);
    }// GEN-LAST:event_jButton6ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
