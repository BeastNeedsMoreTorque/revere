require 'csv'
require 'neography'

@neo = Neography::Rest.new

rows = CSV.read(File.join(File.dirname(__FILE__), '..', 'data', 'PaulRevereAppD.csv'))

group_params = rows.shift.each_with_index.inject([]) {|acc, (group, idx)| acc << { :name => group, :idx => idx }; acc } 
@neo.execute_query("CREATE (:Group {group})", {:group => group_params})

people_params = rows.each_with_index.inject([]) do |acc, (group_membership, idx)| 
	surname, first_name = group_membership[0].split(".")
	acc << { :name => "#{first_name} #{surname}", :idx => idx } 
	acc 
end 
@neo.execute_query("CREATE (:Person {person})", {:person => people_params})

rows.each_with_index do |group_membership, idx|
	group_membership[1..-1].map(&:to_i).each_with_index.select { |member, _| member == 1 }.each do |member, group_idx|
		params = {:pid => idx, :gid => group_idx}
		@neo.execute_query("MATCH p:Person, g:Group WHERE p.idx = {pid} AND g.idx = {gid} CREATE p-[:MEMBER_OF]->g", params)
	end
end