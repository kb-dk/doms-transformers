package dk.statsbiblioteket.doms.transformers.shardmigrator;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.Relation;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;
import dk.statsbiblioteket.doms.transformers.shardmigrator.programStructure.autogenerated.MissingEnd;
import dk.statsbiblioteket.doms.transformers.shardmigrator.programStructure.autogenerated.MissingStart;
import dk.statsbiblioteket.doms.transformers.shardmigrator.programStructure.autogenerated.ProgramStructure;
import dk.statsbiblioteket.doms.transformers.shardmigrator.programStructure.autogenerated.ProgramStructure.Holes;
import dk.statsbiblioteket.doms.transformers.shardmigrator.programStructure.autogenerated.ProgramStructure.Overlaps;
import dk.statsbiblioteket.doms.transformers.shardmigrator.shardmetadata.autogenerated.Hole;
import dk.statsbiblioteket.doms.transformers.shardmigrator.shardmetadata.autogenerated.Overlap;
import dk.statsbiblioteket.doms.transformers.shardmigrator.shardmetadata.autogenerated.ShardMetadata;
import dk.statsbiblioteket.doms.transformers.shardmigrator.shardmetadata.autogenerated.ShardStructure;
import dk.statsbiblioteket.doms.transformers.shardmigrator.tvmeter.autogenerated.TvmeterProgram;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.ws.BindingProvider;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Use DOMS to handle object transformation for shard removal for a UUID.
 */
public class DomsShardMigratorObjectHandler implements ObjectHandler {

    private final PropertyBasedDomsConfig config;
    private CentralWebservice webservice;
    private TVMeterReader tvmeterReader;


    /**
     * Initialise object handler.
     * @param config Configuration.
     */
    public DomsShardMigratorObjectHandler(PropertyBasedDomsConfig config, CentralWebservice webservice) throws IOException {
        this.webservice = webservice;
        this.config = config;
        tvmeterReader = new TVMeterReader();
    }

    @Override
    public void transform(String programUuid)
            throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        List<Relation> shardRelations = webservice.getNamedRelations(programUuid, "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasShard");
        if (shardRelations.isEmpty()) {
            // nothing to do
            return;
        }

        try {

            String shardUuid = shardRelations.get(0).getObject();
            String shardMetadata = webservice.getDatastreamContents(shardUuid, "SHARD_METADATA");
            List<Relation> fileRelations = webservice.getNamedRelations(shardUuid,
                    "http://doms.statsbiblioteket.dk/relations/default/0/1/#consistsOf");


            //get pbcore
            String pbcoreOriginal = webservice.getDatastreamContents(programUuid, "PBCORE");
            //initialise the migrator
            PBCoreMigrator pbCoreMigrator = new PBCoreMigrator(new ByteArrayInputStream(pbcoreOriginal.getBytes()));

            //get the tvmeter contents
            String tvmeterOriginal = webservice.getDatastreamContents(programUuid, "GALLUP_ORIGINAL");
            //parse it
            TvmeterProgram tvmeterStructure = tvmeterReader.readTVMeterFile(tvmeterOriginal);
            //port the info to the pbcore
            pbCoreMigrator.addTVMeterStructure(tvmeterStructure);

            //get the shardMetadataContents
            String shardMetadataContents = webservice.getDatastreamContents(shardUuid, "SHARD_METADATA");
            //parse it up
            ShardMetadata shardStructure = deserializeShardMetadata(shardMetadataContents);
            ProgramStructure programStructure = convertShardStructure(shardStructure);

            //NOW START TO CHANGE THE OBJECT

            webservice.markInProgressObject(Arrays.asList(programUuid),"Updating radio/tv datamodel");
            //relations
            for (Relation fileRelation : fileRelations) {
                fileRelation.setSubject(programUuid);
                webservice.addRelation(programUuid,fileRelation,"Updating radio/tv datamodel");
            }

        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
    
    public ShardMetadata deserializeShardMetadata(String shardMetadataString) throws JAXBException {
        
        JAXBElement<ShardMetadata> obj = (JAXBElement<ShardMetadata>) JAXBContext.newInstance(ShardMetadata.class.getPackage().getName()).createUnmarshaller().unmarshal(
                new ByteArrayInputStream(shardMetadataString.getBytes())); 
        return obj.getValue(); 
    }

    public ProgramStructure convertShardStructure(ShardMetadata shardMetadata)   
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        ProgramStructure programStructure = new ProgramStructure();
        if(shardMetadata.getShardStructure() != null) {
            ShardStructure shardStructure = shardMetadata.getShardStructure(); 
            if(shardStructure.getMissingEnd() != null) {
                MissingEnd missingEnd = new MissingEnd();
                missingEnd.setMissingSeconds(shardStructure.getMissingEnd().getMissingSeconds());
            }
            if(shardStructure.getMissingStart() != null) {
                MissingStart missingStart = new MissingStart();
                missingStart.setMissingSeconds(shardStructure.getMissingStart().getMissingSeconds());
            }
            if(!shardStructure.getHoles().getHole().isEmpty()) {
                Holes holes = new Holes();
                Iterator<Hole> it = shardStructure.getHoles().getHole().iterator();
                while(it.hasNext()) {
                    Hole shardHole = it.next();
                    dk.statsbiblioteket.doms.transformers.shardmigrator.programStructure.autogenerated.Hole programHole = 
                            new dk.statsbiblioteket.doms.transformers.shardmigrator.programStructure.autogenerated.Hole();
                    programHole.setHoleLength(shardHole.getHoleLength());
                    programHole.setFile1UUID(webservice.getFileObjectWithURL(shardHole.getFilePath1()));
                    programHole.setFile2UUID(webservice.getFileObjectWithURL(shardHole.getFilePath2()));
                    holes.getHole().add(programHole);
                }
                programStructure.setHoles(holes);
            }       
            if(!shardStructure.getOverlaps().getOverlap().isEmpty()) {
                Overlaps overlaps = new Overlaps();
                Iterator<Overlap> it = shardStructure.getOverlaps().getOverlap().iterator();
                while(it.hasNext()) {
                    Overlap shardOverlap = it.next();
                    dk.statsbiblioteket.doms.transformers.shardmigrator.programStructure.autogenerated.Overlap programOverlap = 
                            new dk.statsbiblioteket.doms.transformers.shardmigrator.programStructure.autogenerated.Overlap();
                    programOverlap.setOverlapLength(shardOverlap.getOverlapLength());
                    programOverlap.setOverlapType(shardOverlap.getOverlapType());
                    programOverlap.setFile1UUId(webservice.getFileObjectWithURL(shardOverlap.getFilePath1()));
                    programOverlap.setFile2UUID(webservice.getFileObjectWithURL(shardOverlap.getFilePath2()));
                    overlaps.getOverlap().add(programOverlap);
                }
                programStructure.setOverlaps(overlaps);
            }
        }
                
        return programStructure;
    }

}
