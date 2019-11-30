#include "WinSockTCPServer.h"
#include <iostream>

WinSockTCPServer::WinSockTCPServer(int port) : 
	WinSock(port, SOCK_STREAM) {
	this->serverAddr.sin_addr.s_addr = htonl(INADDR_ANY);
	if (::bind(this->sock, (SOCKADDR*)& this->serverAddr,
		sizeof(this->serverAddr)) == SOCKET_ERROR) err_quit("bind()");
	if (::listen(this->sock, SOMAXCONN) == SOCKET_ERROR) err_quit("listen()");
}

SOCKET WinSockTCPServer::accept(SOCKADDR_IN& clientAddr) {
	SOCKET clientSock;
	int addrLen(sizeof(clientAddr));
	if ((clientSock = ::accept(this->sock,
		(SOCKADDR*)& clientAddr, &addrLen)) == INVALID_SOCKET) {
		err_display("accept()");
		return INVALID_SOCKET;
	}
	std::cout << "[TCP] 클라이언트 접속: IP=" << inet_ntoa(clientAddr.sin_addr) 
		<< ", PORT=" << ntohs(clientAddr.sin_port) << std::endl;
	return clientSock;
}

int WinSockTCPServer::send(SOCKET& sock, char* buf, int bufSize) {
	int ret(0);
	if ((ret = ::send(sock, buf, bufSize, 0)) == SOCKET_ERROR) {
		err_display("send()");
		return SOCKET_ERROR;
	}
	return ret;
}

int WinSockTCPServer::receive(SOCKET& sock, char* buf, int bufSize,
	int(__stdcall* recvn)(SOCKET, char*, int, int)) {
	int ret(0);
	if ((ret = recvn(sock, buf, bufSize, 0)) == SOCKET_ERROR) {
		err_display("receive()");
		return SOCKET_ERROR;
	}
	else if (ret == 0) throw 0;
	return ret;
}