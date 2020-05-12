package site.ycsb.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import java.nio.file.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import site.ycsb.ByteIterator;
import site.ycsb.DB;
import site.ycsb.DBException;
import site.ycsb.Status;

/**
 * A database interface layer for generating key-value workload traces.
 */
public class KVTracerClient extends DB {

  public static final String SEP = ":";
  public static final String ESCAPE = "\\";

  public static final String TRACE_FILE_PROPERTY = "kvtracer.tracefile";
  public static final String KEYMAP_FILE_PROPERTY = "kvtracer.keymapfile";
  private static final AtomicInteger NEXT_ID = new AtomicInteger(0);
  // Thread local variable containing each thread's ID
  private static final ThreadLocal<Integer> THREAD_ID =
      new ThreadLocal<Integer>() {
        @Override protected Integer initialValue() {
          return NEXT_ID.getAndIncrement();
        }
      };
  // Returns the current thread's unique ID, assigning it if necessary
  public static int getTID() {
    return THREAD_ID.get();
  }

  // private HashMap<String, HashSet<String>> mKeyMap = new HashMap<String, HashSet<String>>();
  private BufferedWriter mOpWriter;
  private BufferedWriter mKeyWriter;


  @Override
  public void init() throws DBException {
    Properties props = getProperties();
    Path traceDir = Paths.get(props.getProperty(TRACE_FILE_PROPERTY));
    // Path keymapDir = Paths.get(props.getProperty(KEYMAP_FILE_PROPERTY));
    int totalThread = Integer.parseInt(props.getProperty("kvtracer.totalthread"));
    try {
      // if(!Files.exists(traceDir)) {
      //   Files.createDirectories(traceDir);
      // }
      // if(!Files.exists(keymapDir)) {
      //   Files.createDirectories(keymapDir);
      // }
      String traceFileName = traceDir.toAbsolutePath().toString()+"-"+Integer.toString(totalThread)+
          "."+Integer.toString(getTID());
      File traceFile = new File(traceFileName);
      if (!traceFile.exists()) {
        traceFile.createNewFile();
      }
      mOpWriter = new BufferedWriter(new FileWriter(traceFile));

    } catch (IOException exception) {
      throw new DBException(exception);
    }
  }

  @Override
  public void cleanup() throws DBException {
    try {
      if (mOpWriter != null) {
        mOpWriter.close();
      }
    } catch (IOException exception) {
      throw new DBException(exception);
    }
    // mKeyMap.clear();
  }

  public static String escape(String str) {
    String s1 = str.replace(ESCAPE, ESCAPE + ESCAPE);
    return s1.replace(SEP, ESCAPE + SEP);
  }

  public static String unescape(String encStr) {
    String s1 = encStr.replace(ESCAPE + SEP, SEP);
    return s1.replace(ESCAPE + ESCAPE, ESCAPE);
  }

  @Override
  public Status read(final String table, final String key, final Set<String> fields,
      final Map<String, ByteIterator> result) {
    try {
      if (fields == null) {
        mOpWriter.write("Read " + key + "\n");
      } else {
        throw new DBException("only support requests without fields");
      }
      return Status.OK;
    } catch (IOException | DBException exception) {
      exception.printStackTrace();
      return Status.ERROR;
    }
  }

  @Override
  public Status scan(final String table, final String startkey, final int recordcount, final Set<String> fields,
        final Vector<HashMap<String, ByteIterator>> result) {
    System.err.println("scan operation is not supported");
    return Status.ERROR;
  }

  @Override
  public Status update(final String table, final String key, final Map<String, ByteIterator> values) {
    try {
      if(values.size() == 1) {
        for (Map.Entry<String, ByteIterator> entry : values.entrySet()) {
          String value = entry.getValue().toString();
          mOpWriter.write("Update " + key + "\n");
        }
      } else {
        throw new DBException("only support requests without fields");
      }
      return Status.OK;
    } catch (IOException | DBException exception) {
      exception.printStackTrace();
      return Status.ERROR;
    }
  }

  @Override
  public Status insert(final String table, final String key, final Map<String, ByteIterator> values) {
    try {
      if(values.size() == 1) {
        for (Map.Entry<String, ByteIterator> entry : values.entrySet()) {
          String value = entry.getValue().toString();
          mOpWriter.write("Add " + key + "\n");
        }
      } else {
        throw new DBException("only support requests without fields");
      }
      return Status.OK;
    } catch (IOException | DBException exception) {
      exception.printStackTrace();
      return Status.ERROR;
    }
  }

  @Override
  public Status delete(final String table, final String key) {
    try {
      mOpWriter.write("Remove " + key + "\n");
    } catch (IOException exception) {
      exception.printStackTrace();
      return Status.ERROR;
    }
    return Status.OK;
  }
}
