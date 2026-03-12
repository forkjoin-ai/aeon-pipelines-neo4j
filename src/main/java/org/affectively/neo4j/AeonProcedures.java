package org.affectively.neo4j;

import org.neo4j.graphdb.*;
import org.neo4j.procedure.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * Neo4j Procedures for Aeon Pipelines.
 * Allows executing topological analysis directly within Neo4j via Cypher.
 */
public class AeonProcedures {

    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    /**
     * Result record for topological analysis.
     */
    public static class TopologyResult {
        public long beta0;
        public long beta1;
        public long nodeCount;
        public long edgeCount;
        public double buleyMeasure;

        public TopologyResult(long beta0, long beta1, long nodeCount, long edgeCount, double buleyMeasure) {
            this.beta0 = beta0;
            this.beta1 = beta1;
            this.nodeCount = nodeCount;
            this.edgeCount = edgeCount;
            this.buleyMeasure = buleyMeasure;
        }
    }

    /**
     * Analyzes a pipeline subgraph starting from a source node.
     * Usage: CALL aeon.pipeline.analyze(sourceNode, 'FLOWS_TO') YIELD beta1, buleyMeasure
     */
    @Procedure(name = "aeon.pipeline.analyze", mode = Mode.READ)
    @Description("Performs Aeon topological analysis on a pipeline graph structure.")
    public Stream<TopologyResult> analyze(
            @Name("startNode") Node startNode,
            @Name("relationshipType") String relType) {

        Set<Node> nodes = new HashSet<>();
        Set<Relationship> edges = new HashSet<>();
        
        // Traverse the graph to find all reachable nodes and edges
        Queue<Node> queue = new LinkedList<>();
        queue.add(startNode);
        nodes.add(startNode);

        RelationshipType type = RelationshipType.withName(relType);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            for (Relationship rel : current.getRelationships(type, Direction.OUTGOING)) {
                edges.add(rel);
                Node next = rel.getEndNode();
                if (nodes.add(next)) {
                    queue.add(next);
                }
            }
        }

        long n = nodes.size();
        long e = edges.size();
        
        // Simplified Betti calculation for Neo4j context
        // beta0 = connected components (assumed 1 for this traversal)
        long beta0 = 1;
        long beta1 = Math.max(0, e - n + beta0);

        // Buley Measurement: Composite complexity
        double buleyMeasure = (beta1 * 1.5) + (e * 0.5);

        return Stream.of(new TopologyResult(beta0, beta1, n, e, buleyMeasure));
    }
}
