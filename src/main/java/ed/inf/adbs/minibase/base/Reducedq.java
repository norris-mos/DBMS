package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.List;

import ed.inf.adbs.minibase.Utils;

public class Reducedq {
    private Head head;

    private List<Atom> body;

    private List<Term> const_terms;

    private List<Term> var_terms;

    public Reducedq(Head head, List<Atom> body, List<Term> const_terms, List<Term> var_terms) {
        this.head = head;
        this.body = body;
        this.const_terms = const_terms;
        this.var_terms = var_terms;

    }

    public List<Term> getVars() {
        return var_terms;
    }

    public List<Term> getConst() {
        return const_terms;
    }

    public Head getHead() {
        return head;
    }

    public List<Term> getMapspace() {
        List<Term> combinedList = new ArrayList<>(var_terms);
        combinedList.addAll(const_terms);

        return combinedList;
    }

    public List<Atom> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return head + " :- " + Utils.join(body, ", ");
    }

}
