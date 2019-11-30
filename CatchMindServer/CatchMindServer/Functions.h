#pragma once
#include <vector>
#include <string>

class Functions {
public:
	static std::string toString(int n);
	static int toInt(std::string s);
	static std::vector<std::string> split(std::string str, const char* ch);
};