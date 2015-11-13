/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.common;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement(name = "community")
public class DGMashupCommunity extends DGMashupObject
{
	private List<DGMashupCommunity> communities = new ArrayList<DGMashupCommunity>();
	private List<DGMashupCollection> collections = new ArrayList<DGMashupCollection>();

    public DGMashupCommunity(){
    }
    
    public DGMashupCommunity(String name, String handle){
    	super(name, handle);
    }
	
    @JsonProperty("community")
    public List<DGMashupCommunity> getCommunities() {
		return communities;
	}

	public void setCommunities(List<DGMashupCommunity> communities) {
		this.communities = communities;
	}

    @JsonProperty("collections")
    public List<DGMashupCollection> getCollections() {
		return collections;
	}

	public void setCollections(List<DGMashupCollection> collections) {
		this.collections = collections;
	}
}
