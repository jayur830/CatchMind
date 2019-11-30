#pragma once
#include "WinSock.h"

class WinSockUDPServer : public WinSock {
	SOCKADDR_IN clientAddr;
	SOCKET multicastSock;
public:
	WinSockUDPServer(int port);
	int send(char* buf, int bufSize);
	int receive(char* buf, int bufSize,
		int(__stdcall* recvn)(SOCKET, char*, int, int, SOCKADDR*, int*) = ::recvfrom);
};