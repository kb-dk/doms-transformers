package dk.statsbiblioteket.doms.transformers.shardmigrator;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.transformers.common.MockWebservice;
import dk.statsbiblioteket.doms.transformers.shardmigrator.programStructure.autogenerated.ProgramStructure;
import dk.statsbiblioteket.doms.transformers.shardmigrator.shardmetadata.autogenerated.ShardMetadata;
import dk.statsbiblioteket.doms.transformers.shardmigrator.tvmeter.autogenerated.ObjectFactory;
import dk.statsbiblioteket.doms.transformers.shardmigrator.tvmeter.autogenerated.TvmeterProgram;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class DomsShardMigratorObjectHandlerTest {
    
    private static final String TEST_SHARD_METADATA_FILE = "shardMetadata.xml";
    DomsShardMigratorObjectHandler handler;
    
    @Before
    public void setUp() throws Exception {
        handler = new DomsShardMigratorObjectHandler(null, null);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void shardMetadataMigratorTest() throws URISyntaxException, IOException, JAXBException, 
            InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        File testDataFile = 
                new File(Thread.currentThread().getContextClassLoader().getResource(TEST_SHARD_METADATA_FILE).toURI());
        String testShardMetadata = org.apache.commons.io.IOUtils.toString(new FileInputStream(testDataFile));
        ShardMetadata shardMetadata = handler.deserializeShardMetadata(testShardMetadata);
        ProgramStructure programStructure = handler.convertShardStructure(shardMetadata);
    }
}
