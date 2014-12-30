package com.mysoft;

public class InterfaceInvoke {
	public static void main(String[] args) {
		try {
			DubboServiceExecute.printResult();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
