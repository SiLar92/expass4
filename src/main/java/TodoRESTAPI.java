import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import com.google.gson.Gson;
import entities.Todo;

import static spark.Spark.*;


public class TodoRESTAPI {

    private static final String PERSISTENCE_UNIT_NAME = "todos";
    private static EntityManagerFactory factory;

    public static void main(String[] args) {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager manager = factory.createEntityManager();
        port(8080);

        after((req, res) -> { res.type("application/json"); });

        // CREATE
        post("/todos", (req, res) -> {
            Todo make = new Gson().fromJson(req.body(), Todo.class);
            manager.getTransaction().begin();
            manager.persist(make);
            manager.getTransaction().commit();
            return make.toJson();
        });

        // READ
        get("/todos/:todoid", (req, res) -> {
            manager.getTransaction().begin();
            Todo item = manager.find(Todo.class, new Long(req.params(":todoid")));
            manager.getTransaction().commit();
            return item.toJson();
        });

        // UPDATE
        put("/todos", (req, res) -> {
            Todo updated = new Gson().fromJson(req.body(), Todo.class);
            manager.getTransaction().begin();
            manager.merge(updated);
            manager.getTransaction().commit();
            return updated.toJson();
        });

        // DELETE
        delete("/todos", (req, res) -> {
            Todo delete = new Gson().fromJson(req.body(), Todo.class);
            manager.getTransaction().begin();
            delete = manager.find(Todo.class, delete.getId());
            manager.remove(delete);
            manager.getTransaction().commit();
            return delete.toJson();
        });
    }
}