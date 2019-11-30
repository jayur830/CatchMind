#include "Functions.h"

std::string Functions::toString(int n) {
	std::string s;
	if (n != 0) {
		s.append(toString(n / 10));
		s.push_back(n - ((n / 10) * 10) + '0');
	}
	return s;
}

int Functions::toInt(std::string s) {
	int n(0);
	for (char c : s) {
		n *= 10;
		n += c - '0';
	}
	return n;
}

std::vector<std::string> Functions::split(std::string str, const char* ch) {
	std::vector<std::string> set;
	int index(0), len(strlen(ch));
	while ((index = str.find(ch)) != -1) {
		set.push_back(str.substr(0, index));
		str.erase(str.begin(), str.begin() + index + len);
	}
	set.push_back(str);
	return set;
}