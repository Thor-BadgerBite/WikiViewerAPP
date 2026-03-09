/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package view;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.Article;
import model.Category;
import wikiviewer.DatabaseUse;
import wikiviewer.Helpers;
import wikiviewer.WikiViewer;

/**
 * Φόρμα αποθηκευμένων άρθρων.
 */
public class StoredArticlesForm extends JFrame {

    // Η λίστα με τις κατηγορίες
    private List<Category> categories;

    // Η λίστα με τα αποθηκευμένα άρθρα
    private List<Article> articles;

    // Το επιλεγμένο άρθρο
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
     * Constructor της φόρμας αποθηκευμένων άρθρων.
     */
    public StoredArticlesForm() {
        initComponents();

        // Αρχικοποιήσεις
        Image pageIcon = new ImageIcon(MainForm.class.getResource("/resources/wikilogo.png")).getImage();
        setIconImage(pageIcon);
        this.categories = DatabaseUse.getCategories();
        jLabel1.setText("Κατηγορία:");
        jLabel2.setText("Αναζήτηση με λέξη-κλειδί:");
        jTextField1.setText("");
        jButton1.setText("Καθαρισμός");
        jButton1.setIcon(scaledIcon("/resources/sweep.png", 16, 16));
        jButton2.setText("Προβολή επιλεγμένου άρθρου");
        jButton2.setIcon(scaledIcon("/resources/eye.png", 16, 16));
        jButton3.setText("Επιστροφή");
        jButton3.setIcon(scaledIcon("/resources/arrow-left.png", 16, 16));
        jButton4.setText("Διαγραφή άρθρου");
        jButton4.setIcon(scaledIcon("/resources/trash.png", 16, 16));

        // Εισαγωγή κατηγοριών στο combo box
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Όλες");
        for (Category category : categories) {
            jComboBox1.addItem(category.getName());
        }

        // Θέτουμε το μοντέλο στη λίστα
        listModel = new DefaultListModel<>();
        jList1.setModel(listModel);

        // Ο χρήστης μπορεί να επιλέξει μόνο ένα άρθρο από την λίστα
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Προσθήκη listener στο combo box κατηγοριών
        jComboBox1.addActionListener(new ActionListener() {
            // Όταν αλλάζει η κατηγορία προβάλλονται τα άρθρα της κατηγορίας
            @Override
            public void actionPerformed(ActionEvent e) {
                jTextField1.setText("");
                articles = getArticles();
                showArticles();
            }
        });

        // Προσθήκη listener στο πεδίο κειμένου αναζήτησης
        jTextField1.getDocument().addDocumentListener(new DocumentListener() {
            // Όταν αλλάζει το κείμενο αναζήτησης προβάλλονται τα άρθρα που έχουν στον τίτλο
            // ή στο κείμενο τους το κείμενο της αναζήτησης
            private void textChanged() {
                articles = getArticles();
                String searchString = jTextField1.getText().trim();
                if (!searchString.isEmpty()) {
                    search(searchString);
                } else {
                    showArticles();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                textChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        // Τίτλος φόρμας
        setTitle("Αποθηκευμενα άρθρα");
        // Κεντράρισμα της φόρμας
        setLocationRelativeTo(null);
        // Η φόρμα δεν μπορεί να αλλάξει μέγεθος
        setResizable(false);
        // Η φόρμα δεν μπορεί να κλείσει ώστε να μην κλείνει η εφαρμογή
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    // Επιστρέφει τη λίστα άρθρων με βάση την επιλεγμένη κατηγορία
    private List<Article> getArticles() {
        // Βρίσκουμε την επιλεγμένη κατηγορία (θα επιστραφεί null αν ο χρήστης επιλέξει
        // "όλες")
        String selectedCategoryName = jComboBox1.getSelectedIndex() != 0
                ? (String) jComboBox1.getSelectedItem()
                : null;
        Category selectedCategory = DatabaseUse.getCategory(selectedCategoryName);

        // Και με βάση την κατηγορία παίρνουμε από την βάση τα άρθρα της συγκεκριμένης
        // κατηγορίας
        // Αν ο χρήστης έχει επιλέξει "όλες" παίρνουμε όλα τα άρθρα
        if (selectedCategory != null) {
            articles = DatabaseUse.getArticlesForCategory(selectedCategory);
        } else {
            articles = DatabaseUse.getArticles();
        }
        return articles;
    }

    // Ενημερώνει την λίστα με τα άρθρα
    public void updateArticles() {
        articles = getArticles();
        showArticles();
    }

    // Κάνει αναζήτηση στα άρθρα που προβάλονται στην λιστα
    public void search(String searchString) {
        searchString = Helpers.normalizeForSearch(searchString);
        List<Article> filteredArticles = new ArrayList<>();
        for (Article article : articles) {
            String title = Helpers.normalizeForSearch(article.getTitle());
            String content = Helpers.normalizeForSearch(article.getContent());
            if (title.contains(searchString) || content.contains(searchString)) {
                filteredArticles.add(article);
            }
        }
        articles = filteredArticles;
        // Εμφανίζουμε τα άρθρα που βρέθηκαν στην αναζήτηση
        showArticles();
    }

    // Ενημερώνει την λίστα με τα δεδομένα των αποθηκευμένων άρθρων
    private void showArticles() {
        listModel.clear();
        for (Article article : articles) {
            listModel.addElement(article.toString());
        }
    }

    // Εμφανίζει τα στοιχεία του επιλεγμένου άρθρου στη φόρμα προβολής του
    // επιλεγμένου άρθρου
    public void showSelectedArticleDetails() {
        // Το index του επιλεγμένου άρθρου στη λίστα
        int indexSelected = jList1.getSelectedIndex();

        // Εφόσον έχει γίνει επιλογή άρθρου
        if (indexSelected != -1) {
            // Λαμβάνουμε το επιλεγμένο άρθρο
            selectedArticle = articles.get(indexSelected);

            // Κλείνουμε την φόρμα και εμφανίζουμε την φόρμα προβολής του επιλεγμένου άρθρου
            this.dispose();
            ArticleDetailsForm articleDetailsForm = new ArticleDetailsForm(selectedArticle, this);
            articleDetailsForm.setVisible(true);
        } else {
            // Εμφανίζουμε μήνυμα αν δεν έχει επιλεγεί άρθρο
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
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("jLabel1");

        jComboBox1.setModel(
                new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField1.setText("jTextField1");

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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

        jLabel2.setText("jLabel2");

        jButton4.setText("jButton4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout
                                                        .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
                                                                false)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel1,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 74,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jComboBox1,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 134,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)
                                                                .addComponent(jLabel2,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 151,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(
                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jTextField1,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 372,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(jButton1,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE, 134,
                                                                        javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jScrollPane1,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE, 1194,
                                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addContainerGap(28, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 235,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 235,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                        javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 136,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(37, 37, 37)))));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                                javax.swing.GroupLayout.DEFAULT_SIZE,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 27,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton1)
                                        .addComponent(jLabel2))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44,
                                        Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
                                                javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton3)
                                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
                                                javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(25, 25, 25)));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Καθαρισμός κειμένου αναζήτησης
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        jTextField1.setText("");
    }// GEN-LAST:event_jButton1ActionPerformed

    // Μετάβαση στην φόρμα προβολής του επιλεγμένου άρθρου
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton2ActionPerformed
        showSelectedArticleDetails();
    }// GEN-LAST:event_jButton2ActionPerformed

