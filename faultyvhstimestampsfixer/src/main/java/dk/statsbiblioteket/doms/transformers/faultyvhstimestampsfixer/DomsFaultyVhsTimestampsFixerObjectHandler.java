package dk.statsbiblioteket.doms.transformers.faultyvhstimestampsfixer;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.MigrationStatus;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;

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

public class DomsFaultyVhsTimestampsFixerObjectHandler implements ObjectHandler {
    public static final String COMMENT = "Fixing the wrong date for VHS files";
    public static final String VHS_METADATA = "VHS_METADATA";
    private final CentralWebservice webservice;

    private static final Logger log = LoggerFactory.getLogger(DomsFaultyVhsTimestampsFixerObjectHandler.class);

    public DomsFaultyVhsTimestampsFixerObjectHandler(DomsConfig config, CentralWebservice webservice)
            throws JAXBException, IOException, ParseException, URISyntaxException {

        this.webservice = webservice;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public MigrationStatus transform(String uuid) throws Exception {

        String vhsMetadataOriginal = webservice.getDatastreamContents(uuid, VHS_METADATA);


        Source xmlSource =
                new StreamSource(new ByteArrayInputStream(vhsMetadataOriginal.getBytes()), "originalVHSMetadata");
        Source xsltSource =
                new StreamSource(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream("xslt/fixWrongDates.xslt"));

        StringWriter vhsMetadata = new StringWriter();
        StreamResult vhsMetadataResult = new StreamResult(vhsMetadata);

        // create an instance of TransformerFactory
        TransformerFactory transFact = TransformerFactory.newInstance();

        Transformer trans =
                transFact.newTransformer(xsltSource);
        trans.transform(xmlSource, vhsMetadataResult);
        vhsMetadata.flush();

        String newVHSMetadata = vhsMetadata.toString();

        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setIgnoreAttributeOrder(true);


        Diff diff = new Diff(vhsMetadataOriginal, newVHSMetadata);
        if (!diff.similar()) {

            webservice.markInProgressObject(Arrays.asList(uuid), COMMENT);

            webservice.modifyDatastream(uuid, VHS_METADATA, newVHSMetadata, COMMENT);

            webservice.markPublishedObject(Arrays.asList(uuid), COMMENT);

            return MigrationStatus.COMPLETE;
        } else {
            return MigrationStatus.NOOP;
        }


    }


}
