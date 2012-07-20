package dk.statsbiblioteket.doms.transformers.shardmigrator;

import dk.statsbiblioteket.doms.transformers.shardmigrator.tvmeter.autogenerated.ObjectFactory;
import dk.statsbiblioteket.doms.transformers.shardmigrator.tvmeter.autogenerated.TvmeterProgram;
import org.junit.Test;


import javax.xml.bind.JAXBContext;
import java.io.File;
import java.io.InputStream;

public class TvmeterProgamTest {

    @Test
    public void testTvmeterProgramReader() throws Exception {
        File testfile = new File(Thread.currentThread().getContextClassLoader().getResource("objects/6a7f270c-a62e-4950-bec1-1ea230bf52ea_gallup.xml").toURI());
        TvmeterProgram testprogram = new TVMeterReader().readTVMeterFile(testfile.getAbsolutePath());
        JAXBContext.newInstance(TvmeterProgram.class.getPackage().getName()).createMarshaller().marshal(new ObjectFactory().createTvmeterProgram(testprogram),System.out);
    }

}