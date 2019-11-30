#include "WinSock.h"
#include <iostream>

WinSock::WinSock(int port, int protocol) {
	if (WSAStartup(0x0202, new WSADATA) != 0) exit(1);

	if ((this->sock = ::socket(PF_INET, protocol, 0)) ==
		INVALID_SOCKET) err_quit("socket()");

	memset(&this->serverAddr, 0, sizeof(this->serverAddr));
	this->serverAddr.sin_family = AF_INET;
	this->serverAddr.sin_port = htons(port);
}

WinSock::~WinSock() {
	closesocket(this->sock);
	WSACleanup();
}

LPVOID WinSock::error() {
	LPVOID lpMsgBuf;
	FormatMessage(
		FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM,
		nullptr, WSAGetLastError(),
		MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
		(LPTSTR)& lpMsgBuf, 0, nullptr);
	return lpMsgBuf;
}

void WinSock::err_quit(const char* msg) {
	LPVOID lpMsgBuf(error());
	MessageBox(nullptr, (LPCTSTR)lpMsgBuf, msg, MB_ICONERROR);
	LocalFree(lpMsgBuf);
	exit(1);
}

void WinSock::err_display(const char* msg) {
	LPVOID lpMsgBuf(error());
	std::cout << "[" << msg << "] " << (char*)lpMsgBuf;
	LocalFree(lpMsgBuf);
}