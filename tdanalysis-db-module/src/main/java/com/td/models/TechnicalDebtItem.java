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

    /**
     * Format: [Category initial] [Issue code]
     * 
     * e.g. P Bx
     * 
     * http://findbugs.sourceforge.net/bugDescriptions.html
     */
    @Id
    private String id;

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
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
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

}