#pragma once
#include "WinSock.h"

class WinSockUDPClient : public WinSock {
public:
	WinSockUDPClient(const char* ip, int port, bool broadcast = false);
	int send(SOCKET& sock, char* buf, int bufSize);
	int receive(SOCKET& sock, char* buf, int bufSize,
		int(__stdcall* recvn)(SOCKET, char*, int, int, SOCKADDR*, int*) = ::recvfrom);
};