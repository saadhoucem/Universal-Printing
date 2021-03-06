package co.silbersoft.uprint.ui.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import org.apache.log4j.Logger;

import co.silbersoft.uprint.lib.utils.R;
import co.silbersoft.uprint.ui.StatusBar;
import co.silbersoft.uprint.ui.dao.PrinterDao;
import co.silbersoft.uprint.ui.domain.Location;


/**
 *
 * @author Matt Silbernagel
 */
public class BuildingListModel implements PrintViewListModel {

    public BuildingListModel(PrintViewListModel printerListModel) {
        this.printerListModel = printerListModel;
        buildingListModel = new DefaultListModel();
    }

    @Override
    public void buildList(String l) {
        log.debug("Getting Building List...");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                StatusBar.setStatus(R.getString("status.text.getLocations"));
            }
        });
        locations = new ArrayList<Location>();
        log.debug("Entrys in the locations list = " + locations.size());
        locations = printerDao.getBuildings();
        log.debug("Entrys in the locations list after call to printDao = " + locations.size());
        if (locations.isEmpty()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    StatusBar.setStatus(R.getString("status.text.noprinters"));
                }
                
            });
        } else {
            Collections.sort(locations);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    log.debug("clearing the list");
                    buildingListModel.clear();
                    for (Location l : locations) {
                        log.debug("adding " + l.getName());
                        buildingListModel.addElement(l.getName());
                    }
                    StatusBar.setStatus(R.getString("status.text.ready"));
                }
            });
        }
    }

    @Override
    public void clearList() {
        printerDao.reset();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                buildingListModel.clear();
            }
        });
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
            log.debug("Value Changed in Building list");
            // Clear the printer list and disable to print button
            printerListModel.clearList();
            JList list = (JList) lse.getSource();
            String building = (String) list.getSelectedValue();
            printerListModel.buildList(building);
        }
    }

    public void setPrinterDao(PrinterDao printerDao) {
        this.printerDao = printerDao;
    }

    @Override
    public DefaultListModel getListModel() {
        return buildingListModel;
    }


    private static final Logger log = Logger.getLogger(BuildingListModel.class);
    private PrinterDao printerDao;
    private static List<Location> locations;
    private static DefaultListModel buildingListModel;
    private PrintViewListModel printerListModel;
}
