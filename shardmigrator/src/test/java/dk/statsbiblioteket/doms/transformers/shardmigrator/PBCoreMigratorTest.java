package dk.statsbiblioteket.doms.transformers.shardmigrator;

import org.junit.Test;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/19/12
 * Time: 10:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class PBCoreMigratorTest {
    @Test
    public void testAddGallupStructure() throws Exception {
        InputStream samplePBCORE = Thread.currentThread().getContextClassLoader().getResourceAsStream("objects/6a7f270c-a62e-4950-bec1-1ea230bf52ea_pbcore.xml");
        PBCoreMigrator migrator = new PBCoreMigrator(samplePBCORE);
        migrator.addGallupStructure(new Gallup());
    }

    @Test
    public void testAddRitzauStructure() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }
}
