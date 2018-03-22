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
        String usageMessage = "이 프로그램은 DB XML 컨테이너의 문서에서 검색된 정보를 기반으로 Berkeley DB 데이터베이스에서 데이터를 검색한다.\n";
        usageMessage += "DB XML 컨테이너와 Berkeley DB 데이터베이스는 동일한 데이터베이스 환경에 저장된다.\n";
        usageMessage += "XML 문서에서 발견된 노드 값은 데이터베이스에 저장된 샘플 데이터의 Berkeley DB 키로 사용된다.\n";
        usageMessage += "이 프로그램을 실행하기 전에 exampleLoadContainer를 먼저 실행하여 DB XML 컨테이너를 해당 데이터로 미리 채워야 한다.\n";
        usageMessage += "그런 다음 'buildDB' 샘플 프로그램을 사용하여 프로그램에 필요한 Berkeley DB 데이터베이스를 빌드하시오.\n";
        usageMessage += "이 프로그램을 실행할 때 exampleLoadContainer에게 예제 데이터를 배치할 위치를 지정한 디렉터리를 확인하시오.\n";
        usageMessage += "\t-h <dbenv directory>\n";
        usageMessage += "For example:\n";
        usageMessage += "\tjava retrieveDB -h examplesEnvironment\n";

        System.out.println(usageMessage);
        System.exit(-1);
    }

    // 객체를 정리하는 유틸리티 함수. 컨테이너와 환경파일은
    // 닫혀야 한다.
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

            // environment 오픈
            env = new myDbEnv(path2DbEnv);
            XmlManager theMgr = env.getManager();

            // 해당 environment에서 데이터베이스 오픈
            openedDatabase = new myDb(theDB, env.getEnvironment());

            // 트랜잭션 컨테이너 열기
            XmlContainerConfig config = new XmlContainerConfig();
            config.setTransactional(true);
            openedContainer = theMgr.openContainer(theContainer, config);

            // DB를 통해 트랜잭션 시작
            dbTxn = env.getEnvironment().beginTransaction(null, null);
            txn = theMgr.createTransaction();

            // 결과 형식이 값(도큐먼트 아님)으로 설정된 XmlQueryContext를 가져옴
            XmlQueryContext resultsContext = theMgr.createQueryContext();

            String theQuery = "collection('editorContainer.dbxml')";

            // 컨테이너에서 도큐먼트를 가져옴
            XmlResults results = theMgr.query(txn, theQuery, resultsContext);
            XmlValue value = results.next();

            while(value != null) {

                // 도큐먼트 쿼리 결과 집합에서 값을 추출
                String doc = value.asString();
                System.out.println(doc);
                value = results.next();
            }
            results.delete();
            txn.commit();

        } catch (XmlException e) {
            System.err.println(theContainer + "에 대해 쿼리를 수행하는 중 XML 오류발생.");
            System.err.println("\t메시지: " + e.toString());
            if ( txn != null ) {
                txn.abort();
            }
            throw e;
        } catch (DatabaseException de) {
            System.err.println(theContainer + "에 대한 쿼리 수행중 오류발생.");
            System.err.println("\t메시지: " + de.toString());
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
