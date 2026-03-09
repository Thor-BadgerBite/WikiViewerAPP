/**
 *
 * @author Andriopoulos Xristos
 * @author Karagiannis Ioannis
 * @author Demisarlis Thomas
 */
package model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

//POJO κλάση για τον πίνακα Search
/**
 * POJO κλάση για τον πίνακα Search.
 * Αντιστοιχεί σε εγγραφή του πίνακα SEARCH στη βάση δεδομένων.
 */
@Entity
@Table(name = "SEARCH")
@NamedQueries({
        @NamedQuery(name = "Search.findAll", query = "SELECT s FROM Search s"),
        @NamedQuery(name = "Search.findBySearchid", query = "SELECT s FROM Search s WHERE s.searchid = :searchid"),
        @NamedQuery(name = "Search.findBySearchstring", query = "SELECT s FROM Search s WHERE s.searchstring = :searchstring"),
        @NamedQuery(name = "Search.findByNumberofsearches", query = "SELECT s FROM Search s WHERE s.numberofsearches = :numberofsearches"),
        @NamedQuery(name = "Search.Delete", query = "DELETE FROM Search") }) // Φτιάξαμε αυτό το query ώστε κάποιος να
                                                                             // μπορεί να μηδενίσει τα statistics search
public class Search implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "SEARCHID")
    private Integer searchid;
    @Basic(optional = false)
    @Column(name = "SEARCHSTRING")
    private String searchstring;
    @Basic(optional = false)
    @Column(name = "NUMBEROFSEARCHES")
    private int numberofsearches;

    public Search() {
    }

    public Search(Integer searchid) {
        this.searchid = searchid;
    }

    public Search(Integer searchid, String searchstring, int numberofsearches) {
        this.searchid = searchid;
        this.searchstring = searchstring;
        this.numberofsearches = numberofsearches;
    }

    public Integer getSearchid() {
        return searchid;
    }

    public void setSearchid(Integer searchid) {
        this.searchid = searchid;
    }

    public String getSearchstring() {
        return searchstring;
    }

    public void setSearchstring(String searchstring) {
        this.searchstring = searchstring;
    }

    public int getNumberofsearches() {
        return numberofsearches;
    }

    public void setNumberofsearches(int numberofsearches) {
        this.numberofsearches = numberofsearches;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (searchid != null ? searchid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Search)) {
            return false;
        }
        Search other = (Search) object;
        if ((this.searchid == null && other.searchid != null)
                || (this.searchid != null && !this.searchid.equals(other.searchid))) {
            return false;
        }
        return true;
    }
}
