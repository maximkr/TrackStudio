package com.trackstudio.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.trackstudio.app.filter.FValue;
import com.trackstudio.exception.GranException;
import com.trackstudio.startup.I18n;

import net.jcip.annotations.NotThreadSafe;

/**
 * This class is used for slide entries
 */
@NotThreadSafe
public class Slider<T> implements Serializable, Collection<T> {

    private static Log log = LogFactory.getLog(Slider.class);
    private List<T> col;
    private List<T> originalList;
    private int page = 1;
    private int pageSize;
    private int colSize;
    private int totalChildrenCount;
    private List<String> sortorder;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private boolean all;
    private String locale;

    public boolean isAll() {
        return all;
    }

    /**
     * This method sets to show all items in a slider
     * @param all show all or not
     * @param locale locale for label 'all'
     */
    public void setAll(boolean all, String locale) {
        this.all = all;
        this.locale = locale;
        if (all) {
            pageSize = totalChildrenCount;
            col = originalList;
        }
    }

    /**
     * This method gets currency page
     *
     * @return �����
     */
    public int getPage() {
        return page;
    }

    /**
     * This method gets total number of pages
     *
     * @return total pages
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * This method gets total number of entries
     *
     * @return total number of entries
     */
    public int getTotalChildrenCount() {
        return totalChildrenCount;
    }

    /**
     * This method sets total number of entries
     *
     * @param totalChildrenCount total number of entries
     */
    public void setTotalChildrenCount(int totalChildrenCount) {
        this.totalChildrenCount = totalChildrenCount;
    }

    /**
     * This method gets fields which is used in sort list of entries
     *
     * @return List of fields for sorting
     */
    public List<String> getSortorder() {
        return sortorder;
    }

    /**
     * This method gets list of entries
     *
     * @return list of entries
     */
    public List<T> getCol() {
        return col;
    }

    public Slider(List<T> col, int pageSize, List<String> order, int page) {
        this(col, pageSize, order, page, col.size());
    }

    /**
     * Constructor
     *
     * @param col         list of entries
     * @param pageSize    pages size
     * @param order       list of fields for sorting
     * @param page        current page
     */
    public Slider(List<T> col, int pageSize, List<String> order, int page, int total) {
        log.trace("Slider()");
        this.pageSize = pageSize;
        if (pageSize <= 0)
            this.pageSize = DEFAULT_PAGE_SIZE;
        this.page = page;
        if (col.size() == total) {
            this.page = page > 1 && col.size() <= (page - 1) * pageSize ? 1 : page;
        }
        this.sortorder = order;
        this.colSize = total;
        this.originalList = col;
        this.col = new ArrayList<T>();
        this.totalChildrenCount = col.size();
        int index = 0;
        for (T o : col) {
            if (index >= (this.page - 1) * pageSize && index < this.page * pageSize) {
                this.col.add(o);
            }
            if (index == this.page * pageSize)
                break;
            index++;
        }
    }

    /**
     * Default constructor
     */
    public Slider() {
        this.col = new ArrayList<T>();
    }

    /**
     * This method calculates pages size. It depends from total number of entries
     *
     * @return calculated pages size
     */
    public int getPagesCount() {
        if (this.colSize > this.pageSize) {
            if (this.colSize % this.pageSize == 0) {
                return this.colSize / this.pageSize;
            } else {
                return this.colSize / this.pageSize + 1;
            }
        }
        return 1;
    }

    /**
     * This method sets total number of pages
     *
     * @param pagenum new pages size
     */
    public void setPage(int pagenum) {
        if (pagenum > 1) {
            if (pagenum > getPagesCount()) {
                this.page = pagenum;
            } else
                this.page = pagenum;
        } else {
            this.page = 1;
        }
    }

