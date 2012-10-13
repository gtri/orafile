package org.codeswarm.orafile;

import java.util.List;

public class OraStringList implements OraParam {

    final List<String> list;

    public OraStringList(List<String> list) {
        if (list == null) {
            throw new NullPointerException();
        }
        this.list = list;
    }

    public String asString() {
        return null;
    }

    public List<String> asStringList() {
        return list;
    }

    public OraNamedParamList asNamedParamList() {
        return null;
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
        OraStringList that = (OraStringList) o;
        return list.equals(that.list);
    }

    public int hashCode() {
        return list.hashCode();
    }

}
