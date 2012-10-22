package silbersoft.uprint.ui.models;

import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionListener;
import silbersoft.uprint.dao.PrinterDao;

/**
 *
 * @author Matt Silbernagel
 */
public interface PrintViewListModel extends ListSelectionListener {
    
    public DefaultListModel getListModel();
    
    public void setPrinterDao(PrinterDao printerDao);
    
    public void buildList(String location);
    
    public void clearList();
    
}
