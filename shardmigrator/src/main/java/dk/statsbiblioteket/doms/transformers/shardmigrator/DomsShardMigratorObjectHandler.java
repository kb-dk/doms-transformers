package dk.statsbiblioteket.doms.transformers.shardmigrator;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.Relation;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;
import dk.statsbiblioteket.doms.transformers.common.PropertyBasedDomsConfig;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Use DOMS to handle object transformation for shard removal for a UUID.
 */
public class DomsShardMigratorObjectHandler implements ObjectHandler {

    private final PropertyBasedDomsConfig config;
    private CentralWebservice webservice;

    /**
     * Initialise object handler.
     * @param config Configuration.
     */
    public DomsShardMigratorObjectHandler(PropertyBasedDomsConfig config, CentralWebservice webservice) {
        this.webservice = webservice;
        this.config = config;
    }

    @Override
    public void transform(String uuid)
            throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        List<Relation> shardRelations = webservice.getNamedRelations(uuid, "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasShard");
        if (shardRelations.isEmpty()) {
            // nothing to do
        }
        String shardUuid = shardRelations.get(0).getObject();
        String shardMetadata = webservice.getDatastreamContents(shardUuid, "SHARD_METADATA");
        List<Relation> fileRelations = webservice.getNamedRelations(shardUuid,
                                                                    "http://doms.statsbiblioteket.dk/relations/default/0/1/#consistsOf");

        // TODO Move shardStructure to program object and replace file references with UUID references
        // TODO Add file relations to program object
        // TODO Make sure pbcore metadata contains correct info (resolvable channel mappings, real time codes)

        // TODO Update file metadata (Consider: Should files be updated in separate workflow? What happens to unreferenced files. Eek. Also, we still need to register unregistered files anyway, so we need to run through all files anyway.)
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
