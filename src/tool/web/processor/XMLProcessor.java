package tool.web.processor;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**这个类用于处理XML文件
 * @author Mr_Li
 *
 */
public class XMLProcessor {
	private DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	private DocumentBuilder docBuilder;
	private NodeList list;
	private Document doc;

	public XMLProcessor(File f, String tagName) throws ParserConfigurationException, SAXException, IOException {
		if (!f.exists() || f.isDirectory() || f == null)
			throw new NullPointerException("传入f为空文件或目录");
		docBuilder = builderFactory.newDocumentBuilder();
		doc = docBuilder.parse(f);
		list = doc.getElementsByTagName(tagName);
	}

	public void printNode() {
		for (int i = 0; i < list.getLength(); i++) {
			System.out.println(((Element) list.item(i)).getTextContent());
			System.out.println(((Element) list.item(i)).getAttribute("id"));
		}
	}
	
	public String[] getNodeIdList() {
		String[] valueList=new String[list.getLength()];
		for(int i=0;i<valueList.length;i++)
			valueList[i]=((Element) list.item(i)).getAttribute("id");
		return valueList;
	}
}
