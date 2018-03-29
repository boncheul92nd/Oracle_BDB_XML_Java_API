package gettingStarted;

/**
 * Created by USER on 2018-03-16.
 */

import java.io.*;
import java.util.*;

import com.sleepycat.db.*;
import com.sleepycat.dbxml.*;
import dbxml.gettingStarted.mdConst;

public class exampleLoadContainer {

    private static void usage() {
        String usageMessage = "\n�� ���α׷��� ���� XML �����͸� ���� �����̳ʿ� �ε���.\n";
        usageMessage += "�����ͺ��̽� ȯ���� ��ġ�� ���丮�� xmlData ���丮(DB XML ���� ���丮�� ����)�� ��θ� �����Ͻÿ�.\n";
        usageMessage += "\t-h <dbenv directory> -p <filepath>\n";
        usageMessage += "For example:\n";
        usageMessage += "\t > java exampleLoadContainer -h examplesEnvironment -p /home/user1/dbxml-1.1.0/examples/xmlData";
        System.out.println(usageMessage);
        System.exit(-1);
    }

    public static void main(String[] args) throws Throwable {

        File path2DbEnv = null;
        File filePath = null;

        for (int i = 0; i < args.length; i++) {
            if(args[i].startsWith("-")) {
                switch (args[i].charAt(1)) {
                    case 'h':
                        path2DbEnv = new File(args[++i]);
                        break;
                    case 'p':
                        filePath = new File(args[++i]);
                        break;
                    default:
                        usage();
                }
            }
        }

        if (path2DbEnv == null || filePath == null) {
                usage();
        }

        if (!filePath.isDirectory()) {
            usage();
        }

        // �� ���ʹ� ���� �����̳ʿ� �ε��� �� XML ���Ͽ� ���� File ��ü�� �����Ѵ�.
        List <File> files2add = new LinkedList<File>();

        // .../examples/xmlData ���� nsData �� simpleData ��� �� ���� ���� ���丮
        // �� �־�� �Ѵ�. ���⿡�� �ε��Ϸ��� XML ������ ����ִ�. �׷��Ƿ� �̵��� �����ϴ���
        // Ȯ���Ͻÿ�.
        File nsData = new File(filePath.getPath() + File.separator + "nsData");
        confirmDirectory(nsData);

        File simpleData = new File(filePath.getPath() + File.separator + "simpleData");
        confirmDirectory(simpleData);

        // ���� xml ������ ù ��° ��Ʈ�� ���Ϳ� �ε��Ѵ�.
        getXmlFiles(nsData, files2add);

        //�̷��� ������ ���ӽ����̽� �����̳ʿ� �߰��Ѵ�.
        loadXmlFiles(path2DbEnv, "namespaceExampleData.dbxml", files2add);

        files2add.clear();

        // ���ӽ����̽��� ������� �ʴ� XML ���� �����Ϳ� ���� �ݺ�
        getXmlFiles(simpleData, files2add);
        loadXmlFiles(path2DbEnv, "simpleExampleData.dbxml", files2add);
    }

    // -p �� �����ϴ� ���丮�� ����Ű���� Ȯ���ϴ� ���� �޼ҵ�
    private static void confirmDirectory(File directory) {
        if(!directory.isDirectory()) {
            System.out.println("\nError. Directory " + directory.getPath() + "�� �������� ����");
            System.out.println("\t -p �� xmlData ���丮�� �����Ѿ� �Ѵ�.");
            usage();
        }
    }

    // ������ ���丮�� �ִ� ��� xml ������ ã�� ���Ϳ� ����
    private static void getXmlFiles(File filePath, List<File> files2add) {
        boolean filesFound = false;
        String [] dirContents = filePath.list();

        if(dirContents != null) {
            for(int i = 0; i < dirContents.length; i++) {
                File entry = new File(filePath + File.separator + dirContents[i]);
                if(entry.isFile() && entry.toString().toLowerCase().endsWith(".xml")) {
                    files2add.add(entry);
                    filesFound = true;
                }
            }
        }

        if(!filesFound) {
            System.out.println("\nError: " + filePath.getPath() + "���� XML������ �߰ߵ��� ����");
            usage();
        }
    }

