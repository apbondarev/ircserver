# ircserver

A simple text based IRC server based on Netty framework.

Main logic implementation is done within 1 source file, with no persistence (in memory only).

Command set for this server:

/login name password — if user not exists create profile else login

/join channel —try to join channel (max 10 active clients per channel is allowed) If client’s limit exceeded - send error, otherwise join channel and send last 10 messages of activity

/leave - disconnect client

/users — show users in the channel

text message terminated with CR - sends message to the channel. Server must send new message to all connected to the channel clients.
