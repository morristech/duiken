package nl.tjonahen.duiken;

import nl.tjonahen.duiken.deco.DecoTable;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.List;
import com.codename1.ui.animations.CommonTransitions;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.ui.util.UITimer;
import java.io.IOException;

/**
 * Duiken (Dutch for diving) main entry point of the application.
 * 
 * @author Philippe Tjon-A-Hen
 */
public class Duiken {

    private Form current;
    private Resources res;

    public void init(Object context) {
        try {
            res = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(res.getTheme(res.getThemeResourceNames()[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        showSplashAnimation();
    }
    private void  showMainUI() {
             if (current != null) {
            current.show();
            return;
        }
        final Form mainForm = new Form("NOB - Duiktabel");
        final AirConsumptionForm airConsumptionDialog = new AirConsumptionForm();

        Command exitCommand = new Command("Exit") {
            @Override
            public void actionPerformed(ActionEvent ev) {
                Display.getInstance().exitApplication();
            }
        };
        mainForm.setBackCommand(exitCommand);

        mainForm.addCommand(new AirConsumptionCommand(airConsumptionDialog, mainForm));
        
        final DecoTable dt = new DecoTable();

        mainForm.setScrollable(false);
        List l = new List(new DiveListModel(dt));
        l.setRenderer(new DiveListCellRenderer());
        l.setFixedSelection(List.FIXED_NONE);
        l.getStyle().setBgTransparency(0);
        l.addActionListener(new ShowDiveCalculationActionListner(airConsumptionDialog, mainForm));

        mainForm.setLayout(new BorderLayout());
        mainForm.addComponent(BorderLayout.CENTER, l);


        mainForm.show();
    }

    public void stop() {
        current = Display.getInstance().getCurrent();
    }

    public void destroy() {
    }
    
    private void showSplashAnimation() {
        Form splash = new Form();
        splash.setUIID("Splash");
        splash.getContentPane().setUIID("Container");
        splash.getTitleArea().setUIID("Container");
        BorderLayout border = new BorderLayout();
        border.setCenterBehavior(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE);
        splash.setLayout(border);
        splash.setScrollable(false);
        Label title = new Label("Duiken");
        title.setUIID("SplashTitle");
        Label subtitle = new Label("By Philippe");
        subtitle.setUIID("SplashSubTitle");
        splash.addComponent(BorderLayout.NORTH, title);
        splash.addComponent(BorderLayout.SOUTH, subtitle);
        Label beaker = new Label(res.getImage("duiker.png"));
//        Label beakerLogo = new Label(res.getImage("beaker_logo.png"));
        Container layeredLayout = new Container(new LayeredLayout());
        splash.addComponent(BorderLayout.CENTER, layeredLayout);
        layeredLayout.addComponent(beaker);
        Container logoParent = new Container(new BorderLayout());
        layeredLayout.addComponent(logoParent);
//        logoParent.addComponent(BorderLayout.CENTER, beakerLogo);
        splash.revalidate();
        
//        beakerLogo.setX(0);
//        beakerLogo.setY(0);
//        beakerLogo.setWidth(3);
//        beakerLogo.setHeight(3);
        logoParent.setShouldCalcPreferredSize(true);
        logoParent.animateLayoutFade(2000, 0);
        
        splash.show();
        splash.setTransitionOutAnimator(CommonTransitions.createFastSlide(CommonTransitions.SLIDE_VERTICAL, true, 300));
        new UITimer(new Runnable() {
            public void run() {
                showMainUI();
            }
        }).schedule(2500, false, splash);
    }    
}
