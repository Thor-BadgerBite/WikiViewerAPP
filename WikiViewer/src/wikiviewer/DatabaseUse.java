/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package wikiviewer;

import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import model.Article;
import model.Category;
import model.Search;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.swing.JOptionPane;

import java.util.Date;

/**
 * Κλάση για την διαχείριση της βάσης δεδομένων.
 */
public class DatabaseUse {

    // O entity manager για την διαχείριση της βάσης δεδομένων
    public static EntityManager em;

    /**
     * Δημιουργεί τον entity manager.
     */
    public static void createEntityManager() {
        em = Persistence.createEntityManagerFactory("WikiViewerPU").createEntityManager();
    }

    /**
     * Επιστρέφει όλες τις κατηγορίες άρθρων ταξινομημένες ως προς το όνομα.
     * 
     * @return Λίστα κατηγοριών.
     */
    public static List<Category> getCategories() {
        Query q = em.createQuery("SELECT c FROM Category c ORDER BY c.name", Category.class);
        List<Category> categories = q.getResultList();
        return categories;
    }

    /**
     * Επιστρέφει όλα τα άρθρα ταξινομημένα με φθίνουσα διάταξη ως προς την
     * ημερομηνία αποθήκευσης.
     * 
     * @return Λίστα άρθρων.
     */
    public static List<Article> getArticles() {
        Query q = em.createQuery("SELECT a FROM Article a ORDER BY a.savedat DESC", Article.class);
        List<Article> articles = q.getResultList();
        return articles;
    }

    /**
     * Επιστρέφει όλες τις αναζητήσεις ταξινομημένες με φθίνουσα διάταξη ως προς το
     * πλήθος αναζητήσεων
     * και αύξουσα διάταξη ως προς το κείμενο αναζήτησης για όσες έχουν το ίδιο
     * πλήθος.
     * 
     * @return Λίστα αναζητήσεων.
     */
    public static List<Search> getSearches() {
        Query q = em.createQuery("SELECT s from Search s order by s.numberofsearches desc, s.searchstring",
                Search.class);
        return q.getResultList();
    }

    /**
     * Αναζητά και επιστρέφει την κατηγορία με βάση κάποιο όνομα.
     * 
     * @param name Το όνομα της κατηγορίας.
     * @return Η κατηγορία ή null αν δεν την βρει.
     */
    public static Category getCategory(String name) {
        Query q = em.createNamedQuery("Category.findByName", Category.class);
        q.setParameter("name", name);
        try {
            Category category = (Category) q.getSingleResult();
            return category;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Αναζητά και επιστρέφει το άρθρο με βάση κάποιον αριθμό σελίδας.
     * 
     * @param pageId Ο αριθμός σελίδας του άρθρου.
     * @return Το άρθρο ή null αν δεν το βρει.
     */
    public static Article getArticle(int pageId) {
        Query q = em.createNamedQuery("Article.findByPageid", Article.class);
        q.setParameter("pageid", pageId);
        try {
            Article article = (Article) q.getSingleResult();
            return article;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Αναζητά και επιστρέφει την αναζήτηση με βάση κάποιο κείμενο αναζήτησης.
     * 
     * @param searchString Το κείμενο αναζήτησης.
     * @return Η αναζήτηση ή null αν δεν την βρει.
     */
    public static Search getSearch(String searchString) {
        Query q = em.createNamedQuery("Search.findBySearchstring", Search.class);
        q.setParameter("searchstring", searchString);
        try {
            Search search = (Search) q.getSingleResult();
            return search;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Επιστρέφει το πλήθος αποθηκευμένων άρθρων για κάθε κατηγορία (στατιστικά
     * κατηγορίας).
     * Τα στατιστικά επιστρέφονται με φθίνουσα διάταξη ως προς το πλήθος
     * και αύξουσα διάταξη ως προς το όνομα κατηγορίας για όσες έχουν το ίδιο
     * πλήθος.
     * 
     * @return Λίστα με στατιστικά (όνομα κατηγορίας, πλήθος άρθρων).
     */
    public static List<Object[]> getCategoryStatistics() {
        Query q = em.createQuery(
                "SELECT c.name, COUNT(a) FROM Category c LEFT JOIN c.articleList a GROUP BY c.name ORDER BY COUNT(a) DESC, c.name ASC");
        List<Object[]> statistics = q.getResultList();
        return statistics;
    }

    /**
     * Επιστρέφει τα αποθηκευμένα άρθρα για μία κατηγορία.
     * 
     * @param category Η κατηγορία προς αναζήτηση.
     * @return Λίστα άρθρων της κατηγορίας.
     */
    public static List<Article> getArticlesForCategory(Category category) {
        Query q = em.createQuery("SELECT a FROM Article a WHERE a.categoryid = :category ORDER BY a.savedat DESC",
                Article.class);
        q.setParameter("category", category);
        List<Article> articles = q.getResultList();
        return articles;
    }

    /**
     * Αποθηκεύει ένα άρθρο με timestamp την τρέχουσα ημερομηνία/ώρα και το
     * επιστρέφει.
     * 
     * @param article Το άρθρο προς αποθήκευση.
     * @return Το αποθηκευμένο άρθρο.
     */
    public static Article storeArticle(Article article) {
        em.getTransaction().begin();
        article.setSavedat(new Date());
        em.persist(article);
        em.getTransaction().commit();
        return article;
    }

    /**
     * Αποθηκεύει μία αναζήτηση.
     * Αν η αναζήτηση υπάρχει της αυξάνει κατά 1 το πλήθος αναζητήσεων,
     * ενώ αν δεν υπάρχει την δημιουργεί με πλήθος αναζητήσεων = 1.
     * 
     * @param searchString Το κείμενο αναζήτησης.
     */
    public static void storeSearch(String searchString) {
        Search search = getSearch(searchString);
        if (search != null) {
            search.setNumberofsearches(search.getNumberofsearches() + 1);
        } else {
            search = new Search(null, searchString, 1);
        }
        em.getTransaction().begin();
        em.persist(search);
        em.getTransaction().commit();
    }

    /**
     * Εισάγει κατηγορίες στον πίνακα categories αν δεν υπάρχουν ήδη.
     * 
     * @param categoryNames Λίστα με τα ονόματα των κατηγοριών.
     */
    public static void insertCategories(List<String> categoryNames) {
        if (!getCategories().isEmpty()) {
            return;
        }
        em.getTransaction().begin();
        for (String categoryName : categoryNames) {
            Category category = new Category(null, categoryName);
            em.persist(category);
        }
        em.getTransaction().commit();
    }

    /**
     * Διαγράφει ένα άρθρο από τη βάση με βάση το pageid.
     * 
     * @param pageid Ο αριθμός σελίδας του άρθρου.
     * @return true αν διαγράφηκε, false αν δεν βρέθηκε.
     */
    public static boolean deleteArticleByPageid(int pageid) {
        Article article = getArticle(pageid); // χρησιμοποιεί το NamedQuery Article.findByPageid

        if (article == null) {
            return false;
        }

        em.getTransaction().begin();
        em.remove(article); // article είναι managed γιατί ήρθε από query
        em.getTransaction().commit();

        return true;
    }

    public static void clearStatistics() {
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Search").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        }
    }
}
