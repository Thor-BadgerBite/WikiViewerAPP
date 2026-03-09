/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package wikiviewer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.Article;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 * Κλάση για ανάκτηση δεδομένων από το API.
 * Χρησιμοποιεί HashMap για αποθήκευση σελίδων και κειμένων άρθρων
 * αποφεύγοντας επιπλέον κλήσεις στο API.
 */
public class ApiData {
    // Πεδία
    private static final OkHttpClient client = new OkHttpClient();
    private final Gson gson;
    private final String searchString;
    private final int limit;
    private int currentPage;
    private int currentPageResults;
    private int totalResults;

    /*
     * Επειδή το να κάνουμε κλήσεις συνέχεια στο Api είναι αργό αλλά και επικίνδυνο
     * να μας banάρει το API χρησιμοπούμε το
     * HashMap το οποίο είναι ένα stracture το οποίο μας επιτρέπει να αποθηκεύουμε
     * της σελίδες που έχουμε ήδη ανακτήσει από το API
     * χρησιμοποιώντας ως κλειδί τον αριθμό σελίδας και ως τιμή τη λίστα με τα άρθρα
     * της σελίδας
     */
    private final Map<Integer, List<Article>> pages;

    /*
     * Το ίδιο με παραπάνω κάνουμε για να αποθηκεύουμε τα κείμενα των άρθρων που
     * έχουμε ήδη ανακτήσει από το API
     * χρησιμοποιώντας ως κλειδί τον αριθμό άρθρου και ως τιμή το κείμενο του άρθρου
     */
    private final Map<Integer, String> pageContents;

    /**
     * Constructor της κλάσης ApiData.
     * 
     * @param searchString Η συμβολοσειρά αναζήτησης που εισήγαγε ο χρήστης.
     */
    public ApiData(String searchString) {
        this.gson = new Gson();
        this.searchString = searchString;
        // Όριο ώστε να μας φέρνει 10 άρθρα την φορά
        this.limit = 10;
        this.currentPage = 1;
        this.currentPageResults = 0;
        this.totalResults = 0;
        this.pages = new HashMap<>();
        this.pageContents = new HashMap<>();
    }

    /**
     * Δημιουργεί και επιστρέφει τη λίστα άρθρων που ανακτήθηκαν από το API για την
     * τρέχουσα σελίδα.
     * Επιστρέφει null αν δεν μπόρεσε να συνδεθεί στο API ή αν δεν βρέθηκαν
     * δεδομένα.
     * 
     * @return Λίστα άρθρων της τρέχουσας σελίδας ή null σε περίπτωση αποτυχίας.
     */
    public List<Article> getPageArticles() {
        // Παίρνουμε τα άρθρα από το HashMap που έχουμε αποθηκεύσει αποφεύγοντας
        // επιπλεόν κλήσεις αν ο χρήστης κάνει συνέχεια μπρος/πίσω
        if (pages.containsKey(currentPage)) {
            List<Article> articles = pages.get(currentPage);
            currentPageResults = articles.size();
            return articles;
        }

        // Υπολογίζω το offset της σελίδας
        int offset = (currentPage - 1) * limit;

        // Url κλήσης άρθρου
        String url = "https://el.wikipedia.org/w/api.php?action=query&list=search"
                + "&srsearch=" + searchString
                + "&srlimit=" + limit
                + "&sroffset=" + offset
                + "&format=json";

        // Request
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "WikiViewer (ipkaragiannis80@gmail.com)")
                .build();

        // Kλήση στο API
        try (Response response = client.newCall(request).execute()) {
            // Null αν δεν βρεθεί τίποτα
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }

            // Δεδομένα σε μορφή json
            JsonObject root = gson.fromJson(response.body().string(), JsonObject.class);
            JsonObject query = root.getAsJsonObject("query");

            // Αποθήκευση των αποτελεσμάτων
            totalResults = query
                    .getAsJsonObject("searchinfo")
                    .get("totalhits")
                    .getAsInt();

            // Λήψη των άρθρων σε μορφή json πίνακα
            JsonArray searchResults = query.getAsJsonArray("search");

            // Δημιουργούμε τη λίστα άρθρων από τον json πίνακα
            List<Article> articles = parseArticles(searchResults);

            // Αποθήκευση των αποτελεσμάτων της τρέχουσας σελίδας
            currentPageResults = articles.size();

            // Αποθήκευση των άρθρων της τρέχουσας σελίδας στο HashMap pages
            pages.put(currentPage, articles);

