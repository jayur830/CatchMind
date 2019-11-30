#pragma once
#include <vector>
#include <string>

class ClientInfo {
	std::vector<std::pair<std::string, int>> client;
public:
	void add(std::string nickName, int port);
	int remove(std::string nickName, int port);
	int remove(std::string nickName);
	int remove(int port);
	bool contains(std::string nickName);
	size_t size();
	std::pair<std::string, int> operator[](int index);
};