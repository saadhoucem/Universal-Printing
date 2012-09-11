package silbersoft.uprint.service;

import com.typesafe.config.Config;
import java.io.IOException;
import java.util.UUID;
import org.apache.log4j.Logger;
import org.simoes.lpd.common.PrintJob;
import org.simoes.lpd.handler.HandlerInterface;
import org.simoes.util.FileUtil;
import silbersoft.ucprint.ui.PrintView;
import silbersoft.ucprint.ui.models.PrintButtonModel;
import silbersoft.ucprint.ui.models.PrintViewListModel;

/**
 *
 * @author silbermm
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

        final String CREATE_DIR = getTempDir();
        boolean result = false;
        if (null != printJob
                && null != printJob.getControlFile()
                && null != printJob.getDataFile()) {

            String user = printJob.getOwner();

            // create file name, pjb == print job
            String unique = UUID.randomUUID().toString();
            String name = unique + "." + printJob.getControlFile().getJobNumber() + ".pjb";
            String fileName = CREATE_DIR.toString() + "/" + name;
            try {
                FileUtil.writeFile(printJob.getDataFile().getContents(), fileName);
                result = true;
            } catch (IOException e) {
                log.error(METHOD_NAME + e.getMessage());
            }
            printButton.setCurrentFile(fileName);            
            printerView.showFrame();
            buildingList.buildList("all");
                        
            // Now do we need to run an external program?
            //if (config.getBoolean("application.postexec")) {
            //    runAs(config.getString("application.postexec"), user, fileName);
            //}
        } else {
            log.error(METHOD_NAME + "The printJob or printJob.getControlFile() or printJob.getDataFile() were empty");
        }
        return result;
    }
    
    private String getTempDir() {
        String dir = "";
        if (System.getProperty("os.name").startsWith("Win")) {
            String drive = "c:";
            if (System.getProperty("systemdrive") == null) {
                drive = "c:";
            }
            dir = drive + "/Windows/Temp/ucit/wirelessprinting/" + JOB_DIR;
        } else {
            dir = "/tmp/ucit/wirelessprinting/" + JOB_DIR;
        }
        return dir;
    }
    private Config config;
    private PrintButtonModel printButton;
    private PrintView printerView;
    private PrintViewListModel buildingList;
    final String JOB_DIR = "/jobs/";
}
