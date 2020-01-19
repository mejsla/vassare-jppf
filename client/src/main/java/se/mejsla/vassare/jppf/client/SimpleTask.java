package se.mejsla.vassare.jppf.client;

import org.jppf.node.protocol.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTask extends AbstractTask<Boolean> {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(SimpleTask.class);

    private final String name;

    public SimpleTask(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        logger.info("Running task " + name + "...");

        setResult(true);

        logger.info("Running task " + name + "... done");
    }
}
