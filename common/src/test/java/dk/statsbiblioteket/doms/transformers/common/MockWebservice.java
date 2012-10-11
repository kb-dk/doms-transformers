package dk.statsbiblioteket.doms.transformers.common;

import dk.statsbiblioteket.doms.central.CentralWebservice;
import dk.statsbiblioteket.doms.central.InvalidCredentialsException;
import dk.statsbiblioteket.doms.central.InvalidResourceException;
import dk.statsbiblioteket.doms.central.MethodFailedException;
import dk.statsbiblioteket.doms.central.ObjectProfile;
import dk.statsbiblioteket.doms.central.RecordDescription;
import dk.statsbiblioteket.doms.central.Relation;
import dk.statsbiblioteket.doms.central.SearchResult;
import dk.statsbiblioteket.doms.central.User;
import dk.statsbiblioteket.doms.central.ViewBundle;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MockWebservice implements CentralWebservice {


    Map<String, MockObject> objects = new HashMap<String, MockObject>();

    Map<String, String> urlLookupTable = new HashMap<String, String>();

    @Override
    public String newObject(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "oldID", targetNamespace = "") List<String> oldID, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject mockObject = new MockObject();
        mockObject.pid = "uuid:" + UUID.randomUUID().toString();
        objects.put(mockObject.pid, mockObject);
        return mockObject.pid;
    }

    @Override
    public ObjectProfile getObjectProfile(@WebParam(name = "pid", targetNamespace = "") String pid) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject object = objects.get(pid);
        if (object == null) {
            throw new InvalidResourceException("sdf", "sdf");
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
        if (object == null) {
            throw new InvalidResourceException("sdf", "sdf");
        }
        if (!object.writable) {
            throw new InvalidCredentialsException("sdfdsf", "sdfdsf");
        }
        object.label = name;
    }

    @Override
    public void deleteObject(@WebParam(name = "pids", targetNamespace = "") List<String> pids, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void markPublishedObject(@WebParam(name = "pids", targetNamespace = "") List<String> pids, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        for (String pid : pids) {
            MockObject object = objects.get(pid);
            if (object == null) {
                throw new InvalidResourceException("sdf", "sdf");
            }
            object.writable = false;
        }
    }

    @Override
    public void markInProgressObject(@WebParam(name = "pids", targetNamespace = "") List<String> pids, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        for (String pid : pids) {
            MockObject object = objects.get(pid);
            if (object == null) {
                throw new InvalidResourceException("sdf", "sdf");
            }
            object.writable = true;
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void modifyDatastream(@WebParam(name = "pid", targetNamespace = "") String pid,
                                 @WebParam(name = "datastream", targetNamespace = "") String datastream, @WebParam(name = "contents", targetNamespace = "") String contents, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject object = objects.get(pid);
        if (object == null) {
            throw new InvalidResourceException("sdf", "sdf");
        }
        if (!object.writable) {
            throw new InvalidCredentialsException("sdfdsf", "sdfdsf");
        }

        object.datastreams.put(datastream, contents);
    }

    @Override
    public String getDatastreamContents(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "datastream", targetNamespace = "") String datastream) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject object = objects.get(pid);
        if (object == null) {
            throw new InvalidResourceException("sdf", "sdf");
        }
        datastream = object.datastreams.get(datastream);
        if (datastream == null) {
            throw new InvalidResourceException("sdf", "sdf");
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
        if (object == null) {
            throw new InvalidResourceException("sdf", "sdf");
        }
        if (!object.writable) {
            throw new InvalidCredentialsException("sdfdsf", "sdfdsf");
        }
        object.label = permanentURL;
        urlLookupTable.put(permanentURL, pid);
    }

    @Override
    public String getFileObjectWithURL(@WebParam(name = "URL", targetNamespace = "") String url)
            throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        String pid = urlLookupTable.get(url);
        if (pid == null) {
            throw new InvalidResourceException("sfds","sdfdsf");
        }
        return pid;
    }

    @Override
    public void addRelation(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "relation", targetNamespace = "") Relation relation, @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject object = objects.get(pid);
        if (object == null){
            throw new InvalidResourceException("sdfs","Dfsd");
        }
        Collection<Relation> relations = object.relations;
        Relation copy = new Relation();
        copy.setSubject(relation.getSubject());
        copy.setLiteral(relation.isLiteral());
        copy.setObject(relation.getObject());
        copy.setPredicate(relation.getPredicate());
        relations.add(copy);
    }

    @Override
    public List<Relation> getRelations(@WebParam(name = "pid", targetNamespace = "") String pid) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        MockObject object = objects.get(pid);
        if (object == null){
            throw new InvalidResourceException("sdfs","Dfsd");
        }
        Collection<Relation> relations = object.relations;
        List<Relation> output = new ArrayList<Relation>();
        for (Relation relation : relations) {
            Relation copy = new Relation();
            copy.setSubject(relation.getSubject());
            copy.setLiteral(relation.isLiteral());
            copy.setObject(relation.getObject());
            copy.setPredicate(relation.getPredicate());
            output.add(copy);
        }
        return output;
    }

    @Override
    public List<Relation> getNamedRelations(@WebParam(name = "pid", targetNamespace = "") String pid, @WebParam(name = "predicate", targetNamespace = "") String predicate) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {
        List<Relation> relations = getRelations(pid);
        List<Relation> output = new ArrayList<Relation>();
        for (Relation relation : relations) {
            if (relation.getPredicate().equals(predicate)){
                output.add(relation);
            }
        }
        return output;
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
    public void deleteRelation(@WebParam(name = "pid", targetNamespace = "") String pid,
                               @WebParam(name = "relation", targetNamespace = "") Relation relation,
                               @WebParam(name = "comment", targetNamespace = "") String comment) throws InvalidCredentialsException, InvalidResourceException, MethodFailedException {

        MockObject mockobject = objects.get(pid);

        for (Relation relation1 : mockobject.relations) {
            if (relation1.getObject().equals(relation.getObject())
                    && relation1.getSubject().equals(relation.getSubject()) &&
                    relation1.getPredicate().equals(relation.getPredicate())                     ){
                mockobject.relations.remove(relation1);
                break;
            }
        }
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
