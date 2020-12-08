package com.gof.atom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@AllArgsConstructor
@Getter
public class Tuple3<K1,K2,V> {
	private K1 key1;
	private K2 key2;
	private V value;
}
