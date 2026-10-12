package com.example.demo;

import org.springframework.stereotype.Service;

@Service
public class Calculator {

	public int add(int a, int b) {
		return a + b;
	}

	public int divide(int a, int b) {
		if (b == 0) {
			throw new IllegalArgumentException("Không thể chia cho 0");
		}
		return a / b;
	}
}
