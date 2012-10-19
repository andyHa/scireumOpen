package com.scireum.open.xml;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Default / Immutable implementation of {@link SAXElement}.
 *
 * @author Jason Mosser
 */
final class ImmutableSAXElement implements SAXElement{

    private final String uri;
    private final String qName;

    private ImmutableSAXElement(String uri, String qName){
        this.uri = uri;
        this.qName = qName;
    }

    @Override
    public String getURI(){
        return uri;
    }

    @Override
    public String getQName(){
        return qName;
    }

    @Override
    public String toString(){
        return "ImmutableSAXElement{uri='" + uri + "', qName='" + qName + "'}";
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        ImmutableSAXElement that = (ImmutableSAXElement)o;

        return stringsAreEqual(this.qName, that.qName) && stringsAreEqual(this.uri, that.uri);
    }

    @Override
    public int hashCode(){
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (qName != null ? qName.hashCode() : 0);
        return result;
    }

    public static SAXElement build(String uri, String qName){
        return fromCache(uri, qName); // could plug in different cache impl if needed //
    }

    private boolean stringsAreEqual(String leftString, String rightString){
        return !(leftString != null ? !leftString.equals(rightString) : rightString != null);
    }

    // this could be a bit of premature-optimization, but should greatly //
    // reduce throw-away instances of ImmutableSAXElement                //
    private static ConcurrentMap<String, ConcurrentMap<String, SAXElement>> cache =
        new ConcurrentHashMap<String, ConcurrentMap<String, SAXElement>>(5); // can increase if > 5 URI expected //

    private static SAXElement fromCache(String uri, String qName){

        ConcurrentMap<String, SAXElement> secondLevelMap;

        secondLevelMap = cache.get(uri);
        if(secondLevelMap == null){
            // have to create new, it may get thrown away //
            ConcurrentMap<String, SAXElement> newSecondLevelMap =
                new ConcurrentHashMap<String, SAXElement>(50); // can increase if > 50 elements expected //
            ConcurrentMap<String, SAXElement> previouslyCachedMap =
                cache.putIfAbsent(uri, newSecondLevelMap);
            // determine if previously cached map or new instance is used below //
            if(previouslyCachedMap != null){
                secondLevelMap = previouslyCachedMap;
            } else {
                secondLevelMap = newSecondLevelMap;
            }
        }
        // secondLevelMap is not null @ this point //

        SAXElement previouslyCachedElement;

        previouslyCachedElement = secondLevelMap.get(qName);
        if(previouslyCachedElement != null){
            return previouslyCachedElement;
        }

        // have to create new, it may get thrown away //
        SAXElement newElement = new ImmutableSAXElement(uri, qName);
        previouslyCachedElement = secondLevelMap.putIfAbsent(qName, newElement);
        // determine if previously cached or new instance is returned //
        if(previouslyCachedElement != null){
            return previouslyCachedElement;
        } else{
            return newElement;
        }
    }

    // for unit testing only //
    static boolean contains(String uri, String qName){
        ConcurrentMap<String, SAXElement> secondLevelMap = cache.get(uri);
        return secondLevelMap != null && secondLevelMap.containsKey(qName);
    }

}
