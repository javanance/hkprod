package com.gof.atom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@AllArgsConstructor
@Getter
public class Tuple2<K,V>  {
	private K key;
	private V v1;
	
}
