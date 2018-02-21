package ru.nsu.fit.g15201.sogreshilin.controller;


import ru.nsu.fit.g15201.sogreshilin.view.toolbar.MenuToolbarManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Arrays;

public class MainFrame extends JFrame {
    private JMenuBar menuBar;
    protected JToolBar toolBar;
    private static final String RESOURCES_DIR = "../../../../../../";


    private MainFrame() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        toolBar = new JToolBar("Main toolbar");
        toolBar.setRollover(true);
        add(toolBar, BorderLayout.PAGE_START);
    }


    public JMenuBar getJMenuBar() {
        return menuBar;
    }

    public JToolBar getJToolBar() {
        return toolBar;
    }

    public MainFrame(int x, int y, String title) {
        this();
        setSize(x, y);
        setLocationByPlatform(true);
        setTitle(title);
    }


    public JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String icon, String actionMethod) throws SecurityException, NoSuchMethodException {
        JMenuItem item = new JMenuItem(title);
        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);
        if(icon != null) {
            Path currentRelativePath = Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();
            Image image = new ImageIcon(getClass().getResource(RESOURCES_DIR + icon), title).getImage();
            item.setIcon(new ImageIcon(image));
        }
        final Method method = getClass().getMethod(actionMethod);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    method.invoke(MainFrame.this);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return item;
    }


    public void setMenuItemEnabled(String title, boolean b) {
        MenuElement element = getMenuElement(title);
        System.out.println(element);
        element.getComponent().setEnabled(b);

    }


    public JMenuItem createMenuItem(String title, String tooltip, int mnemonic, String actionMethod) throws SecurityException, NoSuchMethodException {
        return createMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }


    public JMenu createSubMenu(String title, int mnemonic) {
        JMenu menu = new JMenu(title);
        menu.setMnemonic(mnemonic);
        return menu;
    }


    public void addSubMenu(String title, int mnemonic) {
        MenuElement element = getParentMenuElement(title);
        if(element == null) {
            throw new InvalidParameterException("Menu path not found: " + title);
        }
        JMenu subMenu = createSubMenu(getMenuPathName(title), mnemonic);
        if(element instanceof JMenuBar) {
            ((JMenuBar) element).add(subMenu);
        } else if(element instanceof JMenu) {
            ((JMenu) element).add(subMenu);
        } else if(element instanceof JPopupMenu) {
            ((JPopupMenu) element).add(subMenu);
        } else {
            throw new InvalidParameterException("Invalid menu path: " + title);
        }
    }


    public JMenuItem addMenuItem(String title, String tooltip, int mnemonic, String icon, String actionMethod) throws SecurityException, NoSuchMethodException {
        MenuElement element = getParentMenuElement(title);
        if(element == null) {
            throw new InvalidParameterException("Menu path not found: " + title);
        }
        JMenuItem item = createMenuItem(getMenuPathName(title), tooltip, mnemonic, icon, actionMethod);
        if(element instanceof JMenu) {
            ((JMenu) element).add(item);
        } else if(element instanceof JPopupMenu) {
            ((JPopupMenu) element).add(item);
        } else {
            throw new InvalidParameterException("Invalid menu path: " + title);
        }
        return item;
    }


    public void addMenuSeparator(String title) {
        MenuElement element = getMenuElement(title);
        if (element == null) {
            throw new InvalidParameterException("Menu path not found: " + title);
        }
        if (element instanceof JMenu) {
            ((JMenu) element).addSeparator();
        } else if (element instanceof JPopupMenu) {
            ((JPopupMenu) element).addSeparator();
        } else {
            throw new InvalidParameterException("Invalid menu path: " + title);
        }
    }


    public void addMenuItem(String title, String tooltip, int mnemonic, String actionMethod) throws SecurityException, NoSuchMethodException {
        addMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }


    private String getMenuPathName(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        return (pos > 0) ? menuPath.substring(pos + 1) : menuPath;
    }


    private MenuElement getParentMenuElement(String menuPath) {
        int pos = menuPath.lastIndexOf('/');
        if(pos > 0) {
            return getMenuElement(menuPath.substring(0, pos));
        } else {
            return menuBar;
        }
    }


    public MenuElement getMenuElement(String menuPath) {
        MenuElement element = menuBar;
        for(String pathElement: menuPath.split("/")) {
            MenuElement newElement = null;
            for(MenuElement subElement: element.getSubElements()) {
                if((subElement instanceof JMenu &&
                        ((JMenu) subElement).getText().equals(pathElement)) ||
                        (subElement instanceof JMenuItem &&
                                ((JMenuItem)subElement).getText().equals(pathElement))) {
                    if(subElement.getSubElements().length == 1 && subElement.getSubElements()[0] instanceof JPopupMenu) {
                        newElement = subElement.getSubElements()[0];
                    } else {
                        newElement = subElement;
                    }
                    break;
                }
            }
            if(newElement == null) {
                return null;
            }
            element = newElement;
        }
        return element;
    }


    public JButton createToolBarButton(JMenuItem item) {
        JButton button = new JButton(item.getIcon());
        button.setBorderPainted(false);
        Arrays.stream(item.getActionListeners()).forEach(button::addActionListener);
        button.setToolTipText(item.getToolTipText());
        return button;
    }


    public JButton createToolBarButton(String menuPath) {
        JMenuItem item = (JMenuItem) getMenuElement(menuPath);
        if (item == null) {
            throw new InvalidParameterException("Menu path not found: " + menuPath);
        }
        return createToolBarButton(item);
    }


    public JButton addToolBarButton(String menuPath) {
        JButton button = createToolBarButton(menuPath);
        toolBar.add(button);
        return button;
    }


    public void addToolBarSeparator() {
        toolBar.addSeparator();
    }
}
