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
        String usageMessage = "\n이 프로그램은 예제 XML 데이터를 예제 컨테이너에 로드함.\n";
        usageMessage += "데이터베이스 환경을 배치할 디렉토리와 xmlData 디렉토리(DB XML 예제 디렉토리에 있음)의 경로를 제공하시오.\n";
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

        // 이 벡터는 예제 컨테이너에 로드할 각 XML 파일에 대한 File 객체를 보유한다.
        List <File> files2add = new LinkedList<File>();

        // .../examples/xmlData 에는 nsData 및 simpleData 라는 두 개의 하위 디랙토리
        // 가 있어야 한다. 여기에는 로드하려는 XML 파일이 들어있다. 그러므로 이들이 존재하는지
        // 확인하시오.
        File nsData = new File(filePath.getPath() + File.separator + "nsData");
        confirmDirectory(nsData);

        File simpleData = new File(filePath.getPath() + File.separator + "simpleData");
        confirmDirectory(simpleData);

        // 예제 xml 파일의 첫 번째 세트를 벡터에 로드한다.
        getXmlFiles(nsData, files2add);

        //이러한 파일을 네임스페이스 컨테이너에 추가한다.
        loadXmlFiles(path2DbEnv, "namespaceExampleData.dbxml", files2add);

        files2add.clear();

        // 네임스페이스를 사용하지 않는 XML 예제 데이터에 대해 반복
        getXmlFiles(simpleData, files2add);
        loadXmlFiles(path2DbEnv, "simpleExampleData.dbxml", files2add);
    }

    // -p 가 존재하는 디랙토리를 가리키는지 확인하는 편리한 메소드
    private static void confirmDirectory(File directory) {
        if(!directory.isDirectory()) {
            System.out.println("\nError. Directory " + directory.getPath() + "가 존재하지 않음");
            System.out.println("\t -p 는 xmlData 디랙토리를 가리켜야 한다.");
            usage();
        }
    }

    // 지정된 디랙토리에 있는 모든 xml 파일을 찾아 벡터에 저장
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
            System.out.println("\nError: " + filePath.getPath() + "에서 XML파일이 발견되지 않음");
            usage();
        }
    }

    // 객체를 정리하는 유틸리티 함수, 예외처리 또는 XmlContainer 및 XmlManager
    // 개체를 닫음
    private static void cleanup(XmlManager theMgr, XmlContainer openedContainer) {
        try {
            if(openedContainer != null)
                openedContainer.delete();
            if(theMgr != null)
                theMgr.delete();
        } catch (Exception e) {
            // 종료시 예외처리 무시
        }
    }

    // 환경 생성. home 이 없는 경우 throw
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

    // 파일 벡터를 가져와 각 요소를 DB XML 컨테이너로 로드
    private static void loadXmlFiles(File path2DbEnv, String theContainer,
                                     List files2add) throws  Throwable{
        // db 환경에서 컨테이너 오픈
        XmlManager theMgr = null;
        XmlContainer openedContainer = null;
        XmlTransaction txn = null;
        Environment env = null;

        try {
            env = createEnv(path2DbEnv);
            theMgr = new XmlManager(env, new XmlManagerConfig());

            // 트랜잭션 컨테이너 생성
            XmlContainerConfig config = new XmlContainerConfig();
            config.setTransactional(true);
            openedContainer = theMgr.createContainer(theContainer, config);

            // DB 를 통해 다른 트랜젝션을 받음. 이는 DB 에서 생성된 트랜젝션이
            // XmlManager.createTransaction 에 전달될 수 있음을 보여줌
            Transaction dbtxn = env.beginTransaction(null, null);
            txn = theMgr.createTransaction();
            Iterator filesIterator = files2add.iterator();

            while(filesIterator.hasNext()) {

                File file = (File)filesIterator.next();
                String theFile = file.toString();

                // String에 XML 파일의 내용 로드
                String theLine = null;
                String xmlString = new String();
                FileInputStream fis = new FileInputStream(theFile);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                while((theLine=br.readLine()) != null) {
                    xmlString += theLine;
                    xmlString += "\n";
                }
                br.close();

                // XML 문서 선언
                XmlDocument xmlDoc = theMgr.createDocument();
                // xml 문서의 내용을 방금 얻은 xmlString 으로 설정
                xmlDoc.setContent(xmlString);

                // 도큐먼트 이름 설정
                xmlDoc.setName(file.getName());

                Date theDate = new Date();
                xmlDoc.setMetaData(mdConst.uri, mdConst.name, new XmlValue(theDate.toString()));

                // 해당 도큐먼트를 컨테이너에 넣음
                openedContainer.putDocument(txn, xmlDoc);
                System.out.println(theFile + "을(를) " + theContainer + "(컨테이너)에 추가함");
            }
            txn.commit();

            // XmlException 은 DatabaseException 을 상속받으며,
            // 다시 DatabaseException 은 Exception 을 상속받는다.
            // 그러므로 Catching Exception 절에서 모든 예외 처리 가능
        } catch (Exception e) {

            System.err.println(theContainer + "컨테이너에 파일 로드중 오류 발생");
            System.err.println("\t메시지: " + e.getMessage());

            // 오류 발생시 작업 중단. 데이터베이스는 이 작업을 시작하기 전과
            // 동일한 상태로 유지
            if(txn != null) {
                txn.abort();
            }
            throw e;
        } finally {
            cleanup(theMgr, openedContainer);
        }
    }
}
