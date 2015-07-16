/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codename.services.endpoints.api;

import java.io.Serializable;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codename.core.exceptions.ServiceException;

/**
 *
 * @author grogdj
 */
@Local
@Path("/query")
public interface UserQueryEndpointService extends Serializable {

    @GET
    @Path("allbylocation")
    @Produces({MediaType.APPLICATION_JSON})
    Response getAllByLocation(@NotNull @QueryParam("lon") Double lon, 
                              @NotNull @QueryParam("lat") Double lat, 
                              @QueryParam("lookingFors") String lookingFors,
                              @QueryParam("categories") String categories) 
                                throws ServiceException;
    
    @GET
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    Response getAll(@QueryParam("lookingFors") String lookingFors, @QueryParam("categories") String categories) throws ServiceException;
    
   

}
