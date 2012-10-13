package org.codeswarm.orafile;

public class OrafileDef {

    private final String name;

    private final OrafileVal param;

    public OrafileDef(String name, OrafileVal param) {
        if (name == null) {
            throw new NullPointerException();
        }
        if (param == null) {
            throw new NullPointerException();
        }
        this.name = name;
        this.param = param;
    }

    public String getName() {
        return name;
    }

    public OrafileVal getParam() {
        return param;
    }

    public String toString() {
        return "( " + name + " = " + param + " )";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrafileDef that = (OrafileDef) o;
        return name.equals(that.name) && param.equals(that.param);
    }

    @Override
    public int hashCode() {
        return 31 * param.hashCode() + name.hashCode();
    }

}
