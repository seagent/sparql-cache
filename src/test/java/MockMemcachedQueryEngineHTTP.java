import net.spy.memcached.MemcachedClient;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.talis.labs.arq.MemcachedQueryEngineHTTP;

/**
 * This class mocks {@link MemcachedQueryEngineHTTP} to check cache state with
 * {@link #isResultInTheCache} value
 * 
 * @author etmen
 * 
 */
public class MockMemcachedQueryEngineHTTP extends MemcachedQueryEngineHTTP {

	/**
	 * flag that indicates whether searched query result is in the cache at that
	 * time.
	 */
	private boolean isResultInTheCache;

	public boolean isResultInTheCache() {
		return isResultInTheCache;
	}

	public MockMemcachedQueryEngineHTTP(String serviceURI, String queryString) {
		this(serviceURI, QueryFactory.create(queryString));
	}

	public MockMemcachedQueryEngineHTTP(String serviceURI, Query query) {
		super(serviceURI, query);
		isResultInTheCache = false;
	}

	public void flush() {
		client.flush();
	}

	public MemcachedClient getClient() {
		return this.client;
	}

	@Override
	public ResultSet execSelect() {
		ResultSet rs = null;
		Object value = client.get(key);
		if (value != null) {
			isResultInTheCache = true;
			rs = ResultSetFactory.fromXML((String) value);
		} else {
			isResultInTheCache = false;
			rs = super.execSelect();
			ResultSetRewindable rewindableRs = ResultSetFactory
					.makeRewindable(rs);
			client.set(key, TTL, ResultSetFormatter.asXMLString(rewindableRs));
			rewindableRs.reset();
			rs = rewindableRs;
		}
		return rs;
	}

	@Override
	public Model execConstruct() {
		Model model = ModelFactory.createDefaultModel();

		Object value = client.get(key);
		if (value != null) {
			isResultInTheCache = true;
			toModel((String) value, model);
		} else {
			isResultInTheCache = false;
			model = super.execConstruct();
			client.set(key, TTL, toString(model));
		}

		return model;
	}

	@Override
	public Model execConstruct(Model m) {
		Model model = ModelFactory.createDefaultModel();

		Object value = client.get(key);
		if (value != null) {
			isResultInTheCache = true;
			toModel((String) value, model);
		} else {
			isResultInTheCache = false;
			model = super.execConstruct(m);
			client.set(key, TTL, toString(model));
		}

		return model;
	}

	@Override
	public Model execDescribe() {
		Model model = ModelFactory.createDefaultModel();

		Object value = client.get(key);
		if (value != null) {
			isResultInTheCache = true;
			toModel((String) value, model);
		} else {
			isResultInTheCache = false;
			model = super.execDescribe();
			client.set(key, TTL, toString(model));
		}

		return model;
	}

	@Override
	public Model execDescribe(Model m) {
		Model model = ModelFactory.createDefaultModel();

		Object value = client.get(key);
		if (value != null) {
			isResultInTheCache = true;
			toModel((String) value, model);
		} else {
			isResultInTheCache = false;
			model = super.execDescribe(m);
			client.set(key, TTL, toString(model));
		}

		return model;
	}

	@Override
	public boolean execAsk() {
		boolean ask;

		Object value = client.get(key);
		if (value != null) {
			isResultInTheCache = true;
			ask = Boolean.parseBoolean((String) value);
		} else {
			isResultInTheCache = false;
			ask = super.execAsk();
			client.set(key, TTL, Boolean.toString(ask));
		}

		return ask;
	}

	@Override
	public void close() {
		client.shutdown();
	}
}
