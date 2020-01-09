package org.flowable.content.engine.impl.alf;


public class AlfrescoUrlUtil {

	public static  String baseServer;
	
	public static  String baseNodeService = "/api/-default-/public/alfresco/versions/1/nodes/";

	public static  String upload = "/children";


	public static String getSaveNodeUrl(String parentId) {
		return baseServer + baseNodeService + parentId + upload;
	}
	
	public static String getSaveNodeUrl() {
		return getSaveNodeUrl("-shared-");
		
	}
	
	public static String getUdateNodeUrl(String nodeId) {
		return baseServer +  baseNodeService + nodeId;
	}
	
	public static String getContentNodeUrl(String nodeId) {
		return baseServer +  baseNodeService + nodeId + "/content";
	}

	public static String getBaseServer() {
		return baseServer;
	}

	public static void setBaseServer(String baseServer) {
		AlfrescoUrlUtil.baseServer = baseServer;
	}

	public static String getDeleteNodeUrl(String nodeId) {
		return baseServer + baseNodeService + nodeId;
	}

	
}
