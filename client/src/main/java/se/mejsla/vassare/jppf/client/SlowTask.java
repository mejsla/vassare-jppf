package se.mejsla.vassare.jppf.client;

import org.jppf.node.protocol.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlowTask extends AbstractTask<Boolean> {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(SlowTask.class);

    private final String name;

    public SlowTask(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        logger.info("Running task " + name + "...");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        setResult(true);

        logger.info("Running task " + name + "... done");
    }
}
