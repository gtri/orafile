package org.codeswarm.orafile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class OraDict implements OraParam {

    final Map<String, OraParam> map = new LinkedHashMap<String, OraParam>();

    public OraDict() {}

    public OraDict(String name, OraParam param) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (param == null) {
            throw new NullPointerException();
        }
        map.put(name, param);
    }

    public void add(OraDict dict) {
        for (Map.Entry<String, OraParam> entry : dict.map.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
    }

    public Map<String, OraParam> asMap() {
        return map;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Entry<String, OraParam> entry : map.entrySet()) {
            sb.append(entry.getKey());
            String v = entry.getValue().toString();
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
        OraDict that = (OraDict) o;
        return entryList().equals(that.entryList());
    }

    public int hashCode() {
        return entryList().hashCode();
    }

    List<Map.Entry<String, OraParam>> entryList() {
        return new ArrayList<Map.Entry<String, OraParam>>(map.entrySet());
    }

    public String asString() {
        return null;
    }

    public List<String> asList() {
        return null;
    }

    public OraDict asDict() {
        return this;
    }

}
