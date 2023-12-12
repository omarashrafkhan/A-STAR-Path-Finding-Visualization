import java.awt.*;

public @interface styleSheet {


     char START_NODE_KEY = 's';
     char END_NODE_KEY = 'e';
     Color GRID_COLOR = Color.lightGray;
     Color WALL_COLOR = Color.black;
     Color OPEN_SET_COLOR = Color.green;
     Color CLOSED_SET_COLOR = Color.yellow;
     Color PATH_COLOR = Color.cyan;
     Color END_NODE_COLOR = Color.red;
     Color START_NODE_COLOR = Color.blue;
     Color COST_COLOR = Color.black;
     Color BARCOLOR =new Color(0,100,100,127);
     int SLIDERMAX = 100;
     int MESSAGETIME = 1000;
}
