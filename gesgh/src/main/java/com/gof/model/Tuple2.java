package com.gof.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;

import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.SEXP;

import com.gof.dao.SmithWilsonDao;
import com.gof.entity.BizLiqPremium;
import com.gof.entity.IrCurveHis;
import com.gof.entity.SmithWilsonParamHis;
import com.gof.entity.SmithWilsonResult;
import com.gof.util.ScriptUtil;

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
