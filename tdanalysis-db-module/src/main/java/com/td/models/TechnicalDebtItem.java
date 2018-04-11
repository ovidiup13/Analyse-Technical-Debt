package com.td.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/***
 * This class is a reference class for the FindBugs issues in the MongoDB
 * database.
 */
@Document(collection = "tditems")
public class TechnicalDebtItem {

    @Id
    private CompositeKey _id;

    @Indexed
    private String category;

    /***
     * Since there are multiple issue codes with different descriptions, add all
     * under the same roof. Separating descriptions is difficult, since there is
     * no straightforward way to identify them from FindBugs output.
     */
    private List<String> descriptions;

    /**
     * @return the descriptions
     */
    public List<String> getDescriptions() {
        return descriptions;
    }

    /**
     * @param descriptions the descriptions to set
     */
    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    /**
     * @return the id
     */
    public CompositeKey getId() {
        return _id;
    }

    /**
     * @param id the id to set
     */
    public void setId(CompositeKey id) {
        this._id = id;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    public static class CompositeKey implements Comparable<CompositeKey> {

        private String categoryInitial;
        private String issueCode;

        public CompositeKey() {
        }

        public CompositeKey(String category, String issueCode) {
            this.categoryInitial = category;
            this.issueCode = issueCode;
        }

        /**
         * @return the categoryInitial
         */
        public String getCategoryInitial() {
            return categoryInitial;
        }

        /**
         * @param categoryInitial the categoryInitial to set
         */
        public void setCategoryInitial(String categoryInitial) {
            this.categoryInitial = categoryInitial;
        }

        /**
         * @return the issueCode
         */
        public String getIssueCode() {
            return issueCode;
        }

        /**
         * @param issueCode the issueCode to set
         */
        public void setIssueCode(String issueCode) {
            this.issueCode = issueCode;
        }

        @Override
        public boolean equals(Object o) {
            CompositeKey key = (CompositeKey) o;
            return this.getCategoryInitial().equals(key.getCategoryInitial())
                    && this.getIssueCode().equals(key.getIssueCode());
        }

        @Override
        public int compareTo(CompositeKey o) {
            int res = this.categoryInitial.compareTo(o.getCategoryInitial());
            if (res == 0) {
                return this.issueCode.compareTo(o.getIssueCode());
            }

            return res;
        }
    }

}