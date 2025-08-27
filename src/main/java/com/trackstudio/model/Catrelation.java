package com.trackstudio.model;

import java.io.Serializable;

/**
 * This class describe relation between 2 categories.
 * If entry here exists for some category pair, user
 * can create specified child category for specified
 * category
 */
public class Catrelation implements Serializable {
    private String id; //identifier

    private Category category;
    private Category child;

    public Catrelation(String id) {
        this.id = id;
    }

    public Catrelation(Category category, Category child) {
        this.category = category;
        this.child = child;
    }

    public Catrelation(String categoryId, String childId) {
        this(categoryId != null ? new Category(categoryId) : null, childId != null ? new Category(childId) : null);
    }

    public Catrelation() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory() {
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getChild() {
        return this.child;
    }

    public void setChild(Category cat) {
        this.child = cat;
    }

    public boolean equals(Catrelation c) {
        return this.getId().equals(c.getId());
    }

    public boolean equals(Object obj) {
        return obj instanceof Catrelation && ((Catrelation) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
