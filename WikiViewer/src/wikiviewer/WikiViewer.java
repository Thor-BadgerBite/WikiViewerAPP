/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package wikiviewer;

import java.util.List;
import view.MainForm;

/**
 * Κλάση κυρίως προγράμματος.
 */
public class WikiViewer {
    /**
     * Η αρχική φόρμα της εφαρμογής.
     */
    public static MainForm mainForm;

    /**
     * Οι κατηγορίες του πίνακα category.
     */
    public static List<String> categoryNames = List.of(
            "Αθλητισμός",
            "Επιστήμη",
            "Κοινωνία",
            "Οικονομία",
            "Παιδεία",
            "Πολιτική",
            "Πολιτισμός",
            "Τέχνη",
            "Τεχνολογία",
            "Υγεία",
            "Ιστορία");

    /**
     * Κυρίως πρόγραμμα.
     * 
     * @param args Οι παράμετροι από τη γραμμή εντολών.
     */
    public static void main(String[] args) {
        // 1. Εμφάνιση Splash Screen
        view.SplashForm splash = new view.SplashForm();
        splash.setVisible(true);

        // 2. Δημιουργία των πινάκων της βάσης δεδομένων αν δεν υπάρχουν
        DatabaseCreation.CreateDatabaseTables();

        // 3. Δημιουργία του entity manager που χειρίζεται τη βάση δεδομένων
        DatabaseUse.createEntityManager();

        // 4. Εισαγωγή των κατηγοριών στον πίνακα category αν δεν υπάρχουν
        DatabaseUse.insertCategories(categoryNames);

        // 5. Καθυστέρηση παρασκηνίου (ώστε το Splash να φανεί ακριβώς 4 δευτερόλεπτα)
        try {
            // Ο χρόνος που ήδη πέρασε για τη βάση είναι μικρός,
            // αλλά περιμένουμε άλλα 4 δεύτερα.
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            // Η εκτέλεση συνεχίζεται κανονικά σε περίπτωση διακοπής
        }

        // 6. Κλείσιμο Splash Screen
        splash.dispose();

        // 7. Δημιουργία και εμφάνιση της αρχικής φόρμας
        mainForm = new MainForm();
        mainForm.setVisible(true);
    }
}
