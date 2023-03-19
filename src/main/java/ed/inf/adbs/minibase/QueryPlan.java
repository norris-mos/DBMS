// package ed.inf.adbs.minibase;

// import java.io.FileWriter;
// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;

// import ed.inf.adbs.minibase.base.JoinAtom;
// import ed.inf.adbs.minibase.base.Query;

// public class QueryPlan {
// private Query query;
// private Operator rootOperator;

// public QueryPlan(Query query) {
// this.query = query;
// this.rootOperator = null;
// }

// public void generatePlan() {
// // Construct the query plan based on the query operators
// // and store the root operator as an instance variable.
// // This method will depend on the specific query operators
// // and their implementation details.
// }

// public static List<JoinOperator> customJoinPlan(List<JoinAtom> joins) {
// List<JoinOperator> joinPlan = new ArrayList<>();
// joinPlan.add(JoinOperator())

// }

// public static List<String> joinPlan(List<String> joins) {
// List<String> joinOrder = new ArrayList<>();
// Set<String> tables = new HashSet<>();

// // Extract tables from join conditions
// for (String join : joins) {
// String[] parts = join.split("=");
// String leftSide = parts[0].trim();
// String rightSide = parts[1].trim();
// String leftTable = leftSide.split("\\.")[0].trim();
// String rightTable = rightSide.split("\\.")[0].trim();
// tables.add(leftTable);
// tables.add(rightTable);
// }

// // Determine join order
// String currentTable = tables.iterator().next();
// joinOrder.add(currentTable);
// tables.remove(currentTable);
// while (!tables.isEmpty()) {
// String nextTable = null;
// for (String table : tables) {
// for (String join : joins) {
// String[] parts = join.split("=");
// String leftSide = parts[0].trim();
// String rightSide = parts[1].trim();
// String leftTable = leftSide.split("\\.")[0].trim();
// String rightTable = rightSide.split("\\.")[0].trim();
// if ((leftTable.equals(currentTable) && rightTable.equals(table)) ||
// (leftTable.equals(table) && rightTable.equals(currentTable))) {
// nextTable = table;
// break;
// }
// }
// if (nextTable != null) {
// break;
// }
// }
// if (nextTable == null) {
// break;
// }
// joinOrder.add(nextTable);
// tables.remove(nextTable);
// currentTable = nextTable;
// }

// return joinOrder;
// }

// public void executePlan(String outputFilePath) throws IOException {
// ArrayList<String[]> results = new ArrayList<>();
// String[] nextTuple = this.rootOperator.getNextTuple();

// while (nextTuple != null) {
// results.add(nextTuple);
// nextTuple = this.rootOperator.getNextTuple();
// }

// try (FileWriter writer = new FileWriter(outputFilePath)) {
// CSVUtils.writeLine(writer, this.query.getHeadings());
// for (String[] tuple : results) {
// CSVUtils.writeLine(writer, tuple);
// }
// }

// System.out.printf("Query results stored in file: %s%n", outputFilePath);
// }

// }

// public static List<String> joinPlan(List<String> relations, List<String>
// attributes, List<String> joinConditions, String rootRelation) {
// List<String> joinOrder = new ArrayList<>();
// List<String> remainingRelations = new ArrayList<>(relations);
// String currentRelation = rootRelation;

// while (!remainingRelations.isEmpty()) {
// List<String> joinableRelations = new ArrayList<>();
// for (String relation : remainingRelations) {
// if (!relation.equals(currentRelation)) {
// boolean joinable = false;
// for (String condition : joinConditions) {
// String[] parts = condition.split("=");
// String leftSide = parts[0].trim();
// String rightSide = parts[1].trim();
// String leftRelation = leftSide.split("\\.")[0].trim();
// String rightRelation = rightSide.split("\\.")[0].trim();
// if ((leftRelation.equals(currentRelation) && rightRelation.equals(relation))
// || (leftRelation.equals(relation) && rightRelation.equals(currentRelation)))
// {
// joinable = true;
// break;
// }
// }
// if (joinable) {
// joinableRelations.add(relation);
// }
// }
// }
// if (!joinableRelations.isEmpty()) {
// String nextRelation = joinableRelations.get(0);
// joinOrder.add(nextRelation);
// remainingRelations.remove(nextRelation);
// currentRelation = nextRelation;
// } else {
// break;
// }
// }

// return joinOrder;
// }