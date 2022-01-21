/**
 * The type Server main.
 */
public class ServerMain {

  /**
   * The constant SERVER_PORT.
   */
  public static final int SERVER_PORT = 6666;


  /**
   * The entry point of application.
   *
   * @param args the input arguments
   */
  public static void main(String[] args) {
    Server server = new Server(SERVER_PORT);
    server.start();
  }


  @Override
  public String toString() {
    return "This is entry point for server to start";
  }


}
