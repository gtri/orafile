package org.codeswarm.orafile;

public class OraNamedParam {

    private final String name;

    private final OraParam param;

    public OraNamedParam(String name, OraParam param) {
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

    public OraParam getParam() {
        return param;
    }

    public String toString() {
        return "( " + name + " = " + param + " )";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OraNamedParam that = (OraNamedParam) o;
        return name.equals(that.name) && param.equals(that.param);
    }

    @Override
    public int hashCode() {
        return 31 * param.hashCode() + name.hashCode();
    }

}
