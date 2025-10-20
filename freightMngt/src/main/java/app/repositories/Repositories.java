package app.repositories;


public class Repositories {


    private static Repositories instance;

    /**
     * The Singleton's constructor should always be private to prevent direct construction calls with the new operator.
     */

    private Repositories() {

    }

    /**
     * This is the static method that controls the access to the singleton instance.
     * On the first run, it creates a singleton object and places it into the static attribute.
     * On subsequent runs, it returns the existing object stored in the static attribute.
     */
    public static Repositories getInstance() {
        if (instance == null) {
            instance = new Repositories();
        }
        return instance;
    }
}
