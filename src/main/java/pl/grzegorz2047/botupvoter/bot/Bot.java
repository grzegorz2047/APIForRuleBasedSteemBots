package pl.grzegorz2047.botupvoter.bot;

public interface Bot {
    boolean init();

    boolean shutdown();

    boolean checkStatus();

    void printAllActions();
}
