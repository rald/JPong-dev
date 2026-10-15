import java.awt.*;

/**
 * Modal dialog for capturing validated player input prior to application startup.
 */
public class NameDialog extends Dialog {
    private TextField tf;
    private Button okBtn;
    private PongApp app;

    public NameDialog(PongApp parent) {
        super(parent, "Enter Player Name", true);
        this.app = parent;
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 15));
        add(new Label("Your Name:"));
        
        tf = new TextField("Player", 10);
        add(tf);
        
        okBtn = new Button("Start Game");
        add(okBtn);
        
        resize(280, 140);
        setResizable(false);
    }

    public boolean action(Event e, Object arg) {
        if (e.target == okBtn || e.target == tf) {
            submitAndClose();
            return true;
        }
        return super.action(e, arg);
    }

    public boolean handleEvent(Event e) {
        if (e.id == Event.WINDOW_DESTROY) {
            submitAndClose();
            return true;
        }
        if (e.id == Event.ACTION_EVENT) {
            if (e.target == okBtn || e.target == tf) {
                submitAndClose();
                return true;
            }
        }
        return super.handleEvent(e);
    }

    private void submitAndClose() {
        if (tf != null) {
            String text = tf.getText();
            if (text != null) {
                text = text.trim();
                if (text.length() > 0) {
                    app.setPlayerName(text);
                }
            }
        }
        dispose();
    }
}