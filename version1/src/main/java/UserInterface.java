import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The type User interface.
 */
public class UserInterface {

  /**
   * The constant LOGIN.
   */
  public static final String LOGIN = "login";
  /**
   * The constant LOGOFF.
   */
  public static final String LOGOFF = "logoff";
  /**
   * The constant DISCONNECT_MESSAGE.
   */
  public static final String DISCONNECT_MESSAGE = "sends a DISCONNECT_MESSAGE to the server";
  /**
   * The constant SENDS_A_CONNECT_MESSAGE_TO_THE_SERVER.
   */
  public static final String SENDS_A_CONNECT_MESSAGE_TO_THE_SERVER = "sends a CONNECT_MESSAGE to the server";
  /**
   * The constant CONNECT_MESSAGE.
   */
  public static final String CONNECT_MESSAGE = SENDS_A_CONNECT_MESSAGE_TO_THE_SERVER;
  /**
   * The constant COMMAND_HELPER.
   */
  public static final String COMMAND_HELPER = "--";
  /**
   * The constant REGEX.
   */
  public static final String REGEX = " ";
  /**
   * The constant PROMPT.
   */
  public static final String PROMPT = "Start type your message, separate command by space!";
  /**
   * The constant MANUAL.
   */
  public static final String MANUAL = "?";
  /**
   * The constant WHO.
   */
  public static final String WHO = "who";
  /**
   * The constant USER_MESSAGE.
   */
  public static final String USER_MESSAGE = "@user";
  /**
   * The constant INSULT.
   */
  public static final String INSULT = "!user";
  /**
   * The constant ALL.
   */
  public static final String ALL = "@all";
  /**
   * The constant USER_TO_THE_SERVER.
   */
  public static final String USER_TO_THE_SERVER = "sends a DIRECT_MESSAGE to the specified user to the server";
  /**
   * The constant QUERY_CONNECTED_USERS.
   */
  public static final String QUERY_CONNECTED_USERS = "sends a QUERY_CONNECTED_USERS to the server";
  /**
   * The constant BROADCAST_MESSAGE_TO_ALL_USERS.
   */
  public static final String BROADCAST_MESSAGE_TO_ALL_USERS = "sends a BROADCAST_MESSAGE to the server, to be sent to all users connected";
  /**
   * The constant NUMBER_OF_ARGS.
   */
  public static final int NUMBER_OF_ARGS = 2;
  /**
   * The constant INSULT_MESSAGE_TO_USER.
   */
  public static final String INSULT_MESSAGE_TO_USER = "sends a SEND_INSULT message to the server, to be sent to the specified user\n";
  /**
   * The constant STATUS.
   */
  public static final int STATUS = 0;
  /**
   * The constant RECIPIENT.
   */
  public static final int RECIPIENT = 1;
  /**
   * The constant INPUT_LENGTH_ERROR_MESSAGE.
   */
  public static final String INPUT_LENGTH_ERROR_MESSAGE = "The number of input arguments is wrong, please provide the host name and port number of the server.";
  /**
   * The constant EXIT_CODE_WRONG_INPUT_LENGTH.
   */
  public static final int EXIT_CODE_WRONG_INPUT_LENGTH = 1;
  /**
   * The constant EXIT_CODE_WRONG_INPUT_VALUE.
   */
  public static final int EXIT_CODE_WRONG_INPUT_VALUE = 2;
  /**
   * The constant INPUT_VALUE_ERROR_MESSAGE.
   */
  public static final String INPUT_VALUE_ERROR_MESSAGE = "The input for hostname or port number of the server is wrong!";
  /**
   * The Client.
   */
  public ChatClient client;

  /**
   * Instantiates a new User interface.
   *
   * @param localhost the localhost
   */
  public UserInterface(ChatClient localhost) {
    if (localhost == null) {
      throw new IllegalArgumentException();
    }
    this.client = localhost;
  }


