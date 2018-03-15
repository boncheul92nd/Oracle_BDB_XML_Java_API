package gettingStarted;

/**
 * Created by USER on 2018-03-15.
 */

import java.io.*;
import com.sleepycat.dbxml.*;
import com.sleepycat.db.*;
import dbxml.gettingStarted.*;

public class retreiveDB {

    private static String theContainer = "namespaceExampleData.dbxml";
    private static String theDB = "testBerkeleyDB";

    private static void usage() {
        String usageMessage = "�� ���α׷��� DB XML �����̳��� �������� �˻��� ������ ������� Berkeley DB �����ͺ��̽����� �����͸� �˻��Ѵ�.\n";
        usageMessage += "DB XML �����̳ʿ� Berkeley DB �����ͺ��̽��� ������ �����ͺ��̽� ȯ�濡 ����ȴ�.\n";
        usageMessage += "XML �������� �߰ߵ� ��� ���� �����ͺ��̽��� ����� ���� �������� Berkeley DB Ű�� ���ȴ�.\n";
        usageMessage += "�� ���α׷��� �����ϱ� ���� exampleLoadContainer�� ���� �����Ͽ� DB XML �����̳ʸ� �ش� �����ͷ� �̸� ä���� �Ѵ�.\n";
        usageMessage += "�׷� ���� 'buildDB' ���� ���α׷��� ����Ͽ� ���α׷��� �ʿ��� Berkeley DB �����ͺ��̽��� �����Ͻÿ�.\n";
        usageMessage += "�� ���α׷��� ������ �� exampleLoadContainer���� ���� �����͸� ��ġ�� ��ġ�� ������ ���͸��� Ȯ���Ͻÿ�.\n";
        usageMessage += "\t-h <dbenv directory>\n";
        usageMessage += "For example:\n";
        usageMessage += "\tjava retreiveDB -h examplesEnvironment\n";

        System.out.println(usageMessage);
        System.exit(-1);
    }

    // Utility function to clean up objects, exceptions or not,
    // containers and environments must be closed.
    private static void cleanup(myDbEnv env, XmlContainer openedContainer) {
        try {
            if(openedContainer != null)
                openedContainer.delete();
            if(env != null)
                env.cleanup();
        } catch (Exception e) {
            // ignore exceptions on close
        }
    }

    public static void main(String[] args) throws Throwable {

        File path2DbEnv = null;

        for(int i = 0; i < args.length; ++i) {
            if(args[i].startsWith("-")) {
                switch (args[i].charAt(1)) {

                    case 'h':
                        path2DbEnv = new File(args[++i]);
                        break;
                    default:
                            usage();
                }
            }
        }

        if (path2DbEnv == null || !path2DbEnv.isDirectory()) {
            usage();
        }

        myDbEnv env = null;
        myDb openedDatabase = null;
        XmlContainer openedContainer = null;
        Transaction dbTxn = null;
        XmlTransaction txn = null;

        try {
            // Open an environment
            env = new myDbEnv(path2DbEnv);
            XmlManager theMgr = env.getManager();

            // Open a database in that environment
            openedDatabase = new myDb(theDB, env.getEnvironment());

            // Open a transactional container
            XmlContainerConfig config = new XmlContainerConfig();
            config.setTransactional(true);
            openedContainer = theMgr.openContainer(theContainer, config);

            // Start a transaction via DB
            dbTxn = env.getEnvironment().beginTransaction(null, null);
            txn = theMgr.createTransaction();

            // Obtain an XmlQueryContext with the result type set to
            // values (not documents).
            XmlQueryContext resultsContext = theMgr.createQueryContext();

            String theQuery = "distinct-values(collection('namespaceExampleData.dbxml')/vendor/salesrep/name)";

            // Get all the vendor documents out of the container
            XmlResults results = theMgr.query(txn, theQuery, resultsContext);
            XmlValue value = results.next();

            while(value != null) {

                // Pull the value out of the document query result set.
                String theSalseRepkey = value.asString();

                DatabaseEntry theKey = new DatabaseEntry(theSalseRepkey.getBytes());
                DatabaseEntry theData = new DatabaseEntry();

                OperationStatus status = openedDatabase.getDatabase().get(txn.getTransaction(),
                                                theKey, theData, null);
                System.out.println("For key: " + theSalseRepkey +
                            ", retrieved:");
                if (status == OperationStatus.NOTFOUND) {
                    System.out.println("key not found: run buildDB first");
                } else {
                    System.out.println(new String(theData.getData(), 0,
                                        theData.getSize()));
                    System.out.println("here");
                    value = results.next();
                }
                results.delete();
                txn.commit();
            }
            results.delete();
            txn.commit();
        } catch (XmlException xe) {
            System.err.println("Xml error performing query against " + theContainer);
            System.err.println("\tMessage: " + xe.toString());
            if (txn != null) {
                txn.abort();
            }
            throw xe;
        } catch (DatabaseException de) {
            System.err.println("Error performing query against " + theContainer);
            System.err.println("\tMessage: " + de.toString());
            if (txn != null) {
                txn.abort();
            }
            throw de;
        } finally {
            if(openedDatabase != null)
                openedDatabase.cleanup();
            cleanup(env, openedContainer);
        }
    }
}
