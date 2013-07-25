require 'csv'
require 'open-uri'

rows = CSV.read(File.join(File.dirname(__FILE__), 'data', 'PaulRevereAppD.csv'))
group_cypher_statements, people_cypher_statements, rel_cypher_statements = [], [], []

groups = rows.shift
groups.each_with_index do |group, idx|
	group_cypher_statements << "CREATE (group#{idx}:Group {name: '#{group}'})"
end

rows.each_with_index do |group_membership, idx|
	surname, first_name = group_membership[0].split(".")

	people_cypher_statements << "CREATE (person#{idx}:Person {name: '#{first_name} #{surname}'})"
	group_membership[1..-1].map(&:to_i).each_with_index do |member, group_idx|	
		rel_cypher_statements << "CREATE (person#{idx})-[:MEMBER_OF]->(group#{group_idx})" if member == 1
	end
end

open("data/statements.cyp", 'w') { |f|
	[group_cypher_statements + people_cypher_statements + rel_cypher_statements].each do |statement|
		f.puts(statement)
	end
}