  /**
   * Create option options.
   *
   * @return the options
   */
  static Options createOption() {
    Options options = new Options();
    options.addOption(
        Option.builder().longOpt(LOGIN).desc(CONNECT_MESSAGE).hasArg(true)
            .build());
    options.addOption(
        Option.builder().longOpt(LOGOFF).desc(DISCONNECT_MESSAGE)
            .hasArg(false)
            .build());
    options.addOption(
        Option.builder().longOpt(WHO).desc(QUERY_CONNECTED_USERS)
            .hasArg(false).build());
    options.addOption(
            Option.builder().longOpt(USER_MESSAGE)
                    .desc(USER_TO_THE_SERVER)
                    .numberOfArgs(1)
                    .build());
    options.addOption(
        Option.builder().longOpt(ALL)
            .desc(BROADCAST_MESSAGE_TO_ALL_USERS)
            .hasArg(true)
            .build());
    options.addOption(
        Option.builder().longOpt(INSULT)
            .desc(INSULT_MESSAGE_TO_USER)
            .hasArg(true).build());
    return options;
  }

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws IOException          the io exception
   * @throws InterruptedException the interrupted exception
   */
  public static void main(String[] args) throws IOException, InterruptedException {

    if (args.length != 2) {
      System.err.println(INPUT_LENGTH_ERROR_MESSAGE);
      System.exit(EXIT_CODE_WRONG_INPUT_LENGTH);
    }

    String host = args[0];
    int port = Integer.parseInt(args[1]);
    ChatClient chatClient = new ChatClient(host, port);
    UserInterface userInterface = new UserInterface(chatClient);

    try {
      chatClient.connect();
    } catch (IOException e) {
      System.err.println(INPUT_VALUE_ERROR_MESSAGE);
      System.exit(EXIT_CODE_WRONG_INPUT_VALUE);
    }

    boolean[] running = new boolean[RECIPIENT];
    Arrays.fill(running, Boolean.TRUE);
    chatClient.startMessageReader(running);
    userInterface.panel(running);
  }


  @Override
  public String toString() {
    return "UserInterface{" +
        "client=" + client +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserInterface)) {
      return false;
    }

    UserInterface that = (UserInterface) o;

    return client.equals(that.client);
  }

  @Override
  public int hashCode() {
    return client.hashCode();
  }

  /**
   * Parse command command line.
   *
   * @param userChoice the user choice
   * @return the command line
   * @throws IOException the io exception
   */
  public CommandLine parseCommand(String userChoice) throws IOException {
    CommandLineParser cmdLine = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();
    CommandLine cmd = null;
    if (MANUAL.equals(userChoice)) {
      formatter.printHelp(RandomSentence.class.getSimpleName(), createOption());
    } else {
      try {
        cmd = cmdLine.parse(createOption(),
            (COMMAND_HELPER + userChoice).split(REGEX));
      } catch (ParseException e) {
        client.sendBroadcastMessage(userChoice);
      }
    }
    return cmd;
  }

  /**
   * Execute.
   *
   * @param cmd the cmd
   * @throws IOException the io exception
   */
  public void execute(CommandLine cmd) throws IOException {
    if (cmd.hasOption(LOGOFF)) {
      client.sendLogoff();
    } else if (cmd.hasOption(WHO)) {
      client.sendQueryUser();
    } else if (cmd.hasOption(USER_MESSAGE)) {
      String str= "";
      for(String x: cmd.getArgList()){
        str+=x+" ";
      }
      client.sendDirectMessage(cmd.getOptionValues(USER_MESSAGE)[STATUS],
              str  );

    } else if (cmd.hasOption(ALL)) {
      String str= cmd.getOptionValue(ALL)+" ";
      for(String x: cmd.getArgList()){
        str+=x+" ";
      }
      client.sendBroadcastMessage(str);
    } else if (cmd.hasOption(INSULT)) {
      client.sendInsult(cmd.getOptionValue(INSULT));
    }
  }

  /**
   * Panel.
   *
   * @param running the running
   * @throws IOException          the io exception
   * @throws InterruptedException the interrupted exception
   */
  public void panel(boolean[] running) throws IOException, InterruptedException {
    Scanner stdin = new Scanner(System.in);
    while (running[STATUS]) {
      System.out.println(PROMPT);
      Thread.sleep(1000);

      if (running[STATUS] == false) {
        break;
      }

      String userChoice = stdin.nextLine().trim();
      CommandLine cmd = parseCommand(userChoice);
      if (cmd != null) {
        execute(cmd);
      }
    }
  }
}
