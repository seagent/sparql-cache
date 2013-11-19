package test;

import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

public class Constants {


	public static final String LOCAL_DBPEDIA_ENDPOINT = "http://155.223.25.212:7000/sparql/";
	public static final String LOCAL_DRUGBANK_ENDPOINT = "http://155.223.25.212:8000/sparql/";
	public static final String LOCAL_NYTIMES_ENDPOINT = "http://155.223.25.212:9000/sparql/";

	/**
	 * Query that retrieves Turkish persons from {@link #LOCAL_DBPEDIA_ENDPOINT}
	 * endpoint
	 */
	public static final String SELECT_DBPEDIA_GET_TURKISH_PERSONS = "PREFIX rdf: <"
			+ RDF.getURI()
			+ "> "
			+ "PREFIX foaf: <"
			+ FOAF.getURI()
			+ "> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX dbp-ont: <http://dbpedia.org/ontology/>"
			+ "SELECT * WHERE {"
			+ "?person dbp-ont:nationality dbpedia:Turkey. "
			+ "?person rdf:type foaf:Person." + "}";

	/**
	 * Query that is similar to {@link #SELECT_DBPEDIA_GET_TURKISH_PERSONS} only
	 * different in variable names.
	 */
	public static final String SELECT_DBPEDIA_GET_TURKISH_PERSONS_2 = "PREFIX rdf: <"
			+ RDF.getURI()
			+ "> "
			+ "PREFIX foaf: <"
			+ FOAF.getURI()
			+ "> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX dbp-ont: <http://dbpedia.org/ontology/>"
			+ "SELECT * WHERE {"
			+ "?person2 dbp-ont:nationality dbpedia:Turkey. "
			+ "?person2 rdf:type foaf:Person.}";

	/**
	 * Query that is similar to {@link #SELECT_DBPEDIA_GET_TURKISH_PERSONS}
	 * different in variable names and triple positions.
	 */
	public static final String SELECT_DBPEDIA_GET_TURKISH_PERSONS_3 = "PREFIX rdf: <"
			+ RDF.getURI()
			+ "> "
			+ "PREFIX foaf: <"
			+ FOAF.getURI()
			+ "> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX dbp-ont: <http://dbpedia.org/ontology/>"
			+ "SELECT * WHERE {"
			+ "?person2 rdf:type foaf:Person."
			+ "?person2 dbp-ont:nationality dbpedia:Turkey. " + "}";

	/**
	 * Query that retrieves Turkish persons from {@link #LOCAL_DBPEDIA_ENDPOINT}
	 * endpoint
	 */
	public static final String CONSTRUCT_DBPEDIA_GET_TURKISH_PERSONS = "PREFIX rdf: <"
			+ RDF.getURI()
			+ "> "
			+ "PREFIX foaf: <"
			+ FOAF.getURI()
			+ "> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX dbp-ont: <http://dbpedia.org/ontology/>"
			+ "CONSTRUCT {"
			+ "?person dbp-ont:nationality dbpedia:Turkey. "
			+ "?person rdf:type foaf:Person."
			+ "} "
			+ "WHERE {"
			+ "?person dbp-ont:nationality dbpedia:Turkey. "
			+ "?person rdf:type foaf:Person." + "}";

	/**
	 * Query that retrieves Turkish persons from {@link #LOCAL_DBPEDIA_ENDPOINT}
	 * endpoint
	 */
	public static final String CONSTRUCT_DBPEDIA_GET_TURKISH_PERSONS_2 = "PREFIX rdf: <"
			+ RDF.getURI()
			+ "> "
			+ "PREFIX foaf: <"
			+ FOAF.getURI()
			+ "> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX dbp-ont: <http://dbpedia.org/ontology/>"
			+ "CONSTRUCT {"
			+ "?person2 dbp-ont:nationality dbpedia:Turkey. "
			+ "?person2 rdf:type foaf:Person.} "
			+ "WHERE {"
			+ "?person2 dbp-ont:nationality dbpedia:Turkey. "
			+ "?person2 rdf:type foaf:Person." + "}";

	public static final String SELECT_20K_TRIPLE = "SELECT * WHERE {?s ?p ?o} LIMIT 20000";
	public static final String CONSTRUCT_100K_TRIPLE = "CONSTRUCT {?s ?p ?o} WHERE {?s ?p ?o} LIMIT 100000";

}
