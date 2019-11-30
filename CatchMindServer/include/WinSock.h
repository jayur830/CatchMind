#pragma once
#include <WinSock2.h>

class WinSock {
protected:
	SOCKET sock;
	SOCKADDR_IN serverAddr;
public:
	WinSock(int port, int protocol);
	~WinSock();
private:
	static LPVOID error();
protected:
	static void err_quit(const char* msg);
	static void err_display(const char* msg);
};