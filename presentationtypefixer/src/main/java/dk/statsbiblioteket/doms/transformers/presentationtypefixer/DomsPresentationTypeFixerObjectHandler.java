package dk.statsbiblioteket.doms.transformers.presentationtypefixer;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.MigrationStatus;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;

public class DomsPresentationTypeFixerObjectHandler implements ObjectHandler {
    public static final String MODIFYING_OBJECT_AS_PART_OF_DATAMODEL_UPGRADE = "Modifying object as part of datamodel upgrade";
    public static final String PBCORE = "PBCORE";
    private final CentralWebservice webservice;

    private static final Logger log = LoggerFactory.getLogger(DomsPresentationTypeFixerObjectHandler.class);

    public DomsPresentationTypeFixerObjectHandler(DomsConfig config, CentralWebservice webservice)
            throws JAXBException, IOException, ParseException, URISyntaxException {

        this.webservice = webservice;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public MigrationStatus transform(String uuid) throws Exception {

        String pbcoreOriginal = webservice.getDatastreamContents(uuid, PBCORE);


        javax.xml.transform.Source xmlSource =
                new javax.xml.transform.stream.StreamSource(new ByteArrayInputStream(pbcoreOriginal.getBytes()), "originalPBCore");
        javax.xml.transform.Source xsltSource =
                new javax.xml.transform.stream.StreamSource(
                        Thread
                                .currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream("xslt/addFormatMediaType.xslt"));

        StringWriter pbcore = new StringWriter();
        StreamResult pbcoreResult = new StreamResult(pbcore);

        // create an instance of TransformerFactory
        TransformerFactory transFact = TransformerFactory.newInstance();

        javax.xml.transform.Transformer trans =
                transFact.newTransformer(xsltSource);
        trans.setParameter("channelMapping", Thread.currentThread().getContextClassLoader().getResource("xslt/channelMapping.xml"));
        trans.transform(xmlSource, pbcoreResult);
        pbcore.flush();

        String newPBCore = pbcore.toString();

        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(pbcoreOriginal, newPBCore);
        if (!diff.identical()) {
            System.out.println(diff.toString());
            webservice.markInProgressObject(Arrays.asList(uuid), MODIFYING_OBJECT_AS_PART_OF_DATAMODEL_UPGRADE);

            webservice.modifyDatastream(uuid, PBCORE,newPBCore, MODIFYING_OBJECT_AS_PART_OF_DATAMODEL_UPGRADE);

            webservice.markPublishedObject(Arrays.asList(uuid), MODIFYING_OBJECT_AS_PART_OF_DATAMODEL_UPGRADE);

            return MigrationStatus.COMPLETE;
        } else {
            return MigrationStatus.NOOP;
        }


    }




}
