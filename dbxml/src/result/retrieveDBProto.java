package result;

/**
 * Created by USER on 2018-03-19.
 */

import java.io.*;
import com.sleepycat.dbxml.*;
import com.sleepycat.db.*;
import dbxml.gettingStarted.*;

public class retrieveDBProto {

    private static String theContainer = "editorContainer.dbxml";
    private static String theDB = "editorContainerDB";

    private static void usage() {
        String usageMessage = "�� ���α׷��� DB XML �����̳��� �������� �˻��� ������ ������� Berkeley DB �����ͺ��̽����� �����͸� �˻��Ѵ�.\n";
        usageMessage += "DB XML �����̳ʿ� Berkeley DB �����ͺ��̽��� ������ �����ͺ��̽� ȯ�濡 ����ȴ�.\n";
        usageMessage += "XML �������� �߰ߵ� ��� ���� �����ͺ��̽��� ����� ���� �������� Berkeley DB Ű�� ���ȴ�.\n";
        usageMessage += "�� ���α׷��� �����ϱ� ���� exampleLoadContainer�� ���� �����Ͽ� DB XML �����̳ʸ� �ش� �����ͷ� �̸� ä���� �Ѵ�.\n";
        usageMessage += "�׷� ���� 'buildDB' ���� ���α׷��� ����Ͽ� ���α׷��� �ʿ��� Berkeley DB �����ͺ��̽��� �����Ͻÿ�.\n";
        usageMessage += "�� ���α׷��� ������ �� exampleLoadContainer���� ���� �����͸� ��ġ�� ��ġ�� ������ ���͸��� Ȯ���Ͻÿ�.\n";
        usageMessage += "\t-h <dbenv directory>\n";
        usageMessage += "For example:\n";
        usageMessage += "\tjava retrieveDB -h examplesEnvironment\n";

        System.out.println(usageMessage);
        System.exit(-1);
    }

    // ��ü�� �����ϴ� ��ƿ��Ƽ �Լ�. �����̳ʿ� ȯ��������
    // ������ �Ѵ�.
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

            // environment ����
            env = new myDbEnv(path2DbEnv);
            XmlManager theMgr = env.getManager();

            // �ش� environment���� �����ͺ��̽� ����
            openedDatabase = new myDb(theDB, env.getEnvironment());

            // Ʈ����� �����̳� ����
            XmlContainerConfig config = new XmlContainerConfig();
            config.setTransactional(true);
            openedContainer = theMgr.openContainer(theContainer, config);

            // DB�� ���� Ʈ����� ����
            dbTxn = env.getEnvironment().beginTransaction(null, null);
            txn = theMgr.createTransaction();

            // ��� ������ ��(��ť��Ʈ �ƴ�)���� ������ XmlQueryContext�� ������
            XmlQueryContext resultsContext = theMgr.createQueryContext();

            String theQuery = "collection('editorContainer.dbxml')";

            // �����̳ʿ��� ��ť��Ʈ�� ������
            XmlResults results = theMgr.query(txn, theQuery, resultsContext);
            XmlValue value = results.next();

            while(value != null) {

                // ��ť��Ʈ ���� ��� ���տ��� ���� ����
                String doc = value.asString();
                System.out.println(doc);
                value = results.next();
            }
            results.delete();
            txn.commit();

        } catch (XmlException e) {
            System.err.println(theContainer + "�� ���� ������ �����ϴ� �� XML �����߻�.");
            System.err.println("\t�޽���: " + e.toString());
            if ( txn != null ) {
                txn.abort();
            }
            throw e;
        } catch (DatabaseException de) {
            System.err.println(theContainer + "�� ���� ���� ������ �����߻�.");
            System.err.println("\t�޽���: " + de.toString());
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
