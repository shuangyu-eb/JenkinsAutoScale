package DomXml;


import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomXml {

  /**
   * 生成xml方法
   */
  public static void createXml(){
    try {
      // 创建解析器工厂
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = factory.newDocumentBuilder();
      Document document = db.newDocument();
      // 不显示standalone="no"
      document.setXmlStandalone(true);
      Element rootElement = document.createElement("ec2s");
//      // 向bookstore根节点中添加子节点book
//      Element book = document.createElement("ec2Instance");
//
//      Element name = document.createElement("instance_name");
//      // 不显示内容 name.setNodeValue("不好使");
//      name.setTextContent("雷神");
//      book.appendChild(name);
//      // 为book节点添加属性
//      book.setAttribute("id", "1");
//      // 将book节点添加到bookstore根节点中
//      bookstore.appendChild(book);
      // 将bookstore节点（已包含book）添加到dom树中
      document.appendChild(rootElement);

      // 创建TransformerFactory对象
      TransformerFactory tff = TransformerFactory.newInstance();
      // 创建 Transformer对象
      Transformer tf = tff.newTransformer();

      // 输出内容是否使用换行
      tf.setOutputProperty(OutputKeys.INDENT, "yes");
      // 创建xml文件并写入内容
      tf.transform(new DOMSource(document), new StreamResult(new File("ec2_creation_history.xml")));
      System.out.println("生成ec2s.xml成功");
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("生成ec2s.xml失败");
    }
  }

  public static void addNode (String fileName, String instanceId, String publicIp, String creationTime) {

    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

      Document document = documentBuilder.parse(fileName);
      Element root = document.getDocumentElement();
      Node newEc2Node = document.createElement("ec2Instance");
      Element id = document.createElement("instance_id");
      Element name = document.createElement("name");
      Element public_id = document.createElement("public_id");
      Element creation_time = document.createElement("creation_time");
      id.setTextContent(instanceId);
      name.setTextContent(instanceId);
      public_id.setTextContent(publicIp);
      creation_time.setTextContent(creationTime);
      newEc2Node.appendChild(id);
      newEc2Node.appendChild(public_id);
      newEc2Node.appendChild(name);
      newEc2Node.appendChild(creation_time);
      root.appendChild(newEc2Node);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty("encoding", "utf-8");
      transformer.setOutputProperty("indent", "yes");
      transformer.transform(new DOMSource(document), new StreamResult(new File(fileName)));
    } catch (SAXException | TransformerException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void delNode (String fileName, String instanceName) {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    DocumentBuilder documentBuilder = null;
    try {
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }

    Document document = null;
    try {
      document = documentBuilder.parse("ec2_creation_history.xml");
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    NodeList nodes = document.getElementsByTagName("ec2Instance");
    for (int i = 0 ; i < nodes.getLength() ; i ++) {
      System.out.println("nodes" + i + ":" + nodes.item(i).getChildNodes().item(3).getTextContent());
      Element nodeParent = (Element) nodes.item(i).getParentNode();
      System.out.println("nodeParent:" + nodeParent);
      if (nodes.item(i).getChildNodes().item(3).getTextContent().equals(instanceName)) {
        System.out.println("remove node");
        nodeParent.removeChild(nodes.item(i));
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
          transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
          e.printStackTrace();
        }
        transformer.setOutputProperty("encoding", "utf-8");
        transformer.setOutputProperty("indent", "yes");
        try {
          transformer.transform(new DOMSource(document), new StreamResult("ec2_creation_history.xml"));
        } catch (TransformerException e) {
          e.printStackTrace();
        }
      }


    }


  }


  public static void main(String[] args) throws IOException {
    Long start = System.currentTimeMillis();
    System.out.println("运行时间："+ (System.currentTimeMillis() - start));
  }


}
