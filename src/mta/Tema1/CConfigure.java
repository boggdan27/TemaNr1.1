package mta.Tema1;

public class CConfigure{
    private static CConfigure instance_config = null;


    int threads_nr;
    int delay;
    String root_director;
    int log_level;

    public CConfigure() {
    }

    public static CConfigure getInstance(){
        if(instance_config == null) {
            instance_config = new CConfigure();
        }
        return instance_config;
    }


    public int getThreads_nr() {
        return threads_nr;
    }

    public int getDelay() {
        return delay;
    }

    public String getRoot_director() {
        return root_director;
    }

    public int getLog_level() {
        return log_level;
    }
}
