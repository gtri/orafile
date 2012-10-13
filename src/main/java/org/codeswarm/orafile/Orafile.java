package org.codeswarm.orafile;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public final class Orafile {

    private Orafile() { }

    public static OraDict params() {
        return new OraDict();
    }

    public static OraDict params(OraDict... dicts) {

        OraDict retval = params();
        for (OraDict dict : dicts) {
            retval.add(dict);
        }
        return retval;
    }

    public static OraDict params(OraNamedParam namedParam) {
        return new OraDict(namedParam);
    }

    public static OraDict params(String key, OraParam value) {
        return new OraDict(namedParam(key, value));
    }

    public static OraDict params(String key, String value) {
        return params(key, string(value));
    }

    public static OraDict params(String key, List<String> value) {
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

    public static OraDict parse(String fileContent) throws RecognitionException {

        ANTLRStringStream stringStream = new ANTLRStringStream(fileContent);
        org.codeswarm.orafile.OraLexer lexer = new OraLexer(stringStream);
        TokenRewriteStream tokens = new TokenRewriteStream(lexer);
        OraParser parser = new OraParser(tokens);
        return parser.file().params;
    }

}
