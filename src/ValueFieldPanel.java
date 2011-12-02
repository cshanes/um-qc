import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;


public abstract class ValueFieldPanel extends JPanel {
    
    private List<JLabel> labels;
    private List<JFormattedTextField> textFields;
    
    public ValueFieldPanel(List<JLabel> labels, List<JFormattedTextField> textFields) {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        this.labels = labels;
        this.textFields = textFields;
        
        //add labels
        c.ipadx = 5;
        for(int row = 0; row < labels.size(); row++) {
            c.gridy = row;
            this.add(labels.get(row), c);
        }
        
        //add text fields
        c.gridx = 1;
        c.ipadx = 20;
        for(int row = 0; row < textFields.size(); row++) {
            c.gridy = row;
            this.add(textFields.get(row), c);
        }
    }

    
    public void constructThisPanel() {

    }
    
}
