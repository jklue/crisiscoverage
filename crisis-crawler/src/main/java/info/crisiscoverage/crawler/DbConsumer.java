package info.crisiscoverage.crawler;

import java.io.File;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class DbConsumer implements CrawlerConstants{
	
	private Environment env = null;
	private Database db = null;

	/**
	 * Setup Database
	 * @param dbDir
	 * @param dbName
	 * @throws Exception 
	 */
	public void setupForRead(String dbDir) throws Exception{
		
		//Setup environment
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(false);
		envConfig.setLocking(false);

		File envHome = new File(dbDir+File.separator+dbFolderName);
		if (!envHome.exists()) {
			if (!envHome.mkdir()) {
				throw new Exception("Couldn't create this folder: " + envHome.getAbsolutePath());
			}
		}
		env = new Environment(envHome, envConfig);
		
		// Open the database
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(false);
        db = env.openDatabase(null, dbName, dbConfig);
	}
	
	public Environment getEnv() {
		return env;
	}
	
	public Database getDb() {
		return db;
	}
} 
