package basic;

/**
 * Created by USER on 2018-03-15.
 *  Query is a very simple Berkeley DB XML program
 *  that perform a query and handle results.
 *  It demonstrates transactional initialization and container creation,
 *  transactional document insertion, transactional query creation
 *  and excution, use of variable in a query and context, and
 *  transactional results handling
 *
 *  To run the example:
 *
 *      > java basic.QueryTxn [-h environmentDirectory]
 */

import java.io.File;

import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlManagerConfig;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlContainerConfig;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlUpdateContext;
import com.sleepycat.dbxml.XmlQueryExpression;
import com.sleepycat.dbxml.XmlResults;
import com.sleepycat.dbxml.XmlValue;
import com.sleepycat.dbxml.XmlQueryContext;

import com.sleepycat.dbxml.XmlTransaction;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.DatabaseException;

public class QueryTxn {

    static void usage() {
        System.err.println("Usage: java basic.queryTxn [-h environmentDirectory]");
        System.exit(-1);
    }

    // This function is used to ensure that database are
    // properly closed, even on exceptions
    private  static void cleanup(Environment env, XmlManager mgr, XmlContainer cont) {

        try {
            if(cont != null)
                cont.delete();
            if(mgr != null)
                mgr.delete();
            if(env != null)
                env.close();
        } catch (Exception e) {
            // ignore exception in cleanup
        }
    }

    private static Environment createEnvironment(String home) throws Throwable {

        EnvironmentConfig config = new EnvironmentConfig();
        config.setTransactional(true);
        config.setAllowCreate(true);
        config.setRunRecovery(true);
        config.setInitializeCache(true);
        config.setCacheSize(25 * 1024 * 1024); // 25MB  cache
        config.setInitializeLocking(true);
        config.setInitializeLogging(true);
        config.setErrorStream(System.err);
        File f = new File(home);
        return new Environment(f, config);
    }

    public static void main(String[] args) throws Throwable{

        // This program uses a named container, which will appear
        // on disk
        String containerName = "people02.dbxml";
        String content = "<people><person><name>joe</name></person><person><name>mary</name></person></people>";
        String docName = "people02";

        // Note that the query uses a variable, which must be set
        // in the query context
        String queryString =
                "collection('people02.dbxml')/people/person[name=$name]";
        String environmentDir = ".";

        if(args.length == 2) {
            environmentDir = args[1];
        } else if(args.length != 0) {
            usage();
        }

        XmlManager mgr = null;
        XmlContainer cont = null;
        Environment env = null;

        try {

            // Create and open a Berkeley DB Transactional Environment.
            env = createEnvironment(environmentDir);
            XmlManagerConfig mconfig = new XmlManagerConfig();
            mconfig.setAllowExternalAccess(true);
            mgr = new XmlManager(env, mconfig);

            // Because the container will exist on disk, remove it
            // first if it exists
            if(mgr.existsContainer(containerName) != 0)
                mgr.removeContainer(containerName);

            // Create a container that is transactional. Specify
            // that it is also a Node Storage container, with nodes
            // indexed
            XmlContainerConfig cconfig = new XmlContainerConfig();
            cconfig.setContainerType(XmlContainer.NodeContainer);
            cconfig.setIndexNodes(XmlContainerConfig.On);
            cconfig.setTransactional(true);
            cont = mgr.createContainer(containerName, cconfig);

            // Perform the putDocument in a transaction, created
            // from the XmlManager
            XmlTransaction txn = mgr.createTransaction();
            cont.putDocument(txn, docName, content);

            // commit the Transaction
            txn.commit();

            // Querying requires an XmlQueryContext
            XmlQueryContext qc = mgr.createQueryContext();

            // Add a variable to the query context, used by the query
            qc.setVariableValue("name", new XmlValue("mary"));

            // Create a new transaction for the query
            txn = mgr.createTransaction();

            // Note the passing of txn to both methods
            XmlQueryExpression expr = mgr.prepare(txn, queryString, qc);
            XmlResults res = expr.execute(txn, qc);

            // Note use of XmlQueryExpression::getQuery() and XmlResults::size()
            System.out.println("The query, '" + expr.getQuery() +
                        "'\n\t returned " + res.size() + " result(s)");

            // Process results -- just print them
            XmlValue value = new XmlValue();
            System.out.print("Result: ");
            while((value = res.next()) != null) {
                System.out.println("\t" + value.asString());
            }

            // done with the transaction
            txn.commit();

            // explicitly delete objects to release resources
            res.delete();
            expr.delete();

        } catch (XmlException xe) {
            System.err.println("XmlException during QueryTxn: " +
                        xe.getMessage());
            throw xe;
        } catch (DatabaseException de) {
            System.err.println("DatabaseException during QueryTxn: " +
                        de.getMessage());
            throw de;
        } finally {
            cleanup(env, mgr, cont);
        }
    }
}
