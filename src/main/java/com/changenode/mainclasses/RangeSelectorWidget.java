package com.changenode.mainclasses;

import com.changenode.interfaces.RangeSelectorInterface;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;

public class RangeSelectorWidget extends Control {
    double[] args;
    String[] cats;
    private RangeSelectorInterface listener;
    public RangeSelectorWidget(double slideAreaWidth, RangeSelectorInterface listener, String[] cats) {
        args = new double[]{slideAreaWidth};
        this.cats = cats;
        this.listener = listener;
        setSkin(new RangeSelectorWidgetSkin(this, args, listener, cats));

    }
    public void setCategories(String[] cats) {
        this.cats = cats;
        ((RangeSelectorWidgetSkin) getSkin()).setCategories(cats);
    }
    @Override
    protected Skin<?> createDefaultSkin() {
        return new RangeSelectorWidgetSkin(this, args, listener, cats);
    }
}
