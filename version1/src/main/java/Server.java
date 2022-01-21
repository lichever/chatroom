import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Server.
 */
public class Server extends Thread {


  private final int serverPort;

  private Map<String, DataOutputStream> workerMap = new ConcurrentHashMap<>();

  /**
   * Instantiates a new Server.
   *
   * @param serverPort the server port
   */
  public Server(int serverPort) {
    this.serverPort = serverPort;
  }


  /**
   * Gets worker map.
   *
   * @return the worker map
   */
  public Map<String, DataOutputStream> getWorkerMap() {
    return workerMap;
  }

  @Override
  public void run() {
    try {
      ServerSocket serverSocket = new ServerSocket(serverPort);
      while (true) {
        if (workerMap.size() > 10) {
          continue;
        }

        Socket clientSocket = serverSocket.accept();
        ServerWorker worker = new ServerWorker(this, clientSocket);
        worker.start();

      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * override equals method
   *
   * @param o the o
   * @return the boolean
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Server)) {
      return false;
    }
    Server server = (Server) o;
    return serverPort == server.serverPort && Objects.equals(workerMap, server.workerMap);
  }

  /**
   * override hashCode method
   *
   * @return the int
   */
  @Override
  public int hashCode() {
    return Objects.hash(serverPort, workerMap);
  }


  /**
   * override toString method
   *
   * @return the string
   */
  @Override
  public String toString() {
    return "This is Server with serverPort:" + serverPort;
  }
}
