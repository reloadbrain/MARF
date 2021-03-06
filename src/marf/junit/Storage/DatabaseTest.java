package marf.junit.Storage;

import marf.Storage.Database;
import marf.Storage.StorageException;

import junit.framework.TestCase;


/**
 * <p>Database data structure unit tests.</p>
 *
 * @author Serguei Mokhov
 * @author Shuxin Fan
 */
public class DatabaseTest
extends TestCase
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(DatabaseTest.class);
	}

	/**
	 * @param name
	 */
	public DatabaseTest(String name)
	{
		super(name);
	}

	protected void setUp()
	throws Exception
	{
		super.setUp();
	}

	protected void tearDown()
	throws Exception
	{
		super.tearDown();
	}

	/**
	 * Test method for 'marf.Storage.Database.clone()'
	 * @throws StorageException
	 */
	public void testClone()
	throws StorageException
	{
		Database oDatabase = new Database();
		//oDatabase.connect();
		Database oDatabaseClone = (Database)oDatabase.clone();
		System.out.println("db:" + oDatabase + "h: " + oDatabase.hashCode());
		System.out.println("cl:" + oDatabaseClone + "h: " +
		oDatabaseClone.hashCode());
		System.out.println("eq:" + oDatabase.equals(oDatabaseClone));
		assertTrue(oDatabase.equals(oDatabaseClone));
	}
}

// EOF