    // ��ü�� �����ϴ� ��ƿ��Ƽ �Լ�, ����ó�� �Ǵ� XmlContainer �� XmlManager
    // ��ü�� ����
    private static void cleanup(XmlManager theMgr, XmlContainer openedContainer) {
        try {
            if(openedContainer != null)
                openedContainer.delete();
            if(theMgr != null)
                theMgr.delete();
        } catch (Exception e) {
            // ����� ����ó�� ����
        }
    }

    // ȯ�� ����. home �� ���� ��� throw
    private static Environment createEnv(File home)
            throws DatabaseException, FileNotFoundException{

        EnvironmentConfig config = new EnvironmentConfig();
        config.setCacheSize(50 * 1024 * 1024);
        config.setAllowCreate(true);
        config.setInitializeCache(true);
        config.setTransactional(true);
        config.setInitializeLocking(true);
        config.setInitializeLogging(true);
        return new Environment(home, config);
    }

    // ���� ���͸� ������ �� ��Ҹ� DB XML �����̳ʷ� �ε�
    private static void loadXmlFiles(File path2DbEnv, String theContainer,
                                     List files2add) throws  Throwable{
        // db ȯ�濡�� �����̳� ����
        XmlManager theMgr = null;
        XmlContainer openedContainer = null;
        XmlTransaction txn = null;
        Environment env = null;

        try {
            env = createEnv(path2DbEnv);
            theMgr = new XmlManager(env, new XmlManagerConfig());

            // Ʈ����� �����̳� ����
            XmlContainerConfig config = new XmlContainerConfig();
            config.setTransactional(true);
            openedContainer = theMgr.createContainer(theContainer, config);

            // DB �� ���� �ٸ� Ʈ�������� ����. �̴� DB ���� ������ Ʈ��������
            // XmlManager.createTransaction �� ���޵� �� ������ ������
            Transaction dbtxn = env.beginTransaction(null, null);
            txn = theMgr.createTransaction();
            Iterator filesIterator = files2add.iterator();

            while(filesIterator.hasNext()) {

                File file = (File)filesIterator.next();
                String theFile = file.toString();

                // String�� XML ������ ���� �ε�
                String theLine = null;
                String xmlString = new String();
                FileInputStream fis = new FileInputStream(theFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                while((theLine=br.readLine()) != null) {
                    xmlString += theLine;
                    xmlString += "\n";
                }
                br.close();

                // XML ���� ����
                XmlDocument xmlDoc = theMgr.createDocument();
                // xml ������ ������ ��� ���� xmlString ���� ����
                xmlDoc.setContent(xmlString);

                // ��ť��Ʈ �̸� ����
                xmlDoc.setName(file.getName());

                Date theDate = new Date();
                xmlDoc.setMetaData(mdConst.uri, mdConst.name, new XmlValue(theDate.toString()));

                // �ش� ��ť��Ʈ�� �����̳ʿ� ����
                openedContainer.putDocument(txn, xmlDoc);
                System.out.println(theFile + "��(��) " + theContainer + "(�����̳�)�� �߰���");
            }
            txn.commit();

            // XmlException �� DatabaseException �� ��ӹ�����,
            // �ٽ� DatabaseException �� Exception �� ��ӹ޴´�.
            // �׷��Ƿ� Catching Exception ������ ��� ���� ó�� ����
        } catch (Exception e) {

            System.err.println(theContainer + "�����̳ʿ� ���� �ε��� ���� �߻�");
            System.err.println("\t�޽���: " + e.getMessage());

            // ���� �߻��� �۾� �ߴ�. �����ͺ��̽��� �� �۾��� �����ϱ� ����
            // ������ ���·� ����
            if(txn != null) {
                txn.abort();
            }
            throw e;
        } finally {
            cleanup(theMgr, openedContainer);
        }
    }
}
