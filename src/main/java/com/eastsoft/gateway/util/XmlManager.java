package com.eastsoft.gateway.util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.eastsoft.gateway.util.ToolUtil;

public class XmlManager {
	DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	private Document document;
	private String xmlPath;

	public static void main(String[] args) {
		
		XmlManager xmlManager = new XmlManager();
		xmlManager.modifyNode("", "Eastsoft_54328");
		String cmd = "netsh wlan add profile filename="+xmlManager.getNowPath()+"\"\\Eastsoft_wifi.xml\"";
		Ping.executeCmd(cmd);
		
		/*String original = xmlManager.getNowPath()+ "\\wifiConfTemplate.xml";
		String target = xmlManager.getNowPath()+"\\Eastsoft_wifi.xml";
		try {
			ToolUtil.copyFile(original, target);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		System.out.println(xmlManager.stringToHexString("Eastsoft_54328").toUpperCase());
	}

	public XmlManager() {
		xmlPath = getNowPath() + "\\wifiConfTemplate.xml";
		try {
			// DOM parser instance
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			// parse an XML file into a DOM tree
			document = builder.parse(new File(xmlPath));
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void modifyNode(String ElementPath,String ElmName) {
		Element rootElement = document.getDocumentElement();

		Element per = (Element)selectSingleNode("/WLANProfile/SSIDConfig/SSID",rootElement);
		per.getElementsByTagName("name").item(0).setTextContent(ElmName);
		
		per = (Element)selectSingleNode("/WLANProfile/SSIDConfig/SSID",rootElement);
		per.getElementsByTagName("hex").item(0).setTextContent(stringToHexString(ElmName).toUpperCase());
		
		per = (Element)selectSingleNode("/WLANProfile",rootElement);
		per.getElementsByTagName("name").item(0).setTextContent(ElmName);
		
		TransformerFactory factory = TransformerFactory.newInstance();
        Transformer former;
		try {
			former = factory.newTransformer();
			former.setOutputProperty(OutputKeys.ENCODING, "GBK");
			String target = getNowPath()+"\\Eastsoft_wifi.xml";
			former.transform(new DOMSource(document), new StreamResult(new File(target)));
			
			/*String original = getNowPath()+ "\\wifiConfTemplate.xml";
			String target = getNowPath()+"\\Eastsoft_wifi.xml";
			ToolUtil.copyFile(original, target);*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Node selectSingleNode(String express, Element source) {
        Node result=null;
        XPathFactory xpathFactory=XPathFactory.newInstance();
        XPath xpath=xpathFactory.newXPath();
        try {
            result=(Node) xpath.evaluate(express, source, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        
        return result;
    }

	public String stringToHexString(String strPart) {
		String hexString = "";
		for (int i = 0; i < strPart.length(); i++) {
			int ch = (int) strPart.charAt(i);
			String strHex = Integer.toHexString(ch);
			hexString = hexString + strHex;
		}
		return hexString;
	}

	private String getNowPath() {
		File directory = new File(".");
		try {
			return directory.getCanonicalPath();
		} catch (Exception exp) {
			exp.printStackTrace();
			return null;
		}
	}
}