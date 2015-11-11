/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.common;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "object")
public class DGMashupObject
{
    private String name;
    private String handle;

    public DGMashupObject(){
    }
    
    public DGMashupObject(String name, String handle){
    	setName(name);
    	setHandle(handle);
    }
        
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }
}
