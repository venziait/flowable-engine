/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.spring.boot.content;

import org.flowable.spring.boot.FlowableServlet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Properties for configuring the content engine.
 *
 * @author Filip Hrisafov
 */
@ConfigurationProperties(prefix = "flowable.content")
public class FlowableContentProperties {

    /**
     * Whether the content engine needs to be started.
     */
    private boolean enabled = true;

    /**
     * The servlet configuration for the Content Rest API.
     */
    @NestedConfigurationProperty
    private final FlowableServlet servlet = new FlowableServlet("/content-api", "Flowable Content Rest API");

    /**
     * The storage properties for the content configuration.
     */
    @NestedConfigurationProperty
    private final Storage storage = new Storage();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public FlowableServlet getServlet() {
        return servlet;
    }

    public Storage getStorage() {
        return storage;
    }
    

    /**
     * The storage configuration for the content engine.
     */
    public static class Storage {

        /**
         * Type of content storage engine
         */
        private String type = "file";
        
        private String alfrescoUrl;
    	
    	private String alfrescoUsername;

    	private String alfrescoPassword;

 
        /**
         * The storage properties for the file system content configuration.
         */
        @NestedConfigurationProperty
        private final File file = new File();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
        
        public File getFile() {
            return file;
        }
        
        public String getAlfrescoUrl() {
    		return alfrescoUrl;
    	}

    	public void setAlfrescoUrl(String alfrescoUrl) {
    		this.alfrescoUrl = alfrescoUrl;
    	}


    	public String getAlfrescoUsername() {
    		return alfrescoUsername;
    	}

    	public void setAlfrescoUsername(String alfrescoUsername) {
    		this.alfrescoUsername = alfrescoUsername;
    	}

    	public String getAlfrescoPassword() {
    		return alfrescoPassword;
    	}

    	public void setAlfrescoPassword(String alfrescoPassword) {
    		this.alfrescoPassword = alfrescoPassword;
    	}

        
        /**
         * The file storage configuration for the content engine.
         */
        public static class File {

            /**
             * Root folder location where content files will be stored, for example, task attachments or form file uploads.
             */
            private String rootFolder;

            /**
             * If the root folder doesn't exist, should it be created?
             */
            private boolean createRoot = true;

            public String getRootFolder() {
                return rootFolder;
            }

            public void setRootFolder(String rootFolder) {
                this.rootFolder = rootFolder;
            }

            public boolean getCreateRoot() {
                return createRoot;
            }

            public void setCreateRoot(Boolean createRoot) {
                this.createRoot = createRoot;
            }
        }
    }
}
