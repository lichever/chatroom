import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * The type Server worker.
 */
public class ServerWorker extends Thread {


  /**
   * The constant CONNECT_MESSAGE.
   */
  public static final int CONNECT_MESSAGE = 19;
  /**
   * The constant CONNECT_RESPONSE.
   */
  public static final int CONNECT_RESPONSE = 20;
  /**
   * The constant DISCONNECT_MESSAGE.
   */
  public static final int DISCONNECT_MESSAGE = 21;
  /**
   * The constant QUERY_CONNECTED_USERS.
   */
  public static final int QUERY_CONNECTED_USERS = 22;
  /**
   * The constant QUERY_USER_RESPONSE.
   */
  public static final int QUERY_USER_RESPONSE = 23;
  /**
   * The constant BROADCAST_MESSAGE.
   */
  public static final int BROADCAST_MESSAGE = 24;
  /**
   * The constant DIRECT_MESSAGE.
   */
  public static final int DIRECT_MESSAGE = 25;
  /**
   * The constant FAILED_MESSAGE.
   */
  public static final int FAILED_MESSAGE = 26;
  /**
   * The constant SEND_INSULT.
   */
  public static final int SEND_INSULT = 27;

  /**
   * The constant CONNECT_FAIL.
   */
  public static final String CONNECT_FAIL = "Invalid repetitive username, please use another username";
  /**
   * The constant DISCONNECT_FAIL.
   */
  public static final String DISCONNECT_FAIL = "Invalid disconnection due to invalid username";
  /**
   * The constant DISCONNECT_OK.
   */
  public static final String DISCONNECT_OK = "You are no longer connected";
  /**
   * The constant BROADCAST_FAIL.
   */
  public static final String BROADCAST_FAIL = "Invalid broadcast due to invalid username";
  /**
   * The constant INSULT_FAIL_SENDER.
   */
  public static final String INSULT_FAIL_SENDER = "Invalid insult message due to invalid sender username";
  /**
   * The constant INSULT_FAIL_RECIPIENT.
   */
  public static final String INSULT_FAIL_RECIPIENT = "Invalid insult message due to invalid recipient username";
  /**
   * The constant SPACE.
   */
  public static final char SPACE = ' ';
  /**
   * The constant THERE_ARE.
   */
  public static final String THERE_ARE = "There are ";
  /**
   * The constant OTHER_CONNECTED_CLIENTS.
   */
  public static final String OTHER_CONNECTED_CLIENTS = " other connected clients";
  private final Socket clientSocket;
  private final Server server;
  private DataInputStream dis;
  private DataOutputStream dos;

  /**
   * Instantiates a new Server worker.
   *
   * @param server       the server
   * @param clientSocket the client socket
   */
  public ServerWorker(Server server, Socket clientSocket) {
    this.server = server;
    this.clientSocket = clientSocket;
  }

  /**
   * Sets dis.
   *
   * @param dis the dis
   */
  public void setDis(DataInputStream dis) {
    this.dis = dis;
  }

  /**
   * Sets dos.
   *
   * @param dos the dos
   */
  public void setDos(DataOutputStream dos) {
    this.dos = dos;
  }

