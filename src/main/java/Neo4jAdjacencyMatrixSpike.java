import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.jblas.DoubleMatrix;

import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.totallylazy.Sequences.sort;


public class Neo4jAdjacencyMatrixSpike {
    public static void main(String[] args) throws SQLException {
        ClientResponse response = client()
                .resource("http://localhost:7474/db/data/cypher")
                .entity(queryAsJson(), MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .post(ClientResponse.class);

        JsonNode result = response.getEntity(JsonNode.class);
        ArrayNode rows = (ArrayNode) result.get("data");

        List<Double> principalEigenvector = JBLASSpike.getPrincipalEigenvector(new DoubleMatrix(asMatrix(rows)));

        List<Person> people = asPeople(rows);
        updatePeopleWithEigenvector(people, principalEigenvector);

        System.out.println(sort(people).take(10));

        updateNeo4jWithEigenvectors(people);
    }

    private static void updateNeo4jWithEigenvectors(List<Person> people) {
        for (Person person : people) {
            ObjectNode request = JsonNodeFactory.instance.objectNode();
            request.put("query", "START p = node({nodeId}) SET p.eigenvectorCentrality={value}");

            ObjectNode params = JsonNodeFactory.instance.objectNode();
            params.put("nodeId", person.nodeId);
            params.put("value", person.eigenvector);

            request.put("params", params);

            client().resource("http://localhost:7474/db/data/cypher")
                    .entity(request, MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(ClientResponse.class);
        }
    }

    private static ObjectNode queryAsJson() {
        ObjectNode request = JsonNodeFactory.instance.objectNode();
        request.put("query", "MATCH p1:Person, p2:Person\n" +
                "WITH p1, p2\n" +
                "MATCH p = p1-[?:MEMBER_OF]->()<-[?:MEMBER_OF]-p2\n" +
                "WITH p1, p2, COUNT(p) AS links\n" +
                "ORDER BY p2.name\n" +
                "RETURN p1.name AS person, ID(p1) AS id, COLLECT(links) AS row\n" +
                "ORDER BY p1.name");

        request.put("params", JsonNodeFactory.instance.objectNode());
        return request;
    }

    private static List<Person> asPeople(ArrayNode rows) {
        List<Person> people = new ArrayList<Person>();
        for (JsonNode row : rows) {
            long nodeId = row.get(1).asLong();
            people.add(new Person(nodeId, row.get(0).asText()));
        }
        return people;
    }

    static class Person implements  Comparable<Person> {

        private String name;
        private Double eigenvector;
        private long nodeId;

        public void addEigenvector(Double eigenvector) {
            this.eigenvector = eigenvector;
        }

        public Person(long nodeId, String name) {
            this.nodeId = nodeId;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", eigenvector=" + eigenvector +
                    ", nodeId=" + nodeId +
                    '}';
        }

        @Override
        public int compareTo(Person o) {
            return this.eigenvector.compareTo(o.eigenvector) * -1;
        }
    }

    private static void updatePeopleWithEigenvector(List<Person> names, List<Double> principalEigenvector) {
        for (int i = 0; i < names.size(); i++) {
            names.get(i).addEigenvector(principalEigenvector.get(i));
        }
    }

    private static double[][] asMatrix(ArrayNode rows) {
        double[][] matrix = new double[rows.size()][254];
        int rowCount = 0;

        for (JsonNode row : rows) {
            ArrayNode matrixRow = (ArrayNode) row.get(2);

            double[] rowInMatrix = new double[254];
            matrix[rowCount] = rowInMatrix;
            int columnCount = 0;
            for (JsonNode jsonNode : matrixRow) {
                matrix[rowCount][columnCount] = jsonNode.asInt();
                columnCount++;
            }

            rowCount++;
        }
        return matrix;
    }

    private static Client client() {
        DefaultClientConfig defaultClientConfig = new DefaultClientConfig();
        defaultClientConfig.getClasses().add(JacksonJsonProvider.class);
        return Client.create(defaultClientConfig);
    }
}
