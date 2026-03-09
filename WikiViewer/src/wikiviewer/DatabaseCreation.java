/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package wikiviewer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Kλάση για την σύνδεση με την βάση δεδομένων και την δημιουργία των πινάκων
 * εφόσον δεν υπάρχουν.
 */
public class DatabaseCreation {

    // Το String για την σύνδεση με τη βάση δεδομένων
    // Η βάση δεδομένων (wikiDB) βρίσκεται μέσα στο φάκελο του project και αν δεν
    // υπάρχει ήδη την δημιουργούμε
    public static String connectionString = "jdbc:derby:wikiDB;user=wiki;password=wiki;create=true";

    /**
     * Συνδέεται στη βάση δεδομένων και εκτελεί ένα sql query για δημιουργία πίνακα.
     * 
     * @param sqlQuery Το SQL query που θα εκτελεστεί.
     */
    public static void runQuery(String sqlQuery) {
        try (Connection connection = DriverManager.getConnection(connectionString);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQuery);
        } catch (SQLException e) {
            // Αν ο πίνακας υπάρχει ήδη προκύπτει SQLException
        }
    }

    /**
     * Δημιουργεί τους πίνακες της βάσης δεδομένων αν δεν υπάρχουν.
     */
    public static void CreateDatabaseTables() {
        // String για τη δημιουργία του πίνακα category
        String createTableCategory = "CREATE TABLE category("
                + "categoryId INT NOT NULL GENERATED ALWAYS AS IDENTITY,"
                + "name VARCHAR(100) NOT NULL,"
                + "UNIQUE(name),"
                + "PRIMARY KEY(CategoryId))";

        // String για τη δημιουργία του πίνακα article
        String createTableArticle = "CREATE TABLE article("
                + "articleId INT NOT NULL GENERATED ALWAYS AS IDENTITY,"
                + "pageId INT NOT NULL,"
                + "title VARCHAR(255) NOT NULL,"
                + "snippet VARCHAR(1000) NOT NULL,"
                + "content CLOB NOT NULL,"
                + "timestamp TIMESTAMP,"
                + "comments VARCHAR(1000),"
                + "rating INT CHECK(rating BETWEEN 1 AND 5),"
                + "categoryId INT,"
                + "savedAt TIMESTAMP,"
                + "FOREIGN KEY(categoryId) REFERENCES category(categoryId),"
                + "UNIQUE(pageId),"
                + "PRIMARY KEY(articleId))";

        // String για τη δημιουργία του πίνακα search
        String createTableSearch = "CREATE TABLE search("
                + "searchId INT NOT NULL GENERATED ALWAYS AS IDENTITY,"
                + "searchString VARCHAR(100) NOT NULL,"
                + "numberOfSearches INT NOT NULL,"
                + "UNIQUE(searchString),"
                + "PRIMARY KEY(searchId))";

        // Δημιουργία των πινάκων
        runQuery(createTableCategory);
        runQuery(createTableArticle);
        runQuery(createTableSearch);
    }
}