    // Μετάβαση στην αρχική φόρμα
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton3ActionPerformed
        this.dispose();
        WikiViewer.mainForm.setVisible(true);
    }// GEN-LAST:event_jButton3ActionPerformed

    // Διαγραφή άρθρου από την βάση
    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton4ActionPerformed
        int indexSelected = jList1.getSelectedIndex();

        if (indexSelected == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Δεν έχετε επιλέξει άρθρο",
                    "Διαγραφή άρθρου",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Πάρε το άρθρο που επιλέχθηκε
        Article articleToDelete = articles.get(indexSelected);

        // Ερώτηση αν θέλει ο χρήστης να διαγραφεί το άρθρο
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Είστε σίγουροι ότι θέλετε να διαγράψετε το άρθρο;",
                "Επιβεβαίωση Διαγραφής",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        // Αν πατήσει ΝΑΙ
        if (confirm == JOptionPane.YES_OPTION) {

            boolean deleted = DatabaseUse.deleteArticleByPageid(articleToDelete.getPageid());

            if (deleted) {
                updateArticles();
                JOptionPane.showMessageDialog(
                        this,
                        "Το άρθρο διαγράφηκε.",
                        "Διαγραφή άρθρου",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Το άρθρο δεν βρέθηκε.",
                        "Διαγραφή άρθρου",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }// GEN-LAST:event_jButton4ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
