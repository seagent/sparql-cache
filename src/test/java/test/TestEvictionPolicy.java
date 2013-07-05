package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.jboss.netty.channel.ChannelException;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap.EvictionPolicy;

public class TestEvictionPolicy {

	private MemCacheDaemon<LocalCacheElement> memCacheServer;

	/**
	 * This method tries to connect memcache server, if server already started
	 * leaves it as working.
	 * 
	 * @param evictionPolicy
	 * @throws UnknownHostException
	 */
	private void provideMemcachedServerAsConnected(EvictionPolicy evictionPolicy)
			throws UnknownHostException {
		try {
			// try to start memcached server
			startMemcachedServer(evictionPolicy);
		} catch (ChannelException e) {
			if (e.getCause().getClass().equals(BindException.class)) {
				// leave server working if it is started before
				e.printStackTrace();
			} else {
				// throw exceptions whose cause are not a BindException...
				throw e;
			}
		}
	}

	/**
	 * This method starts memcache server
	 * 
	 * @param evictionPolicy
	 * @throws UnknownHostException
	 */
	private void startMemcachedServer(EvictionPolicy evictionPolicy)
			throws UnknownHostException {
		// starting memcached server at the address "127.0.0.1:11211"
		memCacheServer = new MemCacheDaemon<LocalCacheElement>();

		CacheStorage<Key, LocalCacheElement> storage = ConcurrentLinkedHashMap
				.create(evictionPolicy, 2, 1000000);
		memCacheServer.setCache(new CacheImpl(storage));
		InetAddress localHost = InetAddress.getByName("127.0.0.1");
		memCacheServer.setAddr(new InetSocketAddress(localHost, 11211));
		memCacheServer.setIdleTime(60 * 60 * 24);
		memCacheServer.start();
		assertTrue(memCacheServer.isRunning());
	}

	@After
	public void after() {
		// stopping memcached server after all tests
		if (memCacheServer != null && memCacheServer.isRunning()) {
			memCacheServer.stop();
			assertFalse(memCacheServer.isRunning());
		}
	}

	@Ignore
	@Test
	public void executeTTIScenario() throws Exception {
		// memcached does not support TTI functionality
	}

	/**
	 * This test ensures that TTL functionality works correctly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executeTTLScenario() throws Exception {

		// provide memcached server as started
		provideMemcachedServerAsConnected(EvictionPolicy.LRU);

		// create a mock memcached query engine to simulate executing sparql
		// queries using memcached cache.
		MockMemcachedQueryEngineHTTP mockQueryEngineDBpedia = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DBPEDIA_ENDPOINT,
				Constants.SELECT_DBPEDIA_GET_TURKISH_PERSONS);

		// set time to live of an cache object as 2 seconds
		MockMemcachedQueryEngineHTTP.TTL = 2;
		// clean cache before executing query
		mockQueryEngineDBpedia.flush();

		// execute query on mock query engine...
		mockQueryEngineDBpedia.execSelect();

		// check that result not contained before in the cache
		assertFalse(mockQueryEngineDBpedia.isResultInTheCache());
		// check that result contained now in the cache
		assertNotNull(mockQueryEngineDBpedia.getClient().get(
				mockQueryEngineDBpedia.getKey()));
		// wait 3 seconds
		Thread.sleep(3000);
		// check that result not contained anymore because of TTL is exceeded.
		assertNull(mockQueryEngineDBpedia.getClient().get(
				mockQueryEngineDBpedia.getKey()));
	}

	/**
	 * This test ensures that FIFO eviction mechanism works correctly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executeFIFOOverflowScenario() throws Exception {

		// provide memcached server as started
		provideMemcachedServerAsConnected(EvictionPolicy.FIFO);

		// set TTL value 1 day again
		MockMemcachedQueryEngineHTTP.TTL = 60 * 60 * 24;

		// read rdf triples from sample DBpedia file into model to avoid
		// execution time of queries.
		Model model = ModelFactory.createDefaultModel();
		model.read("dbpedia100K.rdf");
		// create mock query engine for DBpedia
		MockMemcachedQueryEngineHTTP mockQueryEngineDBpedia = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DBPEDIA_ENDPOINT, Constants.SELECT_20K_TRIPLE,
				model);
		// clean cache before executing query
		mockQueryEngineDBpedia.flush();

		// execute query on dbpedia first...
		mockQueryEngineDBpedia.execSelect();

		// check dbpedia results are in the cache
		assertFalse(mockQueryEngineDBpedia.isResultInTheCache());
		assertNotNull(mockQueryEngineDBpedia.getClient().get(
				mockQueryEngineDBpedia.getKey()));

		/**
		 * --------------------------------------------------------------------
		 */

