package ed.inf.adbs.minibase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.ComparisonOperator;
import ed.inf.adbs.minibase.base.JoinAtom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.SumAggregate;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Variable;
import ed.inf.adbs.minibase.base.Head;

public class QueryInterpreter {
    private List<Operator> operators;
    private Map<String, Operator> scans;
    private List<ComparisonAtom> selections;
    private List<JoinOperator> joinOperators;

    public void QueryPlan(Query query) throws Exception {
        this.selections = query.getSelections();
        scans = new HashMap<>();
        operators = new ArrayList<>();
        joinOperators = new ArrayList<>();
        List<ComparisonAtom> joins = query.getJoins();
        List<ComparisonAtom> selections = query.getSelections();

        // Create a ScanOperator for each relational atom in the query
        for (RelationalAtom rel : query.getRelationalAtoms()) {
            ScanOperator scan = new ScanOperator(rel);
            // check if there are any selections
            if (!selections.isEmpty()) {
                List<String> stringTerms = rel.getTerms().stream().map(Term::toString).collect(Collectors.toList());

                for (ComparisonAtom sel : selections) {
                    Term c1_t = sel.getTerm1();
                    Term c2_t = sel.getTerm2();
                    String c1 = sel.getTerm1String();
                    String c2 = sel.getTerm2String();
                    if (stringTerms.contains(c1) && (c1_t instanceof Variable)
                            || stringTerms.contains(c2) && (c2_t instanceof Variable)) {
                        System.out.println("SELECTION " + sel.toString());
                        String scanname = scan.getRelationName();
                        List<ComparisonAtom> selectionOps = new ArrayList<>();
                        selectionOps.add(sel);
                        Operator explicitSelection = new SelectionOperator(scan, selectionOps);

                        scans.put(scanname, explicitSelection);
                        operators.add(explicitSelection);

                    } else {
                        List<String> selectionTerms = new ArrayList<>();
                        selectionTerms.add(sel.getTerm1String());
                        selectionTerms.add(sel.getTerm2String());
                        String scanname = scan.getRelationName();
                        scans.put(scanname, scan);
                        operators.add(scan);

                    }
                }
            } else {
                String scanname = scan.getRelationName();
                scans.put(scanname, scan);
                operators.add(scan);
            }
            // operators.add(new ScanOperator(rel));
        }

        // // Create a ProjectionOperator for the head of the query
        // List<String> vars = new ArrayList<>();
        // for (Variable var : query.getHead().getVariables()) {
        // vars.add(var.getName());
        // }
        // operators.add(new ProjectionOperator(vars));

        // Create a JoinOperator for each join condition in the query

        if (!joins.isEmpty()) {
            // do the first join

            JoinAtom j1 = (JoinAtom) joins.get(0);
            Operator child1 = scans.get(j1.getRelation1().getName());
            Operator child2 = scans.get(j1.getRelation2().getName());
            List<ComparisonAtom> joinAtom = new ArrayList<>();
            joinAtom.add(j1);
            List<String> usedRelations = new ArrayList<>();
            operators.add(new JoinOperator(child1, child2, joinAtom));
            joinOperators.add(new JoinOperator(child1, child2, joinAtom));

            usedRelations.add(j1.getRelation1().getName());
            usedRelations.add(j1.getRelation2().getName());

            System.out.println("JOIN " + j1.toString());

            for (int i = 1; i < joins.size(); i++) {

                ComparisonAtom join = joins.get(i);
                List<RelationalAtom> rels = ((JoinAtom) join).getRelations();
                for (RelationalAtom used : rels) {
                    List<ComparisonAtom> cast = new ArrayList<>();
                    cast.add(join);
                    if (!usedRelations.contains(used.getName())) {

                        Operator j_new = new JoinOperator(operators.get(i - 1), scans.get(used.getName()), cast);

                        operators.add(j_new);
                        joinOperators.add((JoinOperator) j_new);
                        String[] old_name = ((JoinOperator) j_new).getJoinAtom().split(" ");
                        String[] new_name = joinOperators.get(i - 1).getJoinAtom().split(" ");

                        // String new_name = old_name

                        System.out.println("JOIN " + new_name[0] + "&" + new_name[2] + "= " +
                                old_name[2]);
                        usedRelations.add(used.getName());
                        // fix the cross product issue by checking that all used relations
                        // account for or scans elese take cross product with last join operator.

                    }

                }
            }
            System.out.print("used" + usedRelations);
            for (String relations : scans.keySet()) {
                if (!usedRelations.contains(relations)) {
                    Operator unjoined = scans.get(relations);
                    Operator lastJoin = joinOperators.get(joinOperators.size() - 1);
                    Term r1 = new Term();
                    Term r2 = new Term();
                    List<ComparisonAtom> crosspAtom = new ArrayList<>();
                    crosspAtom.add(new ComparisonAtom(r1, r2, ComparisonOperator.XR));
                    Operator jCrossNew = new JoinOperator(lastJoin, unjoined, crosspAtom);
                    operators.add(jCrossNew);
                    joinOperators.add((JoinOperator) jCrossNew);
                    usedRelations.add(relations);
                    System.out.println("ADDED CROSS PRODUCT");

                }

            }
        } else {

            List<String> scanName = new ArrayList<>();
            List<String> usedScanName = new ArrayList<>();
            for (RelationalAtom rel : query.getRelationalAtoms()) {
                scanName.add(rel.getName());

            }
            Operator child1 = scans.get(scanName.get(0));
            Operator child2 = scans.get(scanName.get(1));
            usedScanName.add(scanName.get(0));
            usedScanName.add(scanName.get(1));
            Term r1 = new Term();
            Term r2 = new Term();
            List<ComparisonAtom> crosspAtom = new ArrayList<>();
            crosspAtom.add(new ComparisonAtom(r1, r2, ComparisonOperator.XR));
            Operator crossJoin = new JoinOperator(child1, child2, crosspAtom);
            operators.add(crossJoin);
            joinOperators.add((JoinOperator) crossJoin);

            for (String relations : scans.keySet()) {
                if (!usedScanName.contains(relations)) {
                    Operator unjoined = scans.get(relations);
                    Operator lastJoin = joinOperators.get(joinOperators.size() - 1);
                    crosspAtom.add(new ComparisonAtom(r1, r2, ComparisonOperator.XR));
                    Operator jCrossNew = new JoinOperator(lastJoin, unjoined, crosspAtom);
                    operators.add(jCrossNew);
                    joinOperators.add((JoinOperator) jCrossNew);
                    usedScanName.add(relations);
                    System.out.println("ADDED CROSS PRODUCT");

                }

            }

        }

        Head head = query.getHead();

        Operator finalOperator = operators.get(operators.size() - 1);
        List<Variable> finalProjection = head.getVariables();
        if (head.getSumAggregate() == null) {

            Operator finalProjections = new ProjectionOperator(finalOperator, finalProjection);
            System.out.println("PROJECTION " + finalProjection);
            operators.add(finalProjections);

        } else {
            SumAggregate agg = head.getSumAggregate();
            List<Term> gbyterms = new ArrayList<>();
            for (Variable v : finalProjection) {
                gbyterms.add((Term) v);

            }

            SumOperator sumOperator = new SumOperator(finalOperator, gbyterms, agg);
            System.out.println("SUM OPERATOR " + finalProjection);
            operators.add(sumOperator);

        }

        // // Create a SelectionOperator for each selection condition in the query
        // for (ComparisonAtom select : selections) {
        // Variable var = (Variable) select.getTerm1();
        // String value = select.getTerm2().toString();
        // ComparisonOperator op = select.getOp();
        // operators.add(new SelectionOperator(var.getName(), op, value));
        // }

    }

    public List<Operator> getOperators() {
        return operators;
    }

    public List<ComparisonAtom> getExplicitSelections() {
        return selections;
    }
}
