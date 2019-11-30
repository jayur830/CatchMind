#include "Rank.h"

void Rank::addUser(std::string nickName) {
	this->scores.push_back(std::pair<std::string, int>(nickName, 0));
}

void Rank::setScore(std::string nickName) {
	for (std::pair<std::string, int>& user : this->scores)
		if (user.first == nickName) {
			user.second += 10;
			break;
		}
	for (int i(this->scores.size() - 1); i > 0; --i)
		for (int j(i); j > 0 &&
			this->scores[j].second > this->scores[j - 1].second; --j)
			std::swap(this->scores[j], this->scores[j - 1]);
}

void Rank::remove(std::string nickName) {
	for (auto iter(this->scores.begin());
		iter != this->scores.end(); ++iter)
		if (iter->first == nickName) {
			this->scores.erase(iter);
			break;
		}
}

void Rank::clearScores() {
	for (std::pair<std::string, int>& user : this->scores) user.second = 0;
}

std::vector<std::pair<std::string, int>> Rank::getRank() {
	return this->scores;
}

std::string Rank::toString() {
	std::string s;
	for (int i(0); i < this->scores.size(); ++i) {
		std::string rankNum(Functions::toString(i + 1));
		if (rankNum == "1") rankNum.append("st");
		else if (rankNum == "2") rankNum.append("nd");
		else if (rankNum == "3") rankNum.append("rd");
		else rankNum.append("th");
		s.append(rankNum).append(": ").append(this->scores[i].first)
			.append(", score: ").append(this->scores[i].second == 0 ? "0" : Functions::toString(this->scores[i].second)).append("\n");
	}
	s.pop_back();
	return s;
}