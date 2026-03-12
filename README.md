# Aeon Pipelines Neo4j Extension

This extension allows you to analyze and execute Aeon-style topological pipelines directly inside a Neo4j database using Java-based Stored Procedures.

## Features
- **Topological Analysis**: Calculate Betti numbers ($\beta_1$) and Buley metrics directly on graph nodes/relationships.
- **Cypher Integration**: Use standard Cypher to define pipelines and then call Aeon procedures to validate or analyze them.

## Installation
1. Build the JAR:
   ```bash
   mvn clean package
   ```
2. Copy the resulting JAR from `target/aeon-pipelines-neo4j-1.0.0-SNAPSHOT.jar` to your Neo4j `plugins/` directory.
3. Restart Neo4j.

## Usage

### 1. Analyze a Pipeline Structure
Once you have a graph of nodes connected by `FLOWS_TO` relationships, you can analyze its complexity:

```cypher
MATCH (source:ComputationNode {id: 'start-node'})
CALL aeon.pipeline.analyze(source, 'FLOWS_TO')
YIELD beta1, buleyMeasure, nodeCount, edgeCount
RETURN beta1, buleyMeasure;
```

### 2. Find High-Complexity "Hotspots"
Find nodes that serve as the root of high-entropy topological superpositions:

```cypher
MATCH (n:ComputationNode)
CALL aeon.pipeline.analyze(n, 'FLOWS_TO')
YIELD beta1, buleyMeasure
WHERE beta1 > 5
RETURN n.id, beta1, buleyMeasure
ORDER BY buleyMeasure DESC;
```

## Mapping Primitives to Cypher
When expressing pipelines in Neo4j, use these relationship types:
- `FORK`: Multiple outgoing relationships from one node.
- `RACE`: Multiple outgoing relationships to a `RACE` node.
- `FOLD`: Multiple incoming relationships to one node.
- `FLOWS_TO`: Standard sequential transition.
