package dk.statsbiblioteket.doms.transformers.presentationtypefixer;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.MigrationStatus;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class DomsPresentationTypeFixerObjectHandler implements ObjectHandler {
    public static final String COMMENT = "Fixing the missing pbcore format media type";
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


        Source xmlSource =
                new StreamSource(new ByteArrayInputStream(pbcoreOriginal.getBytes()), "originalPBCore");
        Source xsltSource =
                new StreamSource(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream("xslt/addFormatMediaType.xslt"));

        StringWriter pbcore = new StringWriter();
        StreamResult pbcoreResult = new StreamResult(pbcore);

        // create an instance of TransformerFactory
        TransformerFactory transFact = TransformerFactory.newInstance();

        Transformer trans =
                transFact.newTransformer(xsltSource);
        trans.setParameter("channelMapping", Thread.currentThread().getContextClassLoader().getResource("channelMapping.xml"));
        trans.transform(xmlSource, pbcoreResult);
        pbcore.flush();

        String newPBCore = pbcore.toString();

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);


        Diff diff = new Diff(pbcoreOriginal, newPBCore);
        if (!diff.similar()) {

            webservice.markInProgressObject(Arrays.asList(uuid), COMMENT);

            webservice.modifyDatastream(uuid, PBCORE, newPBCore, COMMENT);

            webservice.markPublishedObject(Arrays.asList(uuid), COMMENT);

            return MigrationStatus.COMPLETE;
        } else {
            return MigrationStatus.NOOP;
        }


    }


}
