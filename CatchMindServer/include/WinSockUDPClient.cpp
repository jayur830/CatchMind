#include "WinSockUDPClient.h"
#include <iostream>
#include <string>

WinSockUDPClient::WinSockUDPClient(const char* ip, int port, bool broadcast) : WinSock(port, SOCK_DGRAM) {
	if (broadcast) {
		BOOL bEnable(TRUE);
		if (::setsockopt(this->sock, SOL_SOCKET, SO_BROADCAST,
			(char*)& bEnable, sizeof(bEnable)) == SOCKET_ERROR) err_quit("setsockopt()");
		std::string network(ip);
		int index(network.find('.'));
		index = network.find('.', index + 1);
		index = network.find('.', index + 1);
		network = network.substr(0, index);
		std::cout << (network + ".255") << std::endl;
		this->serverAddr.sin_addr.s_addr = inet_addr((network + ".255").c_str());
	}
	else this->serverAddr.sin_addr.s_addr = inet_addr(ip);
}

int WinSockUDPClient::send(SOCKET& sock, char* buf, int bufSize) {
	int recvLen;
	if ((recvLen = ::sendto(sock, buf, bufSize, 0,
		(SOCKADDR*)& this->serverAddr, sizeof(this->serverAddr))) == SOCKET_ERROR) {
		err_display("sendto()");
		throw SOCKET_ERROR;
	}
	return recvLen;
}

int WinSockUDPClient::receive(SOCKET& sock, char* buf, int bufSize,
	int(__stdcall* recvn)(SOCKET, char*, int, int, SOCKADDR*, int*)) {
	SOCKADDR_IN clientAddr;
	int sendLen, addrLen = sizeof(clientAddr);
	if ((sendLen = recvn(sock, buf, bufSize, 0,
		(SOCKADDR*)& clientAddr, &addrLen)) == SOCKET_ERROR) {
		err_display("recvfrom()");
		throw SOCKET_ERROR;
	}
	if (memcmp(&clientAddr, &this->serverAddr, sizeof(clientAddr))) {
		std::cout << "[오류] 잘못된 데이터입니다." << std::endl;
		throw SOCKET_ERROR;
	}
	return sendLen;
}