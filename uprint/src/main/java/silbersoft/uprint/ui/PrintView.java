package silbersoft.uprint.ui;

import javax.swing.Action;
import silbersoft.uprint.ui.models.PrintViewListModel;

/**
 * @author Matt Silbernagel
 */
public interface PrintView {

    public void showFrame();
    public void setPrintList();
    
    public void setPrintModel(Action printAction);
    public void setCancelModel(Action cancelAction);
    public void setAboutModel(Action aboutUsModel);
    public void setGetHelpModel(Action getHelpAction);
    public void setPrinterListModel(PrintViewListModel printListModel);
    public void setBuildingListModel(PrintViewListModel buildingListModel);
    
}
