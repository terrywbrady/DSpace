/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 * GUCODE[[twb27:custom module]]
 */
package org.dspace.rest;


import org.apache.log4j.Logger;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.DSpaceObject;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.factory.ContentServiceFactory;
import org.dspace.content.service.CommunityService;
import org.dspace.content.service.ItemService;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.SiteService;
import org.dspace.discovery.DiscoverQuery;
import org.dspace.discovery.DiscoverResult;
import org.dspace.discovery.SearchUtils;
import org.dspace.handle.factory.HandleServiceFactory;
import org.dspace.handle.service.HandleService;
import org.dspace.rest.common.DGMashupCollection;
import org.dspace.rest.common.DGMashupCommunity;
import org.dspace.rest.common.DGMashupRepository;
import org.dspace.rest.common.DGMashupResult;
import org.dspace.rest.common.DGMashupResults;
import org.dspace.rest.common.DGMashupResponse;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.HttpHeaders;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * This class retrieves items by a constructed metadata query evaluated against a set of Item Filters.
 * 
 * @author Terry Brady, Georgetown University
 */
@Path("/dgMashup")
public class DigitalGeorgetownMashup extends Resource {
    private static Logger log = Logger.getLogger(DigitalGeorgetownMashup.class);
    private enum Actions{getCollections,getItemsByHandle,search;}
    protected HandleService handleService = HandleServiceFactory.getInstance().getHandleService();
    protected ItemService itemService = ContentServiceFactory.getInstance().getItemService();
    protected SiteService siteService = ContentServiceFactory.getInstance().getSiteService();
    protected CommunityService communityService = ContentServiceFactory.getInstance().getCommunityService();
    protected BitstreamService bitstreamService = ContentServiceFactory.getInstance().getBitstreamService();

    /**
     * @param action
     *            getCollections|getItemsByHandle|search
     * @param itemHandles
     *            comma separated list of items to retrieve
     * @param scopeHandle
     *            Collection, Community, or Repository to search
     *            Default 10822/0
     * @param search
     *            Search string
     * @param sort
     *            score|title
     *            default is score unless scopeHandle == "10822/559388" or scopeHandle == "10822/559389"
     * @param rows
     *            Limit number of items to return
     *            Default 10
     * @param from
     *            Offset of start index in list of items of collection. Default
     *            value is 0.
     * @param headers
     *            If you want to access to collection under logged user into
     *            context. In headers must be set header "rest-dspace-token"
     *            with passed token from login method.
     * @return Return instance of collection. It can also return status code
     *         NOT_FOUND(404) if id of collection is incorrect or status code
     * @throws UnsupportedEncodingException 
     * @throws WebApplicationException
     *             It is thrown when was problem with database reading
     *             (SQLException) or problem with creating
     *             context(ContextException). It is thrown by NOT_FOUND and
     *             UNATHORIZED status codes, too.
     */
	@GET
    @Produces({MediaType.APPLICATION_XML})
    public DGMashupResponse status(
            @QueryParam("action") String action, 
            @QueryParam("itemHandles") String itemHandles, 
            @QueryParam("scopeHandle") String scopeHandle, 
    		@QueryParam("search") String search, 
    		@QueryParam("sort") String sort, 
    		@QueryParam("rows") @DefaultValue("10") Integer rows, 
    		@QueryParam("from") @DefaultValue("0") Integer from,
    		@QueryParam("userAgent") String user_agent, @QueryParam("xforwarderfor") String xforwarderfor,
    		@Context HttpHeaders headers, @Context HttpServletRequest request) throws UnsupportedEncodingException {
		
		org.dspace.core.Context context = null;
		DGMashupResponse mashupStatus = new DGMashupResponse();
		mashupStatus.setRequest(request.getQueryString());
		mashupStatus.setApiUrl("https://" + request.getServerName() + "/rest/static/reports/MashupApiDoc.html");
		
        try {
            context = createContext(getUser(headers));
            context.turnOffAuthorisationSystem();            	

    		if (Actions.getCollections.name().equals(action)) {
    			getCollections(mashupStatus, context);
    		} else if (Actions.getItemsByHandle.name().equals(action)) {
    			if (itemHandles == null) {
    			} else if (itemHandles.isEmpty()) {
    			} else {
        			DGMashupResults results = new DGMashupResults(0, from, rows, itemHandles);
        			mashupStatus.setResults(results);
        			for(String handle: itemHandles.split(",")) {
        				handle = handle.trim();
        				DSpaceObject obj = handleService.resolveToObject(context, handle);
        				if (obj == null) {
        				} else if (obj instanceof Item) {
        					addResult(results, context, (Item)obj);
        			        mashupStatus.success();
        					long numFound = results.getNumFound()+1;
        					results.setNumFound(numFound);
        					if (numFound >= rows) {
        						break;
        					}
        				}
        			}   
    			}    			
    		} else if (Actions.search.name().equals(action)) {
    			DiscoverQuery query = new DiscoverQuery();
    			query.setQuery(search);
    			query.setMaxResults(rows);
    			query.setStart(from);
    			DiscoverResult discoverResult = null;
    			if (scopeHandle != null) {
    				DSpaceObject obj = handleService.resolveToObject(context, scopeHandle.trim());
    				if (obj != null) {
    					discoverResult = SearchUtils.getSearchService().search(context, obj, query);
    				}
    			} 
    			
    			if (discoverResult == null){
    				discoverResult = SearchUtils.getSearchService().search(context, query);    				    				    				
    			}
    			
    			if (discoverResult != null) {
        			DGMashupResults results = new DGMashupResults(
           				discoverResult.getTotalSearchResults(), 
           				discoverResult.getStart(), 
           				discoverResult.getMaxResults(), 
           				search
           			); 
           			mashupStatus.setResults(results);
           			for(DSpaceObject obj: discoverResult.getDspaceObjects()) {
           				if (obj instanceof Item) {
           					addResult(results, context, (Item)obj);
           			        mashupStatus.success();
           				}
           			}
           			
           			if (results.getResults().size() == 0) {
           				mashupStatus.failure("No items found for search");
           			}
    			}
    		} else {
    			mashupStatus.failure("The purpose of this service is to permit Blackboard to navigate DigitalGeorgetown");
    		}
    		context.complete();
        } 
        catch (Exception e)
        {
        	e.printStackTrace();
            mashupStatus.failure("DG Mashup Error " + e.getMessage());
            processException("DG Mashup Error . Message: " + e.getClass() + " " + e.getMessage(), context);
        }
        finally
        {
            processFinally(context);
        }
        return mashupStatus;
    }
    
