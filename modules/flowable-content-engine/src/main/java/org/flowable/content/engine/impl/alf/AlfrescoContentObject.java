package org.flowable.content.engine.impl.alf;

import java.io.InputStream;
import org.flowable.content.api.ContentObject;


public class AlfrescoContentObject implements ContentObject {

    protected InputStream inputStream;
    protected String id;

    public AlfrescoContentObject(InputStream inputStream, String id) {
    	this.inputStream = inputStream;
        this.id = id;
    }
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getContentLength() {
        return -1L;
    }

    @Override
    public InputStream getContent() {
           
        return inputStream;
    }
}