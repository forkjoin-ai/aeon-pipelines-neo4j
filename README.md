# Aeon Pipelines Neo4j Extension

Aeon Pipelines Neo4j is a Java stored-procedure extension for analyzing pipeline-shaped graphs inside Neo4j.

The fair brag is that it gives you a direct bridge between graph data you already have in Neo4j and the topological analysis surface from Aeon Pipelines. You do not need to export the graph first just to inspect it.

## What It Helps You Do

- analyze pipeline structures stored in Neo4j,
- calculate values such as `beta1` and the Buley measure,
- and call those procedures from normal Cypher queries.

## Installation

1. Build the JAR:

```bash
mvn clean package
```

2. Copy the resulting JAR from `target/aeon-pipelines-neo4j-1.0.0-SNAPSHOT.jar` into your Neo4j `plugins/` directory.
3. Restart Neo4j.

## Usage

### Analyze A Pipeline

```cypher
MATCH (source:ComputationNode {id: 'start-node'})
CALL aeon.pipeline.analyze(source, 'FLOWS_TO')
YIELD beta1, buleyMeasure, nodeCount, edgeCount
RETURN beta1, buleyMeasure;
```

### Find More Complex Areas

```cypher
MATCH (n:ComputationNode)
CALL aeon.pipeline.analyze(n, 'FLOWS_TO')
YIELD beta1, buleyMeasure
WHERE beta1 > 5
RETURN n.id, beta1, buleyMeasure
ORDER BY buleyMeasure DESC;
```

## Relationship Mapping

When expressing pipelines in Neo4j, the README assumes these relationship shapes:

- `FORK`: one node branching to several others
- `RACE`: several branches feeding a race node
- `FOLD`: several branches merging into one node
- `FLOWS_TO`: normal sequential movement

## Why This README Is Grounded

This is a small extension with a clear job. The strongest fair brag is that it lets Neo4j users run Aeon-style pipeline analysis where their graph already lives.
