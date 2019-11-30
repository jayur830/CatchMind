#include "WinSockUDPMulticastSender.h"
#include <WS2tcpip.h>

WinSockUDPMulticastSender::WinSockUDPMulticastSender(int port) : WinSock(port, SOCK_DGRAM) {
	int ttl(2);
	if (setsockopt(this->sock, IPPROTO_IP, IP_MULTICAST_TTL,
		(char*)& ttl, sizeof(ttl)) == SOCKET_ERROR) err_quit("setsockopt()");
	this->serverAddr.sin_addr.s_addr = inet_addr("235.7.8.9");
}

int WinSockUDPMulticastSender::send(char* buf, int bufSize) {
	int sendLen;
	if ((sendLen = ::sendto(this->sock, buf, bufSize, 0,
		(SOCKADDR*)& this->serverAddr, sizeof(this->serverAddr))) == SOCKET_ERROR) {
		err_display("sendto()");
		throw SOCKET_ERROR;
	}
	return sendLen;
}