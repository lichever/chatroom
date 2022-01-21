## Introduction

The server will be responsible for managing the client connections (up to 10 clients can be connected and in a chat room at one time), accepting messages from one client and sending the messages to all attached clients. Clients will be able to either send a message that is public in the chat room, or that goes directly to a single, specified client.

## Commands

- logoff: sends a DISCONNECT_MESSAGE to the server

- who: sends a QUERY_CONNECTED_USERS to the server

- @user: sends a DIRECT_MESSAGE to the specified user to the server

- @all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected

- !user: sends a SEND_INSULT message to the server, to be sent to the specified user



Note:

1. Find Run Section at the Tool Bar section and Click Run to start Server...

2. Find the main function in the 

3. Click Run configuration
   
   * Add the hostname and port number of the server to the CLI arguments (ex: localhost 6666). Note that the port number for server is fixed to 6666 in this program. 
   * In the modify options, make sure you select the "allow multiple instances" to let you run multiple client program in the IDE.

4. Find Run Section at the Tool Bar section and Click Run to start Client. You can run multiple clients at this step but make sure wait for some time after starting the server.

5. There is some example how you would interact with our program

```console
Please enter your name to login this chat room:
jim
There are 0 other connected clients
Start type your message, separate command by space!
?
usage: RandomSentence
    --!user <arg>   sends a SEND_INSULT message to the server, to be sent
                    to the specified user
    --@all <arg>    sends a BROADCAST_MESSAGE to the server, to be sent to
                    all users connected
    --@user <arg>   sends a DIRECT_MESSAGE to the specified user to the
                    server
    --login <arg>   sends a CONNECT_MESSAGE to the server
    --logoff        sends a DISCONNECT_MESSAGE to the server
    --who           sends a QUERY_CONNECTED_USERS to the server
Start type your message, separate command by space!
!user jim
jim -> jim: May a rabid Rush Limbaugh and a grisly and wormy group of manic weasels seek a battleground in your mother's bed.
Start type your message, separate command by space!
hfipsa
jim: hfipsa
Start type your message, separate command by space!
@user jim test
jim -> jim: test
Start type your message, separate command by space!
logoff
You are no longer connected
```
