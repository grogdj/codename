/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codename.services.endpoints.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.codename.model.User;
import org.codename.services.api.UsersService;
import org.codename.services.endpoints.api.AuthenticationEndpointService;
import org.codename.services.exceptions.ServiceException;
import org.codename.services.filters.auth.GrogAuthenticator;
import org.codename.services.filters.auth.GrogHTTPHeaderNames;
import org.codename.services.util.CodenameUtil;
import static org.codename.services.util.CodenameUtil.createToken;
import org.codename.services.util.Payload;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author grogdj
 */
@Stateless
public class AuthenticationServiceImpl implements AuthenticationEndpointService {

    @Inject
    private UsersService userService;

    @Inject
    private GrogAuthenticator authenticator;

    private Client client;

    private final static Logger log = Logger.getLogger(AuthenticationServiceImpl.class.getName());

    public static final String CLIENT_ID_KEY = "client_id", REDIRECT_URI_KEY = "redirect_uri",
            CLIENT_SECRET = "client_secret", CODE_KEY = "code", GRANT_TYPE_KEY = "grant_type",
            AUTH_CODE = "authorization_code";

    public static final String CONFLICT_MSG = "There is already a %s account that belongs to you",
            NOT_FOUND_MSG = "User not found", LOGING_ERROR_MSG = "Wrong email and/or password",
            UNLINK_ERROR_MSG = "Could not unlink %s account because it is your only sign-in method";

    public static final ObjectMapper MAPPER = new ObjectMapper();

    public AuthenticationServiceImpl() {
        client = ClientBuilder.newClient();

    }

    @Override
    public Response registerUser(@NotNull @Email @NotEmpty @FormParam("email") String email,
            @NotNull @NotEmpty @FormParam("password") String password) throws ServiceException {
        userService.newUser(new User(email, password));
        return Response.ok().build();
    }

