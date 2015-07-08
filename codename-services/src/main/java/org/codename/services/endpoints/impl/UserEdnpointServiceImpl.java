/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codename.services.endpoints.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.io.IOUtils;

import org.codename.model.User;

import org.codename.core.api.UsersService;

import org.codename.services.endpoints.api.UserEndpointService;
import static org.codename.services.endpoints.impl.UsersHelper.createFullJsonUser;
import org.codename.core.exceptions.ServiceException;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author grogdj
 */
@Stateless
public class UserEdnpointServiceImpl implements UserEndpointService {

    @Inject
    private UsersService usersService;

    private final static Logger log = Logger.getLogger(UserEdnpointServiceImpl.class.getName());

    private final String UPLOADED_FILE_PARAMETER_NAME = "file";

    public UserEdnpointServiceImpl() {

    }

    

    @Override
    public Response get(@PathParam("id") Long user_id) throws ServiceException {
        User u = usersService.getById(user_id);
        if (u == null) {
            throw new ServiceException("User " + user_id + " doesn't exists");
        }
        JsonObjectBuilder jsonUserObjectBuilder = createFullJsonUser(u);
        JsonObject jsonObj = jsonUserObjectBuilder.build();
        return Response.ok(jsonObj.toString()).build();
    }
    
    
    @Override
    public Response getAll() throws ServiceException {
        List<User> users = usersService.getAll();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (User u : users) {
            JsonObjectBuilder jsonUserObjectBuilder = createFullJsonUser(u);
            jsonArrayBuilder.add(jsonUserObjectBuilder);
        }
        return Response.ok(jsonArrayBuilder.build().toString()).build();
    }

    @Override
    public Response getAllLive() throws ServiceException {
        List<User> users = usersService.getAllLive();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        for (User u : users) {
            JsonObjectBuilder jsonUserObjectBuilder = createFullJsonUser(u);
            jsonArrayBuilder.add(jsonUserObjectBuilder);
        }
        return Response.ok(jsonArrayBuilder.build().toString()).build();
    }
    
    @Override
    public Response exist(@NotNull @FormParam("user_id") Long user_id) throws ServiceException {
        return Response.ok(usersService.exist(user_id)).build();
    }

    @Override
    public Response update(@NotNull @PathParam("user_id") Long user_id,
            @FormParam("firstname") String firstname,
            @FormParam("lastname") String lastname,
            @FormParam("location") String location,
            @FormParam("bio") String bio,
            @FormParam("title") String title) throws ServiceException {
        usersService.update(user_id, firstname, lastname, location, bio, title);
        return Response.ok().build();

    }

    public Response updateInterests(@NotNull @PathParam("user_id") Long user_id, @FormParam("interests") String interests) throws ServiceException {
        log.info("Storing from the database: (" + user_id + ") " + interests);
        if (interests != null) {
            JsonReader reader = Json.createReader(new ByteArrayInputStream(interests.getBytes()));
            JsonArray array = reader.readArray();
            reader.close();

            List<String> interestsList = new ArrayList<String>(array.size());

            if (array != null) {

                for (int i = 0; i < array.size(); i++) {
                    log.info("Interest[" + i + "]: " + array.getString(i));

                    interestsList.add(array.getString(i));
                }

            }

            usersService.updateInterests(user_id, interestsList);
        }

        return Response.ok().build();
    }

    @Override
    public Response uploadAvatar(@NotNull @PathParam("id") Long user_id, MultipartFormDataInput input) throws ServiceException {
        log.info(">>>> sit back - starting file upload for user_id..." + user_id);

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get(UPLOADED_FILE_PARAMETER_NAME);

        for (InputPart inputPart : inputParts) {
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String filename = getFileName(headers);

            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                byte[] bytes = IOUtils.toByteArray(inputStream);

                log.log(Level.INFO, ">>> File '''{'{0}'}''' has been read, size: #'{'{1}'}' bytes", new Object[]{filename, bytes.length});
                usersService.updateAvatar(user_id, filename, bytes);

            } catch (IOException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        }
        return Response.ok().build();
    }

