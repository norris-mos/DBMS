package ed.inf.adbs.minibase;

import java.util.List;
import java.util.stream.Collectors;

import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.ComparisonAtom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Variable;

public class QueryInterpreter {

    public static String toSql(Query query) {
        StringBuilder sql = new StringBuilder();

        // Create SELECT statement for head
        sql.append("SELECT ");
        // if (query.getHead().isDistinct()) {
        // sql.append("DISTINCT ");
        // }
        List<Variable> headVars = query.getHead().getVariables();
        if (headVars.isEmpty()) {
            sql.append("*");
        } else {
            sql.append(headVars.stream()
                    .map(Object::toString) // map each variable to a string
                    .collect(Collectors.joining(", ")));

        }

        // Create FROM statement for body atoms
        sql.append(" FROM ");
        List<Atom> body = query.getBody();
        // List<ComparisonAtom> catoms = query.getComparisonAtoms();
        List<RelationalAtom> ratoms = query.getRelationalAtoms();
        List<String> tables = ratoms.stream()
                .map(atom -> atom.getName())
                .distinct()
                .collect(Collectors.toList());
        sql.append(tables.stream().collect(Collectors.joining(", ")));

        // Create JOIN and WHERE clauses for relational atoms
        List<ComparisonAtom> catoms = query.getComparisonAtoms();
        if (!catoms.isEmpty()) {
            sql.append(" WHERE ");
            for (int i = 0; i < catoms.size(); i++) {
                ComparisonAtom atom = catoms.get(i);
                if (i > 0) {
                    sql.append(" AND ");
                }
                sql.append(atom.getTerm1String())
                        .append(" ")
                        .append(atom.getOpString())
                        .append(" ")
                        .append(atom.getTerm2String());
            }
        }

        // // Create GROUP BY and HAVING clauses for comparison atoms
        // List<ComparisonAtom> comparisonAtoms = query.getComparisonAtoms();
        // if (!comparisonAtoms.isEmpty()) {
        // sql.append(" GROUP BY ");
        // List<String> groupByVars = comparisonAtoms.stream()
        // .map(atom -> atom.getLeft().getName())
        // .collect(Collectors.toList());
        // sql.append(groupByVars.stream().collect(Collectors.joining(", ")));
        // sql.append(" HAVING ");
        // for (int i = 0; i < comparisonAtoms.size(); i++) {
        // ComparisonAtom atom = comparisonAtoms.get(i);
        // if (i > 0) {
        // sql.append(" AND ");
        // }
        // sql.append(atom.getOperator().getSymbol())
        // .append("(")
        // .append(atom.getLeft().getName())
        // .append(", ")
        // .append(atom.getRight().getName())
        // .append(")");
        // }
        // }

        return sql.toString();
    }
}
