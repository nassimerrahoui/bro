package com.bro.app;

import com.bro.dao.BrotherhoodDAO;
import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import com.bro.filter.GsonProvider;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateResults;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/** Service pour gérer une relation entre deux bros **/
@Path("/brotherhood")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BrotherhoodService {

    private BrotherhoodDAO brotherhoodDAO = new BrotherhoodDAO(BroApp.getDatastore());

    /** Demande d'une brotherhood **/
    @POST
    @Path("/create")
    public Response create(List<User> users){

        User sender = users.get(0);
        User receiver = users.get(1);

        Key<Brotherhood> key = brotherhoodDAO.create(sender, receiver);

        if(key == null){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).build();
    }

    /** Accepte une brotherhood **/
    @POST
    @Path("/accept")
    public Response accept(List<User> users){

        User user = users.get(0);
        User bro = users.get(1);
        Brotherhood thisBrotherhood = brotherhoodDAO.getBrotherhood(user, bro);

        try {
            if(thisBrotherhood != null){

                UpdateResults results = brotherhoodDAO.accept(thisBrotherhood);
                return Response.status(Response.Status.OK).entity(results.getUpdatedCount()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /** Décline une brotherhood **/
    @POST
    @Path("/deny")
    public Response shutDown(List<User> users){

        User user = users.get(0);
        User bro = users.get(1);
        Brotherhood thisBrotherhood = brotherhoodDAO.getBrotherhood(user, bro);

        try {
            if(thisBrotherhood != null){

                UpdateResults results = brotherhoodDAO.deny(thisBrotherhood);
                return Response.status(Response.Status.OK).entity(results.getUpdatedCount()).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }

    /** Retourne la liste des brotherhood**/
    @GET
    @Path("/bros")
    public Response getBros(@HeaderParam("token") String token){

        List<User> bros = brotherhoodDAO.getBrotherhoods(token);

        try{
            if(!bros.isEmpty()){
                return Response.ok(bros).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        catch (Exception e){
            return Response.status(Response.Status.FORBIDDEN).build();
        }
    }
}
