/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codename.services.endpoints.api;

import java.io.Serializable;
import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codename.core.exceptions.ServiceException;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

/**
 *
 * @author grogdj
 */
@Local
@Path("/users")
public interface UserEndpointService extends Serializable {

    @GET
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    Response getAll() throws ServiceException;

    @GET
    @Path("alllive")
    @Produces({MediaType.APPLICATION_JSON})
    Response getAllLive() throws ServiceException;

    @Path("{id}/avatar/upload")
    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    Response uploadAvatar(@NotNull @PathParam("id") Long user_id, MultipartFormDataInput input) throws ServiceException;

    @Path("{id}/cover/upload")
    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    Response uploadCover(@NotNull @PathParam("id") Long user_id, MultipartFormDataInput input) throws ServiceException;

    @Path("{id}/avatar")
    @GET
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    Response getAvatar(@NotNull @PathParam("id") Long user_id) throws ServiceException;

    @Path("{id}/avatar/remove")
    @POST
    Response removeAvatar(@NotNull @PathParam("id") Long user_id) throws ServiceException;

    @Path("{id}/cover/remove")
    @POST
    Response removeCover(@NotNull @PathParam("id") Long user_id) throws ServiceException;

    @Path("{id}/exist")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    Response exist(@NotNull @PathParam("id") Long user_id) throws ServiceException;

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    Response get(@PathParam("id") Long user_id) throws ServiceException;

    @Path("{id}/firstlogin/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateFirstLogin(@NotNull @PathParam("id") Long user_id) throws ServiceException;

    @Path("{id}/live/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateLive(@NotNull @PathParam("id") Long user_id, @FormParam("live") String live) throws ServiceException;

    @Path("{id}/firstname/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateFirstName(@NotNull @PathParam("id") Long user_id, @FormParam("firstname") String firstname) throws ServiceException;

    @Path("{id}/nickname/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateNickName(@NotNull @PathParam("id") Long user_id, @FormParam("nickname") String nickname) throws ServiceException;

    @Path("{id}/twitter/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateTwitter(@NotNull @PathParam("id") Long user_id, @FormParam("twitter") String twitter) throws ServiceException;

    @Path("{id}/website/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateWebsite(@NotNull @PathParam("id") Long user_id, @FormParam("website") String website) throws ServiceException;

    @Path("{id}/linkedin/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateLinkedin(@NotNull @PathParam("id") Long user_id, @FormParam("linkedin") String linkedin) throws ServiceException;

    @Path("{id}/bothnames/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateBothNames(@NotNull @PathParam("id") Long user_id, @FormParam("firstname") String firstname,
            @FormParam("lastname") String lastname) throws ServiceException;

    @Path("{id}/biolongbioiams/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateBioLongBioIams(@NotNull @PathParam("id") Long user_id, @FormParam("bio") String bio,
            @FormParam("longbio") String longbio, @FormParam("iams") String iams) throws ServiceException;

    @Path("{id}/lastname/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateLastName(@NotNull @PathParam("id") Long user_id,
            @FormParam("lastname") String lastname) throws ServiceException;

    @Path("{id}/location/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateLocation(@NotNull @PathParam("id") Long user_id, @FormParam("location") String location,
            @FormParam("lon") Double lon, @FormParam("lat") Double lat) throws ServiceException;

    @Path("{id}/originallyfrom/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateOriginallyFrom(@NotNull @PathParam("id") Long user_id, @FormParam("originallyfrom") String originallyfrom) throws ServiceException;

    @Path("{id}/lookingforandiams/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateLookingForAndIams(@NotNull @PathParam("id") Long user_id, @FormParam("lookingfor") String lookingfor, @FormParam("iams") String iams) throws ServiceException;

    @Path("{id}/categories/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateCategories(@NotNull @PathParam("id") Long user_id, @FormParam("categories") String categories) throws ServiceException;


    @Path("{id}/bio/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateBio(@NotNull @PathParam("id") Long user_id, @FormParam("bio") String bio) throws ServiceException;

    @Path("{id}/longbio/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateLongBio(@NotNull @PathParam("id") Long user_id, @FormParam("longbio") String longbio) throws ServiceException;


    @Path("{id}/interests/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateInterests(@NotNull @PathParam("id") Long user_id, @FormParam("interests") String interests) throws ServiceException;

    
    @Path("{id}/advice/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateAdvice(@NotNull @PathParam("id") Long user_id, @FormParam("advice") String advice) throws ServiceException;
    
    @Path("{id}/hobbies/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateHobbies(@NotNull @PathParam("id") Long user_id, @FormParam("hobbies") String hobbies) throws ServiceException;
    
    @Path("{id}/resources/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateResources(@NotNull @PathParam("id") Long user_id, @FormParam("resources") String resources) throws ServiceException;
    
    @Path("{id}/share/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateShare(@NotNull @PathParam("id") Long user_id, @FormParam("share") String share) throws ServiceException;
    
    @Path("{id}/messageme/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateMessageMe(@NotNull @PathParam("id") Long user_id, @FormParam("messageme") String messageme) throws ServiceException;
    
    @Path("{id}/jobtitle/update")
    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    @Produces({MediaType.APPLICATION_JSON})
    Response updateJobTitle(@NotNull @PathParam("id") Long user_id, @FormParam("jobtitle") String jobtitle) throws ServiceException;
}
