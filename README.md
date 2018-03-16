# Oracle_BDB_XML_Java_API

### buildDB.java

Illustrates how a DBXML container can be used with a Berkeley DB database. A Berkeley DB database is created in the same environment as the container and data corresponding to documents in the container is loaded into the database. The DBXML query and database put are all wrapped in a common transaction.

### retrieveDB.java
  
Illustrates how a DBXML container can be used with a Berkeley DB database. Documents are retrieved from the container and then data corresponding to each document is retrieved from the Berkeley DB database. Again, all queries are wrapped in a common transaction. For best results, run buildDB before running this example.
