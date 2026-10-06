package org.example.wettkampfverwaltung;

import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class ElementHelper {

    public Button hoverButton_small(String name, String cssClassesSeperatedByComma){
        Button tmp = new Button(name);
        tmp.setCursor(Cursor.HAND);

        tmp.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(220), tmp);
            scaleUp.setToX(1.1);
            scaleUp.setToY(1.1);
            scaleUp.play();
        });

        tmp.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), tmp);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
            //tmp.setCursor(Cursor.DEFAULT);
        });

        if(cssClassesSeperatedByComma != null){
            String[] classes = cssClassesSeperatedByComma.split(",");
            for (int i = 0; i < classes.length; i++) {
                tmp.getStyleClass().add(classes[i]);
            }
        }


        return tmp;
    }

    public Button fullscreenButton(String name){
        Button tmp = hoverButton_small(name, "mv-fullscreen");
        tmp.setFocusTraversable(false);
        return tmp;
    }

    public Button leftControlButton(String name){
        Button tmp = hoverButton_small(name, "open-mv");
        tmp.setFocusTraversable(false);
        return tmp;
    }
}