		// create mock query engine for drugbank
		MockMemcachedQueryEngineHTTP mockQueryEngineDrugbank = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DRUGBANK_ENDPOINT, Constants.SELECT_20K_TRIPLE,
				model);

		// execute query on drugbank...
		mockQueryEngineDrugbank.execSelect();

		// sleep 1 second to apply changes into cache
		Thread.sleep(1000);

		// check drugbank results are in the cache
		assertFalse(mockQueryEngineDrugbank.isResultInTheCache());
		assertNotNull(mockQueryEngineDrugbank.getClient().get(
				mockQueryEngineDrugbank.getKey()));

		/**
		 * --------------------------------------------------------------------
		 */

		// create mock query engine for nytimes
		MockMemcachedQueryEngineHTTP mockQueryEngineNytimes = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_NYTIMES_ENDPOINT, Constants.SELECT_20K_TRIPLE,
				model);

		// execute query on nytimes...
		mockQueryEngineNytimes.execSelect();

		// sleep 1 second to apply changes into cache
		Thread.sleep(1000);

		// check dbpedia results have been evicted from the cache
		assertNull(mockQueryEngineDBpedia.getClient().get(
				mockQueryEngineDBpedia.getKey()));
		// check drugbank results are in the cache
		assertNotNull(mockQueryEngineDrugbank.getClient().get(
				mockQueryEngineDrugbank.getKey()));
		// check nytimes results are in the cache
		assertFalse(mockQueryEngineNytimes.isResultInTheCache());
		assertNotNull(mockQueryEngineNytimes.getClient().get(
				mockQueryEngineNytimes.getKey()));

	}

	/**
	 * This test ensures that LRU eviction policy works correctly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executeLRUOverflowScenario() throws Exception {

		// provide memcached server as started
		provideMemcachedServerAsConnected(EvictionPolicy.LRU);

		// set TTL value 1 day again
		MockMemcachedQueryEngineHTTP.TTL = 60 * 60 * 24;

		// read rdf triples from sample DBpedia file into model to avoid
		// execution time of queries.
		Model model = ModelFactory.createDefaultModel();
		model.read("dbpedia100K.rdf");
		// create mock query engine for DBpedia
		MockMemcachedQueryEngineHTTP mockQueryEngineDBpedia = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DBPEDIA_ENDPOINT, Constants.SELECT_20K_TRIPLE,
				model);
		// clean cache before executing query
		mockQueryEngineDBpedia.flush();

		// execute query on dbpedia first...
		mockQueryEngineDBpedia.execSelect();

		// check dbpedia results are in the cache
		assertFalse(mockQueryEngineDBpedia.isResultInTheCache());
		assertNotNull(mockQueryEngineDBpedia.getClient().get(
				mockQueryEngineDBpedia.getKey()));

		/**
		 * --------------------------------------------------------------------
		 */

		// create mock query engine for drugbank
		MockMemcachedQueryEngineHTTP mockQueryEngineDrugbank = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_DRUGBANK_ENDPOINT, Constants.SELECT_20K_TRIPLE,
				model);

		// execute query on drugbank...
		mockQueryEngineDrugbank.execSelect();

		// sleep 1 second to apply changes into cache
		Thread.sleep(1000);

		// check drugbank results are in the cache
		assertFalse(mockQueryEngineDrugbank.isResultInTheCache());
		assertNotNull(mockQueryEngineDrugbank.getClient().get(
				mockQueryEngineDrugbank.getKey()));

		/**
		 * --------------------------------------------------------------------
		 */

		// execute dbpedia query again to make drugbank query LRU
		mockQueryEngineDBpedia.execSelect();

		Thread.sleep(1000);

		/**
		 * ---------------------------------------------------------------------
		 */

		// create mock query engine for nytimes
		MockMemcachedQueryEngineHTTP mockQueryEngineNytimes = new MockMemcachedQueryEngineHTTP(
				Constants.LOCAL_NYTIMES_ENDPOINT, Constants.SELECT_20K_TRIPLE,
				model);

		// execute query on nytimes...
		mockQueryEngineNytimes.execSelect();

		// sleep 1 second to apply changes into cache
		Thread.sleep(1000);

		// check drugbank results have been evicted from the cache
		assertNull(mockQueryEngineDrugbank.getClient().get(
				mockQueryEngineDrugbank.getKey()));
		// check dbpedia results are in the cache
		assertNotNull(mockQueryEngineDBpedia.getClient().get(
				mockQueryEngineDBpedia.getKey()));
		// check nytimes results are in the cache
		assertFalse(mockQueryEngineNytimes.isResultInTheCache());
		assertNotNull(mockQueryEngineNytimes.getClient().get(
				mockQueryEngineNytimes.getKey()));
	}

}
