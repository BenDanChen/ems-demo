package org.cc.ems.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cc.ems.entity.CommonEmployee;
import org.cc.ems.entity.Director;
import org.cc.ems.entity.Employee;
import org.cc.ems.entity.Manager;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * XML工具
 * @author cc
 *
 */
public final class XMLUtil {

	private XMLUtil() {
	}
	
	private static XMLUtil instance;
	
	public synchronized static XMLUtil getInstance(){
		synchronized (XMLUtil.class) {
			if(instance==null) instance=new XMLUtil();
		}
		return instance;
	}

	private Document getDocument(String path){
		try {
			return new SAXReader().read(new File(path));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Document getDocument(Reader reader){
		try {
			return new SAXReader().read(reader);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 存为XML
	 * @param list
	 */
	public void save(List<Employee> list,String path){

		Document document=DocumentHelper.createDocument();
		document.setXMLEncoding("UTF-8");
		
		Element root=document.addElement("employees");
		
		Element commonEmployee=root.addElement("commonEmployee");
		commonEmployee.addAttribute("type","1").addAttribute("position","普通员工");
		Element manager=root.addElement("manager");
		manager.addAttribute("type","2").addAttribute("position","经理");
		Element director=root.addElement("director");
		director.addAttribute("type","3").addAttribute("position","董事");
		
		for(Employee e:list){
			if("普通员工".equals(e.getPosition())){
				Element element=commonEmployee.addElement("employee");
				element.addAttribute("id",e.getId()).addAttribute("name",e.getName()).
				addAttribute("absenteeism",Integer.toString(e.getAbsenteeism())).addAttribute("salary",String.format("%.2f",e.getSalary()));
			}else if("经理".equals(e.getPosition())){
				Element element=manager.addElement("employee");
				element.addAttribute("id",e.getId()).addAttribute("name",e.getName()).
				addAttribute("absenteeism",Integer.toString(e.getAbsenteeism())).addAttribute("salary",String.format("%.2f",e.getSalary()));
			}else if("董事".equals(e.getPosition())){
				Element element=director.addElement("employee");
				element.addAttribute("id",e.getId()).addAttribute("name",e.getName()).
				addAttribute("absenteeism",Integer.toString(e.getAbsenteeism())).addAttribute("salary",String.format("%.2f",e.getSalary()));
			}
		}
		
		 try {
			// Pretty print the document to System.out
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter writer = new XMLWriter( new FileOutputStream(path), format);
			writer.write(document);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 读取XML存档
	 * @return
	 */
	public List<Employee> read(String path){
		try {
			return read(new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Employee> read(Reader reader){
	
		List<Employee> list=new ArrayList<Employee>();
		
		Element root=getDocument(reader).getRootElement();
		for(Iterator<Element> iter=root.elementIterator();iter.hasNext();){
			
			Element e=iter.next();
			String position=e.attributeValue("position");
			
			int type=Integer.parseInt(e.attributeValue("type"));
			
			for(Iterator<Element> iter2=e.elementIterator();iter2.hasNext();){
				Element e2=iter2.next();
				String id=e2.attributeValue("id");
				String name=e2.attributeValue("name");
				double salary=Double.parseDouble(e2.attributeValue("salary"));
				int absenteeism=Integer.parseInt(e2.attributeValue("absenteeism"));
				
				switch(type){
				case 1:
					list.add(new CommonEmployee(id,name,position,absenteeism,salary));
					break;
				case 2:
					list.add(new Manager(id,name,position,absenteeism,salary));
					break;
				case 3:
					list.add(new Director(id,name,position,absenteeism,salary));
					break;
				}
			}
		}
		
		return list;
	}
	
}
