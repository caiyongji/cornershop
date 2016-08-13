package iuv.cns.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

public class Pos58Printer {
	/** 打印机名称 */
	private final String PRITER_NAME = "POS58-A-5870";
	/** 默认工作路径 */
	private final String WORK_PATH = "D:\\";
	/** 操作系统默认编码·（不是JVM） */
	private final String OPERATING_SYSTEM_CHARSET = "gb2312";

	public static void main(String[] args) {
		String fileName = "text";
		String fileContent = "《归去来兮辞》\n是晋宋之际文学家陶渊明创作的抒情小赋，也是一篇脱离仕途回归田园的宣言。\n\n\n\n";
		boolean result = Pos58Printer.print(fileName, fileContent);
		System.out.println(result);
	}

	private Pos58Printer() {
	}

	public static boolean print(String name, String content) {
		Pos58Printer printer = new Pos58Printer();
		if (printer.makeFile(name, content)) {
			return printer.print(name);
		}
		return false;
	}

	private boolean makeFile(String name, String content) {
		File file = new File(WORK_PATH + name);
		OutputStreamWriter out = null;
		try {
			out = new OutputStreamWriter(new FileOutputStream(file), OPERATING_SYSTEM_CHARSET);
			out.write(content);
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param fileName 文件（文件需与计算机系统编码一致）
	 */
	private boolean print(String fileName) {
		FileInputStream psStream = null;
		try {
			psStream = new FileInputStream(WORK_PATH + fileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		DocFlavor psInFormat = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc myDoc = new SimpleDoc(psStream, psInFormat, null);
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		PrintService[] services = PrintServiceLookup.lookupPrintServices(psInFormat, aset);
		PrintService myPrinter = null;
		for (int i = 0; i < services.length; i++) {
			String svcName = services[i].toString();
			if (svcName.contains(PRITER_NAME)) {
				myPrinter = services[i];
				break;
			}
		}

		if (myPrinter != null) {
			 AttributeSet att = myPrinter.getAttributes();
			 for (Attribute a : att.toArray()) {
			 String attributeName = a.getName();
			 String attributeValue = att.get(a.getClass()).toString();
			 System.out.println(attributeName + " : " + attributeValue);
			 }
			DocPrintJob job = myPrinter.createPrintJob();
			try {
				job.print(myDoc, aset);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}