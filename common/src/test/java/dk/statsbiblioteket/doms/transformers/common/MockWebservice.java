package dk.statsbiblioteket.doms.transformers.common;

import dk.statsbiblioteket.doms.central.*;

import javax.jws.WebParam;
import java.lang.String;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MockWebservice implements CentralWebservice{


    Map<String,MockObject> objects = new HashMap<String, MockObject>();
    @Override
    public String newObject(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "oldID", targetNamespace = "") List<String> oldID, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject mockObject = new MockObject();
        mockObject.pid = "uuid:"+UUID.randomUUID().toString();
        objects.put(mockObject.pid,mockObject);
        return mockObject.pid;
    }

    @Override
    public ObjectProfile getObjectProfile(@WebParam(name = "pid", targetNamespace = "") String pid) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject object = objects.get(pid);
        if (object == null){
            throw new InvalidResourceException("sdf","sdf");
        }
        ObjectProfile profile = new ObjectProfile();
        profile.setTitle(object.label);
        profile.setPid(object.pid);
        return profile;
    }

    @Override
    public void setObjectLabel(@WebParam(name = "pid", targetNamespace = "") String pid,
                               @WebParam(name = "name", targetNamespace = "") String name, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject object = objects.get(pid);
        if (object == null){
            throw new InvalidResourceException("sdf","sdf");
        }
        if (!object.writable){
            throw new InvalidCredentialsException("sdfdsf","sdfdsf");
        }
        object.label =  name;
    }

    @Override
    public void deleteObject(@WebParam(name = "pids", targetNamespace = "") List<String> pids, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void markPublishedObject(@WebParam(name = "pids", targetNamespace = "") List<String> pids, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        for (String pid : pids) {
            MockObject object = objects.get(pid);
            if (object == null){
                throw new InvalidResourceException("sdf","sdf");
            }
            object.writable = false;
        }
    }

    @Override
    public void markInProgressObject(@WebParam(name = "pids", targetNamespace = "") List<String> pids, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        for (String pid : pids) {
            MockObject object = objects.get(pid);
            if (object == null){
                throw new InvalidResourceException("sdf","sdf");
            }
            if (!object.writable){
                throw new InvalidCredentialsException("sdfdsf","sdfdsf");
            }
            object.writable = true;
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void modifyDatastream(@WebParam(name = "pid", targetNamespace = "") String pid,
                                 @WebParam(name = "datastream", targetNamespace = "") String datastream, @WebParam(name = "contents", targetNamespace = "") String contents, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject object = objects.get(pid);
        if (object == null){
            throw new InvalidResourceException("sdf","sdf");
        }
        if (!object.writable){
            throw new InvalidCredentialsException("sdfdsf","sdfdsf");
        }
        datastream = object.datastreams.get(datastream);
        object.datastreams.put(datastream,contents);
    }

    @Override
    public String getDatastreamContents(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "datastream", targetNamespace = "") String datastream) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject object = objects.get(pid);
        if (object == null){
            throw new InvalidResourceException("sdf","sdf");
        }
        if (!object.writable){
            throw new InvalidCredentialsException("sdfdsf","sdfdsf");
        }
        datastream = object.datastreams.get(datastream);
        if (datastream == null){
            throw new InvalidResourceException("sdf","sdf");
        }
        return datastream;

    }

    @Override
    public void addFileFromPermanentURL(@WebParam(name = "pid", targetNamespace = "") String pid,
                                        @WebParam(name = "filename", targetNamespace = "") String filename,
                                        @WebParam(name = "md5sum", targetNamespace = "") String md5Sum,
                                        @WebParam(name = "permanentURL", targetNamespace = "") String permanentURL,
                                        @WebParam(name = "formatURI", targetNamespace = "") String formatURI,
                                        @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
         MockObject object = objects.get(pid);
        if (object == null){
            throw new InvalidResourceException("sdf","sdf");
        }
        if (!object.writable){
            throw new InvalidCredentialsException("sdfdsf","sdfdsf");
        }
        object.label = permanentURL;
    }

    @Override
    public String getFileObjectWithURL(@WebParam(name = "URL", targetNamespace = "") String url) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        throw new IllegalAccessError();
    }

    @Override
    public void addRelation(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "relation", targetNamespace = "") Relation relation, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //TODO
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Relation> getRelations(@WebParam(name = "pid", targetNamespace = "") String pid) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Relation> getNamedRelations(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "predicate", targetNamespace = "") String predicate) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Relation> getInverseRelations(@WebParam(name = "pid", targetNamespace = "") String pid) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Relation> getInverseRelationsWithPredicate(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "predicate", targetNamespace = "") String predicate) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //TODO
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteRelation(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "relation", targetNamespace = "") Relation relation, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //TODO
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ViewBundle getViewBundle(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "ViewAngle", targetNamespace = "") String viewAngle) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        throw new IllegalAccessError();
    }

    @Override
    public List<RecordDescription> getIDsModified(@WebParam(name = "since", targetNamespace = "") long since, @WebParam(name = "collectionPid", targetNamespace = "") String collectionPid, @WebParam(name = "viewAngle", targetNamespace = "") String viewAngle, @WebParam(name = "state", targetNamespace = "") String state, @WebParam(name = "offset", targetNamespace = "") Integer offset, @WebParam(name = "limit", targetNamespace = "") Integer limit) throws InvalidCredentialsException, MethodFailedException {
        throw new IllegalAccessError();
    }

    @Override
    public long getLatestModified(@WebParam(name = "collectionPid", targetNamespace = "") String collectionPid, @WebParam(name = "viewAngle", targetNamespace = "") String viewAngle, @WebParam(name = "state", targetNamespace = "") String state) throws InvalidCredentialsException, MethodFailedException {
        throw new IllegalAccessError();
    }

    @Override
    public List<String> findObjectFromDCIdentifier(@WebParam(name = "string", targetNamespace = "") String string) throws InvalidCredentialsException, MethodFailedException {
        throw new IllegalAccessError();
    }

    @Override
    public List<SearchResult> findObjects(@WebParam(name = "query", targetNamespace = "") String query, @WebParam(name = "offset", targetNamespace = "") int offset, @WebParam(name = "pageSize", targetNamespace = "") int pageSize) throws InvalidCredentialsException, MethodFailedException {
        throw new IllegalAccessError();
    }

    @Override
    public void lockForWriting() throws InvalidCredentialsException, MethodFailedException {
        throw new IllegalAccessError();
    }

    @Override
    public void unlockForWriting() throws InvalidCredentialsException, MethodFailedException {
        throw new IllegalAccessError();
    }

    @Override
    public User createTempAdminUser(@WebParam(name = "username", targetNamespace = "") String username, @WebParam(name = "roles", targetNamespace = "") List<String> roles) throws InvalidCredentialsException, MethodFailedException {
        throw new IllegalAccessError();
    }

    @Override
    public List<String> getObjectsInCollection(@WebParam(name = "collectionPid", targetNamespace = "") String collectionPid, @WebParam(name = "contentModelPid", targetNamespace = "") String contentModelPid) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        throw new IllegalAccessError();
    }
}
