package com.jimi.psh.exception.runtime;

import java.util.List;

import com.jimi.psh.util.BytesParser;

/**
 * 包解析异常
 * <br>
 * <b>2017年12月28日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class PackageParseRuntimeException extends RuntimeException {

	public PackageParseRuntimeException(List<Byte> bytes) {
		super(BytesParser.parseBytesToString(bytes));
	}
	
}