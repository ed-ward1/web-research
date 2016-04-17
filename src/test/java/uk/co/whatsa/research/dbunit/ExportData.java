package uk.co.whatsa.research.dbunit;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.database.search.TablesDependencyHelper;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

public class ExportData {

	public static void main(String[] args) throws Exception {
		// database connection
		Class driverClass = Class.forName("com.mysql.jdbc.Driver");
		Connection jdbcConnection = DriverManager.getConnection("jdbc:mysql://localhost/research", "user1", "letmein");
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

		// partial database export
		QueryDataSet partialDataSet = new QueryDataSet(connection);
		partialDataSet.addTable("WEBPAGE", "SELECT * FROM WEBPAGE WHERE ID=1");
		partialDataSet.addTable("WEBPAGE_RESOURCE");
		partialDataSet.addTable("JAVASCRIPT_RESOURCE");
		FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial.xml"));

		// full database export
		IDataSet fullDataSet = connection.createDataSet();
		FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));

		// // dependent tables database export: export table X and all tables that
		// // have a PK which is a FK on X, in the right order for insertion
		// String[] depTableNames =
		// TablesDependencyHelper.getAllDependentTables( connection, "X" );
		// IDataSet depDataset = connection.createDataSet( depTableNames );
		// FlatXmlDataSet.write(depDataset, new FileOutputStream("dependents.xml"));

	}
	
	public static void main2(String[] args) throws Exception {
		// database connection
		Class driverClass = Class.forName("com.mysql.jdbc.Driver");
		Connection jdbcConnection = DriverManager.getConnection("jdbc:mysql://localhost/research", "user1", "letmein");
		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);

		// partial database export
		QueryDataSet partialDataSet = new QueryDataSet(connection);
		partialDataSet.addTable("FOO", "SELECT * FROM TABLE WHERE COL='VALUE'");
		partialDataSet.addTable("BAR");
		FlatXmlDataSet.write(partialDataSet, new FileOutputStream("partial.xml"));

		// full database export
		IDataSet fullDataSet = connection.createDataSet();
		FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));

		// dependent tables database export: export table X and all tables that
		// have a PK which is a FK on X, in the right order for insertion
		String[] depTableNames = TablesDependencyHelper.getAllDependentTables(connection, "X");
		IDataSet depDataSet = connection.createDataSet(depTableNames);
		FlatXmlDataSet.write(depDataSet, new FileOutputStream("dependents.xml"));

	}
}
