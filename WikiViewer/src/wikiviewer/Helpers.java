/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package wikiviewer;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Κλάση βοηθητικών μεθόδων.
 */
public class Helpers {

    /**
     * Διαμορφώνει ημερομηνία και ώρα στο επιθυμητό format.
     * 
     * @param date Η ημερομηνία προς διαμόρφωση.
     * @return Η διαμορφωμένη ημερομηνία ως συμβολοσειρά.
     */
    public static String getFormattedTimestamp(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * Κανονικοποιεί μια συμβολοσειρά.
     * Αφαιρεί τόνους και άλλα διακριτικά σημεία και μετατρέπει όλους τους
     * χαρακτήρες σε πεζούς.
     * 
     * @param string Η συμβολοσειρά προς κανονικοποίηση.
     * @return Η κανονικοποιημένη συμβολοσειρά.
     */
    public static String normalizeForSearch(String string) {
        if (string == null)
            return "";
        return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase();
    }

    /**
     * Λαμβάνει τους χαρακτήρες μιας συμβολοσειράς μέχρι ένα μέγιστο πλήθος
     * χαρακτήρων.
     * 
     * @param string        Η συμβολοσειρά προς περικοπή.
     * @param maxCharacters Το μέγιστο πλήθος χαρακτήρων.
     * @return Η περικομμένη συμβολοσειρά.
     */
    public static String getString(String string, int maxCharacters) {
        return string.substring(0, Math.min(maxCharacters, string.length()));
    }
}
