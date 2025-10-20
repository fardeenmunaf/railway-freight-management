package app;


import app.repositories.Repositories;

/**
 * Bootstrap class to initialize the application with sample data
 */
public class Bootstrap implements Runnable {

    @Override
    public void run() {
        Repositories repositories = Repositories.getInstance();
    }
}