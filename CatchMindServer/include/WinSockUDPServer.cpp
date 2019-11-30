#include "WinSockUDPServer.h"
#include <WS2tcpip.h>
#include <iostream>

WinSockUDPServer::WinSockUDPServer(int port) : WinSock(port, SOCK_DGRAM) {
	this->serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);
	if (::bind(this->sock, (SOCKADDR*)& this->serverAddr,
		sizeof(this->serverAddr)) == SOCKET_ERROR) err_quit("bind()");
}

int WinSockUDPServer::send(char* buf, int bufSize) {
	int sendLen;
	if ((sendLen = ::sendto(this->sock, buf, bufSize, 0,
		(SOCKADDR*)& this->clientAddr, sizeof(this->clientAddr))) == SOCKET_ERROR) {
		err_display("sendto()");
		throw SOCKET_ERROR;
	}
	return sendLen;
}

int WinSockUDPServer::receive(char* buf, int bufSize,
	int(__stdcall* recvn)(SOCKET, char*, int, int, SOCKADDR*, int*)) {
	int recvLen, addrLen(sizeof(this->clientAddr));
	if ((recvLen = recvn(this->sock, buf, bufSize, 0,
		(SOCKADDR*)& this->clientAddr, &addrLen)) == SOCKET_ERROR) {
		err_display("recvfrom()");
		throw SOCKET_ERROR;
	}
	return recvLen;
}