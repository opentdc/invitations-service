/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Arbalo AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.opentdc.invitations;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

// import io.swagger.annotations.*;


import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.opentdc.service.GenericService;
import org.opentdc.service.exception.DuplicateException;
import org.opentdc.service.exception.InternalServerErrorException;
import org.opentdc.service.exception.NotFoundException;
import org.opentdc.service.exception.ValidationException;

/*
 * @author Bruno Kaiser
 */
@Path("/api/invitation")
// @Api(value = "/api/invitation", description = "Operations about invitations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class InvitationsService extends GenericService<ServiceProvider> {
	
	private static ServiceProvider sp = null;

	private static final Logger logger = Logger.getLogger(InvitationsService.class.getName());

	/**
	 * Invoked for each service invocation (Constructor)
	 * @throws ReflectiveOperationException
	 */
	public InvitationsService(
		@Context ServletContext context
	) throws ReflectiveOperationException{
		logger.info("> InvitationsService()");
		if (sp == null) {
			sp = this.getServiceProvider(InvitationsService.class, context);
		}
		logger.info("InvitationsService() initialized");
	}

	/******************************** invitations *****************************************/
	@GET
	@Path("/")
//	@ApiOperation(value = "Return a list of all invitations", response = List<InvitationModel>.class)
	public List<InvitationModel> list(
		@DefaultValue(DEFAULT_QUERY_TYPE) @QueryParam("queryType") String queryType,
		@DefaultValue(DEFAULT_QUERY) @QueryParam("query") String query,
		@DefaultValue(DEFAULT_POSITION) @QueryParam("position") int position,
		@DefaultValue(DEFAULT_SIZE) @QueryParam("size") int size
	) {
		return sp.list(queryType, query, position, size);
	}
	
	/**
	 * Return statistical information about the service
	 * @return the statistical information in the form of a list of properties
	 */
	@GET
	@Path("/statistics")
	public Properties statistics() {
		return sp.statistics();
	}

	@POST
	@Path("/")
	//	@ApiOperation(value = "Create a new invitation", response = InvitationModel.class)
	//	@ApiResponses(value = 
	//			{ @ApiResponse(code = 409, message = "An object with the same id exists already (CONFLICT)") },
	//			{ @ApiResponse(code = 400, message = "Invalid ID supplied or mandatory field missing (BAD_REQUEST)" })
	public InvitationModel create(
		InvitationModel invitation) 
	throws DuplicateException, ValidationException {
		return sp.create(invitation);
	}

	@GET
	@Path("/{id}")
	//	@ApiOperation(value = "Find a invitation by id", response = InvitationModel.class)
	//	@ApiResponses(value = { 
	//			@ApiResponse(code = 405, message = "An object with the given id was not found (NOT_FOUND)" })
	public InvitationModel read(
		@PathParam("id") String id
	) throws NotFoundException {
		return sp.read(id);
	}

	@PUT
	@Path("/{id}")
	//	@ApiOperation(value = "Update the invitation with id with new values", response = InvitationModel.class)
	//	@ApiResponses(value =  
	//			{ @ApiResponse(code = 405, message = "An object with the given id was not found (NOT_FOUND)" },
	//			{ @ApiResponse(code = 400, message = "Invalid new values given or trying to change immutable fields (BAD_REQUEST)" })
	public InvitationModel update(
		@PathParam("id") String id,
		InvitationModel invitation
	) throws NotFoundException, ValidationException {
		return sp.update(id, invitation);
	}

	/**
	 * @param id
	 * @throws NotFoundException
	 * @throws InternalServerErrorException
	 */
	@DELETE
	@Path("/{id}")
	//	@ApiOperation(value = "Delete the invitation with the given id" )
	//	@ApiResponses(value =  
	//			{ @ApiResponse(code = 405, message = "An object with the given id was not found (NOT_FOUND)" },
	//			{ @ApiResponse(code = 500, message = "Data inconsistency found (INTERNAL_SERVER_ERROR)" })
	public void delete(
		@PathParam("id") String id) 
	throws NotFoundException, InternalServerErrorException {
		sp.delete(id);
	}
	
	/**
	 * Retrieve the message of an invitation (mostly for testing purposes).
	 * @param id the invitation 
	 * @return the invitation message 
	 * @throws NotFoundException
	 * @throws InternalServerErrorException
	 */
	@GET
	@Path("/{id}/message")
	public String getMessage(
			@PathParam("id") String id)
		throws NotFoundException, InternalServerErrorException {
		return sp.getMessage(id);
	}
	
	/**
	 * Send an invitation message by email.
	 * @param id the invitation to send
	 * @throws NotFoundException
	 * @throws InternalServerErrorException
	 */
	@POST
	@Path("/{id}/send")
	public void sendMessage(
			@PathParam("id") String id)
		throws NotFoundException, InternalServerErrorException {
		sp.sendMessage(id);
	}
	
	/**
	 * Send all messages to all receivers of invitations being in state INITIAL.
	 * @throws InternalServerErrorException
	 */
	@POST
	@Path("/sendall")
	public void sendAllMessages()
		throws InternalServerErrorException {
		sp.sendAllMessages();
	}
	
	/**
	 * Migrate the data from EventsService to InvitationService (temporary only).
	 * @throws InternalServerErrorException
	 */
	@POST
	@Path("/migrate")
	public void migrate()
		throws InternalServerErrorException {
		sp.migrate();
	}
	
	/**
	 * Register the attendance of an invitation.
	 * @param id the invitation to register for
	 * @param comment a comment
	 * @throws NotFoundException if no such invitation could be found
	 * @throws ValidationException if the invitation was in a wrong state
	 */
	@PUT
	@Path("/{id}/register")
	public void register(
			@PathParam("id") String id,
			String comment)
		throws NotFoundException, ValidationException {
		sp.register(id, comment);
	}
	
	/**
	 * Cancel an existing registration.
	 * @param id the invitation to deregister
	 * @param comment a comment
	 * @throws NotFoundException if no such invitation could be found
	 * @throws ValidationException if the invitation was in a wrong state
	 */
	@PUT
	@Path("/{id}/deregister")
	public void deregister(
			@PathParam("id") String id,
			String comment)
		throws NotFoundException, ValidationException {
		sp.deregister(id, comment);
	}	
}
