package ru.nsu.fit.g15201.sogreshilin.view.settings;

import ru.nsu.fit.g15201.sogreshilin.model.io.Config;
import ru.nsu.fit.g15201.sogreshilin.view.component.LabeledTextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class GameRulesPanel extends JPanel {


    public GameRulesPanel(Config config) {
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

        liveBegin.addValueChangedObserver(config::setLiveBegin);
        liveEnd.addValueChangedObserver(config::setLiveEnd);
        birthBegin.addValueChangedObserver(config::setBirthBegin);
        birthEnd.addValueChangedObserver(config::setBirthEnd);
        firstImpact.addValueChangedObserver(config::setFirstImpact);
        secondImpact.addValueChangedObserver(config::setSecondImpact);

        add(live);
        add(birth);
        add(impact);
    }
}
