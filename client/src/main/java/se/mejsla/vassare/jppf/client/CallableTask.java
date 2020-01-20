package se.mejsla.vassare.jppf.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.Callable;

public class CallableTask implements Callable<Boolean>, Serializable {

    private final String name;

    public CallableTask(String name) {
        this.name = name;
    }

    @Override
    public Boolean call() throws Exception {
        Logger logger = LoggerFactory.getLogger(CallableTask.class);

        logger.info("Running task " + name + "...");
        boolean result = name.startsWith("Mejsla");
        logger.info("Running task " + name + "... done");

        return result;
    }
}
