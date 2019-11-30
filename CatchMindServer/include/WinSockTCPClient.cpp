#include "WinSockTCPClient.h"
#include <exception>

WinSockTCPClient::WinSockTCPClient(const char* serverIP, int port) : WinSock(port, SOCK_STREAM) {
	this->serverAddr.sin_addr.s_addr = inet_addr(serverIP);
	if (::connect(this->sock, (SOCKADDR*)& this->serverAddr,
		sizeof(this->serverAddr)) == SOCKET_ERROR) err_quit("connect()");
}

SOCKET WinSockTCPClient::socket() {
	return this->sock;
}

int WinSockTCPClient::send(SOCKET& sock, char* buf, int bufSize) {
	int ret(0);
	if ((ret = ::send(sock, buf, bufSize, 0)) == SOCKET_ERROR) {
		err_display("send()");
		throw SOCKET_ERROR;
	}
	return ret;
}

int WinSockTCPClient::receive(SOCKET& sock, char* buf, int bufSize,
	int(__stdcall* recvn)(SOCKET, char*, int, int)) {
	int ret(0);
	if ((ret = recvn(sock, buf, bufSize, 0)) == SOCKET_ERROR) {
		err_display("receive()");
		throw SOCKET_ERROR;
	}
	else if (ret == 0) throw 0;
	return ret;
}