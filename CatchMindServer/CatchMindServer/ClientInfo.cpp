#include "ClientInfo.h"

void ClientInfo::add(std::string nickName, int port) {
	this->client.push_back(std::pair<std::string, int>(nickName, port));
}

int ClientInfo::remove(std::string nickName, int port) {
	for (int i(0); i < this->client.size(); ++i)
		if (this->client[i].first == nickName &&
			this->client[i].second == port) {
			this->client.erase(this->client.begin() + i);
			return i;
		}
	return -1;
}

int ClientInfo::remove(std::string nickName) {
	for (int i(0); i < this->client.size(); ++i)
		if (this->client[i].first == nickName) {
			this->client.erase(this->client.begin() + i);
			return i;
		}
	return -1;
}

int ClientInfo::remove(int port) {
	for (int i(0); i < this->client.size(); ++i)
		if (this->client[i].second == port) {
			this->client.erase(this->client.begin() + i);
			return i;
		}
	return -1;
}

bool ClientInfo::contains(std::string nickName) {
	for (std::pair<std::string, int> user : client)
		if (user.first == nickName) return true;
	return false;
}

size_t ClientInfo::size() {
	return this->client.size();
}

std::pair<std::string, int> ClientInfo::operator[](int index) {
	return this->client[index];
}