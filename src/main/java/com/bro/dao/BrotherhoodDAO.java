package com.bro.dao;

import com.bro.entity.Brotherhood;
import com.bro.entity.User;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BrotherhoodDAO extends BasicDAO<Brotherhood, ObjectId> {

    public BrotherhoodDAO(Datastore ds) {
        super(ds);
    }

    /**
     * Creates a brotherhood between two users
     *
     * @param sender   user that sends request trying to make a broship with receiver
     * @param receiver user that receive a broship invitation
     * @return
     */
    public Key<Brotherhood> create(User sender, User receiver) {

        Optional<User> S = getDatastore().createQuery(User.class)
                .field("username").equal(sender.getUsername())
                .asList().stream().findAny();

        Optional<User> R = getDatastore().createQuery(User.class)
                .field("username").equal(receiver.getUsername())
                .asList().stream().findAny();

        if (R.isPresent() && S.isPresent()) {

            Query<Brotherhood> query_brotherhood = getDatastore().find(Brotherhood.class);
            query_brotherhood.or(
                    query_brotherhood.and(
                            query_brotherhood.criteria("sender").equal(S.get()),
                            query_brotherhood.criteria("receiver").equal(R.get())
                    ),
                    query_brotherhood.and(
                            query_brotherhood.criteria("sender").equal(R.get()),
                            query_brotherhood.criteria("receiver").equal(S.get())
                    )
            );

            if (query_brotherhood.asList().isEmpty()) {
                Brotherhood brotherhood = new Brotherhood(S.get(), R.get());
                return save(brotherhood);
            }
        }
        return null;
    }

    /**
     * Gets brotherhood related to user associated to given token
     *
     * @param user user token
     * @param bro  brotherhood id
     * @return Brotherhood or null
     */
    public Brotherhood getBrotherhood(User user, User bro) {

        Optional<User> u = getDatastore().createQuery(User.class)
                .field("username").equal(user.getUsername())
                .asList().stream().findAny();

        Optional<User> b = getDatastore().createQuery(User.class)
                .field("username").equal(bro.getUsername())
                .asList().stream().findAny();

        if (u.isPresent() && b.isPresent()) {
            Query<Brotherhood> query_brotherhood = getDatastore().find(Brotherhood.class);
            query_brotherhood.and(
                    query_brotherhood.or(
                            query_brotherhood.criteria("sender").equal(u.get()),
                            query_brotherhood.criteria("receiver").equal(u.get())),
                    query_brotherhood.or(
                            query_brotherhood.criteria("sender").equal(b.get()),
                            query_brotherhood.criteria("receiver").equal(b.get()))
            );
            return query_brotherhood.get();
        }
        return null;
    }

    /**
     * Accept a brotherhood request, they will become bro forever
     *
     * @param brotherhood a brotherhood
     * @return UpdateResults
     */
    public UpdateResults accept(Brotherhood brotherhood) {
        Query<Brotherhood> query = getDatastore().find(Brotherhood.class);
        query.field("_id").equal(brotherhood.getId());

        UpdateOperations<Brotherhood> ops = getDatastore()
                .createUpdateOperations(Brotherhood.class)
                .set("brolationship", Brotherhood.Brolationship.ACCEPTED);
        return update(query, ops);
    }

    /**
     * Deny a brotherhood
     *
     * @param brotherhood a brotherhood
     * @return UpdateResults
     */
    public void deny(Brotherhood brotherhood) {
        Query<Brotherhood> query = getDatastore().find(Brotherhood.class);
        query.field("_id").equal(brotherhood.getId());

        WriteResult ops = getDatastore().delete(query);
        deleteByQuery(query);
    }

    /**
     * Gets all brotherhoods related to a given token associated to an user
     *
     * @param user an user
     * @return List<User>
     */
    public List<User> getBrotherhoods(User user) {
        Query<Brotherhood> query_brotherhoods = getDatastore().find(Brotherhood.class);
        query_brotherhoods.or(
                query_brotherhoods.criteria("sender").equal(user),
                query_brotherhoods.criteria("receiver").equal(user));
        List<User> bros = new ArrayList<>();
        if (!query_brotherhoods.asList().isEmpty()) {
            for (Brotherhood b : query_brotherhoods.asList()) {
                    if (b.getSender().getUsername().equals(user.getUsername())) {
                        bros.add(b.getReceiver());
                    } else {
                        bros.add(b.getSender());
                    }
            }
        }
        return bros;
    }


    /**
     * Gets all brotherhoods related to a given user for a brolationship
     * See Brotherhood class for more information
     *
     * @param user an user
     * @return List<User>
     */
    public List<User> getBrotherhoods(User user, Brotherhood.Brolationship brolationship) {
        Query<Brotherhood> query_brotherhoods = getDatastore().find(Brotherhood.class);
        query_brotherhoods.or(
                query_brotherhoods.criteria("sender").equal(user),
                query_brotherhoods.criteria("receiver").equal(user));
        List<User> bros = new ArrayList<>();
        if (!query_brotherhoods.asList().isEmpty()) {
            for (Brotherhood b : query_brotherhoods.asList()) {
                if (b.getBrolationship() == brolationship) {
                    if (b.getSender().getUsername().equals(user.getUsername())) {
                        bros.add(b.getReceiver());
                    } else {
                        bros.add(b.getSender());
                    }
                }
            }
        }
        return bros;
    }

    /**
     * Gets all bro who aren't in a brotherhood for a given user
     *
     * @param user an user
     * @return list of users
     */
    public List<User> getNotBro(User user){
        List<User> users = getDatastore().find(User.class).asList();
        users.removeAll(getBrotherhoods(user));
        return users;
    }


}
