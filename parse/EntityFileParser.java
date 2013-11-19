package com.prodning.turtlesim.kernel.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.prodning.turtlesim.kernel.combat.CombatEntity;
import com.prodning.turtlesim.kernel.combat.Fleet;
import com.prodning.turtlesim.kernel.combat.CombatEntity.CombatEntityType;
import com.prodning.turtlesim.kernel.data.Resource;
import com.prodning.turtlesim.kernel.exception.TS_DuplicateIDException;
import com.prodning.turtlesim.kernel.exception.TS_GenericParseException;
import com.prodning.turtlesim.kernel.exception.TS_IDNotFoundException;

public class EntityFileParser {
    private static HashMap<String, CombatEntity> combatEntityCache = new HashMap<String, CombatEntity>();
    private static HashMap<String, Resource> resourceCache = new HashMap<String, Resource>();
    private static HashMap<String, String> nameCache = new HashMap<String, String>();
    private static HashMap<String, String> humanReadableCache = new HashMap<String, String>();

    private static File fleetFile;
    private static File entitiesFile;

    public static ArrayList<String> getListOfFleetIds() throws TS_GenericParseException {
        ArrayList<String> result = new ArrayList<String>();

        NodeList nList = getNodeListByXPath(fleetFile, "/fleets/fleet");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;

                result.add(eElement.getAttribute("id"));
            }
        }

        return result;
    }

    public static Fleet getFleetById(String id)
            throws ParserConfigurationException, SAXException, IOException,
                TS_GenericParseException, TS_DuplicateIDException, TS_IDNotFoundException {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            Document fleetDoc;
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            fleetDoc = dBuilder.parse(fleetFile);

            Fleet result = new Fleet(fleetDoc.getDocumentElement().getAttribute("name"));

            NodeList nList = getNodeListByXPath(
                    fleetFile,
                    "/fleets/fleet[@id='" + id + "']");

        if (nList == null) {
            throw new TS_GenericParseException("Error parsing fleet.");
        }

        if (nList.getLength() > 1) {
            throw new TS_DuplicateIDException("Duplicate fleet id " + id
                    + " in fleets.xml");
        } else if (nList.getLength() == 0) {
            throw new TS_IDNotFoundException("Fleet id " + id
                    + " not found in fleets.xml");
        }

        nList = getNodeListByXPath(
                fleetFile,
                "/fleets/fleet[@id='" + id + "']/entity");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                CombatEntity c;

                if (eElement.hasAttribute("id")) {
                    String entityId = eElement.getAttribute("id");
                    c = createCombatEntityById(entityId);
                } else {
                    throw new TS_GenericParseException("Error: unable to identify entity in " + fleetFile.getCanonicalPath());
                }

                for (int i = 0; i < Integer.parseInt(eElement.getAttribute("count")); i++) {
                    // clone new CombatEntity for fleet
                    if (c != null)
                        result.addToFleet(c.clone());
                    else
                        throw new TS_GenericParseException("Error in adding entity to fleet");
                }
            }
        }

        return result;
    }

    public static CombatEntity createCombatEntityById(String id)
            throws ParserConfigurationException, SAXException, IOException,
            TS_GenericParseException, TS_DuplicateIDException, TS_IDNotFoundException {
        if (combatEntityCache.containsKey(id)) {
            return combatEntityCache.get(id).clone();
        } else {
            NodeList nList = getNodeListByXPath(
                    entitiesFile,
                    "/entities/entity[@id='" + id + "']");

            CombatEntityType type;

            switch (Integer.parseInt(id.substring(0, 1))) {
                case 0:
                    type = CombatEntityType.DEFENSE;
                    break;
                case 1:
                case 2:
                    type = CombatEntityType.SHIP;
                    break;
                default:
                    type = null;
            }

            if (type == null) {
                throw new TS_GenericParseException("Invalid combat entity id " + id);
            }

            double structuralIntegrity = 0;
            double shieldStrength = 0;
            double weaponPower = 0;
            HashMap<String, Integer> rapidFire = new HashMap<String, Integer>();

            if (nList.getLength() == 1) {
                Node nNode = nList.item(0);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    try {
                        structuralIntegrity = Double.parseDouble((eElement
                                .getElementsByTagName("structural_integrity")
                                .item(0).getTextContent()));
                        shieldStrength = Double.parseDouble((eElement
                                .getElementsByTagName("base_shields").item(0)
                                .getTextContent()));
                        weaponPower = Double.parseDouble((eElement
                                .getElementsByTagName("base_weapon").item(0)
                                .getTextContent()));

                        NodeList rapidFireNodeList = getNodeListByXPath(
                                entitiesFile,
                                "/entities/entity[@id='" + id
                                        + "']/rapid_fire_list/rapid_fire");

                        for (int i = 0; i < rapidFireNodeList.getLength(); i++) {
                            Element rapidFireElement = (Element) rapidFireNodeList
                                    .item(i);

                            String rapidFireID = rapidFireElement
                                    .getAttribute("id");
                            Integer rapidFireValue = Integer
                                    .parseInt(rapidFireElement
                                            .getAttribute("value"));

                            rapidFire.put(rapidFireID, rapidFireValue);
                        }
                    } catch (NullPointerException e) {
                        throw new TS_GenericParseException("Error parsing entities.xml");
                    }
                }
            } else if (nList.getLength() > 1) {
                throw new TS_DuplicateIDException("Duplicate entity id " + id
                        + " in entities.xml");
            } else if (nList.getLength() == 0) {
                throw new TS_IDNotFoundException("Entity id " + id
                        + " not found in entities.xml");
            }

            CombatEntity newCE = new CombatEntity(id, type,
                    structuralIntegrity, shieldStrength, weaponPower, rapidFire);

            combatEntityCache.put(id, newCE);

            return newCE.clone();
        }
    }

    public static Resource getResourceById(String id) throws TS_GenericParseException, TS_DuplicateIDException, TS_IDNotFoundException {
        //check in cache
        if (resourceCache.containsKey(id)) {
            //if found in cache, return clone
            return resourceCache.get(id).clone();
        } else {
            //otherwise construct a new resource

            NodeList nList = getNodeListByXPath(entitiesFile, "/entities/entity[@id='" + id + "']");

            int metal = -1, crystal = -1, deuterium = -1;

            if (nList.getLength() == 1) {
                Node nNode = nList.item(0);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    try {
                        String s = eElement.getElementsByTagName("metal").item(0).getTextContent();
                        if (s.equals(""))
                            metal = 0;
                        else
                            metal = Integer.parseInt(s);

                        s = eElement.getElementsByTagName("crystal").item(0).getTextContent();
                        if (s.equals(""))
                            crystal = 0;
                        else
                            crystal = Integer.parseInt(s);

                        s = eElement.getElementsByTagName("deuterium").item(0).getTextContent();
                        if (s.equals(""))
                            deuterium = 0;
                        else
                            deuterium = Integer.parseInt(s);

                    } catch (Exception e) {
                        throw new TS_GenericParseException("Error parsing entities.xml");
                    }
                }
            } else if (nList.getLength() > 1) {
                throw new TS_DuplicateIDException("Duplicate entity id " + id
                        + " in entities.xml");
            } else if (nList.getLength() == 0) {
                throw new TS_IDNotFoundException("Entity id " + id
                        + " not found in entities.xml");
            }

            Resource res = new Resource(metal, crystal, deuterium);

            resourceCache.put(id, res);

            return res.clone();
        }
    }

    public static String getNameById(String id) throws TS_DuplicateIDException, TS_IDNotFoundException, TS_GenericParseException {
        if (nameCache.containsKey(id)) {
            return nameCache.get(id);
        } else {
            NodeList nList = getNodeListByXPath(
                    entitiesFile,
                    "/entities/entity[@id='" + id + "']");

            String name = null;

            if (nList.getLength() == 1) {
                Node nNode = nList.item(0);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    try {
                        name = eElement.getElementsByTagName("name")
                                .item(0).getTextContent();
                    } catch (Exception e) {
                        throw new TS_GenericParseException("Error parsing entities.xml");
                    }
                }
            } else if (nList.getLength() > 1) {
                throw new TS_DuplicateIDException("Duplicate entity id " + id
                        + " in entities.xml");
            } else if (nList.getLength() == 0) {
                throw new TS_IDNotFoundException("Entity id " + id
                        + " not found in entities.xml");
            }

            nameCache.put(id, name);

            return name;
        }
    }

    public static String getHumanReadableById(String id) throws TS_GenericParseException, TS_IDNotFoundException, TS_DuplicateIDException {
        if (humanReadableCache.containsKey(id)) {
            return humanReadableCache.get(id);
        } else {
            NodeList nList = getNodeListByXPath(
                    entitiesFile,
                    "/entities/entity[@id='" + id + "']");

            String name = null;

            if (nList.getLength() == 1) {
                Node nNode = nList.item(0);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    try {
                        name = eElement
                                .getElementsByTagName("humanreadable")
                                .item(0).getTextContent();
                    } catch (Exception e) {
                        throw new TS_GenericParseException("Error parsing entities.xml");
                    }
                }
            } else if (nList.getLength() > 1) {
                throw new TS_DuplicateIDException("Duplicate entity id " + id
                        + " in entities.xml");
            } else if (nList.getLength() == 0) {
                throw new TS_IDNotFoundException("Entity id " + id
                        + " not found in entities.xml");
            }

            humanReadableCache.put(id, name);

            return name;
        }
    }

    private static NodeList getNodeListByXPath(File file, String xPathQuery) throws TS_GenericParseException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        Document resourceEntitiesDoc;
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            resourceEntitiesDoc = dBuilder.parse(file);
        } catch (Exception e) {
            throw new TS_GenericParseException("Error parsing " + file.getPath());
        }

        XPath xPath = XPathFactory.newInstance().newXPath();

        try {
            NodeList nList = (NodeList) xPath.evaluate(xPathQuery, resourceEntitiesDoc.getDocumentElement(), XPathConstants.NODESET);
            return nList;
        } catch (XPathExpressionException e) {
            throw new TS_GenericParseException("Error in xpath expression.  If you are an end user, good luck because it's a source error.");
        }
    }

    public static void setFleetFile(File _fleetFile) {
        fleetFile = _fleetFile;
    }

    public static void setEntitiesFile(File _entitiesFile) {
        entitiesFile = _entitiesFile;
    }
}
