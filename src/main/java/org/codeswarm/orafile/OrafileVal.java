package org.codeswarm.orafile;

import java.util.*;

import static java.util.Arrays.asList;

public abstract class OrafileVal {

    OrafileVal() { }

    /**
     * {@code null} unless this is an instance of {@link OrafileString}.
     */
    public String asString() {
        return null;
    }

    /**
     * {@code null} unless this is an instance of {@link OrafileStringList}.
     */
    public List<String> asStringList() {
        return null;
    }

    /**
     * {@code null} unless this is an instance of {@link OrafileDict}.
     */
    public OrafileDict asNamedParamList() {
        return null;
    }

    public List<List<OrafileVal>> findContextually(String keyword) {

        ArrayDeque<OrafileVal> stack = new ArrayDeque<OrafileVal>();
        stack.push(this);
        return findContextually(keyword, stack);
    }

    static List<List<OrafileVal>> findContextually(
            String keyword, Deque<OrafileVal> stack) {

        List<List<OrafileVal>> retval = new ArrayList<List<OrafileVal>>();
        for (OrafileDef def : stack.peek().getNamedParams()) {
            OrafileVal val = def.getVal();
            if (def.getName().equalsIgnoreCase(keyword)) {
                ArrayList<OrafileVal> path = new ArrayList<OrafileVal>();
                path.add(val);
                path.addAll(stack);
                retval.add(path);
            } else {
                stack.push(val);
                retval.addAll(findContextually(keyword, stack));
                stack.pop();
            }
        }
        return retval;
    }

    public List<OrafileVal> find(String keyword) {

        List<OrafileVal> retval = new ArrayList<OrafileVal>();
        for (OrafileDef def : getNamedParams()) {
            OrafileVal val = def.getVal();
            if (def.getName().equalsIgnoreCase(keyword)) {
                retval.add(val);
            } else {
                retval.addAll(val.find(keyword));
            }
        }
        return retval;
    }

    public String findOneString(String keyword) {

        for (OrafileVal val : find(keyword)) {
            String value = val.asString();
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public List<OrafileVal> get(String name) {

        OrafileDict dict = asNamedParamList();
        if (dict != null) {
            return dict.get(name);
        } else {
            return asList(new OrafileVal[]{});
        }
    }

    public List<OrafileDef> getNamedParams() {

        OrafileDict dict = asNamedParamList();
        if (dict != null) {
            return dict.asList();
        } else {
            return asList(new OrafileDef[]{});
        }
    }

    public List<Map<String, String>> findParamAttrs(
            String keyword, List<String> attrs) {

        List<Map<String, String>> results =
            new ArrayList<Map<String, String>>();

        paths: for (List<OrafileVal> path : findContextually(keyword)) {
            Map<String, String> values = new HashMap<String, String>();
            Set<String> attrsRemaining = new HashSet<String>(attrs);
            for (OrafileVal val : path) {
                for (String attr : attrsRemaining) {
                    String value = val.findOneString(attr);
                    if (value != null) {
                        values.put(attr, value);
                    }
                }
                for (String attr : values.keySet()) {
                    attrsRemaining.remove(attr);
                }
                if (attrsRemaining.size() == 0) {
                    results.add(values);
                    continue paths;
                }
            }
        }

        return results;
    }

}
