package org.codeswarm.orafile;

import java.util.*;

public class OrafileDict extends OrafileVal {

    final Map<String, List<OrafileVal>> map = new HashMap<String, List<OrafileVal>>();
    final List<OrafileDef> list = new ArrayList<OrafileDef>();

    public OrafileDict() {}

    public OrafileDict(OrafileDef def) {
        if (def == null) {
            throw new NullPointerException();
        }
        add(def);
    }

    public void add(OrafileDef def) {
        String name = def.getName();
        {
            List<OrafileVal> list = map.get(name);
            if (list == null) {
                list = new ArrayList<OrafileVal>();
                map.put(name, list);
            }
            list.add(def.getParam());
        }
        list.add(def);
    }

    public void add(OrafileDict dict) {
        for (OrafileDef def : dict.list) {
            add(def);
        }
    }

    public List<OrafileDef> asList() {
        return Collections.unmodifiableList(list);
    }

    public List<OrafileVal> get(String name) {
        List<OrafileVal> params = map.get(name.toUpperCase());
        if (params != null) {
            return params;
        } else {
            return Arrays.asList(new OrafileVal[]{});
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (OrafileDef def : list) {
            sb.append(def.getName());
            String v = def.getParam().toString();
            if (v.contains("\n")) {
                for (String line : v.split("\n")) {
                    sb.append("\n  ").append(line);
                }
            } else {
                sb.append(": ").append(v);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrafileDict that = (OrafileDict) o;
        return list.equals(that.list);
    }

    public int hashCode() {
        return list.hashCode();
    }

    public OrafileDict asNamedParamList() {
        return this;
    }

}
