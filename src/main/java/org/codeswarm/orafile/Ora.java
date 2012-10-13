package org.codeswarm.orafile;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;

import java.util.List;

import static java.util.Arrays.asList;

public final class Ora {

    private Ora() { }

    public static OraDict dict() {
        return new OraDict();
    }

    public static OraDict dict(OraDict ... dicts) {
        OraDict retval = dict();
        for (OraDict dict : dicts) {
            retval.add(dict);
        }
        return retval;
    }

    public static OraDict dict(String key, OraParam value) {
        return new OraDict(key, value);
    }

    public static OraDict dict(String key, String value) {
        return dict(key, string(value));
    }

    public static OraDict dict(String key, List<String> value) {
        return dict(key, list(value));
    }

    public static OraString string(String value) {
        return new OraString(value);
    }

    public static OraList list(List<String> list) {
        return new OraList(list);
    }

    public static OraList list(String... list) {
        return new OraList(asList(list));
    }

    public static OraDict parse(String fileContent) throws RecognitionException {
        ANTLRStringStream stringStream = new ANTLRStringStream(fileContent);
        org.codeswarm.orafile.OraLexer lexer = new OraLexer(stringStream);
        TokenRewriteStream tokens = new TokenRewriteStream(lexer);
        OraParser parser = new OraParser(tokens);
        return parser.file().dict;
    }

}
