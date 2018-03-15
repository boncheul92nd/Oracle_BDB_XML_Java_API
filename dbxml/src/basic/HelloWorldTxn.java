package basic;

/**
 * Created by USER on 2018-03-15.
 *
 * HelloWorld is the simplest poosible Berkeley DB XML program
 * that does something.
 * This program demonstrates transactional initialization and container createion,
 * transactional document insertion and retrieval by name.
 *
 * The length of this program is due to the use of configuration
 * objects, and the need for explicit deletion/cleanup of Java
 * objects in order to release resources.
 *
 * After running this program, you will notice a number of files in the
 * environment directory:
 *      __db.* files are the BDB environment, including cache
 *      log.* files are BDB log files for transactions
 *
 * The actual BDB XML container is not present, since it's only
 * created in-memory, and will disappear at the end of the program.
 *
 * To run the example:
 *      java basic.HelloWorldTxn [-h environmentDirectiory]
 */

import java.io.*;

import com.sleepycat.dbxml.XmlException;
import com.sleepycat.dbxml.XmlManager;
import com.sleepycat.dbxml.XmlManagerConfig;
import com.sleepycat.dbxml.XmlContainer;
import com.sleepycat.dbxml.XmlContainerConfig;
import com.sleepycat.dbxml.XmlDocument;
import com.sleepycat.dbxml.XmlUpdateContext;
import com.sleepycat.dbxml.XmlQueryContext;

import com.sleepycat.dbxml.XmlTransaction;
import com.sleepycat.db.Environment;
import com.sleepycat.db.EnvironmentConfig;
import com.sleepycat.db.DatabaseException;

public class HelloWorldTxn {

    static void usage() {
        System.err.println("Usage: java basic.HelloWorldTxn [-h environmentDirectory]");
        System.exit(-1);
    }

    // This function is used to ensure that database are properly closed,
    // even on exceptions
    private static void cleanup(Environment env, XmlManager mgr, XmlContainer cont) {

        try {
            if(cont != null)
                cont.delete();
            if(mgr != null)
                mgr.delete();
            if(env != null)
                env.close();
        } catch (Exception e) {
            // ignore exceptions in cleanup
        }
    }

    private static Environment createEnvironment(String home) throws Throwable{

        EnvironmentConfig config = new EnvironmentConfig();

        config.setTransactional(true);
        config.setAllowCreate(true);
        config.setRunRecovery(true);
        config.setInitializeCache(true);
        config.setCacheSize(25 * 1024 * 1024); //25Mb cache
        config.setInitializeLocking(true);
        config.setInitializeLogging(true);
        config.setErrorStream(System.err);

        File f = new File(home);

        return new Environment(f, config);
    }

    public static void main(String[] args) throws Throwable{

        // Am empty string means an in-memory container, which
        // will not be persisted
        String containerName = "";
        String content = "<hello>Hello World</hello>";
        String docName = "doc";
        String environmentDir = ".";

        if(args.length == 2) {
            environmentDir = args[1];
        } else if(args.length != 0)
            usage();
    }
}
