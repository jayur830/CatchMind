#pragma once
#include "WinSock.h"

class WinSockUDPMulticastSender : public WinSock {
public:
	WinSockUDPMulticastSender(int port);
	int send(char* buf, int bufSize);
};