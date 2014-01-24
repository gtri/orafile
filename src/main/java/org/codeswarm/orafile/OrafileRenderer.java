package org.codeswarm.orafile;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

public class OrafileRenderer {

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
        Iterator<OrafileDef> defs = dict.list.iterator();
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
            boolean quote = stringVal.isEmpty() || stringVal.contains(" ");
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

                boolean quote = stringVal.isEmpty() || stringVal.contains(" ");
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
            for (OrafileDef nextDef : dict.list) {
                renderDef(writer, nextDef, Parens.Yes, nextIndent);
            }

            if (parens.yes()) writer.append(indent).append(")\n");
        }
    }
}
