/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codename.services.endpoints.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.validation.constraints.NotNull;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.codename.model.Interest;
import org.codename.model.User;
import org.codename.services.api.UsersService;
import org.codename.services.endpoints.api.PublicUserEndpointService;
import org.codename.services.exceptions.ServiceException;



/**
 *
 * @author grogdj
 */
@Stateless
public class PublicUserServiceImpl implements PublicUserEndpointService {

    @Inject
    private UsersService usersService;
  
    
    private final static String serverUrl = "localhost";
    
    private final static Logger log = Logger.getLogger(PublicUserServiceImpl.class.getName());

    public PublicUserServiceImpl() {

    }

    @Override
    public Response getAll() throws ServiceException {
        List<User> users = usersService.getAll();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        
        for(User u : users){
            
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("userId", (u.getId() == null) ? "" : u.getId().toString());
            jsonObjBuilder.add("bio", (u.getBio() == null) ? "" : u.getBio());
            jsonObjBuilder.add("location", (u.getLocation() == null) ? "" : u.getLocation());
            jsonObjBuilder.add("firstname", (u.getFirstname()== null) ? "" : u.getFirstname());
            jsonObjBuilder.add("lastname", (u.getLastname()== null) ? "" : u.getLastname());
            jsonObjBuilder.add("title", (u.getTitle()== null) ? "" : u.getTitle());
            JsonArrayBuilder jsonArrayBuilder2 = Json.createArrayBuilder();
            for(Interest i : u.getInterests()){
                jsonArrayBuilder2.add(i.getName());
            }
            jsonObjBuilder.add("interests", jsonArrayBuilder2);
            jsonArrayBuilder.add(jsonObjBuilder);
            
        }
        return Response.ok(jsonArrayBuilder.build().toString()).build();
    }
    
    

    @Override
    public Response get(@PathParam("id") Long user_id) throws ServiceException {
        User u = usersService.getById(user_id);
        if (u == null) {
            throw new ServiceException("User  " + user_id + " doesn't exists");
        }
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("userId", (u.getId() == null) ? "" : u.getId().toString());
        jsonObjBuilder.add("bio", (u.getBio() == null) ? "" : u.getBio());
        jsonObjBuilder.add("location", (u.getLocation()== null) ? "" : u.getLocation());
        jsonObjBuilder.add("username", (u.getFirstname() == null) ? "" : u.getFirstname());
        jsonObjBuilder.add("lastname", (u.getLastname()== null) ? "" : u.getLastname());
        jsonObjBuilder.add("title", (u.getTitle()== null) ? "" : u.getTitle());
        JsonArrayBuilder jsonArrayBuilder2 = Json.createArrayBuilder();
            for(Interest i : u.getInterests()){
                jsonArrayBuilder2.add(i.getName());
            }
            jsonObjBuilder.add("interests", jsonArrayBuilder2);
        JsonObject jsonObj = jsonObjBuilder.build();
        return Response.ok(jsonObj.toString()).build();
    }

    @Override
    public Response getAvatar(@NotNull @PathParam("id") Long user_id) throws ServiceException {

        byte[] tmp = usersService.getAvatar(user_id);
        final byte[] avatar;
        if (tmp != null && tmp.length > 0) {
            log.info("avatar found");
            avatar = tmp;
            return Response.ok().entity(new StreamingOutput() {
                @Override
                public void write(OutputStream output)
                        throws IOException, WebApplicationException {
                    output.write(avatar);
                    output.flush();
                }
            }).build();
        } else {
            try {
                log.info("avatar not found");
                return Response.temporaryRedirect(new URI("http://"+serverUrl+"/static/img/public-images/default-avatar.png")).build();
            } catch (URISyntaxException ex) {
                Logger.getLogger(PublicUserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return Response.serverError().build();

    }
    
    @Override
    public Response getCover(@NotNull @PathParam("id") Long user_id) throws ServiceException {

        byte[] tmp = usersService.getCover(user_id);
        final byte[] avatar;
        if (tmp != null && tmp.length > 0) {
            log.info("avatar found");
            avatar = tmp;
            return Response.ok().entity(new StreamingOutput() {
                @Override
                public void write(OutputStream output)
                        throws IOException, WebApplicationException {
                    output.write(avatar);
                    output.flush();
                }
            }).build();
        } else {
            try {
                log.info("avatar not found");
                return Response.temporaryRedirect(new URI("http://"+serverUrl+"/static/img/public-images/default-cover.png")).build();
            } catch (URISyntaxException ex) {
                Logger.getLogger(PublicUserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return Response.serverError().build();

    }

}