package se.mejsla.vassare.jppf.client;

import org.jppf.node.protocol.AbstractTask;
import org.jppf.utils.TypedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ListFilesTask extends AbstractTask<List<String>> {

    private static final long serialVersionUID = 1L;

    private final Logger logger = LoggerFactory.getLogger(ListFilesTask.class);

    private final String name;
    private final String directory;

    public ListFilesTask(String name, String directory) {
        this.name = name;
        this.directory = directory;
    }

    @Override
    public void run() {
        logger.info("Running task {}...", name);
        TypedProperties osProperties = getNode().getSystemInformation().getOS();
        logger.info("OS name: {}, number of processors: {}, node UUID: {}",
                osProperties.getString("os.Name"), osProperties.getInt("os.AvailableProcessors"), getNode().getUuid());

        try {
            List<String> fileNames = Files.list(Paths.get(directory))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(toList());
            logger.info("Files in directory '{}': {}", directory, fileNames.toString());
            setResult(fileNames);
        } catch (IOException e) {
            logger.error("Failed to execute: " + e);
            setThrowable(e);
        }

        logger.info("Running task {}... done", name);
    }
}
