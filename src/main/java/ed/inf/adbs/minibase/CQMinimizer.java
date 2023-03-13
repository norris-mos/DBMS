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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        DataCatalog dataCatalog = DataCatalog.getInstance();
        dataCatalog.initialize();
        String path = dataCatalog.getDatabaseSchema("S");
        System.out.println(path);

        // minimizeCQ2(inputFile, outputFile);
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

    public static Reducedq queryvars(Head head, List<Atom> querybody) {
        // Extract all terms from querybody
        List<Term> mapterm = querybody.stream()
                .map(atom -> ((RelationalAtom) atom).getTerms())
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // Initialize lists for variables, constants, head variables, integer constants,
        // and string constants
        List<Term> var_terms = new ArrayList<>();
        List<Term> const_terms = new ArrayList<>();
        List<Variable> head_vars = head.getVariables();
        List<Term> head_variables = new ArrayList<>(head_vars);
        List<Term> const_int_terms = new ArrayList<>();
        List<Term> const_s_terms = new ArrayList<>();

        // Iterate through all terms extracted from querybody
        for (int i = 0; i < mapterm.size(); i++) {
            // If the term is a variable
            if (mapterm.get(i) instanceof Variable) {
                // Get the variable name
                String varName = ((Variable) mapterm.get(i)).getName();
                // If the variable is not in head variables and has not been added to var_terms
                // yet
                if (!head_vars.stream().anyMatch(v -> v.getName().equals(varName))
                        && !var_terms.contains(mapterm.get(i))) {
                    // Add variable to var_terms list
                    var_terms.add(mapterm.get(i));
                }

                // If the term is an integer constant
            } else if (mapterm.get(i) instanceof IntegerConstant) {
                // Get the integer constant value
                Integer const_val = ((IntegerConstant) mapterm.get(i)).getValue();
                // If the integer constant value is not in const_int_terms list
                if (!const_int_terms.stream().anyMatch(c -> ((IntegerConstant) c).getValue().equals(const_val))) {
                    // Add integer constant to const_int_terms list
                    const_int_terms.add(mapterm.get(i));

                    // If the term is a string constant
                } else if (mapterm.get(i) instanceof StringConstant) {
                    // Get the string constant value
                    String const_s_val = ((StringConstant) mapterm.get(i)).getValue();
                    // If the string constant value is not in const_s_terms list
                    if (!const_s_terms.stream().anyMatch(c -> ((StringConstant) c).getValue().equals(const_s_val))) {
                        // Add string constant to const_s_terms list
                        const_s_terms.add(mapterm.get(i));
                    }
                }
            }
        }

        // Combine integer and string constant terms, as well as head variables, into
        // const_list
        List<Term> const_list = new ArrayList<>(const_int_terms);
        const_list.addAll(const_s_terms);
        const_list.addAll(head_variables);

        // Return a new Reducedq object with the given head, query body, const_list, and
        // var_terms
        return new Reducedq(head, querybody, const_list, var_terms);
    }

    /**
     * Generates a new substitution for the given variable and term lists, avoiding
     * previously existing substitutions
     * 
     * @param atomvars              the list of variables in the atom
     * @param atommap               the list of terms that the variables can be
     *                              mapped to
     * @param existingSubstitutions the set of substitutions that have already been
     *                              used
     * @return a new substitution map
     */

    /**
     * Generates a list of all possible mappings between variables in a list and
     * terms in another list.
     * 
     * @param atomvars the list of variables to be mapped
     * @param atommap  the list of terms that the variables can be mapped to
     * @return a list of all possible mappings as hash maps
     */
    public static List<HashMap<Term, Term>> generateMappings(List<Term> atomvars, List<Term> atommap) {
        List<HashMap<Term, Term>> mappings = new ArrayList<HashMap<Term, Term>>();

        // base case: if there are no variables to be mapped, return an empty list of
        // mappings
        if (atomvars.size() == 0) {
            return mappings;
        }

        // for each term in the map list, create a mapping for the first variable in the
        // variable list
        for (int i = 0; i < atommap.size(); i++) {
            Term var1 = atommap.get(i);

            // if there is only one variable, create a mapping and add it to the list of
            // mappings
            if (atomvars.size() == 1) {
                HashMap<Term, Term> mapping = new HashMap<Term, Term>();
                mapping.put(atomvars.get(0), var1);
                mappings.add(mapping);
            }
            // if there are more than one variable, recursively generate sub-mappings for
            // the remaining variables
            else {
                List<Term> remainingVars = atomvars.subList(1, atomvars.size());
                List<HashMap<Term, Term>> subMappings = generateMappings(remainingVars, atommap);

                // for each sub-mapping, create a mapping that maps the first variable to the
                // current term,
                // and merge it with the sub-mapping to create a complete mapping for all
                // variables
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
            }).collect(Collectors.toList());

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
            Set<String> q2body = q2.getBody().stream().map(bod -> bod.toString())
                    .collect(Collectors.toSet());
            isSame = atomNames.equals(q2body);

            if (isSame) {
                homomorphismFound = true;
                break;
            }
        }

        return homomorphismFound;
    }

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

            Reducedq rq = queryvars(head, body);
            System.out.println("Head name: " + rq.getHead());
            System.out.println("Body name: " + rq.getBody());
            System.out.println("Vars name: " + rq.getVars());
            System.out.println("Const name: " + rq.getConst());
            System.out.println("mapspace name: " + rq.getMapspace());

        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
    }

}
