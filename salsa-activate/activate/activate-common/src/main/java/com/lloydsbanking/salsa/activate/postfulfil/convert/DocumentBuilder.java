package com.lloydsbanking.salsa.activate.postfulfil.convert;

import com.lloydsbanking.salsa.date.DateFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DocumentBuilder {

    public static final Logger LOGGER = Logger.getLogger(DocumentBuilder.class);
    public static final String ARGUMENT_ELEMENT = "NS1:argument";
    public static final String ARGUMENT_DATA_ELEMENT = "NS1:argumentData";
    public static final String EFFECTIVE_DATE_ELEMENT = "NS1:effectiveDate";
    public static final String IDENTIFIER_ELEMENT = "NS1:identifier";
    public static final String SEQUENCE_NUMBER_ELEMENT = "NS1:sequenceNumber";
    public static final String VALUE_ELEMENT = "NS1:value";
    public static final String IDENTIFIER_VALUE_DEFAULT = "220";
    public static final String SEQUENCE_NUMBER_DEFAULT_VALUE = "1";
    public static final String PLACE_OF_BIRTH = "Place Of Birth";
    public static final String ARGUMENT_DATA_TAG = "IFWXML_Argument:argumentData";
    public static final String NAME_TAG = "IFWXML_Argument:name";
    public static final String IDENTIFIER_TAG = "IFWXML_Argument:identifier";
    public static final String SEQUENCE_NUMBER_TAG = "IFWXML_Argument:sequenceNumber";
    public static final String EFFECTIVE_DATE_TAG = "IFWXML_Argument:effectiveDate";
    public static final int START_INDEX_WITHOUT_CDATA_START_TAG = 9;
    public static final int START_INDEX_WITHOUT_CDATA = 0;
    @Autowired
    DateFactory dateFactory;

    public String getDescriptionFromCDATA(String retrieveString, String placeOfBirth) {
        return getDocumentContent(retrieveString, placeOfBirth);
    }

    private String getDocumentContent(String xml, String placeOfBirth) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder db;
        String xmlFinal = "";
        try {
            db = dbf.newDocumentBuilder();
            Document document = db.newDocument();
            Element rootElement = document.createElement(ARGUMENT_ELEMENT);
            rootElement.setAttribute("xmlns:NS1", "http://xml.lloydsbanking.com/Schema/Enterprise/InformationTechnology/ESB/IFWXML_Argument");
            document.appendChild(rootElement);
            List<Element> anArgumentDataList = new ArrayList<>();
            Element argumentDataAgain = document.createElement(ARGUMENT_DATA_ELEMENT);
            Element valueIdentifier = document.createElement(IDENTIFIER_ELEMENT);
            Element valueEffectiveDate = document.createElement(EFFECTIVE_DATE_ELEMENT);
            Element valueSequenceNo = document.createElement(SEQUENCE_NUMBER_ELEMENT);
            Element value = document.createElement(VALUE_ELEMENT);
            if (xml != null) {
                valueEffectiveDate.appendChild(document.createTextNode(getArgumentDataElement(xml, EFFECTIVE_DATE_TAG)));
                argumentDataAgain.appendChild(valueEffectiveDate);
                valueIdentifier.appendChild(document.createTextNode(getArgumentDataElement(xml, IDENTIFIER_TAG)));
                argumentDataAgain.appendChild(valueIdentifier);
                valueSequenceNo.appendChild(document.createTextNode(getArgumentDataElement(xml, SEQUENCE_NUMBER_TAG)));
                argumentDataAgain.appendChild(valueSequenceNo);
            } else {
                valueIdentifier.appendChild(document.createTextNode(getCurrentDateInFormat()));
                argumentDataAgain.appendChild(valueEffectiveDate);
                valueIdentifier.appendChild(document.createTextNode(IDENTIFIER_VALUE_DEFAULT));
                argumentDataAgain.appendChild(valueIdentifier);
                valueSequenceNo.appendChild(document.createTextNode(SEQUENCE_NUMBER_DEFAULT_VALUE));
                argumentDataAgain.appendChild(valueSequenceNo);
            }
            value.appendChild(document.createTextNode(placeOfBirth));
            argumentDataAgain.appendChild(value);
            anArgumentDataList.add(argumentDataAgain);
            rootElement.appendChild(anArgumentDataList.get(0));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            xmlFinal = result.getWriter().toString();
        } catch (TransformerFactoryConfigurationError | TransformerException | ParserConfigurationException e) {
            LOGGER.info("TransformerException |TransformerFactoryConfigurationError | ParserConfigurationException occurred wile transforming document", e);
        }
        return xmlFinal;
    }

    private Node getArgumentDataFromCData(String xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        javax.xml.parsers.DocumentBuilder documentBuilder;
        Node argumentData = null;
        try {
            documentBuilder = dbf.newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(xml));
            Document document = documentBuilder.parse(inputSource);
            NodeList nodelist = document.getElementsByTagName(ARGUMENT_DATA_TAG);
            for (int i = 0; i < nodelist.getLength(); i++) {
                if (nodelist.item(i) != null) {
                    argumentData = refactorGetArgumentDataFromCData(argumentData, nodelist, i);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e1) {
            LOGGER.info("Exception occurred during input out parsing SAX ", e1);
        }
        return (argumentData);
    }

    private Node refactorGetArgumentDataFromCData(Node argumentData, NodeList nodelist, int i) {
        int index = i;
        Node argumentDataNode = argumentData;
        NodeList argumentDataNodeList = nodelist;
        NodeList newNodeList = argumentDataNodeList.item(index).getChildNodes();
        if (newNodeList != null && newNodeList.getLength() > 0) {
            for (int k = 0; k < newNodeList.getLength(); k++) {
                if (newNodeList.item(k) != null && newNodeList.item(k).getNodeName() != null) {
                    if (newNodeList.item(k).getNodeName().equals(NAME_TAG) && newNodeList.item(k).getTextContent() != null) {
                        if (newNodeList.item(k).getTextContent().equalsIgnoreCase(PLACE_OF_BIRTH)) {
                            argumentDataNode = argumentDataNodeList.item(index);
                            break;
                        }
                    }
                }
            }
        }
        return argumentDataNode;
    }

    private String getArgumentDataElement(String description, String tag) {
        Node documentContent = getArgumentDataFromCData(description.trim());
        String argumentDataElement = "";
        if (documentContent != null) {
            NodeList documentContentNodeList = documentContent.getChildNodes();
            for (int k = 0; k < documentContentNodeList.getLength(); k++) {
                if (documentContentNodeList.item(k) != null && documentContentNodeList.item(k).getNodeName() != null) {
                    if (documentContentNodeList.item(k).getNodeName().equals(tag)) {
                        argumentDataElement = documentContentNodeList.item(k).getTextContent();
                        break;
                    }
                }
            }
        }
        return (argumentDataElement);
    }

    private String getCurrentDateInFormat() {
        String effectiveDate = null;
        try {
            XMLGregorianCalendar date = dateFactory.dateToXMLGregorianCalendar(new Date());
            effectiveDate = dateFactory.convertXMLGregorianToStringDateFormat(date, "yyyy-MM-dd");
        } catch (DatatypeConfigurationException e) {
            LOGGER.info("Exception occurred during Datatype Configuration", e);
        }
        return effectiveDate;
    }
}
