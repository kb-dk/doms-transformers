package dk.statsbiblioteket.doms.transformers.shardmigrator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/19/12
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class PBCoreMigrator {

    private javax.xml.transform.TransformerFactory transFact;

    private Writer pbcore;

    public PBCoreMigrator(InputStream originalPbcore) throws TransformerException, IOException {

        javax.xml.transform.Source xmlSource =
                new javax.xml.transform.stream.StreamSource(originalPbcore, "originalPBCore");
        javax.xml.transform.Source xsltSource =
                new javax.xml.transform.stream.StreamSource(
                        Thread
                                .currentThread()
                                .getContextClassLoader()
                                .getResourceAsStream("xslt/pbcore_update.xslt"));

        pbcore = new StringWriter();
        StreamResult pbcoreResult = new StreamResult(pbcore);

        // create an instance of TransformerFactory
        transFact = javax.xml.transform.TransformerFactory.newInstance();

        javax.xml.transform.Transformer trans =
                transFact.newTransformer(xsltSource);
        trans.transform(xmlSource, pbcoreResult);
        pbcore.flush();
    }


    public void addGallupStructure(Gallup gallup) throws TransformerException {
        Writer gallupWriter = new StringWriter();
        try {
            JAXBContext.newInstance(Gallup.class).createMarshaller().marshal(gallup, gallupWriter);

            javax.xml.transform.Source xsltSource =
                    new javax.xml.transform.stream.StreamSource(Thread
                            .currentThread()
                            .getContextClassLoader()
                            .getResourceAsStream("xslt/pbcore_include_gallup.xslt"));

            final StreamSource gallupSource = new StreamSource(new StringReader(gallupWriter.toString()), "gallup");
            final StreamSource pbcoreSource = new StreamSource(new StringReader(pbcore.toString()), "pbcore");
            Transformer transformer = transFact.newTransformer(xsltSource);

            transformer.setURIResolver(new URIResolver() {

                @Override
                public Source resolve(String href, String base) throws TransformerException {
                    if (href.equals("pbcore")) {
                        return pbcoreSource;
                    }
                    if (href.equals("gallup")) {
                        return gallupSource;
                    }
                    return null;
                }
            });
            pbcore = new StringWriter();
            transformer.transform(pbcoreSource, new StreamResult(pbcore));

        } catch (JAXBException e) {
            throw new Error(e);
        }

    }

    public void addRitzauStructure(Ritzau ritzau) {

    }

    public String toString() {
        return null;
    }
}
