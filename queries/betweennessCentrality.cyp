CREATE (A {name: "A"})
CREATE (B {name: "B"})
CREATE (C {name: "C"})
CREATE (D {name: "D"})
CREATE (E {name: "E"})

CREATE A-[:TO]->E
CREATE A-[:TO]->B
CREATE B-[:TO]->C
CREATE B-[:TO]->E
CREATE C-[:TO]->D
CREATE E-[:TO]->D

START source=node(*), destination=node(*) 
MATCH p = allShortestPaths(source-[r:TO*]->destination) 
WHERE source <> destination
RETURN NODES(p)

START source=node(*), destination=node(*) 
MATCH p = allShortestPaths(source-[r:TO*]->destination) 
WHERE source <> destination  AND LENGTH(NODES(p)) > 2
RETURN NODES(p)

START source=node(*), destination=node(*) 
MATCH p = allShortestPaths(source-[r:TO*]->destination) 
WHERE source <> destination  AND LENGTH(NODES(p)) > 2
RETURN EXTRACT(n IN NODES(p): n.name)


START source=node(*), destination=node(*) 
MATCH p = allShortestPaths(source-[r:TO*]->destination) 
WHERE source <> destination  AND LENGTH(NODES(p)) > 2
WITH EXTRACT(n IN NODES(p): n.name) AS nodes
RETURN HEAD(nodes) AS source, HEAD(TAIL(TAIL(nodes))) AS destination, COLLECT(nodes) AS paths