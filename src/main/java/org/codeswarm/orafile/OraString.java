package org.codeswarm.orafile;

public class OraString extends OraParam {

    final String string;

    public OraString(String string) {
        if (string == null) {
            throw new NullPointerException();
        }
        this.string = string;
    }

    public String asString() {
        return string;
    }

    public String toString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OraString that = (OraString) o;
        return string.equals(that.string);
    }

    public int hashCode() {
        return string.hashCode();
    }

}
