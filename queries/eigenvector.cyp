MATCH p1:Person-[:MEMBER_OF]->()<-[:MEMBER_OF]-p2:Person
WHERE p1.name = "Thomas Young"
RETURN p2, COUNT(p1)

START p1=node(*)
MATCH p1-[:MEMBER_OF]->()<-[?:MEMBER_OF]-p2:Person
WHERE p1.name! = "Thomas Young" AND p1:Person
RETURN p1, p2;

MATCH p1:Person, p2:Person
WHERE p1.name = "Thomas Young"
WITH p1, p2
MATCH p = p1-[?:MEMBER_OF]->()<-[?:MEMBER_OF]-p2
WITH p1.name AS p1, p2.name AS p2, COUNT(p) AS links
ORDER BY p2
RETURN p1, p2, links;

// adjacency matrix of sub graph
MATCH p1:Person, p2:Person
WITH p1, p2
MATCH p = p1-[?:MEMBER_OF]->()<-[?:MEMBER_OF]-p2
WITH p1.name AS p1, p2.name AS p2, COUNT(p) AS links
ORDER BY p2
RETURN p1, COLLECT(links) AS row
ORDER BY p1

MATCH p1:Person, p2:Person
WITH p1, p2
MATCH p = p1-[?:MEMBER_OF]->()<-[?:MEMBER_OF]-p2
WITH p1, p2, COUNT(p) AS links
ORDER BY p2.name
RETURN p1.name AS person, ID(p1) AS id, COLLECT(links) AS row
ORDER BY p1.name

// narrowing

MATCH p1:Person, p2:Person
WHERE p1.name = "Paul Revere"
WITH p1, p2
MATCH p = p1-[?:MEMBER_OF]->()<-[?:MEMBER_OF]-p2

RETURN p1, p2, COUNT(p)

WITH p1.name AS p1, p2.name AS p2, COUNT(p) AS links
ORDER BY p2
RETURN p1, COLLECT(links) AS row
ORDER BY p1
