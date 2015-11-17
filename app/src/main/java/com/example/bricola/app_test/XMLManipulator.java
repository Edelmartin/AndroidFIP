package com.example.bricola.app_test;

import android.app.Activity;
import android.content.Context;
import android.util.Xml;

import com.example.bricola.app_test.MainActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.SimpleFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Bricola on 20/10/2015.
 */
public class XMLManipulator {

    private String groupXMLFile = "group.xml";
    static Context context;

    public XMLManipulator(Context _context)
    {
        context = _context;
    }

    //CREATE and DELETE group.xml methods
    public void createEmptyGroupXMLFile () throws IOException {

        //XmlSerializer pour créer le contenu du nouveau fichier
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
        //Ecriture du contenu du fichier group.xml vide
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xmlSerializer.startTag("", "groups");
        xmlSerializer.endTag("", "groups");
        xmlSerializer.endDocument();
        //Sauvegarde du fichier group.xml vide
        FileOutputStream fos;
        fos = context.openFileOutput(groupXMLFile, Context.MODE_PRIVATE);
        fos.write(writer.toString().getBytes());
        fos.close();
    }

    //region ADD METHODS
    public void addNewGroupWithMember (String groupName, ArrayList<String> listMemberName) throws IllegalArgumentException, IllegalStateException, IOException, XmlPullParserException {

        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Création du nouveau groupe à rajouter
            Element groupElement = doc.createElement("group");
            groupElement.setAttribute("name", groupName);

            //Ajout du nouveau groupe comme noeud enfant au noeud groups
            NodeList listOfGroups = doc.getElementsByTagName("groups");
            listOfGroups.item(0).appendChild(groupElement);

            //Ajout du noeud members au group créé
            Element membersElement = doc.createElement("members");
            groupElement.appendChild(membersElement);
            //Ajout du noeud transactions au group créé
            Element transactionsElement = doc.createElement("transactions");
            groupElement.appendChild(transactionsElement);

            //Ajout des membres comme noeud enfant au noeud members
            for (String name : listMemberName)
            {
                Element memberNode = doc.createElement("member");
                memberNode.setAttribute("name", name);
                membersElement.appendChild(memberNode);
            }

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException e) {
            e.printStackTrace();
        }
    }
    public void addNewGroupWithMember (String groupName, ArrayList<String> listMemberName, ArrayList<String> listMemberContact) throws IllegalArgumentException, IllegalStateException, IOException, XmlPullParserException {

        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Création du nouveau groupe à rajouter
            Element groupElement = doc.createElement("group");
            groupElement.setAttribute("name", groupName);

            //Ajout du nouveau groupe comme noeud enfant au noeud groups
            NodeList listOfGroups = doc.getElementsByTagName("groups");
            listOfGroups.item(0).appendChild(groupElement);

            //Ajout du noeud members au group créé
            Element membersElement = doc.createElement("members");
            groupElement.appendChild(membersElement);
            //Ajout du noeud transactions au group créé
            Element transactionsElement = doc.createElement("transactions");
            groupElement.appendChild(transactionsElement);

            //Ajout des membres comme noeud enfant au noeud members
            //for (String name : listMemberName)
            for (int i = 0 ; i < listMemberName.size() ; i++)
            {
                Element memberNode = doc.createElement("member");
                memberNode.setAttribute("name", listMemberName.get(i));
                memberNode.setAttribute("contact" , listMemberContact.get(i));
                membersElement.appendChild(memberNode);
            }

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException e) {
            e.printStackTrace();
        }
    }
    public void addNewMemberInGroup (String groupName, String memberName) {

        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Ajout du member au group
            Node membersNode = getMembersNode(doc, groupName);
            Element memberNode = doc.createElement("member");
            memberNode.setAttribute("name", memberName);
            membersNode.appendChild(memberNode);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }
    public void addNewMemberInGroup (String groupName, String memberName, String memberContact) {

        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Ajout du member au group
            Node membersNode = getMembersNode(doc, groupName);
            Element memberNode = doc.createElement("member");
            memberNode.setAttribute("name", memberName);
            memberNode.setAttribute("contact" , memberContact);
            membersNode.appendChild(memberNode);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }

    }
    public void addNewTransaction (String groupName, Transaction transaction) {

        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Ajout de la transaction au group
            Node transactionsNode = getTransactionsNode(doc, groupName);
            Element transactionNode = doc.createElement("transaction");
            transactionNode.setAttribute("name", transaction.getName());
            transactionNode.setAttribute("owner", transaction.getOwner());
            transactionNode.setAttribute("value", transaction.getValue().toString());
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            transactionNode.setAttribute("date", df.format(transaction.getDate()));
            transactionsNode.appendChild(transactionNode);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region DELETE METHODS
    public void deleteGroup (String groupName) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Suppression du group et de ses childNode
            Node groupNode = getGroupNode(doc, groupName);
            removeChilds(groupNode);
            groupNode.getParentNode().removeChild(groupNode);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    public void deleteMemberOfGroup (String groupName, String memberName) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Suppression du member
            Node memberNode = getMemberNode(doc, groupName, memberName);
            memberNode.getParentNode().removeChild(memberNode);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    public void deleteTransaction (String groupName, String transactionName) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Suppression du member
            Node transactionNode = getTransactionNode(doc, groupName, transactionName);
            transactionNode.getParentNode().removeChild(transactionNode);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region MODIFY METHODS
    public void modifyGroupName (String oldGroupName, String newGroupName) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Modification du nom du group
            Node groupNode = getGroupNode(doc, oldGroupName);
            NamedNodeMap groupNodeAttributes = groupNode.getAttributes();
            Node groupNodeAttribute = groupNodeAttributes.getNamedItem("name");
            groupNodeAttribute.setTextContent(newGroupName);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    public void modifyMemberName (String groupName, String oldMemberName, String newMemberName) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Modification du nom du com.example.bricola.app_test.Member
            Node memberNode = getMemberNode(doc, groupName, oldMemberName);
            NamedNodeMap memberNodeAttributes = memberNode.getAttributes();
            Node memberNodeAttribute = memberNodeAttributes.getNamedItem("name");
            memberNodeAttribute.setTextContent(newMemberName);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    public void modifyMemberContact (String groupName, String memberName, String newMemberContact) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Modification du numero du com.example.bricola.app_test.Member
            Node memberNode = getMemberNode(doc, groupName, memberName);
            NamedNodeMap memberNodeAttributes = memberNode.getAttributes();
            Node memberNodeAttribute = memberNodeAttributes.getNamedItem("contact");
            memberNodeAttribute.setTextContent(newMemberContact);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    public void modifyTransaction (String groupName, String transactionName, Transaction newTransaction) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Récupération des attributs du noeud de la transaction
            Node transactionNode = getTransactionNode(doc, groupName, transactionName);
            NamedNodeMap memberNodeAttributes = transactionNode.getAttributes();
            //Modification du name
            Node memberNodeNameAttribute = memberNodeAttributes.getNamedItem("name");
            memberNodeNameAttribute.setTextContent(newTransaction.getName());
            //Modification du owner
            Node memberNodeOwnerAttribute = memberNodeAttributes.getNamedItem("owner");
            memberNodeOwnerAttribute.setTextContent(newTransaction.getOwner());
            //Modification de la value
            Node memberNodeValueAttribute = memberNodeAttributes.getNamedItem("value");
            memberNodeValueAttribute.setTextContent(newTransaction.getValue().toString());
            //Modification de la date
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Node memberNodeDateAttribute = memberNodeAttributes.getNamedItem("date");
            memberNodeDateAttribute.setTextContent(df.format(newTransaction.getDate()));

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    public void modifyTransactionName (String groupName, String transactionName, String newTransactionName) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Récupération des attributs du noeud de la transaction
            Node transactionNode = getTransactionNode(doc, groupName, transactionName);
            NamedNodeMap memberNodeAttributes = transactionNode.getAttributes();
            //Modification du name
            Node memberNodeNameAttribute = memberNodeAttributes.getNamedItem("name");
            memberNodeNameAttribute.setTextContent(newTransactionName);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    public void modifyTransactionOwner (String groupName, String transactionName, String newTransactionOwner) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Récupération des attributs du noeud de la transaction
            Node transactionNode = getTransactionNode(doc, groupName, transactionName);
            NamedNodeMap memberNodeAttributes = transactionNode.getAttributes();
            //Modification du name
            Node memberNodeNameAttribute = memberNodeAttributes.getNamedItem("owner");
            memberNodeNameAttribute.setTextContent(newTransactionOwner);

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    public void modifyTransactionValue (String groupName, String transactionName,Double newTransactionValue ) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Récupération des attributs du noeud de la transaction
            Node transactionNode = getTransactionNode(doc, groupName, transactionName);
            NamedNodeMap memberNodeAttributes = transactionNode.getAttributes();
            //Modification du name
            Node memberNodeNameAttribute = memberNodeAttributes.getNamedItem("value");
            memberNodeNameAttribute.setTextContent(newTransactionValue.toString());

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    public void modifyTransactionDate (String groupName, String transactionName, Date newTransactionDate) {
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            //Récupération des attributs du noeud de la transaction
            Node transactionNode = getTransactionNode(doc, groupName, transactionName);
            NamedNodeMap memberNodeAttributes = transactionNode.getAttributes();
            //Modification du name
            Node memberNodeNameAttribute = memberNodeAttributes.getNamedItem("date");
            memberNodeNameAttribute.setTextContent(newTransactionDate.toString());

            //Enregistrement des modifications
            saveGroupXMLFileModifications(fXmlFile, doc);

        } catch (ParserConfigurationException | TransformerException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
    //endregion

    //region GET METHODS
    public ArrayList<String> getListGroup() throws IOException, XmlPullParserException {
        ArrayList<String> listGroup = new ArrayList<String>();
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            NodeList listOfGroup = doc.getElementsByTagName("group");
            for (int temp = 0; temp < listOfGroup.getLength(); temp++) {
                listGroup.add(listOfGroup.item(temp).getAttributes().getNamedItem("name").getNodeValue());
            }
        } catch (ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
        return listGroup;
    }
    public ArrayList<String> getListMemberOfGroup (String groupName) {
        ArrayList<String> listMemberOfGroup = new ArrayList<String>();
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            Node membersNode = getMembersNode(doc, groupName);
            NodeList listOfMember = membersNode.getChildNodes();
            for (int temp = 0; temp < listOfMember.getLength(); temp++) {
                listMemberOfGroup.add(listOfMember.item(temp).getAttributes().getNamedItem("name").getNodeValue());
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return listMemberOfGroup;
    }
    public ArrayList<String> getListMemberContact (String groupName) {
        ArrayList<String> listMemberContactOfGroup = new ArrayList<String>();
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            Node membersNode = getMembersNode(doc, groupName);
            NodeList listOfMember = membersNode.getChildNodes();
            for (int temp = 0; temp < listOfMember.getLength(); temp++) {
                listMemberContactOfGroup.add(listOfMember.item(temp).getAttributes().getNamedItem("contact").getNodeValue());
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return listMemberContactOfGroup;
    }
    public ArrayList<Transaction> getListTransactionOfGroup (String groupName) {
        ArrayList<Transaction> listTransactionOfGroup = new ArrayList<Transaction>();
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            Node transactionsNode = getTransactionsNode(doc, groupName);
            NodeList listOfTransaction = transactionsNode.getChildNodes();
            Transaction transaction = null;
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            for (int temp = 0; temp < listOfTransaction.getLength(); temp++) {
                String transactionName = listOfTransaction.item(temp).getAttributes().getNamedItem("name").getNodeValue();
                String transactionOwner = listOfTransaction.item(temp).getAttributes().getNamedItem("owner").getNodeValue();
                Double transactionValue = Double.parseDouble(listOfTransaction.item(temp).getAttributes().getNamedItem("value").getNodeValue());
                //Date transactionDate = df.parse(listOfTransaction.item(temp).getAttributes().getNamedItem("date").getNodeValue());
                // info : http://www.mkyong.com/java/how-to-convert-string-to-date-java/
                Date transactionDate = df.parse(listOfTransaction.item(temp).getAttributes().getNamedItem("date").getNodeValue());
                transaction = new Transaction(transactionName, transactionOwner, transactionValue, transactionDate);
                listTransactionOfGroup.add(transaction);
            }
        } catch (ParserConfigurationException | SAXException | IOException | ParseException e) {
            e.printStackTrace();
        }
        return listTransactionOfGroup;
    }
    public ArrayList<Transaction> getListTransactionOfMember (String groupName, String memberName) {
        ArrayList<Transaction> listTransactionOfMember = new ArrayList<Transaction>();
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            Node transactionsNode = getTransactionsNode(doc, groupName);
            NodeList listOfTransaction = transactionsNode.getChildNodes();
            Transaction transaction = null;
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            for (int temp = 0; temp < listOfTransaction.getLength(); temp++) {
                String transactionOwner = listOfTransaction.item(temp).getAttributes().getNamedItem("owner").getNodeValue();
                if (transactionOwner.equals(memberName))
                {
                    String transactionName = listOfTransaction.item(temp).getAttributes().getNamedItem("name").getNodeValue();
                    Double transactionValue = Double.parseDouble(listOfTransaction.item(temp).getAttributes().getNamedItem("value").getNodeValue());
                    Date transactionDate = df.parse(listOfTransaction.item(temp).getAttributes().getNamedItem("date").getNodeValue());
                    transaction = new Transaction(transactionName, transactionOwner, transactionValue, transactionDate);
                    listTransactionOfMember.add(transaction);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | ParseException e) {
            e.printStackTrace();
        }
        return listTransactionOfMember;
    }
    public Transaction getTransaction (String groupName, String _transactionName) throws IOException, SAXException, ParserConfigurationException, ParseException {
        //Ouverture du fichier group.xml
        File fXmlFile = getGroupXMLFile();
        Document doc = getGroupXMLDocument(fXmlFile);

        Node transactionNode = getTransactionNode(doc, groupName, _transactionName);
        String transactionName = transactionNode.getAttributes().getNamedItem("name").getNodeValue();
        String transactionOwner = transactionNode.getAttributes().getNamedItem("owner").getNodeValue();
        Double transactionValue = Double.parseDouble(transactionNode.getAttributes().getNamedItem("value").getNodeValue());
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date transactionDate = df.parse(transactionNode.getAttributes().getNamedItem("date").getNodeValue());
        Transaction transaction = new Transaction(transactionName, transactionOwner, transactionValue, transactionDate);
        return transaction;
    }
    public Double getTotalTransactionAmountOfMember(String groupName, String memberName) {
        Double totalTransactionAmount = 0.0;
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            Node transactionsNode = getTransactionsNode(doc, groupName);
            NodeList listOfTransaction = transactionsNode.getChildNodes();
            for (int temp = 0; temp < listOfTransaction.getLength(); temp++)
            {
                if (listOfTransaction.item(temp).getAttributes().getNamedItem("owner").getNodeValue().equals(memberName))
                {
                    totalTransactionAmount += Double.parseDouble(listOfTransaction.item(temp).getAttributes().getNamedItem("value").getNodeValue());
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return totalTransactionAmount;
    }
    public Integer getNumberOfMember(String groupName) {
        Integer numberOfMember = 0;
        try {
            //Ouverture du fichier group.xml
            File fXmlFile = getGroupXMLFile();
            Document doc = getGroupXMLDocument(fXmlFile);

            Node transactionsNode = getTransactionsNode(doc, groupName);
            NodeList listOfTransaction = transactionsNode.getChildNodes();
            numberOfMember = listOfTransaction.getLength();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return numberOfMember;
    }
    //endregion

    //region PRIVATE METHODS
    private File getGroupXMLFile () {
        File path = context.getFilesDir();
        String pathFile = path.toString() + "/" + groupXMLFile;
        File fXmlFile = new File(pathFile);
        return fXmlFile;
    }
    private Document getGroupXMLDocument (File fXmlFile) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        return doc;
    }
    private void saveGroupXMLFileModifications(File fXmlFile, Document doc) throws TransformerException, FileNotFoundException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(new FileOutputStream(fXmlFile));
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
    }
    private Node getGroupNode (Document groupXMLDoc, String groupName) {
        NodeList listOfGroup = groupXMLDoc.getElementsByTagName("group");
        Node groupNode = null;
        for (Integer i = 0 ; i < listOfGroup.getLength() ; i++)
        {
            if (listOfGroup.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(groupName))
                groupNode = listOfGroup.item(i);
        }
        return groupNode;
    }
    private Node getMembersNode (Document groupXMLDoc, String groupName) {
        Node membersNode = getGroupNode(groupXMLDoc, groupName).getFirstChild();
        return membersNode;
    }
    private Node getMemberNode (Document groupXMLDoc, String groupName, String memberName) {
        Node membersNode = getMembersNode(groupXMLDoc, groupName);
        NodeList listOfMember = membersNode.getChildNodes();
        Node memberNode = null;
        for (Integer i = 0 ; i < listOfMember.getLength() ; i++)
        {
            if (listOfMember.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(memberName))
                memberNode = listOfMember.item(i);
        }
        return memberNode;
    }
    private Node getTransactionsNode (Document groupXMLDoc, String groupName) {
        Node TransactionsNode = getGroupNode(groupXMLDoc, groupName).getLastChild();
        return TransactionsNode;
    }
    private Node getTransactionNode (Document groupXMLDoc, String groupName, String transactionName) {
        Node transactionsNode = getTransactionsNode(groupXMLDoc, groupName);
        NodeList listOfTransactions = transactionsNode.getChildNodes();
        Node transactionNode = null;
        for (Integer i = 0 ; i < listOfTransactions.getLength() ; i++)
        {
            if (listOfTransactions.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(transactionName))
                transactionNode = listOfTransactions.item(i);
        }
        return transactionNode;
    }
    private void removeChilds(Node node) {
        NodeList nodeList = node.getChildNodes();
        Node n = null;
        for (Integer temp = 0 ; temp < nodeList.getLength() ; temp++)
        {
            n = nodeList.item(temp);
            if(n.hasChildNodes())
            {
                removeChilds(n);
                node.removeChild(n);
            }
            else
                node.removeChild(n);
        }
    }
    //endregion
}
