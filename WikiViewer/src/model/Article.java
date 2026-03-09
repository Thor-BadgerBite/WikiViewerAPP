/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import wikiviewer.Helpers;

//POJO κλάση για τον πίνακα Article
/**
 * POJO κλάση για τον πίνακα Article.
 * Αντιστοιχεί σε εγγραφή του πίνακα ARTICLE στη βάση δεδομένων.
 */
@Entity
@Table(name = "ARTICLE")
@NamedQueries({
        @NamedQuery(name = "Article.findAll", query = "SELECT a FROM Article a"),
        @NamedQuery(name = "Article.findByArticleid", query = "SELECT a FROM Article a WHERE a.articleid = :articleid"),
        @NamedQuery(name = "Article.findByPageid", query = "SELECT a FROM Article a WHERE a.pageid = :pageid"),
        @NamedQuery(name = "Article.findByTitle", query = "SELECT a FROM Article a WHERE a.title = :title"),
        @NamedQuery(name = "Article.findBySnippet", query = "SELECT a FROM Article a WHERE a.snippet = :snippet"),
        @NamedQuery(name = "Article.findByTimestamp", query = "SELECT a FROM Article a WHERE a.timestamp = :timestamp"),
        @NamedQuery(name = "Article.findByComments", query = "SELECT a FROM Article a WHERE a.comments = :comments"),
        @NamedQuery(name = "Article.findByRating", query = "SELECT a FROM Article a WHERE a.rating = :rating") })
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ARTICLEID")
    private Integer articleid;
    @Basic(optional = false)
    @Column(name = "PAGEID")
    private int pageid;
    @Basic(optional = false)
    @Column(name = "TITLE")
    private String title;
    @Basic(optional = false)
    @Column(name = "SNIPPET")
    private String snippet;
    @Basic(optional = false)
    @Lob
    @Column(name = "CONTENT")
    private String content;
    @Column(name = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
    @Column(name = "COMMENTS")
    private String comments;
    @Column(name = "RATING")
    private Integer rating;
    @JoinColumn(name = "CATEGORYID", referencedColumnName = "CATEGORYID")
    @ManyToOne
    private Category categoryid;
    @Column(name = "SAVEDAT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date savedat;

    public Article() {
    }

    public Article(Integer articleid) {
        this.articleid = articleid;
    }

    public Article(Integer articleid, int pageid, String title, String snippet, Date timestamp) {
        this.articleid = articleid;
        this.pageid = pageid;
        this.title = title;
        this.snippet = snippet;
        this.timestamp = timestamp;
    }

    public Integer getArticleid() {
        return articleid;
    }

    public void setArticleid(Integer articleid) {
        this.articleid = articleid;
    }

    public int getPageid() {
        return pageid;
    }

    public void setPageid(int pageid) {
        this.pageid = pageid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Category getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(Category categoryid) {
        this.categoryid = categoryid;
    }

    public Date getSavedat() {
        return savedat;
    }

    public void setSavedat(Date savedat) {
        this.savedat = savedat;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (articleid != null ? articleid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Article)) {
            return false;
        }
        Article other = (Article) object;
        if ((this.articleid == null && other.articleid != null)
                || (this.articleid != null && !this.articleid.equals(other.articleid))) {
            return false;
        }
        return true;
    }

    // Διαμορφώνω την toString() έτσι όπως την χρειάζομαι
    @Override
    public String toString() {
        return "<html>" +
                "<br><b>Άρθρο:</b> " + getPageid() + "<br>" +
                "<b>Τίτλος:</b> " + getTitle() + "<br>" +
                "<b>Ημερομηνία-Ώρα:</b> " + Helpers.getFormattedTimestamp(getTimestamp()) + "<br>" +
                "<b>Απόσπασμα:</b> ... " + getSnippet() + " ..." +
                "</html>";
    }
}
