package result;

/**
 * Created by USER on 2018-03-19.
 */

import java.io.*;

import com.sleepycat.dbxml.*;
import com.sleepycat.db.*;
import dbxml.gettingStarted.*;

public class buildDBProto {

    private static String theContainer = "editorContainer.dbxml";
    private static String theDB = "editorContainerDB";

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

            // �ش� environment ���� �����ͺ��̽� ����
            openedDatabase = new myDb(theDB, env.getEnvironment());

            // Ʈ����� �����̳� ����
            XmlContainerConfig config = new XmlContainerConfig();
            config.setTransactional(true);
            openedContainer = theMgr.openContainer(theContainer, config);

        } catch(Exception e) {
            System.err.println(theContainer + "�� ���� ���� ������ ���� �߻�");
            System.err.println("\t�޽���: " + e.getMessage());
            // ������ �߻��ϸ� �۾��� �ߴ��ϰ� �����ͺ��̽��� �� �۾��� ������ ������
            // ������ ���·� ���ܵд�.
            if(txn != null)
                txn.abort();
            throw e;
        } finally {
            if(openedDatabase != null)
                openedDatabase.cleanup();
            cleanup(env, openedContainer);
        }
    }
}