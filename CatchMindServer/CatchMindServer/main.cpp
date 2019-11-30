#include <iostream>
#include <vector>
#include <thread>
#include <string>
#include <algorithm>
#include "WinSockTCPServer.h"
#include "WinSockUDPServer.h"
#include "WinSockUDPMulticastSender.h"
#include "ClientInfo.h"
#include "Rank.h"

#define BUFSIZE 512
#define REGEX "@%#"

typedef int SOCKERRTYPE;

int main() {
	WinSockTCPServer server(32768);
	std::vector<std::thread> threadPool;
	std::vector<SOCKET> sockets;

	ClientInfo clientInfo;
	Rank rank;

	while (true) {
		SOCKADDR_IN clientAddr;
		SOCKET sock(server.accept(clientAddr));
		if (sock == INVALID_SOCKET) continue;
		threadPool.push_back(std::thread(
			[&server, &sockets, &clientInfo, &rank](
				SOCKET sock, SOCKADDR_IN clientAddr) {
					char buf[BUFSIZE + 1];
					BOOL disable(TRUE);
					if (setsockopt(sock, IPPROTO_TCP, TCP_NODELAY,
						(char*)& disable, sizeof(disable)) != SOCKET_ERROR)
						while (true) {
							try {
								buf[0] = '\0';
								int len(server.receive(sock, buf, BUFSIZE));
								if (len == SOCKET_ERROR) break;
								buf[len] = '\0';
								std::cout << "[Received packet] " << buf << std::endl;
								std::vector<std::string> data(Functions::split(buf, REGEX));

								if (data[0] == "100") {
									std::string packet(data[0]);
									packet.append(REGEX);
									if (clientInfo.contains(data[1]))
										packet.append("true");
									else packet.append("false");
									memcpy(buf, packet.c_str(), len = packet.size());
									buf[len] = '\0';
									if (server.send(sock, buf, len) == SOCKET_ERROR) break;
									std::cout << "[Sent packet] " << buf << std::endl;
									break;
								}
								else {
									if (data[0] == "0") {
										sockets.push_back(sock);
										clientInfo.add(data[1], ntohs(clientAddr.sin_port));
										rank.addUser(data[1]);
										std::string packet("0");
										for (int i(0); i < clientInfo.size(); ++i)
											packet.append(REGEX).append(clientInfo[i].first)
											.append(REGEX).append(Functions::toString(clientInfo[i].second));
										memcpy(buf, packet.c_str(), len = packet.size());
									}
									else if (data[0] == "1") {
										sockets.erase(sockets.begin() + clientInfo.remove(ntohs(clientAddr.sin_port)));
										rank.remove(data[1]);
									}
									else if (data[0] == "6") {
										std::string packet(std::string("6")
											.append(REGEX).append(rank.toString()));
										memcpy(buf, packet.c_str(), len = packet.size());
										rank.clearScores();
									}
									else if (data[0] == "7") rank.setScore(data[1]);
									if (clientInfo.size() != 0) {
										for (SOCKET _sock : sockets) server.send(_sock, buf, len);
										std::cout << "[Sent packet] " << buf << std::endl;
									}
								}
							}
							catch (SOCKERRTYPE e) { break; }
						}

					closesocket(sock);
					std::cout << "[TCP] 클라이언트 종료: IP=" << inet_ntoa(clientAddr.sin_addr)
						<< ", PORT=" << ntohs(clientAddr.sin_port) << std::endl;
			}, sock, clientAddr));
	}
	return 0;
}