package dk.statsbiblioteket.doms.transformers.common;

import dk.statsbiblioteket.doms.central.Relation;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: 7/17/12
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MockObject {

    String pid,label;

    Map<String,String> datastreams = new HashMap<String, String> ();

    boolean writable = true;

    Set<Relation> relations = new HashSet<Relation>();

}
