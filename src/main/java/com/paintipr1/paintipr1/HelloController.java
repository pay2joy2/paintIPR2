package com.paintipr1.paintipr1;

import myjar.util.Shape;
import myjar.util.Drawer;
//import com.paintipr1.paintipr1.drawers.Drawer;
//import com.paintipr1.paintipr1.shapes.Shape;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Pair;

public class HelloController {

    private static final Path SAVE_FILE_LOCATION =  Paths.get("save1.txt");
    LinkedHashMap<String, Shape> figuresMap = FiguresMap.getInstance();
    LinkedHashMap<String, Drawer> drawersMap = DrawersMap.getInstance();
    ShapeParser shapeParser = new ShapeParser();
    ArrayList<Text> textsList;
    ArrayList<TextField> textFieldsList;

    @FXML
    SplitPane splitPane;
    @FXML
    TextArea scriptArea;
    @FXML
    private Pane drawingPane;
    @FXML
    private ChoiceBox<String> shapesBox;

    @FXML
    private Text debugText;

    @FXML
    private Text text1;
    @FXML
    private Text text2;
    @FXML
    private Text text3;
    @FXML
    private Text text4;
    @FXML
    private Text text5;

    @FXML
    private TextField textFieldMoveX;
    @FXML
    private TextField textFieldMoveY;

    @FXML
    private TextField textField1;
    @FXML
    private TextField textField2;
    @FXML
    private TextField textField3;
    @FXML
    private TextField textField4;
    @FXML
    private TextField textField5;

    @FXML
    private ToggleButton toggleButton;


    @FXML
    private void shapesBoxChooseShape(){

        if(!shapesBox.getValue().isEmpty()){
            LinkedHashMap<String, Integer> map = figuresMap.get(shapesBox.getValue()).getVariables();
            int i = 0;

            textsList.forEach(x -> x.setVisible(false));
            textFieldsList.forEach(x -> x.setVisible(false));

            for(String x : map.keySet()){
                textsList.get(i).setText(x);
                textsList.get(i).setVisible(true);

                textFieldsList.get(i).setText(String.valueOf(map.get(x)));
                textFieldsList.get(i).setVisible(true);
                i++;
            }
        }
    }

    javafx.scene.shape.Shape selectedNode;
    Paint selectedNodeColor;

    @FXML
    private ToggleButton mirrorToggleButton;

    Line lineAB = new Line();
    boolean flag = true;

    @FXML
    private void mirrorToggleButtonToggled(){
        if(mirrorToggleButton.isSelected()) {
            drawingPane.getChildren().add(lineAB);
        } else {
            drawingPane.getChildren().remove(lineAB);
        }
    }

    int scale = 1;

    @FXML
    private void mirrorHorizontalButtonClicked(){
        scale *= -1;
        selectedNode.setScaleX(scale);
    }

    @FXML
    private void mirrorVerticalButtonClicked(){
        scale *= -1;
        selectedNode.setScaleY(scale);
    }

    @FXML
    private void paneClickListener(MouseEvent mouseEvent){

        for(Node node : drawingPane.getChildren()){
            if(node.isHover()){
                if(mouseEvent.isControlDown()) {
                    if (selectedNode == null) {
                        selectedNode = (javafx.scene.shape.Shape) node;
                        selectedNodeColor = selectedNode.getFill();
                        selectedNode.setStroke(Color.RED);
                        selectedNode.setStrokeWidth(2.0);
                    } else {
                        selectedNode.setStroke(selectedNodeColor);
                        selectedNodeColor = null;
                        selectedNode = null;
                    }
                }
                return;
            }
        }

        if(shapesBox.getValue() == null){
            debugText.setVisible(true);
            debugText.setFill(Color.RED);
            debugText.setText("Choose shape first");
            return;
        }
        ArrayList<Integer> args = new ArrayList<>();
        for(TextField x : textFieldsList) {
            if (x.isVisible() && x.getText().isEmpty()) {
                debugText.setVisible(true);
                debugText.setFill(Color.RED);
                debugText.setText("Fill all the fields");
                return;
            } else if (x.isVisible()) {
                args.add(Integer.valueOf(x.getText()));
            }
        }
        
        debugText.setVisible(false);
        
        Pair<Double, Double> pair = new Pair<>(mouseEvent.getX(), mouseEvent.getY());
        makeDraggable(drawersMap.get(shapesBox.getValue()).draw(drawingPane,pair,args));
    }

    @FXML
    private void toggleButtonToggled(){
        if(!toggleButton.isSelected()){
            rotationPoint.setVisible(false);
        }
    }

    Circle rotationPoint = new Circle();
    private double anchorX;
    private double anchorY;
    double scaleFactor = 1.0;

