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
        String usageMessage = "이 프로그램은 DB XML 컨테이너에 저장된 XML 문서의 특정 집합에서 노드 정보를 검색한 다음, 관련 샘플 데이터를 Berkeley DB 데이터베이스에 저장한다.\n";
        usageMessage += "DB XML 컨테이너와 Berkeley DB 데이터베이스는 동일한 데이터베이스 환경에 저장된다.\n";
        usageMessage += "XML 문서에서 발견된 노드 값은 데이터베이스에 저장된 샘플 데이터의 Berkeley DB 키로 사용된다.\n";
        usageMessage += "'retrieveDB' 샘플 프로그램을 사용하여 저장된 데이터를 검색하시오.\n";
        usageMessage += "이 프로그램을 실행하기 전에 exampleLoadContainer를 실항하여 DB XML 컨테이너를 해당 데이터로 미리 채우시오.\n";
        usageMessage += "이 프로그램을 실행할 때 exampleLoadContainer에 예제 데이터를 배치할 디랙토리 위치 확인: \n";
        usageMessage += "\t-h <dbenv directory>\n";
        usageMessage += "For example:\n";
        usageMessage += "\t > java buildDB -h exampleEnvironment\n";
    }

    // 객체를 정리하는 유틸리티 함수. container와 environment는 정리되어야 한다
    private static void cleanup(myDbEnv env, XmlContainer openedContainer) {
        try {
            if(openedContainer != null)
                openedContainer.delete();
            if(env != null)
                env.cleanup();
        } catch (Exception e) {
            // 종료시 예외처리 무시
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
            // environment 오픈
            env = new myDbEnv(path2DbEnv);
            XmlManager theMgr = env.getManager();

            // 해당 environment에서 데이터베이스 오픈
            openedDatabase = new myDb(theDB, env.getEnvironment());

            // 트랜잭션 컨테이너 오픈
            XmlContainerConfig config = new XmlContainerConfig();
            config.setTransactional(true);
            openedContainer = theMgr.openContainer(theContainer, config);

            // DB를 사용하여 트랜잭션 생성
            Transaction dbTxn = env.getEnvironment().beginTransaction(null, null);
            txn = theMgr.createTransaction(dbTxn);

            XmlQueryContext resultsContext = theMgr.createQueryContext();

            String theQuery =
                    "distinct-values(collection('namespaceExampleData.dbxml')/vendor/salesrep/name)";

            // XmlManager environment 에 대해 쿼리 수행
            XmlResults results = theMgr.query(txn, theQuery, resultsContext);

            // 도큐먼트 조회 결과 세트에서 값을 추출
            XmlValue value = results.next();

            while(value != null) {
                String theSalesRepkey = value.asString();
                System.out.println("key 에 대한 데이터 입력 : " + theSalesRepkey);

                // 이것은 우리가 데이터베이스에 넣고 있는 데이터이다.
                // 실세계의 예제에서는 salesreps의 jpeg 이미지, 공개 키 또는 XML 문서 스키마에 맞지 않는
                // 다른 정보 비트와 같은 BLOB 이 포함된다. 이 경우 데이터를 검색할 때 무슨일이 일어나는지
                // 알 수 있도록 설명 문자열을 넣을 것이다.
                String theSalesRepData =
                        "이것은 " + theSalesRepkey + "의 데이터베이스에 저장된 데이터이다.";

                DatabaseEntry theKey =
                        new DatabaseEntry(theSalesRepkey.getBytes());
                DatabaseEntry theData =
                        new DatabaseEntry(theSalesRepData.getBytes());

                // 마지막으로 Berkeley DB 에 실제 데이터를 쓴다.
                openedDatabase.getDatabase().putNoOverwrite(txn.getTransaction(), theKey, theData);
                value = results.next();
            }
            results.delete();

            // 이로 인해 쓰기가 영구적으로 이루어진다
            txn.commit();

        } catch(Exception e) {

            System.err.println(theContainer + "에 대한 쿼리 수행중 오류 발생");
            System.err.println("\t메시지: " + e.getMessage());

            // 오류가 발생하면 작업을 중단하고 데이터베이스는 이 작업을 시작한 시점과
            // 동일한 상태로 남겨둔다.
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
