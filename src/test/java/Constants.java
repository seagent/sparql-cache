import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

public class Constants {

	public static final String LOCAL_DBPEDIA_ENDPOINT = "http://155.223.25.212:7000/sparql/";

	/**
	 * Query that retrieves Turkish persons from {@link #LOCAL_DBPEDIA_ENDPOINT}
	 * endpoint
	 */
	public static final String DBPEDIA_GET_TURKISH_PERSONS = "PREFIX rdf: <"
			+ RDF.getURI() + "> " + "PREFIX foaf: <" + FOAF.getURI() + "> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX dbp-ont: <http://dbpedia.org/ontology/>"
			+ "SELECT * WHERE {"
			+ "?person dbp-ont:nationality dbpedia:Turkey. "
			+ "?person rdf:type foaf:Person.}";

	/**
	 * Query that is similar to {@link #DBPEDIA_GET_TURKISH_PERSONS} only
	 * different in variable names.
	 */
	public static final String DBPEDIA_GET_TURKISH_PERSONS_2 = "PREFIX rdf: <"
			+ RDF.getURI() + "> " + "PREFIX foaf: <" + FOAF.getURI() + "> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX dbp-ont: <http://dbpedia.org/ontology/>"
			+ "SELECT * WHERE {"
			+ "?person2 dbp-ont:nationality dbpedia:Turkey. "
			+ "?person2 rdf:type foaf:Person.}";

	/**
	 * Query that is similar to {@link #DBPEDIA_GET_TURKISH_PERSONS} different
	 * in variable names and triple positions.
	 */
	public static final String DBPEDIA_GET_TURKISH_PERSONS_3 = "PREFIX rdf: <"
			+ RDF.getURI() + "> " + "PREFIX foaf: <" + FOAF.getURI() + "> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX dbp-ont: <http://dbpedia.org/ontology/>"
			+ "SELECT * WHERE {" + "?person2 rdf:type foaf:Person."
			+ "?person2 dbp-ont:nationality dbpedia:Turkey. " + "}";

}
