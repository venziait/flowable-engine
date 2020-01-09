package org.flowable.content.engine.impl.alf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.content.api.ContentObject;
import org.flowable.content.api.ContentStorage;
import org.flowable.content.api.ContentStorageException;
import org.flowable.content.engine.ContentEngineConfiguration;
import org.flowable.content.engine.impl.fs.FileSystemContentObject;
import org.json.JSONObject;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;


public class SimpleAlfrescoContentStorage implements ContentStorage {
	
	public static final String STORE_NAME = "alfresco";

	private String alfrescoUserName;

	private String alfrescoPassword;
	

	private HttpHeaders headers;


	public SimpleAlfrescoContentStorage(ContentEngineConfiguration contentEngineConfiguration) {
		
		AlfrescoUrlUtil.setBaseServer(contentEngineConfiguration.getAlfrescoUrl());
		this.alfrescoPassword = contentEngineConfiguration.getAlfrescoPassword();
		this.alfrescoUserName = contentEngineConfiguration.getAlfrescoUserName();
		
		headers = createHeaders(this.alfrescoUserName, this.alfrescoPassword); 
		
	}

	@Override
	public ContentObject createContentObject(InputStream contentStream, Map<String, Object> metaData) {
		
		RestTemplate restTemplate = new RestTemplate();

		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		 
	    String randomUUIDString =  UUID.randomUUID().toString();
	    
		body.add("filedata", new MultipartInputStreamFileResource(contentStream,randomUUIDString));
		body.add("name", randomUUIDString );
		body.add("nodeType", "cm:content");
		body.add("relativePath", "Flowable Attachments" );

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		

		ResponseEntity<String> response = restTemplate.postForEntity(AlfrescoUrlUtil.getSaveNodeUrl(), requestEntity, String.class);
		
		if(response.getStatusCode() == HttpStatus.CREATED) {
			JSONObject nodeJSON = new JSONObject(response.getBody());
			String nodeId = nodeJSON.getJSONObject("entry").getString("id");
			return new AlfrescoContentObject(contentStream, nodeId);
			
		}else {
			
			 throw new ContentStorageException("Error while updating content in Alfresco Repo " + AlfrescoUrlUtil.baseServer);
		}
		
		

	}

	@Override
	public ContentObject updateContentObject(String id, InputStream contentStream, Map<String, Object> metaData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentObject getContentObject(String id) {
		RestTemplate restTemplate = new RestTemplate();
		 
         headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
         HttpEntity<String> entity = new HttpEntity<>(headers);
         ResponseEntity<byte[]> response = restTemplate.exchange(AlfrescoUrlUtil.getContentNodeUrl(id), HttpMethod.GET, entity, byte[].class);
         
         File ret;
		try {
			ret = File.createTempFile("download", "tmp");
			 StreamUtils.copy(response.getBody(), new FileOutputStream(ret));
	         return new FileSystemContentObject(ret,id);
		} catch (IOException e) {
			throw new ContentStorageException("Error while download content in Alfresco Repo " + AlfrescoUrlUtil.baseServer);
		}
	}

	@Override
	public Map<String, Object> getMetaData() {
		  // Currently not yet supported
        return null;
	}

	@Override
	public void deleteContentObject(String id) {
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
		ResponseEntity<String> response = restTemplate.exchange(AlfrescoUrlUtil.getDeleteNodeUrl(id), HttpMethod.DELETE, requestEntity, String.class);
		if(response.getStatusCode() != HttpStatus.NO_CONTENT) {
			throw new ContentStorageException("Error while delete content in Alfresco Repo " + AlfrescoUrlUtil.baseServer);
		}
	}

	@Override
	public String getContentStoreName() {
		return "alfresco";
	}
	

	private HttpHeaders createHeaders(String username, String password) {
		return new HttpHeaders() {
			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				set("Authorization", authHeader);
			}
		};
	}
	
	/**
     * Works with {@link ResourceHttpMessageConverterHandlingInputStreams} to forward input stream from
     * file-uploads without reading everything into memory.
     *
     */
    public class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }
        @Override
        public String getFilename() {
        	try {
				return super.getFile().getName();
			} catch (IOException e) {
				return filename;
			}
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // we do not want to generally read the whole stream into memory ...
        }
    }


}