  @Override
  public void run() {
    try {
      handleClientSocket();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Handle client socket.
   *
   * @throws IOException the io exception
   */
  public void handleClientSocket() throws IOException {

    DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
    DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
    this.dis = dis;
    this.dos = dos;
    boolean runningFlag = true;

    while (runningFlag) {

      int id = dis.readInt();
      dis.readChar();

      switch (id) {

        case CONNECT_MESSAGE:
          int connectMessageUserSize = dis.readInt();
          dis.readChar();

          byte[] connectMessage = new byte[connectMessageUserSize];
          dis.read(connectMessage, 0, connectMessageUserSize);
          String connectMessageUsername = new String(connectMessage, StandardCharsets.UTF_8);

          handleConnect(dos, connectMessageUsername);
          break;

        case DISCONNECT_MESSAGE:
          int disconnectMessageUserSize = dis.readInt();
          dis.readChar();

          byte[] disconnectMessage = new byte[disconnectMessageUserSize];
          dis.read(disconnectMessage, 0, disconnectMessageUserSize);
          String disconnectMessageUsername = new String(disconnectMessage, StandardCharsets.UTF_8);

          handleDisconnect(dos, disconnectMessageUsername);
          runningFlag = false;
          break;

        case QUERY_CONNECTED_USERS:
          int queryMessageUserSize = dis.readInt();
          dis.readChar();

          byte[] queryMessage = new byte[queryMessageUserSize];
          dis.read(queryMessage, 0, queryMessageUserSize);
          String queryMessageUsername = new String(queryMessage, StandardCharsets.UTF_8);

          handleQuery(dos, queryMessageUsername);
          break;

        case BROADCAST_MESSAGE:
          int broadcastMessageUserSize = dis.readInt();
          dis.readChar();

          byte[] broadcastSender = new byte[broadcastMessageUserSize];
          dis.read(broadcastSender, 0, broadcastMessageUserSize);
          String broadcastSenderUsername = new String(broadcastSender, StandardCharsets.UTF_8);
          dis.readChar();

          int broadcastMessageSize = dis.readInt();
          dis.readChar();

          byte[] broadcastMessageArr = new byte[broadcastMessageSize];
          dis.read(broadcastMessageArr, 0, broadcastMessageSize);

          handleBroadcast(dos, broadcastMessageUserSize, broadcastSenderUsername,
              broadcastMessageSize, broadcastMessageArr);

          break;

        case DIRECT_MESSAGE:
          int directMessageSenderSize = dis.readInt();
          dis.readChar();

          byte[] directSender = new byte[directMessageSenderSize];
          dis.read(directSender, 0, directMessageSenderSize);
          String directSenderUsername = new String(directSender, StandardCharsets.UTF_8);
          dis.readChar();

          int directRecipientSize = dis.readInt();
          dis.readChar();

          byte[] directRecipientNameArr = new byte[directRecipientSize];
          dis.read(directRecipientNameArr, 0, directRecipientSize);
          String directRecipientName = new String(directRecipientNameArr, StandardCharsets.UTF_8);
          dis.readChar();

          int directMessageSize = dis.readInt();
          dis.readChar();

          byte[] directMessageArr = new byte[directMessageSize];
          dis.read(directMessageArr, 0, directMessageSize);

          handleDirectMessage(dos, directSenderUsername, directRecipientName, directMessageSize,
              directMessageArr);

          break;

        case SEND_INSULT:
          int insultSenderSize = dis.readInt();
          dis.readChar();

          byte[] insultSender = new byte[insultSenderSize];
          dis.read(insultSender, 0, insultSenderSize);
          String insultSenderUsername = new String(insultSender, StandardCharsets.UTF_8);
          dis.readChar();

          int insultRecipientSize = dis.readInt();
          dis.readChar();

          byte[] insultRecipientNameArr = new byte[insultRecipientSize];
          dis.read(insultRecipientNameArr, 0, insultRecipientSize);
          String insultRecipientName = new String(insultRecipientNameArr, StandardCharsets.UTF_8);

          handleInsult(dos, insultSenderUsername, insultRecipientName);

          break;
        default:
          break;

      }
    }

    clientSocket.close();
  }


  /**
   * Handle connect.
   *
   * @param dos      the dos
   * @param username the username
   * @throws IOException the io exception
   */
  public void handleConnect(DataOutputStream dos, String username) throws IOException {

    dos.writeInt(CONNECT_RESPONSE);
    dos.writeChar(SPACE);

    if (server.getWorkerMap().containsKey(username)) {

      dos.writeBoolean(false);
      dos.writeChar(SPACE);
      dos.writeInt(CONNECT_FAIL.getBytes(StandardCharsets.UTF_8).length);
      dos.writeChar(SPACE);
      dos.write(CONNECT_FAIL.getBytes(StandardCharsets.UTF_8));

    } else {

      dos.writeBoolean(true);
      dos.writeChar(SPACE);
      String temp = THERE_ARE + server.getWorkerMap().size() + OTHER_CONNECTED_CLIENTS;
      dos.writeInt(temp.getBytes(StandardCharsets.UTF_8).length);
      dos.writeChar(SPACE);
      dos.write(temp.getBytes(StandardCharsets.UTF_8));
      server.getWorkerMap().put(username, dos);
    }
  }

  /**
   * Handle disconnect.
   *
   * @param dos      the dos
   * @param username the username
   * @throws IOException the io exception
   */
  public void handleDisconnect(DataOutputStream dos, String username) throws IOException {

    dos.writeInt(CONNECT_RESPONSE);
    dos.writeChar(SPACE);
    if (server.getWorkerMap().containsKey(username)) {
      dos.writeBoolean(true);
      dos.writeChar(SPACE);
      dos.writeInt(DISCONNECT_OK.getBytes(StandardCharsets.UTF_8).length);
      dos.writeChar(SPACE);
      dos.write(DISCONNECT_OK.getBytes(StandardCharsets.UTF_8));
      server.getWorkerMap().remove(username);

    } else {
      dos.writeBoolean(false);
      dos.writeChar(SPACE);
      dos.writeInt(DISCONNECT_FAIL.getBytes(StandardCharsets.UTF_8).length);
      dos.writeChar(SPACE);
      dos.write(DISCONNECT_FAIL.getBytes(StandardCharsets.UTF_8));
    }
  }

  /**
   * Handle query.
   *
   * @param dos      the dos
   * @param username the username
   * @throws IOException the io exception
   */
  public void handleQuery(DataOutputStream dos, String username) throws IOException {

    dos.writeInt(QUERY_USER_RESPONSE);
    dos.writeChar(SPACE);
    if (server.getWorkerMap().containsKey(username)) {
      int size = server.getWorkerMap().size();

      if (size == 1) {
        dos.writeInt(size - 1);
      } else {
        dos.writeInt(size - 1);
        for (String name : server.getWorkerMap().keySet()) {
          if (name.equals(username)) {
            continue;
          }
          dos.writeChar(SPACE);
          dos.writeInt(name.getBytes(StandardCharsets.UTF_8).length);
          dos.writeChar(SPACE);
          dos.write(name.getBytes(StandardCharsets.UTF_8));
        }
      }
    } else {
      dos.writeInt(0);
    }
  }


  /**
   * Handle broadcast.
   *
   * @param dos                      the dos
   * @param broadcastMessageUserSize the broadcast message user size
   * @param broadcastSenderUsername  the broadcast sender username
   * @param broadcastMessageSize     the broadcast message size
   * @param broadcastMessageArr      the broadcast message arr
   * @throws IOException the io exception
   */
  public void handleBroadcast(DataOutputStream dos, int broadcastMessageUserSize,
      String broadcastSenderUsername, int broadcastMessageSize, byte[] broadcastMessageArr)
      throws IOException {
    if (server.getWorkerMap().containsKey(broadcastSenderUsername)) {

      for (DataOutputStream os : server.getWorkerMap().values()) {

        os.writeInt(BROADCAST_MESSAGE);
        os.writeChar(SPACE);
        os.writeInt(broadcastMessageUserSize);
        os.writeChar(SPACE);
        os.write(broadcastSenderUsername.getBytes(StandardCharsets.UTF_8));
        os.writeChar(SPACE);
        os.writeInt(broadcastMessageSize);
        os.writeChar(SPACE);
        os.write(broadcastMessageArr);
      }
    } else {
      writeFailedMessage(BROADCAST_FAIL, dos);
    }
  }


  /**
   * Handle direct message.
   *
   * @param dosSender            the dos sender
   * @param directSenderUsername the direct sender username
   * @param directRecipientName  the direct recipient name
   * @param directMessageSize    the direct message size
   * @param directMessageArr     the direct message arr
   * @throws IOException the io exception
   */
  public void handleDirectMessage(DataOutputStream dosSender, String directSenderUsername,
      String directRecipientName, int directMessageSize, byte[] directMessageArr)
      throws IOException {

    if (server.getWorkerMap().containsKey(directSenderUsername)) {
      if (server.getWorkerMap().containsKey(directRecipientName)) {

        DataOutputStream dosRecipient = server.getWorkerMap().get(directRecipientName);
        dosRecipient.writeInt(DIRECT_MESSAGE);
        dosRecipient.writeChar(SPACE);
        dosRecipient.writeInt(directSenderUsername.getBytes(StandardCharsets.UTF_8).length);
        dosRecipient.writeChar(SPACE);
        dosRecipient.write(directSenderUsername.getBytes(StandardCharsets.UTF_8));
        dosRecipient.writeChar(SPACE);
        dosRecipient.writeInt(directRecipientName.getBytes(StandardCharsets.UTF_8).length);
        dosRecipient.writeChar(SPACE);
        dosRecipient.write(directRecipientName.getBytes(StandardCharsets.UTF_8));
        dosRecipient.writeChar(SPACE);
        dosRecipient.writeInt(directMessageSize);
        dosRecipient.writeChar(SPACE);
        dosRecipient.write(directMessageArr);

      }
    }
  }

  /**
   * Handle insult.
   *
   * @param dosSender            the dos sender
   * @param insultSenderUsername the insult sender username
   * @param insultRecipientName  the insult recipient name
   * @throws IOException the io exception
   */
  public void handleInsult(DataOutputStream dosSender, String insultSenderUsername,
      String insultRecipientName)
      throws IOException {
    if (server.getWorkerMap().containsKey(insultSenderUsername)) {
      if (server.getWorkerMap().containsKey(insultRecipientName)) {

        String insult = RandomSentence.generateInsult();
        for (DataOutputStream dosRecipient : server.getWorkerMap().values()) {
          dosRecipient.writeInt(DIRECT_MESSAGE);
          dosRecipient.writeChar(SPACE);
          dosRecipient.writeInt(insultSenderUsername.getBytes(StandardCharsets.UTF_8).length);
          dosRecipient.writeChar(SPACE);
          dosRecipient.write(insultSenderUsername.getBytes(StandardCharsets.UTF_8));
          dosRecipient.writeChar(SPACE);
          dosRecipient.writeInt(insultRecipientName.getBytes(StandardCharsets.UTF_8).length);
          dosRecipient.writeChar(SPACE);
          dosRecipient.write(insultRecipientName.getBytes(StandardCharsets.UTF_8));
          dosRecipient.writeChar(SPACE);
          dosRecipient.writeInt(insult.getBytes(StandardCharsets.UTF_8).length);
          dosRecipient.writeChar(SPACE);
          dosRecipient.write(insult.getBytes(StandardCharsets.UTF_8));

        }
      } else {
        writeFailedMessage(INSULT_FAIL_RECIPIENT, dosSender);
      }
    } else {
      writeFailedMessage(INSULT_FAIL_SENDER, dosSender);
    }
  }

  /**
   * Write failed message.
   *
   * @param message the message
   * @param dos     the dos
   * @throws IOException the io exception
   */
  public void writeFailedMessage(String message, DataOutputStream dos) throws IOException {
    dos.writeInt(FAILED_MESSAGE);
    dos.writeChar(SPACE);
    dos.writeInt(message.getBytes(StandardCharsets.UTF_8).length);
    dos.writeChar(SPACE);
    dos.write(message.getBytes(StandardCharsets.UTF_8));
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ServerWorker)) {
      return false;
    }
    ServerWorker that = (ServerWorker) o;
    return Objects.equals(clientSocket, that.clientSocket) && Objects.equals(
        server, that.server);
  }

  @Override
  public int hashCode() {
    return Objects.hash(clientSocket, server);
  }


  @Override
  public String toString() {
    return "This is the ServerWorker to handle client";
  }
}
