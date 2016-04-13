import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author zelle (christian@zellot.at)
 */
public class Generator {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, TransformerException, SVGConverterException {

        int jammCount = 0;
        Jammer jammer = new Jammer();
        jammer.setName("Jammer Jamalius Jamming");
        jammer.getSkills().put("art3d", 2);
        jammer.getSkills().put("art2d", 1);
        jammer.getSkills().put("programming", 3);
        jammer.getSkills().put("support", 3);
        jammer.setFri(0);
        jammer.setSat(1);
        jammer.setSu(2);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File("/home/zelle/Dropbox/Gamejam2016/badge2.svg"));


        NodeList nodeListName = document.getElementsByTagName("tspan");
        Node nodeName = findById(nodeListName, "tspan3463");
        if (nodeName != null) {
            nodeName.setTextContent(jammer.getName());
        }
        NodeList nodeListPath = document.getElementsByTagName("path");
        String[] icons = Jammer.getSkillNames();
        for (String icon : icons) {
            int skillPoints = jammer.getSkill(icon);
            Node iconNode = findById(nodeListPath, icon + "_icon");
            if (iconNode != null) {
                if (iconNode.getAttributes() != null && iconNode.getAttributes().getNamedItem("style") != null && skillPoints == 0) {
                    iconNode.getAttributes().getNamedItem("style").setNodeValue("fill:#808080ff");
                } else {
                    System.out.println("icon " + icon + " has no style attr");
                }

            } else {
                System.out.println("icon " + icon + " not found");
            }

            for (int i = 1; i < 4; i++) {
                Node star = findById(nodeListPath, icon + "_star" + i);
                if (star != null && i > skillPoints) {
                    star.getParentNode().removeChild(star);
                }

            }

        }
        NodeList nodeListRect = document.getElementsByTagName("rect");
        setBarColor(findById(nodeListRect, "fr_bar"), jammer.getFri());
        setBarColor(findById(nodeListRect, "sa_bar"), jammer.getSat());
        setBarColor(findById(nodeListRect, "su_bar"), jammer.getSu());

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        File outputFile = new File("/home/zelle/Dropbox/Gamejam2016/" + jammCount + ".svg");
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        StreamResult result = new StreamResult(new FileOutputStream(outputFile, false));

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);

        // Convert the SVG into PDF
        File pdfOutputFile = new File("/home/zelle/Dropbox/Gamejam2016/" + jammCount + ".pdf");
        if (!pdfOutputFile.exists()) {
            pdfOutputFile.createNewFile();
        }
        SVGConverter converter = new SVGConverter();
        converter.setDestinationType(DestinationType.PDF);
        converter.setSources(new String[]{outputFile.toString()});
        converter.setDst(pdfOutputFile);
        converter.execute();

        System.out.println("File saved!");
    }

    public static Node setBarColor(Node barNode, int amount){
        String color = "";
        switch (amount){
            case 0:
                color = "#ffffffff";
            break;
            case 1:
              color = "#ff0000ff";
            break;
            case 2:
                color = "#ff6600ff";
            break;
            default:
                color = "#008000ff";
                break;
        }
        String style = "opacity:1;fill:"+color+";fill-opacity:1;stroke:#000000;stroke-width:0.89533901;stroke-miterlimit:4;stroke-dasharray:none;stroke-opacity:1";
        if (barNode != null) {
            if (barNode.getAttributes() != null && barNode.getAttributes().getNamedItem("style") != null) {
                barNode.getAttributes().getNamedItem("style").setNodeValue(style);
                barNode.getAttributes().getNamedItem("style");
            } else {
                System.out.println("barnode has no style attr");
            }

        } else {
            System.out.println("barnode not found");
        }
        return  barNode;
    }

    public static Node findById(NodeList nodeList, String idStr) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getAttributes() != null && node.getAttributes().getNamedItem("id") != null) {
                Node id = node.getAttributes().getNamedItem("id");
                if (id.getNodeValue().equals(idStr)) {
                    return node;
                }
            }
        }
        return null;
    }
}