            // Επιστρέφουμε την λίστα άρθρων της τρέχουσας σελίδας
            return articles;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Πρόβλημα στην ανάκτηση δεδομένων από το API",
                    "Αποτυχία ανάκτησης δεδομένων", JOptionPane.ERROR_MESSAGE);
            // Null αν δεν βρεθεί τίποτα
            return null;
        }
    }

    /**
     * Δημιουργεί άρθρα από τα στοιχεία του json array και τα επιστρέφει ως λίστα.
     * 
     * @param jsonArray Ο πίνακας JSON με τα στοιχεία των άρθρων.
     * @return Λίστα με τα άρθρα που δημιουργήθηκαν.
     */
    private List<Article> parseArticles(JsonArray jsonArray) {
        // Δημιουργία λίστας άρθρων
        List<Article> articles = new ArrayList<>();

        // Σάρωση του json array με τα στοιχεία των άρθρων
        for (JsonElement element : jsonArray) {
            // Λαμβάνουμε τα στοιχεία του άρθρου(αριθμό σελίδας, τίτλο, στιγμιότυπο,
            // ημερομηνία και ώρα)
            JsonObject item = element.getAsJsonObject();
            int pageId = item.get("pageid").getAsInt();
            String title = item.get("title").getAsString();
            // Αφαιρούμε από το στιγμιότυπο πιθανά HTML tags με Regex
            String snippet = item.get("snippet").getAsString().replaceAll("<[^>]+>", "");
            Date timestamp = Date.from(Instant.parse(item.get("timestamp").getAsString()));

            // Δημιουργούμε το άρθρο και το προσθέτουμε στην λίστα
            articles.add(new Article(null, pageId, title, snippet, timestamp));
        }

        // Επιστρέφουμε την λίστα άρθρων
        return articles;
    }

    /**
     * Λήψη του πλήρους κειμένου ενός συγκεκριμένου άρθρου.
     * Αν το άρθρο έχει αποθηκευτεί στο HashMap το παίρνουμε από εκεί.
     * 
     * @param pageId Ο αριθμός σελίδας του άρθρου στο Wikipedia.
     * @return Το κείμενο του άρθρου ή null σε περίπτωση αποτυχίας.
     */
    public String getPageContents(int pageId) {
        // Αν το άρθρο έχει αποθηκευτεί στο HashMap το παίρνουμε από εκεί
        if (pageContents.containsKey(pageId)) {
            return pageContents.get(pageId);
        }

        // Url με explaintext=1 ώστε να μην έρθουν HTML tags
        String url = "https://el.wikipedia.org/w/api.php?action=query"
                + "&prop=extracts|links|extlinks"
                + "&pageids=" + pageId
                + "&explaintext=1"
                + "&pllimit=max"
                + "&format=json";

        // Request
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "WikiViewer (ipkaragiannis80@gmail.com)")
                .build();

        // Κάνουμε την κλήση στο API
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                return null;
            }

            JsonObject root = gson.fromJson(response.body().string(), JsonObject.class);
            JsonObject pagesJson = root.getAsJsonObject("query").getAsJsonObject("pages");
            JsonObject page = pagesJson.entrySet().iterator().next().getValue().getAsJsonObject();
            String extract = page.get("extract").getAsString();
            StringBuilder result = new StringBuilder(extract);

            // Εσωτερικά links
            if (page.has("links")) {
                result.append("\n\nΕσωτερικοί σύνδεσμοι:\n");
                for (JsonElement el : page.getAsJsonArray("links")) {
                    JsonObject linkObj = el.getAsJsonObject();
                    String title = linkObj.get("title").getAsString();
                    result.append(title).append("\n");
                }
            }

            // Εξωτερικά links
            if (page.has("extlinks")) {
                result.append("\n\nΕξωτερικοί σύνδεσμοι:\n");
                for (JsonElement el : page.getAsJsonArray("extlinks")) {
                    JsonObject linkObj = el.getAsJsonObject();
                    String link = linkObj.get("*").getAsString();
                    result.append(link).append("\n");
                }
            }

            // Αποθήκευση του κειμένου στο HashMap pageContents
            pageContents.put(pageId, result.toString());
            return result.toString();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Πρόβλημα στην ανάκτηση δεδομένων από το API",
                    "Αποτυχία ανάκτησης δεδομένων", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Επιστρέφει τα άρθρα της επόμενης σελίδας αποτελεσμάτων.
     * 
     * @return Λίστα άρθρων της επόμενης σελίδας.
     */
    public List<Article> getNextPageArticles() {
        if (hasNext()) {
            currentPage++;
        }
        return getPageArticles();
    }

    /**
     * Επιστρέφει τα άρθρα της προηγούμενης σελίδας αποτελεσμάτων.
     * 
     * @return Λίστα άρθρων της προηγούμενης σελίδας.
     */
    public List<Article> getPreviousPageArticles() {
        if (hasPrevious()) {
            currentPage--;
        }
        return getPageArticles();
    }

    /**
     * Έλεγχος αν υπάρχει επόμενη σελίδα με δεδομένα στο API.
     * 
     * @return true αν υπάρχει επόμενη σελίδα, false αλλιώς.
     */
    public boolean hasNext() {
        return currentPage < getTotalPages();
    }

    /**
     * Έλεγχος αν υπάρχει προηγούμενη σελίδα με δεδομένα στο API.
     * 
     * @return true αν υπάρχει προηγούμενη σελίδα, false αλλιώς.
     */
    public boolean hasPrevious() {
        return currentPage > 1;
    }

    /**
     * Επιστρέφει το σύνολο των σελίδων αποτελεσμάτων.
     * 
     * @return Ο συνολικός αριθμός σελίδων.
     */
    public int getTotalPages() {
        return (int) Math.ceil((double) totalResults / limit);
    }

    /**
     * Επιστρέφει τον αριθμό της τρέχουσας σελίδας.
     * 
     * @return Ο αριθμός της τρέχουσας σελίδας.
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Επιστρέφει το σύνολο αποτελεσμάτων από το API.
     * 
     * @return Ο συνολικός αριθμός αποτελεσμάτων.
     */
    public int getTotalResults() {
        return totalResults;
    }

    /**
     * Επιστρέφει το πλήθος αποτελεσμάτων της τρέχουσας σελίδας.
     * 
     * @return Ο αριθμός αποτελεσμάτων της τρέχουσας σελίδας.
     */
    public int getCurrentPageResults() {
        return currentPageResults;
    }

    /**
     * Επιστρέφει το όριο αποτελεσμάτων ανά σελίδα.
     * 
     * @return Το μέγιστο πλήθος αποτελεσμάτων ανά σελίδα.
     */
    public int getLimit() {
        return limit;
    }
}
