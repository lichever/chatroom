import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


/**
 * The type Chat client.
 */
public class ChatClient {

  /**
   * The constant LOGIN_PROMPT.
   */
  public static final String LOGIN_PROMPT = "Please enter your name to login this chat room:";
  /**
   * The constant ONLINE.
   */
  public static final String ONLINE = "online: ";
  /**
   * The constant COLON.
   */
  public static final String COLON = ": ";
  /**
   * The constant ARROW.
   */
  public static final String ARROW = " -> ";
  private final String serverName;
  private final int serverPort;
  private Socket socket;
  private String user;
  private DataOutputStream outputStream;
  private DataInputStream inputStream;

  /**
   * Instantiates a new Chat client.
   *
   * @param serverName the server name
   * @param serverPort the server port
   */
  public ChatClient(String serverName, int serverPort) {
    this.serverName = serverName;
    this.serverPort = serverPort;
    this.user = "";
  }

  /**
   * Start message reader.
   *
   * @param running the running
   */
  public void startMessageReader(boolean[] running) {
    Thread t = new Thread() {
      @Override
      public void run() {
        try {
          handleServerSocket(running);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    t.start();
  }


  /**
   * Handle server socket.
   *
   * @param running the running
   * @throws IOException the io exception
   */
  public void handleServerSocket(boolean[] running) throws IOException {
    while (running[0]) {
      DataInputStream reader = inputStream;
      int messageIdentifier = reader.readInt();
      reader.readChar();
      switch (messageIdentifier) {
        case ServerWorker.CONNECT_RESPONSE:
          boolean success = reader.readBoolean();
          reader.readChar();
          int msgSize = reader.readInt();
          reader.readChar();
          byte[] message = new byte[msgSize];
          reader.read(message, 0, msgSize);
          if (ServerWorker.DISCONNECT_OK
              .equals(new String(message, StandardCharsets.UTF_8))) {
            running[0] = false;
          }
          System.out.println(new String(message, StandardCharsets.UTF_8));
          break;
        case ServerWorker.QUERY_USER_RESPONSE:
          int numberOfUsers = reader.readInt();
          for (int userCount = 0; userCount < numberOfUsers; userCount++) {
            reader.readChar();
            int usernameSize = reader.readInt();
            reader.readChar();
            byte[] username = new byte[usernameSize];
            reader.read(username, 0, usernameSize);
            System.out.println(ONLINE + new String(username, StandardCharsets.UTF_8));
          }
          break;
        case ServerWorker.FAILED_MESSAGE:
          int messageSize = reader.readInt();
          reader.readChar();
          byte[] failureMessage = new byte[messageSize];
          reader.read(failureMessage, 0, messageSize);
          System.out.println(new String(failureMessage, StandardCharsets.UTF_8));
          break;

        case ServerWorker.BROADCAST_MESSAGE:
          int senderSize = reader.readInt();
          reader.readChar();
          byte[] sender = new byte[senderSize];
          reader.read(sender, 0, senderSize);
          reader.readChar();

          int broadcastSize = reader.readInt();
          reader.readChar();
          byte[] broadcast = new byte[broadcastSize];
          reader.read(broadcast, 0, broadcastSize);

          System.out.println(
              new String(sender, StandardCharsets.UTF_8) + COLON + new String(broadcast,
                  StandardCharsets.UTF_8));
          break;

        case ServerWorker.DIRECT_MESSAGE:
          int senderNameSize = reader.readInt();
          reader.readChar();
          byte[] senderName = new byte[senderNameSize];
          reader.read(senderName, 0, senderNameSize);
          reader.readChar();
          int recipientNameSize = reader.readInt();
          reader.readChar();
          byte[] recipientName = new byte[recipientNameSize];
          reader.read(recipientName, 0, recipientNameSize);
          reader.readChar();
          int directMessageSize = reader.readInt();
          reader.readChar();
          byte[] directMessage = new byte[directMessageSize];
          reader.read(directMessage, 0, directMessageSize);
          System.out.println(
              new String(senderName, StandardCharsets.UTF_8) + ARROW + new String(recipientName,
                  StandardCharsets.UTF_8) + COLON + new String(directMessage,
                  StandardCharsets.UTF_8));
          break;
        default:
          break;
      }
    }
  }


  /**
   * Handle login.
   *
   * @throws IOException the io exception
   */
  public void handleLogin() throws IOException {
    Scanner stdin = new Scanner(System.in);
    String username = "";
    while (username.isEmpty() || username.isBlank()) {
      System.out.println(LOGIN_PROMPT);
      username = stdin.nextLine().trim();
    }
    this.user = username;
    sendMessage(user, ServerWorker.CONNECT_MESSAGE);
    handleLoginResponse();
  }

  /**
   * Handle login response.
   *
   * @throws IOException the io exception
   */
  public void handleLoginResponse() throws IOException {
    DataInputStream reader = inputStream;
    int messageIdentifier = reader.readInt();
    reader.readChar();
    boolean success = reader.readBoolean();
    reader.readChar();
    int msgSize = reader.readInt();
    reader.readChar();
    byte[] message = new byte[msgSize];
    reader.read(message, 0, msgSize);
    String response = new String(message, StandardCharsets.UTF_8);
    System.out.println(response);

    if (response.equals(ServerWorker.CONNECT_FAIL)) {
      handleLogin();
    }

  }


  /**
   * Sets output stream.
   *
   * @param outputStream the output stream
   */
  public void setOutputStream(DataOutputStream outputStream) {
    this.outputStream = outputStream;
  }

  /**
   * Sets input stream.
   *
   * @param inputStream the input stream
   */
  public void setInputStream(DataInputStream inputStream) {
    this.inputStream = inputStream;
  }

  /**
   * Send message.
   *
   * @param message           the message
   * @param messageIdentifier the message identifier
   * @throws IOException the io exception
   */
  public void sendMessage(String message, int messageIdentifier)
      throws IOException {
    DataOutputStream dataOutputStream = outputStream;
    byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
    dataOutputStream.writeInt(messageIdentifier);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    dataOutputStream.writeInt(buffer.length);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    dataOutputStream.write(buffer);

  }

  /**
   * Send logoff.
   *
   * @throws IOException the io exception
   */
  public void sendLogoff() throws IOException {
    sendMessage(this.user, ServerWorker.DISCONNECT_MESSAGE);
  }

  /**
   * Send query user.
   *
   * @throws IOException the io exception
   */
  public void sendQueryUser() throws IOException {
    sendMessage(this.user, ServerWorker.QUERY_CONNECTED_USERS);
  }

  /**
   * Send direct message.
   *
   * @param recipient the recipient
   * @param message   the message
   * @throws IOException the io exception
   */
  public void sendDirectMessage(String recipient, String message)
      throws IOException {
    DataOutputStream dataOutputStream = outputStream;
    byte[] sendBuffer = user.getBytes(StandardCharsets.UTF_8);
    dataOutputStream.writeInt(ServerWorker.DIRECT_MESSAGE);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    dataOutputStream.writeInt(sendBuffer.length);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    dataOutputStream.write(sendBuffer);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    byte[] recipientBuffer = recipient.getBytes(StandardCharsets.UTF_8);
    dataOutputStream.writeInt(recipientBuffer.length);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    dataOutputStream.write(recipientBuffer);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    byte[] messageBuffer = message.getBytes(StandardCharsets.UTF_8);
    dataOutputStream.writeInt(messageBuffer.length);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    dataOutputStream.write(messageBuffer);
  }

  /**
   * Send broadcast message.
   *
   * @param message the message
   * @throws IOException the io exception
   */
  public void sendBroadcastMessage(String message)
      throws IOException {
    sendBroadcast(message, ServerWorker.BROADCAST_MESSAGE);
  }


  /**
   * Send broadcast.
   *
   * @param message           the message
   * @param messageIdentifier the message identifier
   * @throws IOException the io exception
   */
  public void sendBroadcast(String message, int messageIdentifier)
      throws IOException {
    DataOutputStream dataOutputStream = outputStream;
    byte[] sendBuffer = user.getBytes(StandardCharsets.UTF_8);
    dataOutputStream.writeInt(messageIdentifier);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    dataOutputStream.writeInt(sendBuffer.length);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    dataOutputStream.write(sendBuffer);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    byte[] messageBuffer = message.getBytes(StandardCharsets.UTF_8);
    dataOutputStream.writeInt(messageBuffer.length);
    dataOutputStream.writeChar(ServerWorker.SPACE);
    dataOutputStream.write(messageBuffer);

  }

  /**
   * Send insult.
   *
   * @param recipient the recipient
   * @throws IOException the io exception
   */
  public void sendInsult(String recipient)
      throws IOException {
    sendBroadcast(recipient, ServerWorker.SEND_INSULT);
  }

  /**
   * Connect.
   *
   * @throws IOException the io exception
   */
  public void connect() throws IOException {
    this.socket = new Socket(serverName, serverPort);
    inputStream = new DataInputStream(socket.getInputStream());
    outputStream = new DataOutputStream(socket.getOutputStream());
    handleLogin();
  }
}
