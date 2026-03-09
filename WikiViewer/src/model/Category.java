/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

//POJO κλάση για τον πίνακα Category
/**
 * POJO κλάση για τον πίνακα Category.
 * Αντιστοιχεί σε εγγραφή του πίνακα CATEGORY στη βάση δεδομένων.
 */
@Entity
@Table(name = "CATEGORY")
@NamedQueries({
        @NamedQuery(name = "Category.findAll", query = "SELECT c FROM Category c"),
        @NamedQuery(name = "Category.findByCategoryid", query = "SELECT c FROM Category c WHERE c.categoryid = :categoryid"),
        @NamedQuery(name = "Category.findByName", query = "SELECT c FROM Category c WHERE c.name = :name") })
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "CATEGORYID")
    private Integer categoryid;
    @Basic(optional = false)
    @Column(name = "NAME")
    private String name;
    @OneToMany(mappedBy = "categoryid")
    private List<Article> articleList;

    public Category() {
    }

    public Category(Integer categoryid) {
        this.categoryid = categoryid;
    }

    public Category(Integer categoryid, String name) {
        this.categoryid = categoryid;
        this.name = name;
    }

    public Integer getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(Integer categoryid) {
        this.categoryid = categoryid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (categoryid != null ? categoryid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Category)) {
            return false;
        }
        Category other = (Category) object;
        if ((this.categoryid == null && other.categoryid != null)
                || (this.categoryid != null && !this.categoryid.equals(other.categoryid))) {
            return false;
        }
        return true;
    }
}
