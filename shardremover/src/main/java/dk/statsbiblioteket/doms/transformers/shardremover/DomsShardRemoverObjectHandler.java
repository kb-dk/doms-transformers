package dk.statsbiblioteket.doms.transformers.shardremover;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.Relation;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;

/**
 * Use DOMS to handle object transformation for shard removal for a UUID.
 */
public class DomsShardRemoverObjectHandler implements ObjectHandler {

    private final DomsConfig config;
    private CentralWebservice webservice;

    /**
     * Initialise object handler.
     * @param config Configuration.
     */
    public DomsShardRemoverObjectHandler(DomsConfig config, CentralWebservice webservice) {
        this.config = config;
        this.webservice = webservice;
    }

    @Override
    public void transform(String uuid) throws InvalidCredentialsException, MethodFailedException, InvalidResourceException, 
            ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
        List<Relation> shardRelations = webservice.getNamedRelations(uuid, "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasShard");
        if (shardRelations.isEmpty()) {
            // nothing to do
        }
        String shardUuid = shardRelations.get(0).getObject();
        
        webservice.markInProgressObject(Arrays.asList(uuid), "Updating radio/tv object");
        // Add UUID to program object as extra ID (add as Identifier element in DC datastream)        
        String originalDC = webservice.getDatastreamContents(uuid, "DC");
        String newDC = addIdentifierToDC(originalDC, shardUuid);
        webservice.modifyDatastream(uuid, "DC", newDC, "Adding old shard ID to DC");
        // Remove relation to shard
        webservice.deleteRelation(uuid, shardRelations.get(0), 
                "Removing shard relation from program object as part of migration");
        // Remove shard object
        webservice.deleteObject(Arrays.asList(shardUuid), "Marking shard object as deleted");
        
        webservice.markPublishedObject(Arrays.asList(uuid),"Done updating radio/tv object");

    }
    
    private String addIdentifierToDC(String originalDC, String shardUUID) throws ParserConfigurationException, 
            SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance(); 
        domFactory.setIgnoringComments(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder(); 
        Document doc = builder.parse(new ByteArrayInputStream(originalDC.getBytes()));

        NodeList nodes = doc.getElementsByTagName("dc:identifier");

        Text a = doc.createTextNode(shardUUID); 
        Element p = doc.createElement("dc:identifier"); 
        p.appendChild(a); 

        nodes.item(0).getParentNode().insertBefore(p, nodes.item(0));
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);

        return result.getWriter().toString();
    }

}
