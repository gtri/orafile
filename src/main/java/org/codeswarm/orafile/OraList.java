package org.codeswarm.orafile;

import java.util.List;

public class OraList implements OraParam {

    final List<String> list;

    public OraList(List<String> list) {
        if (list == null) {
            throw new NullPointerException();
        }
        this.list = list;
    }

    public String asString() {
        return null;
    }

    public List<String> asList() {
        return list;
    }

    public OraDict asDict() {
        return null;
    }

    public String toString() {
        return list.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OraList that = (OraList) o;
        return list.equals(that.list);
    }

    public int hashCode() {
        return list.hashCode();
    }

}
