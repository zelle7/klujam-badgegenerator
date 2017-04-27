import org.apache.batik.apps.rasterizer.DestinationType;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zelle (christian@zellot.at)
 */
public class Generator {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, TransformerException, SVGConverterException {

        String directPath = "/Gamejam2017/";
        List<Jammer> jammerList = createJammer(directPath + "members.csv");

        PDFMergerUtility ut = new PDFMergerUtility();
        int jammCount = 0;
        for (Jammer jammer : jammerList) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(directPath + "badge2.svg"));


            NodeList nodeListName = document.getElementsByTagName("tspan");
            Node nodeName = findById(nodeListName, "tspan3463");
            Node nodeName2 = findById(nodeListName, "tspan3463-6");
            if(jammer.getName().length() < 24){
                if (nodeName != null) {
                    nodeName.setTextContent(jammer.getName());
                }
                if(nodeName2 != null){
                    nodeName2.getParentNode().removeChild(nodeName2);
                }
            } else {
                if (nodeName2 != null) {
                    nodeName2.setTextContent(jammer.getName());
                }
                if(nodeName != null){
                    nodeName.getParentNode().removeChild(nodeName);
                }
            }


            NodeList nodeListPath = document.getElementsByTagName("path");
            String[] icons = Jammer.getSkillNames();
            for (String icon : icons) {
                int skillPoints = jammer.getSkill(icon);
                Node iconNode = findById(nodeListPath, icon + "_icon");
                if (iconNode != null) {

                    if (iconNode.getAttributes() != null && skillPoints == 0) {
                        iconNode.getParentNode().removeChild(iconNode);
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

            setBarColor(nodeListRect, "fr", jammer.getFri());
            setBarColor(nodeListRect, "sa", jammer.getSat());
            setBarColor(nodeListRect, "su", jammer.getSu());

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            File outputFile = new File(jammCount + ".svg");
            outputFile.deleteOnExit();
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
            StreamResult result = new StreamResult(new FileOutputStream(outputFile, false));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            // Convert the SVG into PDF
            File pdfOutputFile = new File(jammCount + ".pdf");
            pdfOutputFile.deleteOnExit();
            if (!pdfOutputFile.exists()) {
                pdfOutputFile.createNewFile();

            }
            SVGConverter converter = new SVGConverter();
            converter.setDestinationType(DestinationType.PDF);
            converter.setSources(new String[]{outputFile.toString()});
            converter.setDst(pdfOutputFile);
            converter.execute();

            ut.addSource(pdfOutputFile);
            System.out.println("File saved!");
            jammCount++;
        }
        ut.setDestinationFileName(directPath + "badges_all.pdf");
        ut.mergeDocuments();

    }

    public static void setBarColor(NodeList nodeListRect, String day, int amount) {
        Node barGreen = findById(nodeListRect, day + "_bar");
        Node barOrange = findById(nodeListRect, day + "_bar_orange");
        Node barRed = findById(nodeListRect, day + "_bar_red");
        try {
            switch (amount) {
                case 0:
                    barGreen.getParentNode().removeChild(barGreen);
                    barOrange.getParentNode().removeChild(barOrange);
                    barRed.getParentNode().removeChild(barRed);
                    break;
                case 1:
                    barGreen.getParentNode().removeChild(barGreen);
                    barOrange.getParentNode().removeChild(barOrange);
                    break;
                case 2:
                    barGreen.getParentNode().removeChild(barGreen);
                    barRed.getParentNode().removeChild(barRed);
                    break;
                case 3:
                    barOrange.getParentNode().removeChild(barOrange);
                    barRed.getParentNode().removeChild(barRed);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static List<Jammer> createJammer(String pathToFile) {
        String csvFile = pathToFile;
        List<Jammer> jammerList = new ArrayList<Jammer>();
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        boolean firstLine = true;
        try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                if (!firstLine) {
                    // use comma as separator
                    String[] jammerVals = line.split(cvsSplitBy);
                    if (jammerVals.length >= 2) {
                        Jammer jammer = new Jammer();
                        String name = jammerVals[2].trim() + " " + jammerVals[3].trim();

                        jammer.setName(firstLetterCapitilize(name));
                        jammer.setFri(jammerVals[14].contains("Friday") ? 3 : 0);
                        jammer.setSat(jammerVals[14].contains("Saturday") ? 3 : 0);
                        jammer.setSu(jammerVals[14].contains("Sunday") ? 3 : 0);
                        jammer.setSkill(Jammer.ART2D, jammerVals[15].contains("2D Art"));
                        jammer.setSkill(Jammer.ART3D, jammerVals[15].contains("3D Art"));
                        jammer.setSkill(Jammer.MUSIC, jammerVals[15].contains("Sound and Music"));
                        jammer.setSkill(Jammer.GAMEDESIGN, jammerVals[15].contains("Game Design"));
                        jammer.setSkill(Jammer.MANAGEMENT, jammerVals[15].contains("Management and Production"));
                        jammer.setSkill(Jammer.PROGRAMMING, jammerVals[15].contains("Programming"));
                        jammer.setSkill(Jammer.STORY, jammerVals[15].contains("Narratives and Stories"));
                        List<String> supporter = Arrays.asList("Mathias Lux", "Christian Zellot", "Veit Frick", "Andreas Leibetseder", "Sabrina Kletz");
                        if (supporter.contains(name)) {
                            jammer.setSkill(Jammer.SUPPORT, true);
                        }
                        jammerList.add(jammer);
                    } else {
                        System.out.println("line " + line);
                    }
                } else {
                    firstLine = false;
                }


            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        for (int i=0; i<15; i++){
            Jammer jammer = new Jammer();
            String name = "_________________";

            jammer.setName(firstLetterCapitilize(name));
            jammer.setFri(0);
            jammer.setSat(0);
            jammer.setSu(0);
            jammer.setSkill(Jammer.ART2D, false);
            jammer.setSkill(Jammer.ART3D, false);
            jammer.setSkill(Jammer.MUSIC, false);
            jammer.setSkill(Jammer.GAMEDESIGN, false);
            jammer.setSkill(Jammer.MANAGEMENT, false);
            jammer.setSkill(Jammer.PROGRAMMING, false);
            jammer.setSkill(Jammer.STORY, false);
            jammerList.add(jammer);
        }
        return jammerList;
    }

    public static int strToAmountTime(String str) {
        switch (str) {
            case "All of the time":
                return 3;
            case "Most of the time":
                return 2;
            case "Some of the time":
                return 1;
            default:
                return 0;
        }
    }

    public static int strToAmountSkills(String str) {
        switch (str) {
            case "Expert":
                return 3;
            case "Knowledgable":
                return 2;
            case "Some Knowledge":
                return 1;
            default:
                return 0;
        }
    }

    public static String firstLetterCapitilize(String name) {
        String[] names = name.trim().split(" ");
        String result = "";
        for (String s : names) {
            result += StringUtils.capitalize(s) + " ";
        }
        return result.trim();
    }

    public static String splitIfTooLong(String name){
        String[] names = name.trim().split(" ");
        String result = "";
        if(names.length > 1 && name.length() > 18) {
            int half = names.length / 2;
            for (int i=0; i<names.length; i++){
                if(i == half){
                    result += "\n";
                }
                result += names[i] + " ";

            }
        }
        return result.trim();
    }
}


