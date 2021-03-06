package cc.darhao.jiminal.test;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import cc.darhao.dautils.api.ClassScanner;
import cc.darhao.dautils.api.FieldUtil;
import cc.darhao.jiminal.constant.JustForTestLine;
import cc.darhao.jiminal.core.BasePackage;
import cc.darhao.jiminal.core.PackageParser;
import cc.darhao.jiminal.entity.JustForTestBoardNumPackage;
import cc.darhao.jiminal.entity.JustForTestBoardNumReplyPackage;
import cc.darhao.jiminal.entity.JustForTestControlReplyPackage;
import cc.darhao.jiminal.entity.JustForTestHeartPackage;
import cc.darhao.jiminal.entity.JustForTestTextPackage;
import cc.darhao.jiminal.exception.CRCException;
import cc.darhao.jiminal.exception.EnumValueNotExistException;
import cc.darhao.jiminal.exception.PackageParseException;
import cc.darhao.jiminal.exception.ProtocolNotMatchException;
import cc.darhao.jiminal.exception.runtime.EnumClassNotFoundException;
import cc.darhao.jiminal.exception.runtime.ReplyPackageNotMatchException;

/**
 * 解析器单元测试
 * <br>
 * <b>2017年12月27日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class ParserTest{

	private static List<Class> packageClasses = ClassScanner.searchClass("cc.darhao.jiminal.entity");

	
	@Test
	public void utf8Test() throws PackageParseException, UnsupportedEncodingException {
		String test = "你好世界";
		byte[] test2 = test.getBytes("UTF-8");
		String test3 = new String(test2, "UTF-8");
		Assert.assertEquals("你好世界", test3);
		List<Byte> bytes = Arrays.asList(new Byte[] {0x15, 0x03 , (byte) 0xe4 , (byte) 0xbd , (byte) 0xa0 , (byte) 0xe5 , (byte) 0xa5 , 
				(byte) 0xbd , (byte) 0xe4 , (byte) 0xb8 ,(byte) 0x96 , (byte) 0xe7 , (byte) 0x95 , (byte) 0x8c , (byte) 0xFE , 
				(byte) 0xFE , (byte) 0xFE , (byte)0xFE, 0x00 , 0x00 , (byte) 0x45, (byte) 0x36});
		JustForTestTextPackage testTextPackage = (JustForTestTextPackage) PackageParser.parse(bytes, packageClasses, false);
		List<Byte> bytes2 = PackageParser.serialize(testTextPackage);
		Assert.assertArrayEquals(bytes.toArray(), bytes2.toArray());
		Assert.assertEquals("你好世界", testTextPackage.getContent());
	}
	
	
	@Test
	public void crc() {
		List<Byte> bytes = Arrays.asList(new Byte[] {0x09, 0x48, 0x01, 0x5A, 0x1B, (byte) 0xAD, (byte) 0xA3, 0x03, 0x00, 0x02, 0x17, 0x5B});
		Assert.assertEquals(true, PackageParser.crc(bytes));
		List<Byte> bytes2 = Arrays.asList(new Byte[] {0x09, 0x42, 0x01, 0x5A, 0x1B, (byte) 0xAD, (byte) 0xA3, 0x03, 0x00, 0x02, 0x17, 0x5B});
		Assert.assertEquals(false, PackageParser.crc(bytes2));
	}
	
	@Test
	public void initPackageInfo() {
		JustForTestHeartPackage justForTestHeartPackage = new JustForTestHeartPackage();
		PackageParser.initPackageInfo(justForTestHeartPackage);
		Assert.assertEquals(justForTestHeartPackage.length, 5 + 6);
		Assert.assertEquals(justForTestHeartPackage.protocol, "JustForTestHeart");
		
		JustForTestControlReplyPackage justForTestControlReplyPackage = new JustForTestControlReplyPackage();
		PackageParser.initPackageInfo(justForTestControlReplyPackage);
		Assert.assertEquals(justForTestControlReplyPackage.length, 5 + 3);
		Assert.assertEquals(justForTestControlReplyPackage.protocol, "JustForTestControl");
	}
	
	@Test
	public void createReplyPackage() {
		JustForTestBoardNumPackage justForTestBoardNumPackage = new JustForTestBoardNumPackage();
		justForTestBoardNumPackage.serialNo = 999;
		JustForTestBoardNumReplyPackage justForTestBoardNumReplyPackage = (JustForTestBoardNumReplyPackage) PackageParser.createReplyPackage(justForTestBoardNumPackage, packageClasses);
		Assert.assertEquals(justForTestBoardNumPackage.serialNo, justForTestBoardNumReplyPackage.serialNo);
		try{
			PackageParser.createReplyPackage(justForTestBoardNumPackage, new ArrayList<Class>());
			Assert.fail();
		}catch (ReplyPackageNotMatchException e) {
			System.out.println(" '回复包未匹配' 测试异常捕捉成功");
		}
	}
	
	@Test
	public void serialize() throws ParseException {
		JustForTestHeartPackage justForTestHeartPackage = new JustForTestHeartPackage();
		justForTestHeartPackage.serialNo = 1024;
		justForTestHeartPackage.setAlarmEnabled(true);
		justForTestHeartPackage.setConveyorEnabled(true);
		justForTestHeartPackage.setInfraredEnabled(true);
		justForTestHeartPackage.setLine(JustForTestLine.L307);
		justForTestHeartPackage.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2017-01-01 18:30:01"));
		List<Byte> bytes = PackageParser.serialize(justForTestHeartPackage);
		List<Byte> bytes2 =  Arrays.asList(new Byte[] {0x0B, 0x48, 0x07, 0x58, 0x68, (byte) 0xDA, 0x29, 0x07, 0x04, 0x00, 0x5F, (byte) 0x9F});
		Assert.assertArrayEquals(bytes.toArray(), bytes2.toArray());
	}
	
	@Test
	public void parse() throws ParseException, PackageParseException {
		//参考对象
		JustForTestHeartPackage justForTestHeartPackage = new JustForTestHeartPackage();
		justForTestHeartPackage.serialNo = 1024;
		justForTestHeartPackage.setAlarmEnabled(true);
		justForTestHeartPackage.setConveyorEnabled(true);
		justForTestHeartPackage.setInfraredEnabled(true);
		justForTestHeartPackage.setLine(JustForTestLine.L307);
		justForTestHeartPackage.crc = 0x5F9F;
		justForTestHeartPackage.setTimestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2017-01-01 18:30:01"));
		PackageParser.initPackageInfo(justForTestHeartPackage);
		
		List<Byte> normal =  Arrays.asList(new Byte[] {0x0B, 0x48, 0x07, 0x58, 0x68, (byte) 0xDA, 0x29, 0x07, 0x04, 0x00, 0x5F, (byte) 0x9F});
		List<Byte> crcError =  Arrays.asList(new Byte[] {0x09 , 0x43 , (byte) 0xFF , 0x08 , 0x02 , 0x01 , 0x01 , 0x05 , (byte) 0xD3 , 0x15});
		List<Byte> enumError =  Arrays.asList(new Byte[] {0x09 , (byte) 0xFF , (byte) 0xFF , 0x08 , 0x02 , 0x01 , 0x00 , 0x05 ,  0x32 , (byte) 0xD8});
		List<Byte> protocolError =  Arrays.asList(new Byte[] {0x09 ,  0x01 , (byte) 0xFF , 0x08 , 0x02 , 0x01 , 0x00 , 0x05 ,  0x23 , (byte)0xA8});
		JustForTestHeartPackage heartPackage2 = (JustForTestHeartPackage) PackageParser.parse(normal, packageClasses, false);
		Assert.assertEquals(FieldUtil.md5(justForTestHeartPackage), FieldUtil.md5(heartPackage2));
		try {
			PackageParser.parse(crcError, packageClasses, false);
			Assert.fail();
		} catch (CRCException e) {
			System.out.println(" 'CRC校验错误' 测试异常捕捉成功");
			System.out.println(e.getMessage());
		}
		try {
			PackageParser.parse(enumError, packageClasses, false);
			Assert.fail();
		} catch (EnumClassNotFoundException e) {
			System.out.println(" '字段枚举未匹配' 测试异常捕捉成功");
		}
		try {
			PackageParser.parse(protocolError, packageClasses, false);
			Assert.fail();
		} catch (ProtocolNotMatchException e) {
			System.out.println(" '协议未匹配' 测试异常捕捉成功");
			System.out.println(e.getMessage());
		}
	}
	
	@Test
	public void finalTest() throws PackageParseException{
		try{
			List<Byte> bytes =  Arrays.asList(new Byte[] {0x08, 0x43, (byte) 0xFF, 0x01, 0x00, 0x00 , 0x05, (byte) 0xE2, 0x7C});
			PackageParser.parse(bytes, packageClasses, true);
			Assert.fail();
		}catch (EnumValueNotExistException e) {
			System.out.println(" '枚举值未匹配' 测试异常捕捉成功");
			System.out.println(e.getMessage());
		}
		//控制包
		List<Byte> bytes =  Arrays.asList(new Byte[] {0x0B, 0x48, 0x07, (byte) 0x88, 0x68, (byte) 0xDA, 0x29, 0x07, 0x04, 0x00, (byte) 0xAB, 0x4D});
		BasePackage p = PackageParser.parse(bytes, packageClasses, false);
		List<Byte> bytes2 = PackageParser.serialize(p);
		Assert.assertArrayEquals(bytes.toArray(), bytes2.toArray());
		//板子包（数目：-10）负数测试
		List<Byte> bytes3 =  Arrays.asList(new Byte[] {0x0D, 0x42, 0x01, 0x5A, 0x1B, (byte) 0xAD, (byte) 0xA3, (byte) 0xFF, (byte) 0xFF, (byte) 0xF6, 0x00, 0x03, (byte)0xBE,  0x52});
		BasePackage p1 = PackageParser.parse(bytes3, packageClasses, false);
		List<Byte> bytes4 = PackageParser.serialize(p1);
		Assert.assertArrayEquals(bytes3.toArray(), bytes4.toArray());
	}
}
