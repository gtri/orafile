package edu.gatech.gtri.orafile;

import java.util.List;

/**
 * A val whose value is a list of strings.
 */
public class OrafileStringList extends OrafileVal {

    final List<String> list;

    public OrafileStringList(List<String> list) {
        if (list == null) {
            throw new NullPointerException();
        }
        this.list = list;
    }

    public List<String> asStringList() {
        return list;
    }

    public void add(String string) {
        list.add(string);
    }

    public String toString() {
        return list.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrafileStringList that = (OrafileStringList) o;
        return list.equals(that.list);
    }

    public int hashCode() {
        return list.hashCode();
    }

}
