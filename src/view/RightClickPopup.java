package view;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import controller.SelectionController;


public class RightClickPopup extends JPopupMenu implements IView{
    JMenuItem menuItem;
    Rectangle originalSelection;
    SelectionController controller;
    
    public RightClickPopup(Rectangle originalSelection, SelectionController controller) {
        this.originalSelection = originalSelection;
        this.controller = controller;
        
        menuItem = new JMenuItem("Restore original selection");
        menuItem.addActionListener(new PopupListener());
        add(menuItem);
    }
    
    class PopupListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            restoreOriginalSelection();
        }
    }

    @Override
    public void modelPropertyChange(PropertyChangeEvent evt) {
        /* 
         * Don't need to do anything here since this only cares about 
         * the original selection
         */
    }
    
    private void restoreOriginalSelection() {
      controller.changeSelectionX(originalSelection.x);
      controller.changeSelectionY(originalSelection.y);
      controller.changeSelectionWidth(originalSelection.width);
      controller.changeSelectionHeight(originalSelection.height);
  }
    
}
