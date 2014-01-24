package edu.gatech.gtri.orafile;

/**
 * A single map entry from keyword to {@link OrafileVal}.
 *
 * <p>For example, in</p>
 * <blockquote><tt>(ADDRESS=(PROTOCOL=ipc)(KEY=extproc))</tt></blockquote>
 * <p>the keyword is the string</p>
 * <blockquote><tt>ADDRESS</tt></blockquote>
 * <p>and the value is an {@link OrafileDict}</p>
 * <blockquote><tt>(PROTOCOL=ipc)(KEY=extproc)</tt></blockquote>
 */
public class OrafileDef {

    private final String name;

    private final OrafileVal val;

    public OrafileDef(String name, OrafileVal val) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (val == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.val = val;
    }

    public String getName() {
        return name;
    }

    public OrafileVal getVal() {
        return val;
    }

    public String toString() {
        return "( " + name + " = " + val + " )";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrafileDef that = (OrafileDef) o;
        return name.equals(that.name) && val.equals(that.val);
    }

    @Override
    public int hashCode() {
        return 31 * val.hashCode() + name.hashCode();
    }

}
