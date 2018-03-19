# Oracle_BDB_XML_Java_API

### buildDB.java

Berkeley DB 데이터베이스에서 DBXML 컨테이너를 사용하는 방법을 보여준다. Berkeley DB 데이터베이스는 컨테이너와 동일한 환경에서 작성되며 컨테이너의 문서에 해당하는 데이터가 데이터베이스에 로드 된다. DBXML 쿼리와 데이터베이스 넣기는 모두 공통 트랜잭션으로 래핑된다.

### retrieveDB.java
  
Berkeley DB 데이터베이스에서 DBXML 컨테이너를 사용하는 방법을 보여준다. 컨테이너에서 문서를 검색한 다음 각 문서에 해당하는 데이터를 Berkeley DB 데이터베이스에서 검색한다. 다시 말하지만 모든 쿼리는 공통 트랜잭션으로 래핑된다. 최상의 결과를 얻으려면 이 예제를 실행하기 전에 buildDB를 실행하시오.

### BDB XML 명령어

![help](https://github.com/boncheul92nd/Oracle_BDB_XML_Java_API/blob/master/img/help.PNG)

|명령어    |설명     |
|------------|----------|
|`#`|코멘트, 아무것도 하지 않는다.|
|`abort`|현재 트랜잭션을 중단한다.|
|`addAlias`||
