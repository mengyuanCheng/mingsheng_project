package com.pda.scan;

interface IHWScan{

	int init();  //init scaner engine
	
	void close();
	
	void scan();  //start scanning 
	
	void enableSymbology(int symCode); //
	
	void disableSymbology(int symCode);
	
	void setInputMode(int mode) ;
	
	void setSurfix(String surfix) ;
	
	void setPrefix(String prefix) ;
	
}