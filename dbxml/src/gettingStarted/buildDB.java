package gettingStarted;

/**
 * Created by USER on 2018-03-16.
 */

import java.io.*;

import com.sleepycat.dbxml.*;
import com.sleepycat.db.*;
import dbxml.gettingStarted.*;

public class buildDB {

    private static String theContainer = "namespaceExampleData.dbxml";
    private static String theDB = "testBerkeleyDB";

    private static void usage() {
        String usageMessage = "�� ���α׷��� DB XML �����̳ʿ� ����� XML ������ Ư�� ���տ��� ��� ������ �˻��� ����, ���� ���� �����͸� Berkeley DB �����ͺ��̽��� �����Ѵ�.\n";
        usageMessage += "DB XML �����̳ʿ� Berkeley DB �����ͺ��̽��� ������ �����ͺ��̽� ȯ�濡 ����ȴ�.\n";
        usageMessage += "XML �������� �߰ߵ� ��� ���� �����ͺ��̽��� ����� ���� �������� Berkeley DB Ű�� ���ȴ�.\n";
        usageMessage += "'retrieveDB' ���� ���α׷��� ����Ͽ� ����� �����͸� �˻��Ͻÿ�.\n";
        usageMessage += "�� ���α׷��� �����ϱ� ���� exampleLoadContainer�� �����Ͽ� DB XML �����̳ʸ� �ش� �����ͷ� �̸� ä��ÿ�.\n";
        usageMessage += "�� ���α׷��� ������ �� exampleLoadContainer�� ���� �����͸� ��ġ�� ���丮 ��ġ Ȯ��: \n";
        usageMessage += "\t-h <dbenv directory>\n";
        usageMessage += "For example:\n";
        usageMessage += "\t > java buildDB -h exampleEnvironment\n";
    }

    // ��ü�� �����ϴ� ��ƿ��Ƽ �Լ�. container�� environment�� �����Ǿ�� �Ѵ�
    private static void cleanup(myDbEnv env, XmlContainer openedContainer) {
        try {
            if(openedContainer != null)
                openedContainer.delete();
            if(env != null)
                env.cleanup();
        } catch (Exception e) {
            // ����� ����ó�� ����
        }
    }

    public static void main(String[] args) throws Throwable {

        File path2DbEnv = null;

        for(int i = 0; i < args.length; i++) {
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

        if(path2DbEnv == null || !path2DbEnv.isDirectory()) {
            usage();
        }

        myDbEnv env = null;
        myDb openedDatabase = null;
        XmlContainer openedContainer = null;
        XmlTransaction txn = null;

        try {
            // environment ����
            env = new myDbEnv(path2DbEnv);
            XmlManager theMgr = env.getManager();

            // �ش� environment���� �����ͺ��̽� ����
            openedDatabase = new myDb(theDB, env.getEnvironment());

            // Ʈ����� �����̳� ����
            XmlContainerConfig config = new XmlContainerConfig();
            config.setTransactional(true);
            openedContainer = theMgr.openContainer(theContainer, config);

            // DB�� ����Ͽ� Ʈ����� ����
            Transaction dbTxn = env.getEnvironment().beginTransaction(null, null);
            txn = theMgr.createTransaction(dbTxn);

            XmlQueryContext resultsContext = theMgr.createQueryContext();

            String theQuery =
                    "distinct-values(collection('namespaceExampleData.dbxml')/vendor/salesrep/name)";

            // XmlManager environment �� ���� ���� ����
            XmlResults results = theMgr.query(txn, theQuery, resultsContext);

            // ��ť��Ʈ ��ȸ ��� ��Ʈ���� ���� ����
            XmlValue value = results.next();

            while(value != null) {
                String theSalesRepkey = value.asString();
                System.out.println("key �� ���� ������ �Է� : " + theSalesRepkey);

                // �̰��� �츮�� �����ͺ��̽��� �ְ� �ִ� �������̴�.
                // �Ǽ����� ���������� salesreps�� jpeg �̹���, ���� Ű �Ǵ� XML ���� ��Ű���� ���� �ʴ�
                // �ٸ� ���� ��Ʈ�� ���� BLOB �� ���Եȴ�. �� ��� �����͸� �˻��� �� �������� �Ͼ����
                // �� �� �ֵ��� ���� ���ڿ��� ���� ���̴�.
                String theSalesRepData =
                        "�̰��� " + theSalesRepkey + "�� �����ͺ��̽��� ����� �������̴�.";

                DatabaseEntry theKey =
                        new DatabaseEntry(theSalesRepkey.getBytes());
                DatabaseEntry theData =
                        new DatabaseEntry(theSalesRepData.getBytes());

                // ���������� Berkeley DB �� ���� �����͸� ����.
                openedDatabase.getDatabase().putNoOverwrite(txn.getTransaction(), theKey, theData);
                value = results.next();
            }
            results.delete();

            // �̷� ���� ���Ⱑ ���������� �̷������
            txn.commit();

        } catch(Exception e) {

            System.err.println(theContainer + "�� ���� ���� ������ ���� �߻�");
            System.err.println("\t�޽���: " + e.getMessage());

            // ������ �߻��ϸ� �۾��� �ߴ��ϰ� �����ͺ��̽��� �� �۾��� ������ ������
            // ������ ���·� ���ܵд�.
            if(txn != null) {
                txn.abort();
            }
            throw e;

        } finally {
            if(openedDatabase != null) {
                openedDatabase.cleanup();
            }
            cleanup(env, openedContainer);
        }
    }
}
