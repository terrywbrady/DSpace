/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.common;

import org.codehaus.jackson.annotate.JsonProperty;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Used to handle/determine status of REST API.
 * Mainly to know your authentication status
 *
 */
@XmlRootElement(name = "service")
public class DGMashupResponse
{
	
	private DGMashupStatus status;
    private String apiUrl = "my-url";
    private String description;
    private String request;
    
    private DGMashupRepository repository;
    private DGMashupResults results;

    public DGMashupResponse() {
    	status = new DGMashupStatus();
    	status.setSuccess(false);
    }

    public void success() {
    	status.setSuccess(true);
    }

    public void failure(String note) {
    	status.setSuccess(false);
    	setDescription(note);
    }

    public DGMashupStatus getStatus() {
        return status;
    }

    public void setStatus(DGMashupStatus status) {
        this.status = status;
    }
    
    @JsonProperty("api-url")
    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }


    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DGMashupRepository getRepository() {
        return repository;
    }

    public void setRepository(DGMashupRepository repository) {
        this.repository = repository;
    }

    public DGMashupResults getResults() {
        return results;
    }

    public void setResults(DGMashupResults results) {
        this.results = results;
    }

}
