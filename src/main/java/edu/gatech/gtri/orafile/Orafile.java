package edu.gatech.gtri.orafile;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Utility class for dealing with ora files.
 *
 * <p>The important method is {@link #parse(String)}.</p>
 *
 * <p>Contains constructor methods for {@link OrafileDef},
 * {@link OrafileDict}, {@link OrafileString}, {@link OrafileStringList}.</p>
 */
public final class Orafile {

    private Orafile() { }

    public static OrafileDict dict() {
        return new OrafileDict();
    }

    public static OrafileDict dict(OrafileDict... dicts) {

        OrafileDict retval = dict();
        for (OrafileDict dict : dicts) {
            retval.add(dict);
        }
        return retval;
    }

    public static OrafileDict dict(OrafileDef def) {
        return new OrafileDict(def);
    }

    public static OrafileDict dict(String key, OrafileVal val) {
        return new OrafileDict(def(key, val));
    }

    public static OrafileDict dict(String key, String val) {
        return dict(key, string(val));
    }

    public static OrafileDict dict(String key, List<String> val) {
        return dict(key, strings(val));
    }

    public static OrafileString string(String val) {
        return new OrafileString(val);
    }

    public static OrafileStringList strings(List<String> list) {
        return new OrafileStringList(list);
    }

    public static OrafileStringList strings(String... list) {
        return new OrafileStringList(new ArrayList<String>(asList(list)));
    }

    public static OrafileDef def(String name, OrafileVal val) {
        return new OrafileDef(name, val);
    }

    public static OrafileDict parse(String fileContent)
            throws RecognitionException {

        ANTLRStringStream stringStream = new ANTLRStringStream(fileContent);
        OrafileLexer lexer = new OrafileLexer(stringStream);
        TokenRewriteStream tokens = new TokenRewriteStream(lexer);
        OrafileParser parser = new OrafileParser(tokens);
        return parser.file().dict;
    }

}
