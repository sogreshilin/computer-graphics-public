package ru.nsu.fit.g15201.sogreshilin.controller;


import javax.swing.*;
import java.awt.*;
import java.security.InvalidParameterException;
import java.util.Arrays;
import ru.nsu.fit.g15201.sogreshilin.view.toolbar.MenuAction;

public class MainFrame extends JFrame {
    private final JMenuBar menuBar;
    private final JToolBar toolBar;
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

    private JMenuItem createMenuItem(String title, String tooltip, int mnemonic,
                                     String icon, final MenuAction actionMethod)
            throws SecurityException, NoSuchMethodException {
        JMenuItem item = new JMenuItem(title);
        item.setMnemonic(mnemonic);
        item.setToolTipText(tooltip);
        if(icon != null) {
            Image image = new ImageIcon(getClass().getResource(RESOURCES_DIR + icon), title).getImage();
            item.setIcon(new ImageIcon(image));
        }
        item.addActionListener(evt -> {
            try {
                actionMethod.invoke();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return item;
    }

    public void setMenuItemEnabled(String title, boolean b) {
        MenuElement element = getMenuElement(title);
        System.out.println(element);
        if (element != null) {
            element.getComponent().setEnabled(b);
        }
    }

    public JMenuItem createMenuItem(String title, String tooltip,
                                    int mnemonic, MenuAction actionMethod)
            throws SecurityException, NoSuchMethodException {
        return createMenuItem(title, tooltip, mnemonic, null, actionMethod);
    }

    private JMenu createSubMenu(String title, int mnemonic) {
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

    public JMenuItem addMenuItem(String title, String tooltip,
                                 int mnemonic, String icon, MenuAction actionMethod)
            throws SecurityException, NoSuchMethodException {
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

    public void addMenuItem(String title, String tooltip,
                            int mnemonic, MenuAction actionMethod)
            throws SecurityException, NoSuchMethodException {
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

    private MenuElement getMenuElement(String menuPath) {
        MenuElement element = menuBar;
        for(String pathElement: menuPath.split("/")) {
            MenuElement newElement = null;
            for(MenuElement subElement: element.getSubElements()) {
                if((subElement instanceof JMenu &&
                        ((JMenu) subElement).getText().equals(pathElement)) ||
                        (subElement instanceof JMenuItem &&
                                ((JMenuItem)subElement).getText().equals(pathElement))) {
                    if(subElement.getSubElements().length == 1 &&
                            subElement.getSubElements()[0] instanceof JPopupMenu) {
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

    private JButton createToolBarRegularButton(JMenuItem item) {
        JButton button = new JButton(item.getIcon());
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        Arrays.stream(item.getActionListeners()).forEach(button::addActionListener);
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    private JToggleButton createToolBarToggleButton(JMenuItem item) {
        JToggleButton button = new JToggleButton(item.getIcon());
        button.setBorderPainted(true);
        Arrays.stream(item.getActionListeners()).forEach(button::addActionListener);
        button.addChangeListener(e -> {
            if (button.isSelected()) {
                button.setBorder(BorderFactory.createLoweredBevelBorder());
            } else {
                button.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });
        button.setToolTipText(item.getToolTipText());
        return button;
    }

    private JButton createToolBarRegularButton(String menuPath) {
        JMenuItem item = (JMenuItem) getMenuElement(menuPath);
        if (item == null) {
            throw new InvalidParameterException("Menu path not found: " + menuPath);
        }
        return createToolBarRegularButton(item);
    }

    private JToggleButton createToolBarToggleButton(String menuPath) {
        JMenuItem item = (JMenuItem) getMenuElement(menuPath);
        if (item == null) {
            throw new InvalidParameterException("Menu path not found: " + menuPath);
        }
        return createToolBarToggleButton(item);
    }

    public JButton addToolBarRegularButton(String menuPath) {
        JButton button = createToolBarRegularButton(menuPath);
        toolBar.add(button);
        return button;
    }

    public JToggleButton addToolBarToggleButton(String menuPath) {
        JToggleButton button = createToolBarToggleButton(menuPath);
        toolBar.add(button);
        return button;
    }

    public void addToolBarSeparator() {
        toolBar.addSeparator();
    }
}
