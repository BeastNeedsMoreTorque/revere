require 'neography'

neo = Neography::Rest.new

betweenness_centrality = {}
neo.execute_query("MATCH p:Person RETURN p.name")["data"].each do |row|
	betweenness_centrality[row[0]] = 0
end
 
query =  " MATCH path = AllShortestPaths(p:Person-[:MEMBER_OF*..4]-otherPerson:Person)"
query << " WHERE p <> otherPerson AND LENGTH(NODES(path)) > 3"
query << " RETURN DISTINCT p.name, otherPerson.name, EXTRACT(node IN (FILTER(node IN NODES(path): LABELS(node) = [\"Person\"])): node.name)" 

result = neo.execute_query(query)["data"]
result.map { |row| { :from => row[0], :to => row[1], :through => row[2][1..-2] }}.group_by { |x| [x[:from], x[:to]] }.each do |k,v|
	v.each { |row| row[:through].each { |person| betweenness_centrality[person] += (1.0 / v.size) } }		
end

p betweenness_centrality.sort_by { |k,v| v }