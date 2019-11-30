#pragma once
#include "Functions.h"

class Rank {
	std::vector<std::pair<std::string, int>> scores;
public:
	void addUser(std::string nickName);
	void setScore(std::string nickName);
	void remove(std::string nickName);
	void clearScores();
	std::vector<std::pair<std::string, int>> getRank();
	std::string toString();
};