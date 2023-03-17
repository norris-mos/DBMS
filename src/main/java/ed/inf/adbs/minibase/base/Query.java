package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.Utils;
import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.ComparisonOperator;
import ed.inf.adbs.minibase.base.JoinAtom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Query {
    private Head head;

    private List<ComparisonAtom> comparisonAtoms;
    private List<RelationalAtom> relationalAtoms;

    private List<ComparisonAtom> joins;
    private List<ComparisonAtom> selections;

    private List<Atom> body;

    public Query(Head head, List<Atom> body) {
        this.head = head;
        this.body = body;
        this.comparisonAtoms = new ArrayList<>();
        this.relationalAtoms = new ArrayList<>();

        this.joins = new ArrayList<>();
        this.selections = new ArrayList<>();
        separateComparisonAtoms();

    }

    public Head getHead() {
        return head;
    }

    public List<Atom> getBody() {
        return body;
    }

    public List<RelationalAtom> getRelationalAtoms() {
        return relationalAtoms;
    }

    public List<ComparisonAtom> getComparisonAtoms() {
        return comparisonAtoms;

    }

    public List<ComparisonAtom> getJoins() {
        return joins;
    }

    public List<ComparisonAtom> getSelections() {
        return selections;
    }

    private void separateComparisonAtoms() {
        List<RelationalAtom> relations = new ArrayList<>();
        Map<String, RelationalAtom> varToRelationalAtom = new HashMap<>();
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                RelationalAtom relationalAtom = (RelationalAtom) atom;
                relations.add(relationalAtom);
                relationalAtoms.add(relationalAtom);
                for (Term var : relationalAtom.getTerms()) {
                    if (varToRelationalAtom.containsKey(var.toString())) {
                        // Variable already exists in another relational atom
                        joins.add(new JoinAtom(var, varToRelationalAtom.get(var.toString()), var, relationalAtom,
                                ComparisonOperator.EQ));
                    } else {
                        varToRelationalAtom.put(var.toString(), relationalAtom);
                    }
                }
            } else if (atom instanceof ComparisonAtom) {

                ComparisonAtom compAtom = (ComparisonAtom) atom;
                comparisonAtoms.add(compAtom);
                Term leftTerm = compAtom.getTerm1();
                Term rightTerm = compAtom.getTerm2();
                boolean leftIsVariable = leftTerm instanceof Variable;
                boolean rightIsVariable = rightTerm instanceof Variable;
                if (leftIsVariable && rightIsVariable) {
                    // Both terms are variables, add to joins list
                    String leftVar = ((Variable) leftTerm).getName();
                    String rightVar = ((Variable) rightTerm).getName();
                    RelationalAtom leftRelationalAtom = varToRelationalAtom.get(leftVar);
                    RelationalAtom rightRelationalAtom = varToRelationalAtom.get(rightVar);
                    if (leftRelationalAtom != rightRelationalAtom) {
                        joins.add(compAtom);
                    } else {
                        selections.add(compAtom);
                    }
                } else if (leftIsVariable && !rightIsVariable) {
                    // Left term is a variable, right term is a constant, add to selections list
                    selections.add(compAtom);
                } else if (!leftIsVariable && rightIsVariable) {
                    // Left term is a constant, right term is a variable, add to selections list
                    selections.add(new ComparisonAtom(rightTerm, leftTerm, compAtom.getOp()));
                } else {
                    // Both terms are constants, ignore
                }
            }
        }
    }

    public List<ComparisonAtom> getEquiJoinConditions() {
        List<ComparisonAtom> joinConditions = new ArrayList<>();
        List<RelationalAtom> relations = new ArrayList<>();
        for (Atom atom : body) {
            if (atom instanceof RelationalAtom) {
                relations.add(((RelationalAtom) atom));
            }
        }
        for (RelationalAtom relation1 : relations) {
            for (RelationalAtom relation2 : relations) {
                if (relation1.getName() != relation2.getName()) {
                    for (Term attribute1 : relation1.getTerms()) {
                        for (Term attribute2 : relation2.getTerms()) {
                            System.out
                                    .println("just checking the term vs string issue:" + attribute1.equals(attribute2));
                            if (attribute1.equals(attribute2)) {

                                joinConditions.add(new ComparisonAtom(attribute1, attribute2, ComparisonOperator.EQ));

                            }
                        }
                    }
                }
            }
        }
        return joinConditions;
    }

    @Override
    public String toString() {
        return head + " :- " + Utils.join(body, ", ");
    }
}
