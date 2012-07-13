package dk.statsbiblioteket.doms.transformers.shardremover;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.CentralWebserviceService;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.Relation;
import dk.statsbiblioteket.doms.transformers.common.DomsConfig;
import dk.statsbiblioteket.doms.transformers.common.ObjectHandler;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Use DOMS to handle object transformation for shard removal for a UUID.
 */
public class DomsShardRemoverObjectHandler implements ObjectHandler {

    private final DomsConfig config;
    private CentralWebservice webservice;

    /**
     * Initialise object handler.
     * @param config Configuration.
     */
    public DomsShardRemoverObjectHandler(DomsConfig config, CentralWebservice webservice) {
        this.config = config;
        this.webservice = webservice;
    }

    @Override
    public void transform(String uuid)
            throws InvalidCredentialsException, MethodFailedException, InvalidResourceException {
        List<Relation> shardRelations = webservice.getNamedRelations(uuid, "http://doms.statsbiblioteket.dk/relations/default/0/1/#hasShard");
        if (shardRelations.isEmpty()) {
            // nothing to do
        }
        String shardUuid = shardRelations.get(0).getObject();

        //TODO Add UUID to program object as extra ID (add as Identifier element in DC datastream)
        //TODO Remove relation to shard
        //TODO Remove shard object

        throw new UnsupportedOperationException("Not implemented yet");
    }

}
