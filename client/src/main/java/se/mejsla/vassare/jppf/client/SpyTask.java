package se.mejsla.vassare.jppf.client;

import org.jppf.node.protocol.AbstractTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SpyTask extends AbstractTask<String> {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(SpyTask.class);

    private final String name;

    public SpyTask(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        logger.info("Running task " + name + "...");

        setResult("Task " + name + " ran on " + getHostName());

        logger.info("Running task " + name + "... done");
    }

    private String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "<unknown>";
        }
    }
}
