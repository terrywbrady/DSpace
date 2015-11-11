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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "results")
public class DGMashupResults
{
	private List<DGMashupResult> results = new ArrayList<DGMashupResult>();
	private long numFound;
	private int from;
	private int rows;
	private String search;

    public DGMashupResults(){
    }
    
    public DGMashupResults(long numFound, int from, int rows, String Search){
    	setNumFound(numFound);
    	setFrom(from);
    	setRows(rows);
    	setSearch(search);
    }
    
    @XmlAttribute(name = "numFound")
    public long getNumFound() {
    	return this.numFound;
    }
    
    public void setNumFound(long numFound) {
    	this.numFound = numFound;
    }
    
    @XmlAttribute(name = "from")
    public int getFrom() {
    	return this.from;
    }
    
    public void setFrom(int from) {
    	this.from = from;
    }
    
    @XmlAttribute(name = "rows")
    public int getRows() {
    	return this.rows;
    }
    
    public void setRows(int rows) {
    	this.rows = rows;
    }
    
    @XmlAttribute(name = "search")
    public String getSearch() {
    	return this.search;
    }
    
    public void setSearch(String search) {
    	this.search = search;
    }    
    
    @XmlElement(name = "result")
    public List<DGMashupResult> getResults() {
		return results;
	}

	public void setResults(List<DGMashupResult> results) {
		this.results = results;
	}

}