    @Override
    public Response uploadCover(@NotNull @PathParam("id") Long user_id, MultipartFormDataInput input) throws ServiceException {
        log.info(">>>> sit back - starting file upload for user_id..." + user_id);

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get(UPLOADED_FILE_PARAMETER_NAME);

        for (InputPart inputPart : inputParts) {
            MultivaluedMap<String, String> headers = inputPart.getHeaders();
            String filename = getFileName(headers);

            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);

                byte[] bytes = IOUtils.toByteArray(inputStream);

                log.log(Level.INFO, ">>> File '''{'{0}'}''' has been read, size: #'{'{1}'}' bytes", new Object[]{filename, bytes.length});
                usersService.updateCover(user_id, filename, bytes);

            } catch (IOException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
            }
        }
        return Response.ok().build();
    }

    @Override
    public Response removeAvatar(@NotNull @PathParam("id") Long user_id) throws ServiceException {
        usersService.removeAvatar(user_id);
        return Response.ok().build();
    }

    @Override
    public Response removeCover(@NotNull @PathParam("id") Long user_id) throws ServiceException {
        usersService.removeCover(user_id);
        return Response.ok().build();
    }

    @Override
    public Response getAvatar(@NotNull @PathParam("id") Long user_id) throws ServiceException {
        final byte[] avatar = usersService.getAvatar(user_id);
        return Response.ok().entity(new StreamingOutput() {
            @Override
            public void write(OutputStream output)
                    throws IOException, WebApplicationException {
                output.write(avatar);
                output.flush();
            }
        }).build();
    }

    /**
     * Extract filename from HTTP heaeders.
     *
     * @param headers
     * @return
     */
    private String getFileName(MultivaluedMap<String, String> headers) {
        String[] contentDisposition = headers.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                String finalFileName = sanitizeFilename(name[1]);
                return finalFileName;
            }
        }
        return "unknown";
    }

    private String sanitizeFilename(String s) {
        return s.trim().replaceAll("\"", "");
    }

    @Override
    public Response updateFirstLogin(Long user_id) throws ServiceException {
        usersService.updateFirstLogin(user_id);
        return Response.ok().build();
    }

    @Override
    public Response updateFirstName(Long user_id, String firstname) throws ServiceException {
        usersService.updateFirstName(user_id, firstname);
        return Response.ok().build();
    }

    @Override
    public Response updateNickName(Long user_id, String nickname) throws ServiceException {
        usersService.updateNickName(user_id, nickname);
        return Response.ok().build();
    }

    @Override
    public Response updateLastName(Long user_id, String lastname) throws ServiceException {
        usersService.updateLastName(user_id, lastname);
        return Response.ok().build();
    }

    @Override
    public Response updateLocation(Long user_id, String location) throws ServiceException {
        usersService.updateLocation(user_id, location);
        return Response.ok().build();
    }

    @Override
    public Response updateBio(Long user_id, String bio) throws ServiceException {
        usersService.updateBio(user_id, bio);
        return Response.ok().build();
    }

    @Override
    public Response updateTitle(Long user_id, String title) throws ServiceException {
        usersService.updateTitle(user_id, title);
        return Response.ok().build();
    }

    public Response updateBothNames(Long user_id, String firstname, String lastname) throws ServiceException {
        usersService.updateBothNames(user_id, firstname, lastname);
        return Response.ok().build();
    }
    
    public Response updateBioLongBioIams(Long user_id, String bio, String longbio, String iams) throws ServiceException {
        List<String> iAmsList = null;
        if (iams != null) {
            JsonReader reader = Json.createReader(new ByteArrayInputStream(iams.getBytes()));
            JsonArray array = reader.readArray();
            reader.close();

            if (array != null) {
                iAmsList = new ArrayList<String>(array.size());
                for (int i = 0; i < array.size(); i++) {
                    log.info("I am [" + i + "]: " + array.getString(i));
                    iAmsList.add(array.getString(i));
                }
                
            }

        }
        usersService.updateBioLongBioIams(user_id, bio, longbio, iAmsList);
        return Response.ok().build();
    }

    public Response updateOriginallyFrom(Long user_id, String originallyfrom) throws ServiceException {
        usersService.updateOriginallyFrom(user_id, originallyfrom);
        return Response.ok().build();
    }

    public Response updateLookingFor(Long user_id, String lookingfor) throws ServiceException {
        log.info("Storing from the database: (" + user_id + ") " + lookingfor);
        System.out.println("Looking Fors here: " + lookingfor);
        if (lookingfor != null) {
            JsonReader reader = Json.createReader(new ByteArrayInputStream(lookingfor.getBytes()));
            JsonArray array = reader.readArray();
            reader.close();

            if (array != null) {
                List<String> lookingForList = new ArrayList<String>(array.size());
                for (int i = 0; i < array.size(); i++) {
                    log.info("Looking For[" + i + "]: " + array.getString(i));
                    lookingForList.add(array.getString(i));
                }
                usersService.updateLookingFor(user_id, lookingForList);
            }

        }

        return Response.ok().build();
    }

    public Response updateCategories(Long user_id, String categories) throws ServiceException {
        log.info("Storing from the database: (" + user_id + ") " + categories);
        if (categories != null) {
            JsonReader reader = Json.createReader(new ByteArrayInputStream(categories.getBytes()));
            JsonArray array = reader.readArray();
            reader.close();

            if (array != null) {
                List<String> categoriesList = new ArrayList<String>(array.size());
                for (int i = 0; i < array.size(); i++) {
                    log.info("Category [" + i + "]: " + array.getString(i));
                    categoriesList.add(array.getString(i));
                }
                usersService.updateCategories(user_id, categoriesList);
            }

        }

        return Response.ok().build();
    }

    public Response updateLongBio(Long user_id, String longbio) throws ServiceException {
        usersService.updateLongBio(user_id, longbio);
        return Response.ok().build();
    }

    public Response updateLive(Long user_id, String live) throws ServiceException {
        System.out.println("Updating profile live? "+ live);
        usersService.updateLive(user_id, live);
        return Response.ok().build();
    }

    public Response updateIam(Long user_id, String iams) throws ServiceException {
        log.info("Storing from the database: (" + user_id + ") " + iams);
        if (iams != null) {
            JsonReader reader = Json.createReader(new ByteArrayInputStream(iams.getBytes()));
            JsonArray array = reader.readArray();
            reader.close();

            if (array != null) {
                List<String> iAmsList = new ArrayList<String>(array.size());
                for (int i = 0; i < array.size(); i++) {
                    log.info("I am [" + i + "]: " + array.getString(i));
                    iAmsList.add(array.getString(i));
                }
                usersService.updateIams(user_id, iAmsList);
            }

        }

        return Response.ok().build();
    }

    public Response updateTwitter(Long user_id, String twitter) throws ServiceException {
        usersService.updateTwitter(user_id, twitter);
        return Response.ok().build();
    }

    public Response updateWebsite(Long user_id, String website) throws ServiceException {
        usersService.updateWebsite(user_id, website);
        return Response.ok().build();
    }

    public Response updateLinkedin(Long user_id, String linkedin) throws ServiceException {
        usersService.updateLinkedin(user_id, linkedin);
        return Response.ok().build();
    }
    
    

}