    /**
     * This method prints HTML text in JSP
     *
     * @param baseref base URL
     * @param element html tag
     * @param style   class for tag
     * @return html-text
     */
    public String drawSlider(String baseref, String element, String style) {
        StringBuffer outp = new StringBuffer();
        String delim = "?";
        if (baseref.indexOf("?") > -1) delim = "&";
        if (this.getPagesCount() > 1) {
            outp.append("<").append(element).append(" class=\"").append(style).append("\">");
            int top_sl = 10;
            int bottom_sl = 1;

            if (this.getPage() - 5 < 1) {
                bottom_sl = 1;

                if (this.getPagesCount() > 11) {
                    top_sl = 11;
                } else
                    top_sl = this.getPagesCount();
            } else {
                if (this.getPagesCount() > this.getPage() + 5) {
                    top_sl = this.getPage() + 5;
                    bottom_sl = this.getPage() - 5;
                } else {
                    top_sl = this.getPagesCount();

                    if (top_sl - 10 > 1) {
                        bottom_sl = top_sl - 10;
                    } else
                        bottom_sl = 1;
                }
            }

            if (bottom_sl != 1) {
                outp.append("<a href=\"");
                outp.append(baseref).append(delim).append("sliderPage=").append(1).append((sortorder != null && !sortorder.isEmpty() ? "&sliderOrder=" + FValue.getSortOrderAsString(sortorder) : "")).append("\">1</a>&nbsp;");
                if (bottom_sl != 2) outp.append("...&nbsp;");
            }
            for (int i = bottom_sl; i <= top_sl; i++) {
                if (i == this.getPage()) {
                    outp.append("<span>").append(i).append("</span>&nbsp;");
                } else {
                    outp.append("<a href=\"").append(baseref).append(delim).append("sliderPage=").append(i).append((sortorder != null && !sortorder.isEmpty() ? "&sliderOrder=" + FValue.getSortOrderAsString(sortorder) : "")).append("\">").append(i).append("</a>&nbsp;");
                }
            }
            if (top_sl < this.getPagesCount()) {
                outp.append("...&nbsp;<a href=\"").append(baseref).append(delim).append("sliderPage=").append(this.getPagesCount()).append((sortorder != null && !sortorder.isEmpty() ? "&sliderOrder=" + FValue.getSortOrderAsString(sortorder) : "")).append("\">").append(this.getPagesCount()).append("</a>");
            }
            try {
                outp.append("&nbsp;").append("<a href=\"").append(baseref).append(delim).append("all=true").append((sortorder != null && !sortorder.isEmpty() ? "&sliderOrder=" + FValue.getSortOrderAsString(sortorder) : "")).append("\">").append(I18n.getString(locale, "ALL")).append("</a>").append("&nbsp;");
            } catch (GranException e) {
                log.debug("Debug", e);
            }
            outp.append("</").append(element).append(">");
        }
        return outp.toString();
    }

    /**
     * This method gets total size of entries
     *
     * @return total size of entries
     */
    public String getColSize() {
        return String.valueOf(colSize);
    }

    /**
     * This method gets total size of entries
     *
     * @return total size of entries
     */
    public int size() {
        return colSize;
    }

    /**
     * This method checks empty or not list of entries
     *
     * @return TRUE - empty, FALSE - not empty
     */
    public boolean isEmpty() {
        return col.isEmpty();
    }

    /**
     * This method checkes if list of entries has concreate object
     *
     * @param o checked object
     * @return TRUE - has, FALSE - does not has
     */
    public boolean contains(Object o) {
        return col.contains(o);
    }

    /**
     * This method returns iterator of entries
     *
     * @return entries iterator
     */
    public Iterator<T> iterator() {
        return col.iterator();
    }

    /**
     * This method returns array of entries from list
     *
     * @return array of entries
     */
    public T[] toArray() {
        return (T[]) col.toArray();
    }

    /**
     * This method removes element from collections entries
     *
     * @param o element
     * @return TRUE - remove, FALSE - does not remove
     */
    public boolean remove(Object o) {
        return col.remove(o);
    }

    /**
     * This method checks if entries collection has taking collection
     *
     * @param c taking collection
     * @return TRUE - has, FALSE - does not have
     */
    public boolean containsAll(Collection<?> c) {
        return col.containsAll(c);
    }

    /**
     * This method remotes takes collection from entries collection
     *
     * @param c taking collection
     * @return TRUE - remove, FALSE - does not remove
     */
    public boolean removeAll(Collection<?> c) {
        return col.removeAll(c);
    }

    /**
     * This method convets entries collection to array by special array mask
     *
     * @param a   special array
     * @param <T> returns type of array
     * @return array
     */
    public <T> T[] toArray(T[] a) {
        return col.toArray(a);
    }

    /**
     * This method adds new element to collection
     *
     * @param o new element
     * @return TRUE - adds, FALSE - does not add
     */
    public boolean add(T o) {
        return col.add(o);
    }

    /**
     * This method removes all elements which is not in taking collection
     *
     * @param c taking collection
     * @return TRUE - removes, FALSE - does not remove
     */
    public boolean retainAll(Collection<?> c) {
        return col.retainAll(c);
    }

    /**
     * Hash code
     *
     * @return hash code
     */
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * This method adds new collection to entries collection
     *
     * @param c new collection
     * @return TRUE - does, FALSE - does not
     */
    public boolean addAll(Collection<? extends T> c) {
        return col.addAll(c);
    }

    /**
     * This method clears entries collection
     */
    public void clear() {
        col.clear();
    }

    /**
     * This method gets size of entries collection
     *
     * @return size of collection
     */
    public int getSize() {
        return size();
    }

    public List<T> getOriginalList() {
        return originalList;
    }
}