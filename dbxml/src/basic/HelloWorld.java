package basic;

import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlDocument;

/**
 * Created by BON-CHEUL on 2018-03-12.
 *
 * HelloWorld is the simplest possible Berkeley DB XML program
 * that does something.
 * This program demonstrates initialization, container creation,
 * document insertion and document retrieval by name.
 * <p>
 * To run the example:
 * <pre>
 * java basic.HelloWorld
 * </pre>
 */

public class HelloWorld {

    // This function is used to ensure that databases are
    // properly closed, even on exceptions
    private static void cleanup(XmlManager mgr, XmlContainer cont) {
        try {
            if (cont != null)
                cont.delete();
            if (mgr != null)
                mgr.delete();
        } catch (Exception e) {
            // ignore exception in cleanup
        }
    }
/*
    public static void main(String[] args) throws Throwable{
        // An empty string means an in-memory container, which
        // will not be persisted.
        String containerName = "";
        String content = "<hello>Hello World</hello>";
        String docName = "doc";
        XmlManager mgr = null;
        XmlContainer cont = null;

        try {
            // All BDB XML programs require an XmlManager instance
            mgr = new XmlManager();
            cont = mgr.createContainer(containerName);
            cont.putDocument(docName, content);

            // Now, get the document
            XmlDocument doc = cont.getDocument(docName);
            String name = doc.getName();
            String docContent = doc.getContentAsString();

            // print it
            System.out.println("Document name: " + name + "\nContent: " + docContent);

        } catch (XmlException xe) {
            System.err.println("XmlException during HelloWorld: " + xe.getMessage());
        } finally {
            cleanup(mgr, cont);
        }
    }
*/
}
