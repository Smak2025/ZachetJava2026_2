package ru.smak.ui;

import ru.smak.painting.DPoint;
import ru.smak.painting.convertation.Converter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainWindow extends JFrame {
    private final Map<Integer, List<DPoint>> coloredPoints = new HashMap<>();
    private final Converter converter = new Converter(0.0, 1.0, 0.0, 1.0);
    private final List<UserActionListener> userActionListeners = new ArrayList<>();
    private final JPanel panel;

    public void addUserActionListener(UserActionListener l){
        userActionListeners.add(l);
    }

    public void removeUserActionListener(UserActionListener l){
        userActionListeners.remove(l);
    }

    public MainWindow(){
        setTitle("Сетевая рисовалка");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));

        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                synchronized (coloredPoints) {
                    coloredPoints.forEach((c, pts) -> {
                        g.setColor(new Color(c));
                        DPoint prev = null;
                        var prevX = 0;
                        var prevY = 0;
                        for (var pt : pts) {
                            if (pt != null) {
                                int currX = (int) converter.xCrt2Scr(pt.x());
                                int currY = (int) converter.yCrt2Scr(pt.y());
                                if (prev != null) {
                                    g.drawLine(prevX, prevY, currX, currY);
                                }
                                prevX = currX;
                                prevY = currY;
                            }
                            prev = pt;
                        }
                    });
                }
            }
        };

        panel.setBackground(Color.WHITE);

        add(panel, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                var point = new DPoint(
                        converter.xScr2Crt(e.getX()),
                        converter.yScr2Crt(e.getY())
                );
                userActionListeners.forEach(l -> l.onAction(ActionType.DO_PAINT, point));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                var point = new DPoint(
                        converter.xScr2Crt(e.getX()),
                        converter.yScr2Crt(e.getY())
                );
                userActionListeners.forEach(l -> l.onAction(ActionType.STOP_PAINT, point));
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                var point = new DPoint(
                        converter.xScr2Crt(e.getX()),
                        converter.yScr2Crt(e.getY())
                );
                userActionListeners.forEach(l -> l.onAction(ActionType.DO_PAINT, point));
            }
        });

        setSize(new Dimension(800, 600));
    }

    public void addPoint(int color, DPoint point){
        synchronized (coloredPoints) {
            List<DPoint> points;
            if (coloredPoints.containsKey(color)) {
                points = coloredPoints.get(color);
            } else {
                points = new ArrayList<>();
                coloredPoints.put(color, points);
            }
            points.add(point);
        }
        panel.repaint();
    }
}