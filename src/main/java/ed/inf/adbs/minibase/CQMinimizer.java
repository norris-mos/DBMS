package ed.inf.adbs.minibase;

import ed.inf.adbs.minibase.base.Atom;
import ed.inf.adbs.minibase.base.Query;
import ed.inf.adbs.minibase.base.Reducedq;
import ed.inf.adbs.minibase.base.RelationalAtom;
import ed.inf.adbs.minibase.base.Term;
import ed.inf.adbs.minibase.base.Variable;
import ed.inf.adbs.minibase.base.Head;
import ed.inf.adbs.minibase.base.IntegerConstant;
import ed.inf.adbs.minibase.parser.QueryParser;
import ed.inf.adbs.minibase.base.StringConstant;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.math.BigInteger;

/**
 *
 * Minimization of conjunctive queries
 *
 */
public class CQMinimizer {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: CQMinimizer input_file output_file");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        // minimizeCQ(inputFile, outputFile);

        minimizeCQ2(inputFile, outputFile);
    }
    // iterate through the body, remove atoms, check if homomorphism, remove
    // if there is homomorphism.

    /**
     * CQ minimization procedure
     *
     * Assume the body of the query from inputFile has no comparison atoms
     * but could potentially have constants in its relational atoms.
     * // *
     * //
     */

    public static void minimizeCQ(String inputFile, String outputFile) {
        try {
            Query query = QueryParser.parse(Paths.get(inputFile));
            Head qhead = query.getHead();
            List<Atom> queryAtoms = query.getBody(); // get the atoms in the query
            List<Atom> minimalQueryAtoms = new ArrayList<>(queryAtoms); // make a copy of the query atoms
            for (Atom atom : queryAtoms) {
                List<Atom> atomless = new ArrayList<>(minimalQueryAtoms);
                atomless.remove(atom); // remove the current atom from the minimal set of atoms
                Query q1 = new Query(qhead, minimalQueryAtoms);
                Query q2 = new Query(qhead, atomless);
                if (existsHomomorphism(q1, q2)) {
                    // if a homomorphism exists, the current atom can be removed from the query
                    queryAtoms.remove(atom);
                    System.out.println("True");
                } else {
                    // if no homomorphism exists, keep the current atom in the query
                    minimalQueryAtoms.add(atom);
                }
                Query min = new Query(qhead, queryAtoms);
                System.out.println("Entire query: " + min);
            }
        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

    public static void minimizeCQ2(String inputFile, String outputFile) {
        try {
            Query query = QueryParser.parse(Paths.get(inputFile));
            Head qhead = query.getHead();

            List<Atom> final_query = query.getBody();
            List<Atom> minimalQueryAtoms = new ArrayList<>(final_query);

            for (Atom atom : final_query) {
                List<Atom> atomless = new ArrayList<>(minimalQueryAtoms);
                atomless.remove(atom); // remove the current atom from the minimal set of atoms
                Query q1 = new Query(qhead, minimalQueryAtoms);
                Query q2 = new Query(qhead, atomless);

                if (existsHomomorphism(q1, q2)) {
                    // if a homomorphism exists, the current atom can be removed from the query
                    minimalQueryAtoms.remove(atom);
                    System.out.println("homo found: ");

                } else {
                    System.out.println("no homo found: ");

                    // if no homomorphism exists, keep the current atom in the query
                    continue;
                }

            }
            Query min = new Query(qhead, minimalQueryAtoms);
            System.out.println("full query: " + query);
            System.out.println("minimized: " + min);
        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }

    }

    // public static Reducedq queryvars(Head head, List<Atom> querybody) {
    // List<Term> mapterm = querybody.stream()
    // .map(atom -> ((RelationalAtom) atom).getTerms())
    // .flatMap(List::stream)
    // .collect(Collectors.toList());

    // System.out.println("const_list: " + map);
    // List<Term> var_terms = new ArrayList<>();
    // List<Term> const_terms = new ArrayList<>();
    // List<Variable> head_vars = head.getVariables();

    // for (Term term : mapterm) {
    // if (term instanceof Variable) {
    // String varName = ((Variable) term).getName();
    // if (!head_vars.stream().anyMatch(v -> v.getName().equals(varName))
    // && !var_terms.contains(term)) {
    // var_terms.add(term);
    // } else if (!const_terms.contains(term)) {
    // const_terms.add(term);
    // }
    // } else if (!const_terms.contains(term)) {
    // const_terms.add(term);
    // }
    // }

    // List<Term> const_list = const_terms.stream()
    // .distinct()
    // .collect(Collectors.toList());

    // List<Term> var_list = var_terms.stream()
    // .distinct()
    // .collect(Collectors.toList());

    // System.out.println("const_list: " + const_list);
    // System.out.println("var_list: " + var_list);

    // return new Reducedq(head, querybody, const_list, var_list);
    // }

    // // the minimized query contains only the atoms that are necessary for a
    // homomorphism to exist
    // Query minimizedQuery = new Query(query.getHead(), queryAtoms);

    // to check for homomorphisms you need to map all variables to
    // public static existsHomomorphism(Reducedq q1, Reducedq q2){

    // }
    public static Reducedq queryvars(Head head, List<Atom> querybody) {

        List<Term> mapterm = querybody.stream()
                .map(atom -> ((RelationalAtom) atom).getTerms())
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<Term> var_terms = new ArrayList<>();
        List<Term> const_terms = new ArrayList<>();
        List<Variable> head_vars = head.getVariables();
        List<Term> head_variables = new ArrayList<>(head_vars);

        List<Term> const_int_terms = new ArrayList<>();
        List<Term> const_s_terms = new ArrayList<>();

        for (int i = 0; i < mapterm.size(); i++) {
            if (mapterm.get(i) instanceof Variable) {
                String varName = ((Variable) mapterm.get(i)).getName();
                if (!head_vars.stream().anyMatch(v -> v.getName().equals(varName))
                        && !var_terms.contains(mapterm.get(i))) {
                    var_terms.add(mapterm.get(i));
                }

            } else if (mapterm.get(i) instanceof IntegerConstant) {
                Integer const_val = ((IntegerConstant) mapterm.get(i)).getValue();
                if (!const_int_terms.stream().anyMatch(c -> ((IntegerConstant) c).getValue().equals(const_val))) {
                    const_int_terms.add(mapterm.get(i));

                }

            } else if (mapterm.get(i) instanceof StringConstant) {
                String const_s_val = ((StringConstant) mapterm.get(i)).getValue();
                if (!const_s_terms.stream().anyMatch(c -> ((StringConstant) c).getValue().equals(const_s_val))) {
                    const_s_terms.add(mapterm.get(i));

                }
            }

        }

        List<Term> const_list = new ArrayList<>(const_int_terms);
        const_list.addAll(const_s_terms);
        const_list.addAll(head_variables);

        // Set<Term> var_terms_set = new HashSet<>(var_terms);
        // List<Term> var_list = new ArrayList<>();
        // var_list.addAll(var_terms_set);

        // System.out.println("const_terms_set: " + const_list);
        // System.out.println("var_terms_set: " + var_list);

        // return the result of the method
        return new Reducedq(head, querybody, const_list, var_terms);
    }

    public static Map<Term, Term> findSubstitution(List<Term> atomvars, List<Term> atommap,
            Set<Map<Term, Term>> existingSubstitutions) {
        Random random = new Random();
        Map<Term, Term> substitution = new HashMap<>();
        for (Term var : atomvars) {
            substitution.put(var, atommap.get(random.nextInt(atommap.size())));
        }
        if (existingSubstitutions.contains(substitution)) {

            return findSubstitution(atomvars, atommap, existingSubstitutions);
        } else {

            existingSubstitutions.add(substitution);
            // System.err.println(substitution);

            return substitution;
        }
    }

    public static List<HashMap<Term, Term>> generateMappings(List<Term> atomvars, List<Term> atommap) {
        List<HashMap<Term, Term>> mappings = new ArrayList<HashMap<Term, Term>>();

        if (atomvars.size() == 0) {
            return mappings;
        }

        for (int i = 0; i < atommap.size(); i++) {
            Term var1 = atommap.get(i);

            if (atomvars.size() == 1) {
                HashMap<Term, Term> mapping = new HashMap<Term, Term>();
                mapping.put(atomvars.get(0), var1);
                mappings.add(mapping);
            } else {
                List<Term> remainingVars = atomvars.subList(1, atomvars.size());
                List<HashMap<Term, Term>> subMappings = generateMappings(remainingVars, atommap);

                for (int j = 0; j < subMappings.size(); j++) {
                    HashMap<Term, Term> subMapping = subMappings.get(j);
                    HashMap<Term, Term> mapping = new HashMap<Term, Term>();
                    mapping.put(atomvars.get(0), var1);
                    mapping.putAll(subMapping);
                    mappings.add(mapping);
                }
            }
        }

        return mappings;
    }

    public static boolean existsHomomorphism(Query qoriginal, Query qatomless) {

        // Reduce the original and atomless queries to simplify variable matching
        Reducedq q1 = queryvars(qoriginal.getHead(), qoriginal.getBody());
        Reducedq q2 = queryvars(qatomless.getHead(), qatomless.getBody());

        boolean homomorphismFound = false;
        boolean isSame = false;
        Set<Map<Term, Term>> existingSubstitutions = new HashSet<>();
        List<HashMap<Term, Term>> combo_subs = generateMappings(q1.getVars(), q2.getMapspace());

        for (int i = 0; i < combo_subs.size() && !homomorphismFound; i++) {
            HashMap<Term, Term> subdict = combo_subs.get(i);

            // Apply the substitution dictionary to the query
            List<RelationalAtom> substitutedQuery = q1.getBody().stream().map(atom -> {
                List<Term> terms = ((RelationalAtom) atom).getTerms();
                List<Term> substitutedTerms = terms.stream().map(term -> {
                    if (subdict.containsKey(term)) {
                        Term mappedValue = subdict.get(term);
                        return mappedValue;
                    }
                    return term;
                })
                        .collect(Collectors.toList());

                return new RelationalAtom(((RelationalAtom) atom).getName(), substitutedTerms);
            })
                    .collect(Collectors.toList());
            List<RelationalAtom> uniqueAtomsList = new ArrayList<>();
            Set<String> atomNames = new HashSet<>();
            for (RelationalAtom atom : substitutedQuery) {
                String atomName = atom.toString();
                // System.out.println("atom name " + atom.toString());
                if (atomNames.contains(atomName)) {
                    // System.out.println("Duplicate atom name found: " + atomName);
                    // Handle the duplicate atom here
                    // uniqueAtomsList.remove(atom);
                    continue;

                } else {
                    atomNames.add(atomName);
                    uniqueAtomsList.add(atom);
                }
            }

            // Remove duplicates and check if it's equal to qatomless
            // Set<RelationalAtom> uniqueAtoms = new HashSet<>(substitutedQuery);

            // System.out.println("query reduced " + q2.getBody());
            System.out.println("substituted query " + substitutedQuery);

            System.out.println("unique atoms " + uniqueAtomsList);
            // found [R(w, 5, z), R(w, 5, z), R(x, 5, u)] and [R(x, 5, u), R(w, 5, z)]
            Set<String> q2body = q2.getBody().stream().map(bod -> bod.toString()).collect(Collectors.toSet());
            isSame = atomNames.equals(q2body);

            if (isSame) {
                homomorphismFound = true;
                break;
            }
        }

        return homomorphismFound;
    }

    public static void parsingExample2(String filename) {

        try {
            Query query = QueryParser.parse(Paths.get(filename));
            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w)");
            // Query query = QueryParser.parse("Q(x) :- R(x, 'z'), S(4, z, w)");

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);
            Reducedq rq = queryvars(head, query.getBody());

        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

    // }

    public static void parsingExample(String filename) {

        try {
            Query query = QueryParser.parse(Paths.get(filename));
            // Query query = QueryParser.parse("Q(x, y) :- R(x, z), S(y, z, w)");
            // Query query = QueryParser.parse("Q(x) :- R(x, 'z'), S(4, z, w)");

            System.out.println("Entire query: " + query);
            Head head = query.getHead();
            System.out.println("Head: " + head);

            // System.out.println("Head vars: " + head.getVariables());
            // System.out.println("Head name: " + head.getName());

            List<Atom> body = query.getBody();
            System.out.println("Body 1: " + body);

            List<Term> termmap = new ArrayList<>();
            for (int i = 0; i < body.size(); i++) {
                RelationalAtom term1 = (RelationalAtom) body.get(i);
                List<Term> terms1 = term1.getTerms();
                termmap.addAll(terms1);
            }

            List<Term> mapterm = body.stream()
                    .map(atom -> ((RelationalAtom) atom).getTerms())
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            List<Term> var_terms = new ArrayList<>();
            List<Term> const_int_terms = new ArrayList<>();
            List<Term> const_s_terms = new ArrayList<>();
            List<Variable> head_vars = head.getVariables();
            // for (int i = 0; i < mapterm.size(); i++) {

            // if (mapterm.get(i) instanceof Variable) {
            // String varName = ((Variable) mapterm.get(i)).getName();
            // System.out.println("Head name: " + varName);

            // if (!head_vars.stream().anyMatch(v -> v.getName().equals(varName))
            // && !var_terms.contains(mapterm.get(i))) {
            // var_terms.add(mapterm.get(i));
            // } else if (!const_terms.contains(mapterm.get(i))) {
            // const_terms.add(mapterm.get(i));
            // }

            // } else if (!const_terms.contains(mapterm.get(i))) {
            // const_terms.add(mapterm.get(i));
            // }
            // }

            for (int i = 0; i < mapterm.size(); i++) {
                if (mapterm.get(i) instanceof Variable) {
                    String varName = ((Variable) mapterm.get(i)).getName();
                    if (!head_vars.stream().anyMatch(v -> v.getName().equals(varName))
                            && !var_terms.contains(mapterm.get(i))) {
                        var_terms.add(mapterm.get(i));
                    }

                } else if (mapterm.get(i) instanceof IntegerConstant) {
                    Integer const_val = ((IntegerConstant) mapterm.get(i)).getValue();
                    if (!const_int_terms.stream().anyMatch(c -> ((IntegerConstant) c).getValue().equals(const_val))) {
                        const_int_terms.add(mapterm.get(i));

                    }

                } else if (mapterm.get(i) instanceof StringConstant) {
                    String const_s_val = ((StringConstant) mapterm.get(i)).getValue();
                    if (!const_s_terms.stream().anyMatch(c -> ((StringConstant) c).getValue().equals(const_s_val))) {
                        const_s_terms.add(mapterm.get(i));

                    }
                }

            }
            List<Term> mapspace = new ArrayList<>(const_int_terms);
            mapspace.addAll(const_s_terms);
            mapspace.addAll(var_terms);

            // Set<Term> var_terms_set = new HashSet<>(var_terms);
            // System.out.println("var_terms_set: " + var_terms);
            // System.out.println("const_terms_set: " + const_terms_set);
            // System.out.println("const_int_terms_set: " + const_int_terms);
            // System.out.println("const_s_terms_set: " + const_s_terms);
            // System.out.println("combined: " + mapspace);
            // List<Term> combinedlist = new ArrayList<>(var_terms);
            // combinedlist.addAll(const_terms);
            // System.out.println("combinedlist: " + combinedlist);
            // Set<Term> uni = combinedlist.steam().map(term -> ())

            Reducedq rq = queryvars(head, body);
            System.out.println("Head name: " + rq.getHead());
            System.out.println("Body name: " + rq.getBody());
            System.out.println("Vars name: " + rq.getVars());
            System.out.println("Const name: " + rq.getConst());
            System.out.println("mapspace name: " + rq.getMapspace());

            // RelationalAtom term1 = (RelationalAtom) body.get(0);

            // List<Term> terms1 = term1.getTerms();
            // System.out.println("term 1: " + terms1);
            // Atom term2 = body.get(1);
            // System.out.println("term 2: " + term2);

            // for (int i = 0; i < body.size(); i++) {
            // List<Atom> slice = body.subList(0, i+1);
            // System.out.println("sublist: " + slice);

            // }

        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
