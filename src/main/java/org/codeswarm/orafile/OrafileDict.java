package org.codeswarm.orafile;

import java.util.*;

/**
 * A list of {@link OrafileDef}s.
 *
 * <p>The list is accessible via {@link #asList()}.
 * For convenience, this data is also represented as a multimap
 * (see {@link #get(String)}).</p>
 */
public class OrafileDict extends OrafileVal {

    final Map<CaseInsensitiveString, List<OrafileVal>> map =
        new HashMap<CaseInsensitiveString, List<OrafileVal>>();

    final List<OrafileDef> list = new ArrayList<OrafileDef>();

    /**
     * Construct an empty dict.
     */
    public OrafileDict() {}

    /**
     * Construct a dict with a single entry.
     */
    public OrafileDict(OrafileDef def) {
        if (def == null) {
            throw new NullPointerException();
        }
        add(def);
    }

    /**
     * Append {@code def} to the end of this list.
     */
    public void add(OrafileDef def) {
        CaseInsensitiveString name = new CaseInsensitiveString(def.getName());
        {
            List<OrafileVal> list = map.get(name);
            if (list == null) {
                list = new ArrayList<OrafileVal>();
                map.put(name, list);
            }
            list.add(def.getVal());
        }
        list.add(def);
    }

    /**
     * Copy all defs from {@code dict} onto the end of this list.
     */
    public void add(OrafileDict dict) {
        for (OrafileDef def : dict.list) {
            add(def);
        }
    }

    /**
     * @return An unmodifiable view of the def list.
     */
    public List<OrafileDef> asList() {
        return Collections.unmodifiableList(list);
    }

    /**
     * @return A val for each def whose name is {@code name}, in the
     * same in which they appear in this list. If no defs exist having
     * the specified name, the list will be empty.
     */
    public List<OrafileVal> get(String name) {
        List<OrafileVal> vals = map.get(new CaseInsensitiveString(name));
        if (vals != null) {
            return vals;
        } else {
            return Arrays.asList(new OrafileVal[]{});
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (OrafileDef def : list) {
            sb.append(def.getName());
            String v = def.getVal().toString();
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

    /**
     * @return {@code this}
     */
    public OrafileDict asNamedParamList() {
        return this;
    }

}
