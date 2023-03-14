package ed.inf.adbs.minibase.base;

import ed.inf.adbs.minibase.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class Query {
    private Head head;

    private List<Atom> body;

    private List<ComparisonAtom> ComparisonAtom;

    public Query(Head head, List<Atom> body) {
        this.head = head;
        this.body = body;
    }

    public Head getHead() {
        return head;
    }

    public List<Atom> getBody() {
        return body;
    }

    public List<ComparisonAtom> getComparisonAtoms() {

        List<ComparisonAtom> catoms = body.stream()
                .filter(atom -> atom instanceof ComparisonAtom)
                .map(atom -> ((ComparisonAtom) atom))
                .collect(Collectors.toList());
        return catoms;

    }

    public List<RelationalAtom> getRelationalAtoms() {

        List<RelationalAtom> ratoms = body.stream()
                .filter(atom -> atom instanceof RelationalAtom)
                .map(atom -> ((RelationalAtom) atom))
                .collect(Collectors.toList());
        return ratoms;

    }

    @Override
    public String toString() {
        return head + " :- " + Utils.join(body, ", ");
    }
}
