package edu.gatech.gtri.orafile;

final class CaseInsensitiveString {

    private final String string;

    CaseInsensitiveString(String string) {
        this.string = string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CaseInsensitiveString that = (CaseInsensitiveString) o;

        return string.equalsIgnoreCase(that.string);
    }

    @Override
    public int hashCode() {
        return string.toUpperCase().hashCode();
    }

}
