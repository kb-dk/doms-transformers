package dk.statsbiblioteket.doms.transformers.shardremover;

import dk.statsbiblioteket.doms.central.*;
import dk.statsbiblioteket.doms.transformers.common.CalendarUtils;
import dk.statsbiblioteket.doms.transformers.common.MockWebservice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.String;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import junit.framework.Assert;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.tuscany.sdo.codegen.BytecodeInterfaceGenerator;
import org.hamcrest.CoreMatchers;



/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/20/12
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class DomsShardRemoverObjectHandlerTest {

    private static final String testDC = "<oai_dc:dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">  <dc:title>Radio/TV Program</dc:title>  <dc:identifier>317333RitzauProgram</dc:identifier>  <dc:identifier>uuid:6995eaa1-d148-45a6-951c-34f4486490c9</dc:identifier>  </oai_dc:dc>";
    
    private static final String shardUuid = "uuid:b25277d8-658f-42ac-ac44-c11c72787b01";
    
    private String testFileObjectPid;
    private String programObjectPid;
    private String shardObjectPid;

    CentralWebservice webservice;
    private static final String TEST_SHARD_METADATA_FILE = "shardMetadata.xml";

    @Before
    public void setUp() throws Exception {
        /*String testMuxFileName = "mux1.1287514800-2010-10-19-21.00.00_1287518400-2010-10-19-22.00.00_dvb1-1.ts";

        webservice = new MockWebservice();

        testFileObjectPid = webservice.newObject(null, null, null);
        webservice.addFileFromPermanentURL(testFileObjectPid,null,null,"http://bitfinder.statsbiblioteket.dk/bart/"+testMuxFileName,null,null);

        programObjectPid = webservice.newObject(null, null, null);

        shardObjectPid = webservice.newObject(null, null, null);

        webservice.modifyDatastream(programObjectPid,"PBCORE",
                IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("objects/27026d8e-bbb6-499f-b304-8511426ebfdb_pbcore.xml")
                ),"comment");

        webservice.modifyDatastream(programObjectPid,"GALLUP_ORIGINAL",
                IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("objects/27026d8e-bbb6-499f-b304-8511426ebfdb_gallup.xml")
                ),"comment");


        webservice.modifyDatastream(programObjectPid,"RITZAU_ORIGINAL",
                IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("objects/27026d8e-bbb6-499f-b304-8511426ebfdb_ritzau.xml")
                ),"comment");
        Relation shardRelation = new Relation();
        shardRelation.setObject(shardObjectPid);
        shardRelation.setSubject(programObjectPid);
        shardRelation.setPredicate("http://doms.statsbiblioteket.dk/relations/default/0/1/#hasShard");
        shardRelation.setLiteral(false);
        webservice.addRelation(programObjectPid, shardRelation, "comment");
        webservice.markPublishedObject(Arrays.asList(programObjectPid),"comment");

        webservice.modifyDatastream(shardObjectPid,"SHARD_METADATA",
                IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream("shardMetadata.xml"))
                ,"comment");
        Relation fileRelation = new Relation();
        fileRelation.setObject(testFileObjectPid);
        fileRelation.setSubject(shardObjectPid);
        fileRelation.setPredicate("http://doms.statsbiblioteket.dk/relations/default/0/1/#consistsOf");
        fileRelation.setLiteral(false);
        webservice.addRelation(shardObjectPid, fileRelation, "comment");
        webservice.markPublishedObject(Arrays.asList(shardObjectPid),"comment");*/
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testTransform() throws Exception {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance(); 
        domFactory.setIgnoringComments(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder(); 
        Document doc = builder.parse(new ByteArrayInputStream(testDC.getBytes()));

        NodeList nodes = doc.getElementsByTagName("dc:identifier");

        Text a = doc.createTextNode(shardUuid); 
        Element p = doc.createElement("dc:identifier"); 
        p.appendChild(a); 

        nodes.item(0).getParentNode().insertBefore(p, nodes.item(0));
        System.out.println("Original: " + testDC);
        
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);

        String xmlOutput = result.getWriter().toString();
       
        System.out.println("Transformed: " + xmlOutput);
        
    }

}
