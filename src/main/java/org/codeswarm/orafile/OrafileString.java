package org.codeswarm.orafile;

/**
 * A val whose value is a string.
 */
public class OrafileString extends OrafileVal {

    final String string;

    public OrafileString(String string) {
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
        OrafileString that = (OrafileString) o;
        return string.equals(that.string);
    }

    public int hashCode() {
        return string.hashCode();
    }

}
