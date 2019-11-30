#pragma once
#include "WinSock.h"
#include <vector>
#include <thread>

class WinSockTCPServer : public WinSock {
public:
	WinSockTCPServer(int port);
	SOCKET accept(SOCKADDR_IN& clientAddr);
	int send(SOCKET& sock, char* buf, int bufSize);
	int receive(SOCKET& sock, char* buf, int bufSize,
		int(__stdcall* recvn)(SOCKET, char*, int, int) = ::recv);
};