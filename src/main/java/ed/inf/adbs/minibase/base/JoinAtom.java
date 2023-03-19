package ed.inf.adbs.minibase.base;

import java.util.ArrayList;
import java.util.List;

public class JoinAtom extends ComparisonAtom {

    private RelationalAtom relation1;
    private RelationalAtom relation2;

    public JoinAtom(Term term1, RelationalAtom relation1, Term term2, RelationalAtom relation2, ComparisonOperator op) {
        super(term1, term2, op);
        this.relation1 = relation1;
        this.relation2 = relation2;
    }

    public RelationalAtom getRelation1() {
        return relation1;
    }

    public RelationalAtom getRelation2() {
        return relation2;
    }

    public List<RelationalAtom> getRelations() {
        List<RelationalAtom> rels = new ArrayList<>();
        rels.add(relation1);
        rels.add(relation2);

        return rels;
    }

    @Override
    public String toString() {
        return relation1.getName() + "." + super.getTerm1String() +
                " " + super.getOpString() + " " +
                relation2.getName() + "." + super.getTerm2String();
    }
}
