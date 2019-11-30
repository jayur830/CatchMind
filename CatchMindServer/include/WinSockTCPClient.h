#pragma once
#include "WinSock.h"

class WinSockTCPClient : public WinSock {
public:
	WinSockTCPClient(const char* serverIP, int port);
	SOCKET socket();
	int send(SOCKET& sock, char* buf, int bufSize);
	int receive(SOCKET& sock, char* buf, int bufSize,
		int(__stdcall* recvn)(SOCKET, char*, int, int) = ::recv);
};