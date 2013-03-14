IS52025A-ChatRoom
=================

You must implement an Internet chat room with the following components:

A chat client.
The Server
This chat client has two windows, one for typing in messages and commands and another for receiving messages from users. There are the following commands:

!join username
This is the initial request to join the chatroom with particular username.

@user:message
This sends a message only to the user called `user'.

!who
This displays all online users.

!quit
This closes the client gracefully.

ANY OTHER MESSAGE WILL BE BROADCAST TO ALL ONLINE USERS!

The received messages should be of the form user:message where user is the name of the user who sent the message.
You should make sure your client and server programs do not crash.