package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;

public class TestRegisterIntoCache {

	private static MemCacheDaemon<LocalCacheElement> memCacheServer;

	@BeforeClass
	public static void beforeClass() throws UnknownHostException {
		// start memcached server
		startMemcachedServer();
	}

	private static void startMemcachedServer() throws UnknownHostException {
		// starting memcached server at the address "127.0.0.1:11211"
		memCacheServer = new MemCacheDaemon<LocalCacheElement>();

		CacheStorage<Key, LocalCacheElement> storage = ConcurrentLinkedHashMap
				.create(ConcurrentLinkedHashMap.EvictionPolicy.LRU, 1000,
						100000000);
		memCacheServer.setCache(new CacheImpl(storage));
		InetAddress localHost = InetAddress.getByName("127.0.0.1");
		memCacheServer.setAddr(new InetSocketAddress(localHost, 11211));
		memCacheServer.setIdleTime(60 * 60 * 24);
		memCacheServer.start();
		assertTrue(memCacheServer.isRunning());
	}

	@AfterClass
	public static void afterClass() {
		// stopping memcached server after all tests
		memCacheServer.stop();
		assertFalse(memCacheServer.isRunning());
	}

	/**
	 * This test controls putting a query result into memcached cache
	 * successfully which is not contained in cache.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executingQueryNotExistsInTheCache() throws Exception {

		// create a mock memcached query engine to simulate executing sparql
		// queries using memcached cache.
		MockMemcachedQueryEngineHTTP mockQueryEngine = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DBPEDIA_ENDPOINT,
				Constants.SELECT_DBPEDIA_GET_TURKISH_PERSONS);
		// clean cache before executing query
		mockQueryEngine.flush();

		assertNull(mockQueryEngine.getClient().get(mockQueryEngine.getKey()));

		// execute query on mock query engine...
		mockQueryEngine.execSelect();

		assertFalse(mockQueryEngine.isResultInTheCache());
		assertNotNull(mockQueryEngine.getClient().get(mockQueryEngine.getKey()));

		mockQueryEngine.shutdown();

		// release client
		MockMemcachedQueryEngineHTTP.setClient(null);

	}

	/**
	 * This test controls putting a query result into memcached cache
	 * successfully which is not contained in cache.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executingQueryNotExistsInTheCacheConstruct() throws Exception {

		// create a mock memcached query engine to simulate executing sparql
		// queries using memcached cache.
		MockMemcachedQueryEngineHTTP mockQueryEngine = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DBPEDIA_ENDPOINT,
				Constants.CONSTRUCT_DBPEDIA_GET_TURKISH_PERSONS);
		// clean cache before executing query
		mockQueryEngine.flush();

		assertNull(mockQueryEngine.getClient().get(mockQueryEngine.getKey()));

		// execute query on mock query engine...
		mockQueryEngine.execConstruct();

		assertFalse(mockQueryEngine.isResultInTheCache());
		assertNotNull(mockQueryEngine.getClient().get(mockQueryEngine.getKey()));

		mockQueryEngine.shutdown();

		// release client
		MockMemcachedQueryEngineHTTP.setClient(null);

	}

	/**
	 * This test controls getting a query result from memcached cache
	 * successfully which is contained in cache.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executingQueryExistsInTheCache() throws Exception {

		// create a mock memcached query engine to simulate executing sparql
		// queries using memcached cache.
		MockMemcachedQueryEngineHTTP mockQueryEngine = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DBPEDIA_ENDPOINT,
				Constants.SELECT_DBPEDIA_GET_TURKISH_PERSONS);
		// clean cache before executing query
		mockQueryEngine.flush();
		// execute query on mock query engine first time...
		mockQueryEngine.execSelect();

		// execute query on mock query engine after putting into cache...
		mockQueryEngine.execSelect();
		assertTrue(mockQueryEngine.isResultInTheCache());
		assertNotNull(mockQueryEngine.getClient().get(mockQueryEngine.getKey()));

		mockQueryEngine.shutdown();

		// release client
		MockMemcachedQueryEngineHTTP.setClient(null);

	}

	@Ignore
	/**
	 * This test controls executing two queries that are same queries but only
	 * different variable names.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executingQueriesThatOnlyVariableNamesDifferent()
			throws Exception {

		// create a mock memcached query engine to execute first query.
		MockMemcachedQueryEngineHTTP mockQueryEngineFirst = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DBPEDIA_ENDPOINT,
				Constants.SELECT_DBPEDIA_GET_TURKISH_PERSONS);
		// clean cache before executing query
		mockQueryEngineFirst.flush();
		// execute query on mock query engine...
		mockQueryEngineFirst.execSelect();

		mockQueryEngineFirst.shutdown();

		// create a mock memcached query engine to execute nearly same query
		// with first one.
		MockMemcachedQueryEngineHTTP mockQueryEngineSecond = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DBPEDIA_ENDPOINT,
				Constants.SELECT_DBPEDIA_GET_TURKISH_PERSONS_2);
		// execute similar query on mock query engine...
		mockQueryEngineSecond.execSelect();

		assertTrue(mockQueryEngineSecond.isResultInTheCache());
		assertNotNull(mockQueryEngineSecond.getClient().get(
				mockQueryEngineSecond.getKey()));

		mockQueryEngineSecond.shutdown();

		// release client
		MockMemcachedQueryEngineHTTP.setClient(null);

	}

	@Ignore
	/**
	 * This test controls executing two queries that are same queries but
	 * different variable names and triple order.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executingQueriesThatVariableNamesAndTripleOrderDifferent()
			throws Exception {

		// create a mock memcached query engine to execute first query.
		MockMemcachedQueryEngineHTTP mockQueryEngineFirst = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DBPEDIA_ENDPOINT,
				Constants.SELECT_DBPEDIA_GET_TURKISH_PERSONS);
		// clean cache before executing query
		mockQueryEngineFirst.flush();
		// execute query on mock query engine...
		mockQueryEngineFirst.execSelect();

		mockQueryEngineFirst.shutdown();

		// create a mock memcached query engine to execute nearly same query
		// with first one.
		MockMemcachedQueryEngineHTTP mockQueryEngineSecond = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DBPEDIA_ENDPOINT,
				Constants.SELECT_DBPEDIA_GET_TURKISH_PERSONS_3);
		// execute similar query on mock query engine...
		mockQueryEngineSecond.execSelect();

		assertTrue(mockQueryEngineSecond.isResultInTheCache());
		assertNotNull(mockQueryEngineSecond.getClient().get(
				mockQueryEngineSecond.getKey()));

		mockQueryEngineSecond.shutdown();

		// release client
		MockMemcachedQueryEngineHTTP.setClient(null);

	}

}
