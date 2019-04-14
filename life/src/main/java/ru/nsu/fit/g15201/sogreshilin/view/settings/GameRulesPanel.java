package ru.nsu.fit.g15201.sogreshilin.view.settings;

import ru.nsu.fit.g15201.sogreshilin.model.io.Config;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledTextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

class GameRulesPanel extends JPanel {
    private Config config;

    public GameRulesPanel(Config config) {
        this.config = config;
        setBorder(new TitledBorder("Game rules"));
        setLayout(new GridLayout(1, 3));
        JPanel live = new JPanel(new GridLayout(2, 1));
        live.setBorder(new TitledBorder("Live"));
        LabeledTextField liveBegin = new LabeledTextField("Begin", config.getLiveBegin());
        LabeledTextField liveEnd = new LabeledTextField("End", config.getLiveEnd());
        live.add(liveBegin);
        live.add(liveEnd);

        JPanel birth = new JPanel(new GridLayout(2, 1));
        birth.setBorder(new TitledBorder("Birth"));
        LabeledTextField birthBegin = new LabeledTextField("Begin", config.getBirthBegin());
        LabeledTextField birthEnd = new LabeledTextField("End", config.getBirthEnd());
        birth.add(birthBegin);
        birth.add(birthEnd);

        JPanel impact = new JPanel(new GridLayout(2, 1));
        impact.setBorder(new TitledBorder("Impact"));
        LabeledTextField firstImpact = new LabeledTextField("First neighbours", config.getFirstImpact());
        LabeledTextField secondImpact = new LabeledTextField("Second neighbours", config.getSecondImpact());
        impact.add(firstImpact);
        impact.add(secondImpact);

        liveBegin.addValueChangedObserver(this::setLiveBegin);
        liveEnd.addValueChangedObserver(this::setLiveEnd);
        birthBegin.addValueChangedObserver(this::setBirthBegin);
        birthEnd.addValueChangedObserver(this::setBirthEnd);
        firstImpact.addValueChangedObserver(this::setFirstImpact);
        secondImpact.addValueChangedObserver(this::setSecondImpact);

        add(live);
        add(birth);
        add(impact);
    }

    private void setLiveBegin(double liveBegin) {
        config.setLiveBegin(liveBegin);
    }
    private void setLiveEnd(double liveEnd) {
        config.setLiveEnd(liveEnd);
    }
    private void setBirthBegin(double birthBegin) {
        config.setBirthBegin(birthBegin);
    }
    private void setBirthEnd(double birthEnd) {
        config.setBirthEnd(birthEnd);
    }
    private void setFirstImpact(double firstImpact) {
        config.setFirstImpact(firstImpact);
    }
    private void setSecondImpact(double secondImpact) {
        config.setSecondImpact(secondImpact);
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
