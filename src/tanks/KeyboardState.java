package tanks;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 *
 * @author igor
 */
public class KeyboardState extends JComponent{
    private boolean[] keyMap;
    
    public KeyboardState(){
        this.keyMap = new boolean[256];
        for(int i = 0; i < this.keyMap.length; ++i){
            final int keyCode = i;
            
            this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(keyCode, 0, false), 2 * i);
            this.getActionMap().put(2 * i, new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    keyMap[keyCode] = true;
                }
            
            });
            
            this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                    .put(KeyStroke.getKeyStroke(keyCode, 0, true), 2 * i + 1);
            this.getActionMap().put(2 * i + 1, new AbstractAction(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    keyMap[keyCode] = false;
                }
            
            });
        }
    }
    
    public boolean[] getMap(){
        return Arrays.copyOf(keyMap, keyMap.length);
    }
    
    public boolean isKeyPressed(int keyCode){
        return this.keyMap[keyCode];
    }
}
