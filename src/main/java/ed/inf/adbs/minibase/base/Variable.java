package ed.inf.adbs.minibase.base;

import java.util.Objects;

public class Variable extends Term {
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Variable)) {
            return false;
        }
        Variable var = (Variable) obj;
        return Objects.equals(name, var.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
