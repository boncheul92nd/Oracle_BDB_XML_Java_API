# Oracle_BDB_XML_Java_API

### buildDB.java

Berkeley DB 데이터베이스에서 DBXML 컨테이너를 사용하는 방법을 보여준다. Berkeley DB 데이터베이스는 컨테이너와 동일한 환경에서 작성되며 컨테이너의 문서에 해당하는 데이터가 데이터베이스에 로드 된다. DBXML 쿼리와 데이터베이스 넣기는 모두 공통 트랜잭션으로 래핑된다.

### retrieveDB.java
  
Berkeley DB 데이터베이스에서 DBXML 컨테이너를 사용하는 방법을 보여준다. 컨테이너에서 문서를 검색한 다음 각 문서에 해당하는 데이터를 Berkeley DB 데이터베이스에서 검색한다. 다시 말하지만 모든 쿼리는 공통 트랜잭션으로 래핑된다. 최상의 결과를 얻으려면 이 예제를 실행하기 전에 buildDB를 실행하시오.

### BDB XML 명령어

![help](https://github.com/boncheul92nd/Oracle_BDB_XML_Java_API/blob/master/img/help.PNG)

|명령어    |설명     |
|------------|:----------|
|`#`|코멘트, 아무것도 하지 않는다.|
|`abort`|현재 트랜잭션을 중단한다.|
|`addAlias`|기본 컨테이너에 별칭 추가|
|`addIndex`|기본 컨테이너에 인덱스 추가|
|`commit`|현재 트랜잭션을 커밋하고 새 트랜잭션을 시작|
|`compactContainer`|컨테이너를 압축하여 컨테이너 사이즈 감소|
|`contextQuery`|마지막 결과를 컨텍스트 항목으로 사용하여 쿼리 식 실행|
|`cquery`|기본 컨테이너의 컨텍스트에서 표현식 실행|
|`createContainer`|디폴트 컨테이너가 되는 새로운 컨테이너를 작성|
|`debug`|지정된 쿼리 식 또는 기본 컨테이너를 디버깅한다|
|`debugOptimization`|디버그 최적화 명령 -- 내부 전용|
|`delIndex`|기본 컨테이너에서 인덱스 삭제|
|`echo`|에코를 출력으로|
|`getDocuments`|기본 컨테이너에서 이름으로 문서를 가져옴|
|`getMetaData`|명명 된 컨테이너에서 메타데이터 항목 가져오기|
|`help`|도움말 정보를 인쇄, 확장된 도움말은 `help commandName`을 사용하시오|
|`info`|기본 컨테이너에 대한 정보 얻기|
|`listIndexes`|기본 컨테이너의 모든 색인 나열|
|`lookupEdgeIndex`|기본 컨테이너에서 에지 인덱스 조회 수행|
|`lookupIndex`|기본 컨테이너에서 인덱스 조회를 수행|
|`lookupStats`|기본 컨테이너에서 색인 통계 조회|
|`openContainer`|컨테이너를 열고 기본 컨테이너로 사용|
|`preload`|컨테이너를 미리 로드 함|
|`prepare`|지정된 쿼리 식을 미리 파싱된 기본 쿼리로 준비함|
|`print`|가장 최근의 결과를 출력, 선택적으로 파일에 출력가능|
|`putDocument`|기본 컨테이너에 문서 삽입|
|`query`|주어진 쿼리 식 또는 미리 파싱된 기본 쿼리를 실행|
|`queryPlan`|지정된 쿼리 식에 대한 쿼리 계획을 출력|
|`quit`|프로그램 종료|
|`reindexContainer`|컨테이너 다시 인덱싱, 선택적으로 인덱스 타입을 변경 가능|
|`removeAlias`|기본 컨테이너에서 별칭 제거|
|`removeContainer`|컨테이너 제거|
|`removeDocument`|기본 컨테이너에서 도큐먼트 제거|
|`run`|지정된 파일을 스크립트로 실행|
|`setAutoIndexing`|기본 컨테이너의 자동 인덱싱 상태 설정|
|`setBaseUri`|기본 컨테이너에서 기본 URI를 set/get 한다|
|`setIgnore`|쉘에게 스크립트 에러를 무시하도록 지시|
|`setLazy`|기본 컨텍스트에서 지연 평가를 설정하거나 해제|
|`setMetaData`|명명 된 도큐먼트에 메타데이터 항목 설정|
|`setNamespace`|기본 컨텍스트에서 prefix->namespace 바인딩 만들기|
|`setProjection`|도큐먼트 프로젝션 최적화 사용 또는 사용 중지|
|`setQueryTimeout`|기본 컨텍스트에서 쿼리 시간 초과를 초 단위로 설정|
|`setReturnType`|기본 컨텍스트에서 반환 유형을 설정|
|`setTypedVariable`|변수를 기본 컨텍스트에서 지정된 유형으로 설정|
|`setVariable`|기본 컨텍스트에서 변수 설정|
|`setVerbose`|쉘의 상세도 설정|
|`sync`|현재 컨테이너를 디스크에 동기화|
|`time`|wall-clock 타이머로 명령 랩핑|
|`transaction`|모든 후속 작업에 사용할 트랜잭션 생성|
|`upgradeContainer`|컨테이너를 현재 컨테이너 형식으로 업그레이드|
|`verifyContainer`|현재 컨테이너 형식으로 컨테이너 확인|

### print help

![print_help](https://github.com/boncheul92nd/Oracle_BDB_XML_Java_API/blob/master/img/help_print.PNG)
