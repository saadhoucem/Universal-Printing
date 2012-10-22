package silbersoft.uprint.service;

import com.typesafe.config.Config;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.simoes.lpd.common.PrintJob;
import org.simoes.lpd.handler.HandlerInterface;
import org.simoes.util.FileUtil;
import silbersoft.uprint.ui.PrintView;
import silbersoft.uprint.ui.models.PrintButtonModel;
import silbersoft.uprint.ui.models.PrintViewListModel;

/**
 *
 * @author Matt Silbernagel
 */
public class UCITHandler implements HandlerInterface {

    static Logger log = Logger.getLogger(UCITHandler.class);

    public UCITHandler(Config config) {
        super();
        this.config = config;
    }

    public UCITHandler(Config config, PrintButtonModel printButton, PrintViewListModel buildingList, PrintView printerView) {
        this.config = config;
        this.buildingList = buildingList;
        this.printButton = printButton;
        this.printerView = printerView;
    }

    /**
     * Writes the printJob to disk using the specified jobName
     *
     * @param printJob the PrintJob we are processing
     * @return the result of our work, true for success or false for non-success
     */
    @Override
    public boolean process(PrintJob printJob) {
        final String METHOD_NAME = "process(): ";
        final File CREATE_DIR = new File(getTempDir());
        boolean result = false;
        if (null != printJob
                && null != printJob.getControlFile()
                && null != printJob.getDataFile()) {
            if(!CREATE_DIR.exists()){
                if(!CREATE_DIR.mkdirs()){
                    log.error("unable to create the tmp directory " + CREATE_DIR);
                    return result;
                }
            }
            String user = printJob.getOwner();

            // create file name, pjb == print job
            String unique = UUID.randomUUID().toString();
            String name = unique + "." + printJob.getControlFile().getJobNumber() + ".pjb";
            File fileName = new File(CREATE_DIR.getAbsolutePath() + File.separator + name);
            fileName.deleteOnExit();
            try {
                FileUtil.writeFile(printJob.getDataFile().getContents(), fileName.getAbsolutePath());
                result = true;
            } catch (IOException e) {
                log.error(METHOD_NAME + e.getMessage());
                return result;
            }
            log.debug("setting filename in the printButtonModel to " + fileName.getAbsolutePath());
            printButton.setCurrentFile(fileName.getAbsolutePath());
            try {
                printButton.setJobName(URLEncoder.encode(printJob.getName(), "UTF-16LE"));
            } catch (UnsupportedEncodingException ex) {
                printButton.setJobName(name);
            }
            printerView.showFrame();
            buildingList.buildList("all");
        } else {
            log.error(METHOD_NAME + "The printJob or printJob.getControlFile() or printJob.getDataFile() were empty");
        }
        return result;
    }
    
    private String getTempDir() {
        String tmpDir = config.getString("java.io.tmpdir");
        return tmpDir + File.separator + "uprint" + File.separator + "jobs" + File.separator;
    }
    private Config config;
    private PrintButtonModel printButton;
    private PrintView printerView;
    private PrintViewListModel buildingList;
    final String JOB_DIR = "/jobs/";
}
