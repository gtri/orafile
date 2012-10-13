package org.codeswarm.orafile;

import java.util.*;

import static java.util.Arrays.asList;

public abstract class OraParam {

    OraParam() { }

    /**
     * {@code null} unless this is an instance of {@link OraString}.
     */
    public String asString() {
        return null;
    }

    /**
     * {@code null} unless this is an instance of {@link OraStringList}.
     */
    public List<String> asStringList() {
        return null;
    }

    /**
     * {@code null} unless this is an instance of {@link OraNamedParamList}.
     */
    public OraNamedParamList asNamedParamList() {
        return null;
    }

    public List<List<OraParam>> findContextually(String keyword) {

        ArrayDeque<OraParam> stack = new ArrayDeque<OraParam>();
        stack.push(this);
        return findContextually(keyword, stack);
    }

    static List<List<OraParam>> findContextually(String keyword, Deque<OraParam> stack) {

        List<List<OraParam>> retval = new ArrayList<List<OraParam>>();
        for (OraNamedParam namedParam : stack.peek().getNamedParams()) {
            OraParam param = namedParam.getParam();
            if (namedParam.getName().equalsIgnoreCase(keyword)) {
                ArrayList<OraParam> path = new ArrayList<OraParam>();
                path.add(param);
                path.addAll(stack);
                retval.add(path);
            } else {
                stack.push(param);
                retval.addAll(findContextually(keyword, stack));
                stack.pop();
            }
        }
        return retval;
    }

    public List<OraParam> find(String keyword) {

        List<OraParam> retval = new ArrayList<OraParam>();
        for (OraNamedParam namedParam : getNamedParams()) {
            OraParam param = namedParam.getParam();
            if (namedParam.getName().equalsIgnoreCase(keyword)) {
                retval.add(param);
            } else {
                retval.addAll(param.find(keyword));
            }
        }
        return retval;
    }

    public String findOneString(String keyword) {

        for (OraParam param : find(keyword)) {
            String value = param.asString();
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public List<OraParam> get(String name) {

        OraNamedParamList namedParamList = asNamedParamList();
        if (namedParamList != null) {
            return namedParamList.get(name);
        } else {
            return asList(new OraParam[]{});
        }
    }

    public List<OraNamedParam> getNamedParams() {

        OraNamedParamList namedParamList = asNamedParamList();
        if (namedParamList != null) {
            return namedParamList.asList();
        } else {
            return asList(new OraNamedParam[]{});
        }
    }

    public List<Map<String, String>> findParamAttrs(
            String keyword, List<String> attrs) {

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();

        paths: for (List<OraParam> path : findContextually(keyword)) {
            Map<String, String> values = new HashMap<String, String>();
            Set<String> attrsRemaining = new HashSet<String>(attrs);
            for (OraParam param : path) {
                for (String attr : attrsRemaining) {
                    String value = param.findOneString(attr);
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