    private void addToList(Item item, List<String> list, String schema, String element, String qualifier) {
    	for(MetadataValue m: itemService.getMetadata(item, schema, element, qualifier, org.dspace.content.Item.ANY)) {
    		list.add(m.getValue());
    	}
    }
	
	private List<String> getMetadataList(Item item, String schema, String element, String qualifier) {
		List<String> list = new ArrayList<String>();
		addToList(item, list, schema, element, qualifier);
		return list;
	}

	private String getMetadata(Item item, String schema, String element, String qualifier) {
		List<String> list = getMetadataList(item, schema, element, qualifier);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	
	private void addResult(DGMashupResults results, org.dspace.core.Context context, Item item) throws SQLException {
		DGMashupResult result = new DGMashupResult();
		results.getResults().add(result);
		List<String> creator = getMetadataList(item, "dc", "contributor", "author");
		addToList(item, creator, "dc", "creator", Item.ANY);
		result.setCreator(creator);
		result.setDateCreated(getMetadata(item, "dc", "date", "created"));
		result.setDescription(getMetadataList(item, "dc", "description", null));
		result.setHandle(item.getHandle());
		String iurl = handleService.resolveToURL(context, item.getHandle());
		result.setItemUrl(iurl);
		result.setPermalink("http://hdl.handle.net/"+item.getHandle());
		result.setSubject(getMetadataList(item, "dc", "subject", Item.ANY));
		String thumbnail = null;
		if (iurl != null) {
			Bitstream bit = bitstreamService.getFirstBitstream(item, "THUMBNAIL");
			if (bit != null) {
				thumbnail = iurl.replace("handle", "bitstream/handle") + bit.getName() + "?sequence=" + bit.getSequenceID();
			}			
		}
		result.setThumbnailUrl(thumbnail);
		result.setTitle(item.getName());
	}
	
	private void getCollections(DGMashupResponse mashupStatus, org.dspace.core.Context context) throws SQLException {
		DGMashupRepository repo = new DGMashupRepository("All of DigitalGeorgetown", siteService.findSite(context).getHandle());
		mashupStatus.setRepository(repo);
		List<Community> dspaceCommunities = communityService.findAllTop(context);
		processCommunity(repo, dspaceCommunities);
		mashupStatus.success();
	}
	
	private void processCommunity(DGMashupCommunity parent, List<Community> communities) throws SQLException {
		if (communities == null){
			return;
		}
		if (communities.size() == 0) {
			return;
		}
		List<DGMashupCommunity> parentComms = new ArrayList<DGMashupCommunity>();
		parent.setCommunities(parentComms);
		for(Community comm: communities) {
			DGMashupCommunity dgcomm = new DGMashupCommunity(comm.getName(), comm.getHandle());
			parentComms.add(dgcomm);
			List<Collection> colls = comm.getCollections();
			if (colls.size() > 0) {
				List<DGMashupCollection> myColls = new ArrayList<DGMashupCollection>();
				dgcomm.setCollections(myColls);
				for(Collection coll: colls) {
					DGMashupCollection dgcoll = new DGMashupCollection(coll.getName(), coll.getHandle());
					myColls.add(dgcoll);
				}
			}
			processCommunity(dgcomm, comm.getSubcommunities());
		}		
		
	}
}
