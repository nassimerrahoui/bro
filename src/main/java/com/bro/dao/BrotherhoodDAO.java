package com.bro.dao;

import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.List;
import java.util.Optional;


public class BrotherhoodDAO extends BasicDAO<Brotherhood, ObjectId> {

    public BrotherhoodDAO(Datastore ds) { super(ds); }

    /** Demande d'une brotherhood **/
    public Key<Brotherhood> create(User sender, User receiver) {

        Optional<User> S = getDatastore().createQuery(User.class)
                .field("username").equal(sender.getUsername())
                .asList().stream().findAny();

        Optional<User> R = getDatastore().createQuery(User.class)
                .field("username").equal(receiver.getUsername())
                .asList().stream().findAny();

        if(R.isPresent() && S.isPresent()) {
            Brotherhood brotherhood = new Brotherhood(S.get(), R.get());
            return save(brotherhood);
        }
        return null;
    }

    /** Retourne une brotherhood pour un user et l'id d'un brotherhood **/
    public Brotherhood getBrotherhood(String token, String id) {

        Optional<User> user = getDatastore().createQuery(User.class)
                .field("token").equal(token)
                .asList().stream().findAny();

        if(user.isPresent()) {
            ObjectId objectId = new ObjectId(id);
            Query<Brotherhood> query_brotherhood = getDatastore().find(Brotherhood.class);
            query_brotherhood.field("_id").equal(objectId);
            query_brotherhood.or(
                    query_brotherhood.criteria("sender").equal(user.get()),
                    query_brotherhood.criteria("receiver").equal(user.get()));

            return query_brotherhood.get();
        }
        return null;
    }

    /** Accepte une brotherhood **/
    public UpdateResults accept(Brotherhood brotherhood){
        Query<Brotherhood> query = getDatastore().find(Brotherhood.class);
        query.field("_id").equal(brotherhood.getId());

        UpdateOperations<Brotherhood> ops = getDatastore()
                .createUpdateOperations(Brotherhood.class)
                .set("brolationship", Brotherhood.Brolationship.ACCEPTED);
        return update(query, ops);
    }

    /** Décline une brotherhood **/
    public UpdateResults deny(Brotherhood brotherhood){
        Query<Brotherhood> query = getDatastore().find(Brotherhood.class);
        query.field("_id").equal(brotherhood.getId());

        UpdateOperations<Brotherhood> ops = getDatastore()
                .createUpdateOperations(Brotherhood.class)
                .set("brolationship", Brotherhood.Brolationship.DENIED);
        return update(query, ops);
    }

    /** Retourne la liste des brotherhoods **/
    public List<Brotherhood> getBrotherhoods(String token) {

         Optional<User> user = getDatastore().createQuery(User.class)
         .field("token").equal(token)
         .asList().stream().findAny();

         if(user.isPresent()) {
             Query<Brotherhood> query_brotherhoods = getDatastore().find(Brotherhood.class);
             query_brotherhoods.or(
                 query_brotherhoods.criteria("sender").equal(user.get()),
                 query_brotherhoods.criteria("receiver").equal(user.get()));

             return query_brotherhoods.asList();
         }
         return null;
    }

    /** Vérifie si le user dont le token correspond est le sender dans une brotherhood **/
    public boolean isSender(Brotherhood brotherhood, String token){
        Optional<User> user = getDatastore().createQuery(User.class)
                .field("token").equal(token)
                .asList().stream().findAny();

        if(user.isPresent()) {
            Query<Brotherhood> sender = getDatastore().find(Brotherhood.class);
            sender.field("sender").equal(user.get());

            return sender.asList().isEmpty();
        }
        return false;
    }
}
