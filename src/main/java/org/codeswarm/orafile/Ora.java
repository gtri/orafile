package org.codeswarm.orafile;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;

import java.util.*;

import static java.util.Arrays.asList;

public final class Ora {

    private Ora() { }

    public static OraNamedParamList params() {
        return new OraNamedParamList();
    }

    public static OraNamedParamList params(OraNamedParamList... namedParamLists) {

        OraNamedParamList retval = params();
        for (OraNamedParamList namedParamList : namedParamLists) {
            retval.add(namedParamList);
        }
        return retval;
    }

    public static OraNamedParamList params(OraNamedParam namedParam) {
        return new OraNamedParamList(namedParam);
    }

    public static OraNamedParamList params(String key, OraParam value) {
        return new OraNamedParamList(namedParam(key, value));
    }

    public static OraNamedParamList params(String key, String value) {
        return params(key, string(value));
    }

    public static OraNamedParamList params(String key, List<String> value) {
        return params(key, strings(value));
    }

    public static OraString string(String value) {
        return new OraString(value);
    }

    public static OraStringList strings(List<String> list) {
        return new OraStringList(list);
    }

    public static OraStringList strings(String... list) {
        return new OraStringList(new ArrayList<String>(asList(list)));
    }

    public static OraNamedParam namedParam(String name, OraParam param) {
        return new OraNamedParam(name, param);
    }

    public static OraNamedParamList parse(String fileContent) throws RecognitionException {

        ANTLRStringStream stringStream = new ANTLRStringStream(fileContent);
        org.codeswarm.orafile.OraLexer lexer = new OraLexer(stringStream);
        TokenRewriteStream tokens = new TokenRewriteStream(lexer);
        OraParser parser = new OraParser(tokens);
        return parser.file().params;
    }

    public static List<List<OraParam>> findContextually(OraParam root, String keyword) {

        ArrayDeque<OraParam> stack = new ArrayDeque<OraParam>();
        stack.push(root);
        return findContextually(keyword, stack);
    }

    private static List<List<OraParam>> findContextually(String keyword, Deque<OraParam> stack) {

        List<List<OraParam>> retval = new ArrayList<List<OraParam>>();
        for (OraNamedParam namedParam : getNamedParams(stack.peek())) {
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

    public static List<OraParam> find(OraParam root, String keyword) {

        List<OraParam> retval = new ArrayList<OraParam>();
        for (OraNamedParam namedParam : getNamedParams(root)) {
            OraParam param = namedParam.getParam();
            if (namedParam.getName().equalsIgnoreCase(keyword)) {
                retval.add(param);
            } else {
                retval.addAll(find(param, keyword));
            }
        }
        return retval;
    }

    public static String findOneString(OraParam root, String keyword) {

        for (OraParam param : find(root, keyword)) {
            String value = param.asString();
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public static List<OraParam> get(OraParam param, String name) {

        OraNamedParamList namedParamList = param.asNamedParamList();
        if (namedParamList != null) {
            return namedParamList.get(name);
        } else {
            return asList(new OraParam[]{});
        }
    }

    public static List<OraNamedParam> getNamedParams(OraParam param) {

        OraNamedParamList namedParamList = param.asNamedParamList();
        if (namedParamList != null) {
            return namedParamList.asList();
        } else {
            return asList(new OraNamedParam[]{});
        }
    }

    public static List<Map<String, String>> findParamAttrs(
            OraParam root, String keyword, List<String> attrs) {

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();

        paths: for (List<OraParam> path : findContextually(root, keyword)) {
            Map<String, String> values = new HashMap<String, String>();
            Set<String> attrsRemaining = new HashSet<String>(attrs);
            for (OraParam param : path) {
                for (String attr : attrsRemaining) {
                    String value = findOneString(param, attr);
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
