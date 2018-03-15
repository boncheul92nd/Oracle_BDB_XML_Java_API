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
        String usageMessage = "이 프로그램은 DB XML 컨테이너의 문서에서 검색된 정보를 기반으로 Berkeley DB 데이터베이스에서 데이터를 검색한다.\n";
        usageMessage += "DB XML 컨테이너와 Berkeley DB 데이터베이스는 동일한 데이터베이스 환경에 저장된다.\n";
        usageMessage += "XML 문서에서 발견된 노드 값은 데이터베이스에 저장된 샘플 데이터의 Berkeley DB 키로 사용된다.\n";
        usageMessage += "이 프로그램을 실행하기 전에 exampleLoadContainer를 먼저 실행하여 DB XML 컨테이너를 해당 데이터로 미리 채워야 한다.\n";
        usageMessage += "그런 다음 'buildDB' 샘플 프로그램을 사용하여 프로그램에 필요한 Berkeley DB 데이터베이스를 빌드하시오.\n";
        usageMessage += "이 프로그램을 실행할 때 exampleLoadContainer에게 예제 데이터를 배치할 위치를 지정한 디렉터리를 확인하시오.\n";
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
