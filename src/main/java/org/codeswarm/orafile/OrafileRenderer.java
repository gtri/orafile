package org.codeswarm.orafile;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class OrafileRenderer {

    final boolean sortByKey;

    public OrafileRenderer() {
        sortByKey = false;
    }

    OrafileRenderer(boolean sortByKey) {
        this.sortByKey = sortByKey;
    }

    /**
     * @param sortByKey True to sort entries by key. False to preserve original ordering.
     * @return A new {@link OrafileRenderer}.
     */
    public OrafileRenderer sortByKey(boolean sortByKey) {
        return new OrafileRenderer(sortByKey);
    }

    private enum Parens {

        Yes, No;

        boolean yes() {
            return this == Yes;
        }
    }

    public String renderFile(OrafileDict dict) {
        StringWriter writer = new StringWriter();
        try {
            renderFile(dict, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    public void renderFile(OrafileDict dict, Writer writer) throws IOException {
        Iterator<OrafileDef> defs = defs(dict).iterator();
        while (defs.hasNext()) {

            OrafileDef def = defs.next();

            renderDef(writer, def, Parens.No, "");

            if (defs.hasNext()) {
                writer.append("\n");
            }
        }
    }

    void renderDef(Writer writer, OrafileDef def, Parens parens, String indent) throws IOException {

        String nextIndent = indent + "  ";

        OrafileVal val = def.getVal();

        if (val instanceof OrafileString) {

            String stringVal = ((OrafileString) val).string;

            writer.append(indent);
            if (parens.yes()) writer.append("(");
            writer.append(def.getName()).append(" = ");
            boolean quote = stringVal.contains(" ");
            if (quote) writer.append("\"");
            writer.append(stringVal);
            if (quote) writer.append("\"");
            if (parens.yes()) writer.append(")\n");

        } else if (val instanceof OrafileStringList) {

            OrafileStringList stringListVal = (OrafileStringList) val;

            writer.append(indent);
            if (parens.yes()) writer.append("(");
            writer.append(def.getName()).append(" = (\n");
            Iterator<String> stringVals = stringListVal.list.iterator();
            while (stringVals.hasNext()) {

                String stringVal = stringVals.next();

                boolean quote = stringVal.contains(" ");
                writer.append(nextIndent);
                if (quote) writer.append("\"");
                writer.append(stringVal);
                if (quote) writer.append("\"");
                if (stringVals.hasNext()) writer.append(",");
                writer.append("\n");
            }
            writer.append(indent).append(")");
            if (parens.yes()) writer.append(")\n");

        } else {

            OrafileDict dict = (OrafileDict) val;

            writer.append(indent);
            if (parens.yes()) writer.append("(");
            writer.append(def.getName()).append(" =\n");
            for (OrafileDef nextDef : defs(dict)) {
                renderDef(writer, nextDef, Parens.Yes, nextIndent);
            }

            if (parens.yes()) writer.append(indent).append(")\n");
        }
    }

    List<OrafileDef> defs(OrafileDict dict) {

        if (!sortByKey) return dict.list;

        List<OrafileDef> defs = new ArrayList<OrafileDef>(dict.list);
        Collections.sort(defs, DEF_KEY_COMPARATOR);
        return defs;
    }

    static final Comparator<OrafileDef> DEF_KEY_COMPARATOR = new Comparator<OrafileDef>() {
        @Override
        public int compare(OrafileDef a, OrafileDef b) {
            return a.getName().compareTo(b.getName());
        }
    };
}
