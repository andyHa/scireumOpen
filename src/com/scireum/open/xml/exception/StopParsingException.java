package com.scireum.open.xml.exception;

import org.xml.sax.SAXException;

/**
 * Subclass of {@link SAXException} that indicates parser should not continue traversing
 * content.
 *
 * @author Jason Mosser
 */
public final class StopParsingException extends SAXException{

    public StopParsingException(){
        super("Stop Parsing Requested");
    }

}