    @FXML
    private void initialize(){
        rotationPoint.setFill(Color.RED);
        rotationPoint.setRadius(5);
        rotationPoint.setVisible(false); // Точка видна только после клика
        drawingPane.getChildren().add(rotationPoint);

        drawingPane.setOnMouseClicked(event -> {
            if(toggleButton.isSelected()) {
                if(event.isStillSincePress()) {
                    anchorX = event.getX();
                    anchorY = event.getY();

                    rotationPoint.setCenterX(anchorX);
                    rotationPoint.setCenterY(anchorY);
                    rotationPoint.setVisible(true);
                }
            } else {
                paneClickListener(event);
            }
        });

        drawingPane.setOnMouseDragged(event -> {
            if(toggleButton.isSelected()) {
                if (rotationPoint.isVisible()) {
                    double deltaX = event.getX() - anchorX;
                    double deltaY = event.getY() - anchorY;

                    double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));

                    Rotate rotate = new Rotate(angle, anchorX, anchorY);
                    selectedNode.getTransforms().clear();
                    selectedNode.getTransforms().setAll(rotate);
                }
            }
        });

        drawingPane.setOnScroll(event -> {
            for(Node x : drawingPane.getChildren()){
                if(x.isHover()){
                    if (event.getDeltaY() > 0) {
                        scaleFactor *= 1.1;
                    } else {
                        scaleFactor /= 1.1;
                    }

                    x.setScaleX(scaleFactor);
                    x.setScaleY(scaleFactor);
                }
            }
        });

        Divider divider = splitPane.getDividers().get(0);
        divider.positionProperty().addListener(
            (observable, oldvalue, newvalue) -> divider.setPosition(0.7436224489795918));

        textsList = new ArrayList<>();
        textsList.addAll(Arrays.asList(text1,text2,text3,text4,text5));
        textFieldsList = new ArrayList<>();
        textFieldsList.addAll(Arrays.asList(textField1,textField2,textField3,textField4,textField5));
        shapesBox.getItems().addAll(figuresMap.keySet());
    }

    private double startX;
    private double startY;

    private void makeDraggable(Node node) {

        node.setOnMousePressed(e -> {
            startX = e.getSceneX() - node.getTranslateX();
            startY = e.getSceneY() - node.getTranslateY();
        });

        node.setOnMouseDragged(e -> {
            node.setTranslateX(e.getSceneX() - startX);
            node.setTranslateY(e.getSceneY() - startY);
        });

        node.setOnMouseEntered(e -> {
            if (!e.isPrimaryButtonDown()) {
                node.getScene().setCursor(Cursor.HAND);
            }
        });

        node.setOnMouseExited(e -> {
            if (!e.isPrimaryButtonDown()) {
                node.getScene().setCursor(Cursor.DEFAULT);
            }
            scaleFactor = 1.0;
            switch (node.getClass().getSimpleName()) {
                case ("Circle") -> {
                    Circle circle = (Circle) node;
                    circle.setCenterY(circle.getCenterY() + circle.getTranslateY());
                    circle.setCenterX(circle.getCenterX() + circle.getTranslateX());
                    circle.setTranslateX(0);
                    circle.setTranslateY(0);
                }
                case ("Rectangle") -> {
                    Rectangle rectangle = (Rectangle) node;
                    rectangle.setY(rectangle.getY() + rectangle.getTranslateY());
                    rectangle.setX(rectangle.getX() + rectangle.getTranslateX());
                    rectangle.setTranslateX(0);
                    rectangle.setTranslateY(0);
                }
                case ("Ellipse") -> {
                    Ellipse ellipse = (Ellipse) node;
                    ellipse.setCenterY(ellipse.getCenterY() + ellipse.getTranslateY());
                    ellipse.setCenterX(ellipse.getCenterX() + ellipse.getTranslateX());
                    ellipse.setTranslateX(0);
                    ellipse.setTranslateY(0);
                }
                case ("Polygon") -> {
                    Polygon polygon = (Polygon) node;
                    for (int i = 0; i < polygon.getPoints().size(); i += 2) {
                        double x = polygon.getPoints().get(i);
                        double y = polygon.getPoints().get(i + 1);

                        polygon.getPoints().set(i, x + polygon.getTranslateX());
                        polygon.getPoints().set(i + 1, y + polygon.getTranslateY());
                    }
                    polygon.setTranslateX(0);
                    polygon.setTranslateY(0);
                }
            }
        });
    }

    @FXML
    private void scriptButtonClicked(){
        if(scriptArea.getText().isEmpty()){
            debugText.setVisible(true);
            debugText.setFill(Color.RED);
            debugText.setText("No script provided");
            return;
        }
        ArrayList<String> scriptList = new ArrayList<>(
            List.of(scriptArea.getText().split("\n")));

        ArrayList<Node> parsedList = shapeParser.parseInput(scriptList);
        if(parsedList.size() == 0){
            debugText.setVisible(true);
            debugText.setFill(Color.RED);
            debugText.setText("Failed to parse provided script");
            return;
        }

        debugText.setVisible(false);

        for(Node x : parsedList){
            makeDraggable(x);
            drawingPane.getChildren().add(x);
        }
    }

    @FXML
    private void moveButtonClicked(){
        if(selectedNode != null) {
            if(!textFieldMoveX.getText().isEmpty()) {
                selectedNode.setTranslateX(
                    selectedNode.getTranslateX() + Double.parseDouble(textFieldMoveX.getText()));
            }
            if(!textFieldMoveY.getText().isEmpty()) {
                selectedNode.setTranslateY(
                    selectedNode.getTranslateY() - Double.parseDouble(textFieldMoveY.getText()));
            }
        }
    }

    @FXML
    private void saveToXMLButton() {
        try(FileWriter fileWriter = new FileWriter(new File(SAVE_FILE_LOCATION.toUri()), false)){
            for(Node x: drawingPane.getChildren()){
                if(x.getClass() != ToolBar.class) {
                    javafx.scene.shape.Shape shape = (javafx.scene.shape.Shape) x;
                    fileWriter.write(shape.toString());
                    fileWriter.write("\n");
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void loadFromXMLButton(){
        ArrayList<String> stringList = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(
            SAVE_FILE_LOCATION.toFile()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringList.add(line);
            }

            for (Node x : shapeParser.parseInput(stringList)) {
                makeDraggable(x);
                drawingPane.getChildren().add(x);
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void deleteSelectedShape(){
        drawingPane.getChildren().remove(selectedNode);
        selectedNode = null;
    }
}
