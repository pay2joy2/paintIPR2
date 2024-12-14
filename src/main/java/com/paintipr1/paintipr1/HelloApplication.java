package com.paintipr1.paintipr1;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import myjar.util.Shape;
import myjar.util.Drawer;
//import com.paintipr1.paintipr1.shapes.Shape;
//import com.paintipr1.paintipr1.drawers.Drawer;
//import com.paintipr1.paintipr1.drawers.EllipseDrawer;
import com.paintipr1.paintipr1.drawers.IsoscelesTriangleDrawer;
import com.paintipr1.paintipr1.drawers.ParallelepipedDrawer;
import com.paintipr1.paintipr1.drawers.RectangleDrawer;
import com.paintipr1.paintipr1.drawers.RightPolygonDrawer;
import com.paintipr1.paintipr1.drawers.RightTiangleDrawer;
//import com.paintipr1.paintipr1.shapes.Ellipse;
import com.paintipr1.paintipr1.shapes.IsoscelesTriangle;
import com.paintipr1.paintipr1.shapes.Parallelepiped;
import com.paintipr1.paintipr1.shapes.Rectangle;
import com.paintipr1.paintipr1.shapes.RightPolygon;
import com.paintipr1.paintipr1.shapes.RightTriangle;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloApplication extends Application {

    LinkedHashMap<String, Shape> shapesMap = FiguresMap.getInstance();
    LinkedHashMap<String, Drawer> drawersMap = DrawersMap.getInstance();

    public void addJar(String pathToJar)
        throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        JarFile jarFile = new JarFile(pathToJar);
        Enumeration<JarEntry> e = jarFile.entries();

        URL[] urls = { new URL("jar:file:" + pathToJar+"!/") };
        URLClassLoader cl = URLClassLoader.newInstance(urls);

        while (e.hasMoreElements()) {
            JarEntry je = e.nextElement();
            if(je.isDirectory() ||
                !je.getName().endsWith(".class") ||
                !je.getName().contains("impl")){
                continue;
            }
            String className = je.getName().substring(0,je.getName().length()-6);
            className = className.replace('/', '.');
            Class<?> c = cl.loadClass(className);
            Constructor<?> constructor = c.getConstructor();
            Object classInstance = constructor.newInstance();

            if(je.getName().endsWith("Drawer.class")) {
                String noDrawerClassName = c.getSimpleName().replace("Drawer", "");
                drawersMap.put(noDrawerClassName, (Drawer) classInstance);
            } else {
                shapesMap.put(c.getSimpleName(), (Shape) classInstance);
            }

        }
    }

    @Override
    public void start(Stage stage)
        throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        File f = new File("plugins");

        for(String pathToJar : Objects.requireNonNull(f.list())){
            pathToJar = "plugins/" + pathToJar;
            addJar(pathToJar);
        }

        Rectangle rectangle = new Rectangle();
        Parallelepiped parallelepiped = new Parallelepiped();
        RightTriangle rightTriangle = new RightTriangle();
        RightPolygon rightPolygon = new RightPolygon();
        IsoscelesTriangle isoscelesTriangle = new IsoscelesTriangle();
        shapesMap.put("Rectangle", rectangle);
        shapesMap.put("Parallelepiped",parallelepiped);
        shapesMap.put("RightTriangle", rightTriangle);
        shapesMap.put("RightPolygon", rightPolygon);
        shapesMap.put("IsoscelesTriangle", isoscelesTriangle);
        RectangleDrawer rectangleDrawer = new RectangleDrawer();
        ParallelepipedDrawer parallelepipedDrawer = new ParallelepipedDrawer();
        RightTiangleDrawer rightTiangleDrawer = new RightTiangleDrawer();
        RightPolygonDrawer rightPolygonDrawer = new RightPolygonDrawer();
        IsoscelesTriangleDrawer isoscelesTriangleDrawer = new IsoscelesTriangleDrawer();
        drawersMap.put("Rectangle", rectangleDrawer);
        drawersMap.put("Parallelepiped", parallelepipedDrawer);
        drawersMap.put("RightTriangle", rightTiangleDrawer);
        drawersMap.put("RightPolygon", rightPolygonDrawer);
        drawersMap.put("IsoscelesTriangle", isoscelesTriangleDrawer);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}