    @Override
    public Response registerFullUser(String firstname, String lastname,
            String email, String password, String gender, String birthday) throws ServiceException {
        User user = new User(email, password);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setGender(gender);
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date result = null;
        try {
            result = df.parse(birthday);
        } catch (ParseException ex) {
            Logger.getLogger(AuthenticationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        user.setBirthday(result);
        userService.newUser(user);
        return Response.ok().build();
    }

    @Override
    public Response login(
            @Context HttpHeaders httpHeaders,
            @NotNull @Email @NotEmpty @FormParam("email") String email,
            @NotNull @NotEmpty @FormParam("password") String password) throws ServiceException {

        
        User authUser = userService.getByEmail(email);

        if (authUser == null || !authUser.getProvider().equals(User.UserProvider.FHELLOW)) {
            return getNoCacheResponseBuilder(Response.Status.UNAUTHORIZED).build();
        }
        String authToken = authenticator.login(email, password);

        boolean firstLogin = authUser.isIsFirstLogin();
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("email", email);
        
        jsonObjBuilder.add("auth_token", authToken);
        jsonObjBuilder.add("user_id", authUser.getId());
        jsonObjBuilder.add("firstLogin", firstLogin);

        JsonObject jsonObj = jsonObjBuilder.build();

        return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObj.toString()).build();

    }

    @Override
    public Response logout(
            @Context HttpHeaders httpHeaders) throws ServiceException {

        String serviceKey = httpHeaders.getHeaderString(GrogHTTPHeaderNames.SERVICE_KEY);

        String authToken = httpHeaders.getHeaderString(GrogHTTPHeaderNames.AUTH_TOKEN);

        authenticator.logout(serviceKey, authToken);

        return getNoCacheResponseBuilder(Response.Status.NO_CONTENT).build();

    }

    private Response.ResponseBuilder getNoCacheResponseBuilder(Response.Status status) {

        CacheControl cc = new CacheControl();

        cc.setNoCache(true);

        cc.setMaxAge(-1);

        cc.setMustRevalidate(true);

        return Response.status(status).cacheControl(cc);

    }

    public UsersService getUserService() {
        return userService;
    }

    public void setUserService(UsersService userService) {
        this.userService = userService;
    }

    public GrogAuthenticator getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(GrogAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @POST
    @Path("google")
    public Response loginGoogle(@Valid final Payload payload,
            @Context final HttpServletRequest request) throws ServiceException {
        final String accessTokenUrl = "https://accounts.google.com/o/oauth2/token";
        final String peopleApiUrl = "https://www.googleapis.com/plus/v1/people/me/openIdConnect";

        Response response;

        // Step 1. Exchange authorization code for access token.
        final MultivaluedMap<String, String> accessData = new MultivaluedHashMap<String, String>();
        accessData.add(CLIENT_ID_KEY, payload.getClientId());
        accessData.add(REDIRECT_URI_KEY, payload.getRedirectUri());
        accessData.add(CLIENT_SECRET, "aHdIf4d4JcNlGAEGvv7cM7WF");
        accessData.add(CODE_KEY, payload.getCode());
        accessData.add(GRANT_TYPE_KEY, AUTH_CODE);
        response = client.target(accessTokenUrl).request().post(Entity.form(accessData));
        accessData.clear();

        String accessToken = "";
        Map<String, Object> userInfo = null;
        try {
            accessToken = (String) getResponseEntity(response).get("access_token");
            System.out.println("accessToken = " + accessToken);
            response
                    = client.target(peopleApiUrl).request("text/plain")
                    .header(CodenameUtil.AUTH_HEADER_KEY, String.format("Bearer %s", accessToken)).get();

            userInfo = getResponseEntity(response);

        } catch (JsonMappingException ex) {
            Logger.getLogger(AuthenticationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(AuthenticationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        System.out.println("userInfo.get(\"sub\") = "+ userInfo.get("sub"));
        User byEmail = userService.getByEmail((String) userInfo.get("email"));
        if (byEmail == null) {
            Long newUser = userService.newUser(new User((String) userInfo.get("email"), userInfo.get("sub").toString(), 
                    User.UserProvider.GOOGLE, userInfo.get("sub").toString()));
            
            
            userService.updateBothNames(newUser, (String) userInfo.get("given_name"), (String) userInfo.get("family_name"));
            byte[] bytes = null;
            String profilePic = (String) userInfo.get("picture");
            try {
                InputStream inputStream = new URL(profilePic).openStream();

                bytes = IOUtils.toByteArray(inputStream);
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(PublicInitServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            userService.updateAvatar(newUser, profilePic, bytes);

        }
       
        String token = "";
        try {
            
            token = createToken(request.getRemoteHost(), userInfo.get("sub").toString());
        } catch (JOSEException ex) {
            Logger.getLogger(AuthenticationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

        
        JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
        jsonObjBuilder.add("token", token);

        return getNoCacheResponseBuilder(Response.Status.OK).entity(jsonObjBuilder.build()).build();


    }

    /*
     * Helper methods
     */
    private Map<String, Object> getResponseEntity(final Response response) throws JsonParseException,
            JsonMappingException, IOException {
        return MAPPER.readValue(response.readEntity(String.class),
                new TypeReference<Map<String, Object>>() {
                });
    }
    
    public Response loginExternal(HttpServletRequest request) throws ServiceException {
        try {
            User authUser = getAuthUser(request);
            if (authUser == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            
            String authToken = authenticator.loginWithExternalToken( 
                    authUser.getEmail(), 
                    CodenameUtil.getSubject(request.getHeader(CodenameUtil.AUTH_HEADER_KEY)));
            JsonObjectBuilder jsonObjBuilder = Json.createObjectBuilder();
            jsonObjBuilder.add("email", authUser.getEmail());
            jsonObjBuilder.add("auth_token", authToken);
            jsonObjBuilder.add("user_id", authUser.getId());
            jsonObjBuilder.add("firstLogin", authUser.isIsFirstLogin());
            return Response.ok().entity(jsonObjBuilder.build()).build();
        } catch (ParseException ex) {
            throw new ServiceException(ex.getMessage());
        } catch (JOSEException ex) {
            throw new ServiceException(ex.getMessage());
        }
    }

    /*
     * Helper methods
     */
    
    private User getAuthUser(HttpServletRequest request) throws ParseException, JOSEException {
        String subject = CodenameUtil.getSubject(request.getHeader(CodenameUtil.AUTH_HEADER_KEY));
        return userService.getByProviderId(subject);
    }

}
