/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//This class especially use for Smart Home user.
package sim.app.ubik.people;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.Automaton;
//import sim.app.ubik.behaviors.AutomatonWorker;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.behaviors.pathfinderDemos.Pathfinder;
import sim.app.ubik.behaviors.pathfinderDemos.PathfinderThread;
import sim.app.ubik.building.rooms.Room;
import sim.engine.SimState;
import sim.util.Int2D;
import ubik3d.model.HomePieceOfFurniture;
import sim.app.ubik.building.Location;
import sim.app.ubik.building.SpaceArea;
import sim.app.ubik.building.connectionSpace.Door;
import sim.app.ubik.building.connectionSpace.ConnectionSpaceInABuilding;
import sim.app.ubik.domoticDevices.PresenceSensor;
import sim.app.ubik.domoticDevices.DoorSensor;
import sim.util.Int2D;
import sim.app.ubik.domoticDevices.PresenceActuator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import sim.app.ubik.domoticDevices.Actuator;
import sim.app.ubik.domoticDevices.DomoticDevice;
import sim.app.ubik.furniture.Furniture;
import sim.app.ubik.mySQLconnection.*;
import sim.util.MutableInt2D;
import ubik3d.model.Wall;
import ubik3d.model.Home;



/**
 *
 * @author SA2305
 */
public class Worker extends Person{
    private static final Logger LOG = Logger.getLogger(Worker.class.getName());
    private static String globalGoal=null; //goal for all agents     
   //rivate static String exits[] = {"Exit1", "Exit2", "Exit3", "Exit4"}; //list of poosible goals
    private static String rooms[] = {"room1", "room2", "room3", "room4","room5","room6","room7","room8","room9"};
//    private static String rooms1[] = {"room2",};
    private DBConnection database;
    
           
   
    private Pathfinder pf;
    
    private List<Int2D> goals;
    private Int2D currentGoal=null;
    private Object initialPosition;
    private boolean activity;
    private PresenceSensor ps;
    private PresenceActuator PA;
    private Actuator Aa;
    private boolean pa;
    private String actualActivity="";
    private SimState SS;
    private int onn =1;
    private int offf =0;
    protected Color color;
    
    //constructor
    public Worker(int floor, HomePieceOfFurniture person3DModel, Ubik ubik) {
        super(floor, person3DModel, ubik);
        database = new DBConnection();
        database.connect();
    }
    
@Override
    public void step(SimState state) {
        super.step(state);        
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateTime = sdf.format(cal.getTime());
        int x = this.getPosition().x;
        int y = this.getPosition().y;
        //System.out.println(state.schedule.getTime()+" "+state.schedule.getSteps());
        
        Room room = (Room) ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceArea(x, y, Room.class);
        
        //SpaceArea sa = (Room) ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceArea(x, y, SpaceArea.class);
        if(room!=null) {
            ConnectionSpaceInABuilding cs = room.getConnectionSpaceNearerTo(x, y);
            //System.out.println("cell size"+ ubik.getCellSize());
            
            //DoorSensor sensor1 = (DoorSensor) ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceArea(position.x, position.y, DoorSensor.class);
            //System.out.println(sensor1.getFloor());
            
            if (cs != null) {
                if (cs instanceof Door) {
                    Door door = (Door) cs;
                   // System.out.println("cellsize  " + cs.getCenter().distance(new Int2D(x,y))*ubik.getCellSize() + cs.getName());
                    if (cs.getCenter().distance(new Int2D(x,y))*ubik.getCellSize() < 90) {
                        if (door.isOpened()) {
                            //System.out.printf("Door name (Door Open)", door.getName());
                            //System.out.println("Door open");
                            door.close();
                            //door.getModel().setVisible(false);
                            
                            String query = "insert into measure values(default,2," + door.getName() + ",1,0,'" + dateTime + "');";
                            System.out.println(query);
                            database.executeUpdate(query);                  
                        } else {
                            door.open();
                           // System.out.printf("Door name (Door Close)", door.getName());
                           
                            String query = "insert into measure values(default,2," + door.getName()+ ",0,1,'" + dateTime + "');";
                            System.out.println(query);
                            database.executeUpdate(query);                            
                        }
                    }
                }else{
                    System.out.println("A door has not been found!");}
                
            }
            //System.out.println("room not found");
        }
        
        //DoorSensor ds = (DoorSensor) ubik.getBuilding().getFloor(floor).getDeviceHandler().getDeviceByName("7");
        DoorSensor ds = (DoorSensor) ubik.getBuilding().getFloor(floor).getDeviceHandler().getDeviceByName("DoorSensor");
        
        //System.out.println(ds.getId()+ds.getName()+ds.getStatus());
       // System.out.println(ds.getName());
       //List<Furniture> furnitures = (List<Furniture>) ubik.getBuilding().getFloor(floor).getFurnitureHandler().getFurnitures();
      List <DomoticDevice> dd;
        dd = (List <DomoticDevice>) ubik.getBuilding().getFloor(floor).getDeviceHandler().getDevicesInstanceOf(DomoticDevice.class);
      
        
//            for(int i=0; i<dd.size(); i++){
//                
//                if((dd.get(i).getClass().getSimpleName()== "PresenceSensor")&& (room.getConnectionSpaceNearerTo(x, y).isOpened())){
//                    System.out.println("Door Open");
//                    room.
//                    
//                } else {
//                }
//        System.out.println("DoorSensor i  "+ds.get(i)+"Get ID"+ds.get(i).getId()+"Get Name"+ ds.get(i).getName()+" Get type"+ds.get(i).getClass().getSimpleName());
//        }
//            if(ds.get(i)!=null){
//                if(ds.get(i).equals(DoorSensor.class){
//                 if(ds(i).getStatus().equals("opened")){
//                    String query = "insert into measure values(default,2," + ds.getName()+ ",0,1,'" + dateTime + "');";
//                    System.out.println(query);
//                    database.executeUpdate(query);
//                }else{
//                    String query = "insert into measure values(default,2," + ds.getName()+ ",1,0,'" + dateTime + "');";
//                    System.out.println(query);
//                    database.executeUpdate(query);
//                }
//            }
//        }
       //}
//        
//        Int2D position;
//        System.out.println("PRESENCE SENSORS:");
//        List<DomoticDevice> pSensors = ubik.getBuilding().getFloor(floor).getDeviceHandler().getDevicesInstanceOf(PresenceSensor.class);
//        for(int i=0;i<pSensors.size();i++){
//            position = new Int2D(pSensors.get(i).getPosition());
//            System.out.println(pSensors.get(i).getName() + " - (" + position.x + "-" + position.y + ")" + "get Id" +pSensors.get(i).getId());
//        }
////        
        
        
        

        //This has to be checked!!!
//        Aa  =(Actuator)ubik.getBuilding().getFloor(floor).getDeviceHandler().getNearestDevice(new Int2D(x,y),Actuator.class);
//        if(Aa.isActivated())

//      /*****************************************************************************************************************************************************
        /********************************                                Presence Sensor working                  *********************************************
        ******************************************************************************************************************************************************/
        
        ps = (PresenceSensor) ubik.getBuilding().getFloor(floor).getDeviceHandler().getNearestDevice(new Int2D(x,y),PresenceSensor.class);
        
        //System.out.println(sa.getName()+sa.getId());
       // ps= (PresenceSensor) ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceAreas().ge
        //if(ps.getSpaceArea().getCenter().distance(new Int2D(x,y))*ubik.getCellSize() < 80){
        //if( "room0".equals(ps.getSpaceArea().getName())){
//        if("room7".equals(sa.getName())){
//                System.out.println(sa.getName()+sa.getId());
//                if( this.isMoving()){ 
//                String query = "insert into measure values(default,2," + ps.getName() + ",0,1,'" + dateTime + "');";
//                System.out.println(query);
//                 database.executeUpdate(query);
//                }
//        
//            else{
//                System.out.println(sa.getName()+sa.getId());
//                String query = "insert into measure values(default,2," + ps.getName() + ",1,0,'" + dateTime + "');";
//                System.out.println(query);
//                database.executeUpdate(query);
//                }
//         
//        }    
//        
//        //if("room8".equals(sa.getName())){
//            if( this.isMoving()&& ("room8".equals(sa.getName()))){ 
//                String query = "insert into measure values(default,2," + ps.getName() + ",0,1,'" + dateTime + "');";
//                System.out.println(query);
//                 database.executeUpdate(query);
//                }
//        
//            else{
//                String query = "insert into measure values(default,2," + ps.getName() + ",1,0,'" + dateTime + "');";
//                System.out.println(query);
//                database.executeUpdate(query);
//                }
//         
       // }   
           
       
//       SpaceArea sa1 = (SpaceArea) ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceAreaByName("room1");
//            if (sa1.contains(this.getPosition().getX(), this.getPosition().getY())){
//                if(this.isMoving){                 
//                    String query = "insert into measure values(default,2," + ps.getName() + ",0,1,'" + dateTime + "');";
//                    System.out.println(query);
//                    database.executeUpdate(query);
//                    this.isMoving=false;
//                }                
//                else{
//                String query = "insert into measure values(default,2," + ps.getName() + ",1,0,'" + dateTime + "');";
//                System.out.println(query);
//                database.executeUpdate(query);
//                this.isMoving=true;
//                }
//            }
//            
//        SpaceArea sa = (SpaceArea) ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceAreaByName("room5");
    
        
//            if (sa1.contains(this.getPosition().getX(), this.getPosition().getY())){
//                if(this.isMoving){                 
//                    String query = "insert into measure values(default,2," + ps.getName() + ",0,1,'" + dateTime + "');";
//                    System.out.println(query);
//                    database.executeUpdate(query);
//                    this.isMoving=false;
//                }                
//                else{
//                String query = "insert into measure values(default,2," + ps.getName() + ",1,0,'" + dateTime + "');";
//                System.out.println(query);
//                database.executeUpdate(query);
//                this.isMoving=true;
//                }
//            }
//            
            List<SpaceArea> sa = (List<SpaceArea>) ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceAreas();
            for(int i=0;i<sa.size();i++){
                if (sa.get(i).contains(this.getPosition().getX(), this.getPosition().getY())){
                if(this.isMoving){       
                    if(sa.get(i).contains(ps.getPosition().getX(), ps.getPosition().getY())){
                    String query = "insert into measure values(default,2," + ps.getName() + ",0,1,'" + dateTime + "');";
                    System.out.println(query);
                    database.executeUpdate(query);
                    this.isMoving=false;
                    }
                }
                else{
                    if(sa.get(i).contains(ps.getPosition().getX(), ps.getPosition().getY())){
                    String query = "insert into measure values(default,2," + ps.getName() + ",1,0,'" + dateTime + "');";
                    System.out.println(query);
                    database.executeUpdate(query);
                    this.isMoving=true;
                    }
                }
            }
//           System.out.println( "index# "+ i + "get name " + sa.get(i).getName()+ "id " +sa.get(i).getId());
                   
       }
    
    
    
 
//            //System.out.println("Presence Sensor: " + ps.getName() + ps.getCurrentState());
                
            
            

        
        //      /*****************************************************************************************************************************************************
        /********************************                                Furniture working                  *********************************************
        ******************************************************************************************************************************************************/
        
        
         
    

                       
//            ps = (PresenceSensor) ubik.getBuilding().getFloor(floor).getDeviceHandler().getDeviceByName("36");
//            if(ps.thereAnyActivity()){
//                System.out.println(ps.getName()+ " " + ps.activity);
//                if(actualActivity.equals("")){
//                    actualActivity = String.valueOf(ps.activity);                     
//                }
//                System.out.println(actualActivity + " - " + String.valueOf(ps.activity));
//                if(!actualActivity.equals(String.valueOf(ps.activity))){
//                    Calendar cal = Calendar.getInstance();
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                    String dateTime = sdf.format(cal.getTime());
//                    try{
//                        try{
//                            Class.forName("com.mysql.jdbc.Driver");
//                        }catch(ClassNotFoundException e){
//                            System.err.println(e);
//                        }
//                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/db_datafromsimulation","root","1234");
//                        Statement stmt = conn.createStatement();
//                        String query = "insert into measure values(default,2," + ps.getName() + ",";
//                        actualActivity = String.valueOf(ps.activity);
//                            if(String.valueOf(ps.activity).equals("true")){
//                                query += "0,1,";
//                            }else{
//                                query += "1,0,";
//                            }
//                        query += "'" + dateTime + "');";
//                        System.out.println(query);
//    //                    stmt.executeUpdate(query);
//                    }catch(SQLException se){
//                        System.err.println(se);
//                    } 
//                }                
//            }
            
            
//        
     
        
        if (pf == null) {//generate pathfidner and goals in first step
            pf = new Pathfinder(this);
            //pf = new PathfinderThread(this);
            generateGoals();
        }
 
        if (goals.isEmpty()) {//if no remaining goals, put in red and do not follow      
            return;
        }
       
        
        if (currentGoal == null || pf.isInGoal()) {
            
//            long startTime = System.currentTimeMillis(); //fetch starting time
//                        while(false||(System.currentTimeMillis()-startTime)<5000)
//            {
//                // do something
//            }  
            //this.person3DModel.setVisible(false);
               List<Furniture> furnitures = (List<Furniture>) ubik.getBuilding().getFloor(floor).getFurnitureHandler().getFurnitures();
            for(int i=0;i<furnitures.size();i++){
           //System.out.println(furnitures.get(i).getName() + " + " + "index# "+ i + "(" + furnitures.get(i).getCenter().x + "," + furnitures.get(i).getCenter().y +")");
//            furnitures.get(i).getFurniture3DModel().setColor(Color.RED.getRGB());          
       }
                   

              //Kettle (19) position
              MutableInt2D kettlePosition = new MutableInt2D(15, 37);
                if(this.getPosition().equals( kettlePosition)){
                //    System.out.println("not ok " + this.getPosition()); 
                    furnitures.get(2).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    String query = "insert into measure values(default,2," + furnitures.get(2).getFurniture3DModel().getName() + ",0,1,'" + dateTime + "');";
                    System.out.println(query);
                    database.executeUpdate(query);
                    System.out.println("Kettle on");
                } 
                //CupBoard Position
                MutableInt2D cupBoardPosition = new MutableInt2D(11,27);
                if(this.getPosition().equals( cupBoardPosition)){
                //    System.out.println("not ok " + this.getPosition()); 
                    furnitures.get(33).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    String query = "insert into measure values(default,2," + furnitures.get(33).getFurniture3DModel().getName() + ",0,1,'" + dateTime + "');";
                    this.waitingTime(4000);
                    furnitures.get(33).getFurniture3DModel().setColor(Color.YELLOW.getRGB());
                    String query1 = "insert into measure values(default,2," + furnitures.get(33).getFurniture3DModel().getName() + ",1,0,'" + dateTime + "');";
                    System.out.println(query);
                    System.out.println(query1);
                    database.executeUpdate(query);
                    database.executeUpdate(query1);
                    System.out.println(" Cupbaord on");
                } 
                
                // Mug Kitchen
                MutableInt2D mug = new MutableInt2D(11,37);
                if(this.getPosition().equals( mug)){
                //    System.out.println("not ok " + this.getPosition()); 
                    //furnitures.get(3).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    furnitures.get(22).getFurniture3DModel().setVisible(true);
                    System.out.println(" mug on");
                } 
                // Mug Taken
                MutableInt2D mugAgain = new MutableInt2D(14,37);
                if(this.getPosition().equals( mugAgain)){
                //    System.out.println("not ok " + this.getPosition()); 
                    //furnitures.get(3).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    furnitures.get(2).getFurniture3DModel().setColor(Color.lightGray.getRGB());
                    String query = "insert into measure values(default,2," + furnitures.get(2).getFurniture3DModel().getName() + ",1,0,'" + dateTime + "');";
                    System.out.println(query);
                    database.executeUpdate(query);
                    System.out.println("Kettle on");
                    this.waitingTime(2000);
                    furnitures.get(22).getFurniture3DModel().setVisible(false);
                    System.out.println(" mug taken");
                } 
                // Front of the Milk
                 MutableInt2D milk = new MutableInt2D(13,37);
                if(this.getPosition().equals( milk)){
                //    System.out.println("not ok " + this.getPosition()); 
                    //furnitures.get(3).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    furnitures.get(25).getFurniture3DModel().setVisible(true);
                    this.waitingTime(2000);
                    furnitures.get(25).getFurniture3DModel().setVisible(false);
                    System.out.println(" Milk on");
                } 
                //Front of the Fridger
                MutableInt2D frigo = new MutableInt2D(10,37);
                if(this.getPosition().equals( frigo)){
                //    System.out.println("not ok " + this.getPosition()); 
                    //furnitures.get(3).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    furnitures.get(21).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    String query = "insert into measure values(default,2," + furnitures.get(21).getFurniture3DModel().getName() + ",0,1,'" + dateTime + "');";
                    this.waitingTime(2000);
                    furnitures.get(21).getFurniture3DModel().setColor(Color.RED.getRGB());
                    String query1 = "insert into measure values(default,2," + furnitures.get(21).getFurniture3DModel().getName() + ",1,0,'" + dateTime + "');";
                    System.out.println(query);
                    System.out.println(query1);
                    database.executeUpdate(query);
                    database.executeUpdate(query1);
                    System.out.println(" Fridge on");
                }
                
                MutableInt2D frigoAgain = new MutableInt2D(9,37);
                if(this.getPosition().equals( frigoAgain)){
                //    System.out.println("not ok " + this.getPosition()); 
                    //furnitures.get(3).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    //furnitures.get(23).getFurniture3DModel().setVisible(false);
                    furnitures.get(21).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    String query = "insert into measure values(default,2," + furnitures.get(21).getFurniture3DModel().getName() + ",0,1,'" + dateTime + "');";
                    this.waitingTime(2000);
                    furnitures.get(21).getFurniture3DModel().setColor(Color.RED.getRGB());
                    String query1 = "insert into measure values(default,2," + furnitures.get(21).getFurniture3DModel().getName() + ",1,0,'" + dateTime + "');";
                    System.out.println(query);
                    System.out.println(query1);
                    database.executeUpdate(query);
                    database.executeUpdate(query1);
                    System.out.println(" Fridge again on");
                }
                
                // Sitting on the Chair
                 MutableInt2D  chair = new MutableInt2D(37,10);
                if(this.getPosition().equals( chair)){
                //    System.out.println("not ok " + this.getPosition()); 
                    //furnitures.get(3).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    this.person3DModel.setVisible(false);
                    furnitures.get(23).getFurniture3DModel().setVisible(true);
                    furnitures.get(24).getFurniture3DModel().setVisible(true);
                    String query = "insert into measure values(default,2," + furnitures.get(3).getFurniture3DModel().getName() + ",1,0,'" + dateTime + "');";
                    //String query1 = "insert into measure values(default,2," + furnitures.get(22).getFurniture3DModel().getName() + ",1,0,'" + dateTime + "');";
                    System.out.println(query);
                    //System.out.println(query1);
                    database.executeUpdate(query);
                    //database.executeUpdate(query1);
                    System.out.println(" User Sitting on the chair");
                    
                    this.waitingTime(7000);
                    furnitures.get(23).getFurniture3DModel().setVisible(false);
                    this.person3DModel.setVisible(true);
                    String query1 = "insert into measure values(default,2," + furnitures.get(3).getFurniture3DModel().getName() + ",1,0,'" + dateTime + "');";
                    System.out.println(query1);
                    //System.out.println(query1);
                    database.executeUpdate(query1);
                    //database.executeUpdate(query1);
                    System.out.println(" User standing");
                }
                
                MutableInt2D  clothing = new MutableInt2D(25,39);
                if(this.getPosition().equals( clothing)){
                //    System.out.println("not ok " + this.getPosition()); 
                    //furnitures.get(3).getFurniture3DModel().setColor(Color.GREEN.getRGB());
                    //this.person3DModel.setRol("Teacher");
                    System.out.println(" User going out");
                    this.stop();
                } 
                
        
//                else{
////                    List<Furniture> furnitures = (List<Furniture>) ubik.getBuilding().getFloor(floor).getFurnitureHandler().getFurnitures();
//                    
//                    System.out.println("ok");
//                //    List<Furniture> furnitures = (List<Furniture>) ubik.getBuilding().getFloor(floor).getFurnitureHandler().getFurnitures();
//                    furnitures.get(2).getFurniture3DModel().setColor(Color.GREEN.getRGB());
//                    
//                }              
//            
            //if no current goal or it is in a goal, remove current goal and replace it
            goals.remove(currentGoal);
            currentGoal=null;
            if (!goals.isEmpty()) {
                currentGoal = goals.get(0);
                pf.setGoalAndGeneratePath(currentGoal);
            }else{
                this.setColor(Color.WHITE); //blue to say that agent has accomplished all goals
                this.stop();
            }            
        }
        else{
            pf.step(state);//take steps to the goal (step can regenerate paths)
        }
     
 
    }
    // The method will stop the user for certain "waiting= milisecond" time.
    public void waitingTime(int waiting){
        
        try{
                  Thread.sleep(waiting);
              
              }
              catch(InterruptedException e){
                  
              }
    }
    
    
  
 
    
 
   
    
     private void generateGoals() {
         
        goals = new ArrayList();
//        goals.add(PositionTools.getRoom(this,rooms[8]).getCenter());              
//        
//       //goals.add(PositionTools.getRoom(this,rooms[2]).getCenter());
//        
//        goals.add(PositionTools.getRoom(this,rooms[1]).getCenter());
//        
////        goals.add(PositionTools.getRoom(this,rooms[2]).getCenter());
////        goals.add(PositionTools.getRoom(this,rooms[3]).getCenter());
////        goals.add(PositionTools.getRoom(this,rooms[4]).getCenter());
//        goals.add(PositionTools.getRoom(this,rooms[5]).getCenter());
//        goals.add(PositionTools.getRoom(this,rooms[0]).getCenter());
//        waitingTime(2000);
//        goals.add(PositionTools.getRoom(this,rooms[6]).getCenter());        
//        goals.add(PositionTools.getRoom(this,rooms[4]).getCenter());
//        System.out.println(PositionTools.getRoom(this,rooms[7]).getCenter().x + " , " + PositionTools.getRoom(this,rooms[7]).getCenter().y);
        //Front of the Kattle
            List<Furniture> furnitures = (List<Furniture>) ubik.getBuilding().getFloor(floor).getFurnitureHandler().getFurnitures();
            for(int i=0;i<furnitures.size();i++){
           //System.out.println(furnitures.get(i).getName() + " + " + "index# "+ i + "(" + furnitures.get(i).getCenter().x + "," + furnitures.get(i).getCenter().y +")");
//            furnitures.get(i).getFurniture3DModel().setColor(Color.RED.getRGB());          
       }
            furnitures.get(0).getFurniture3DModel().setColor(Color.GREEN.getRGB());
            //this.waitingTime(1000);
            furnitures.get(26).getFurniture3DModel().setVisible(false);
            furnitures.get(27).getFurniture3DModel().setVisible(true);
            furnitures.get(0).getFurniture3DModel().setColor(Color.YELLOW.getRGB());
            this.waitingTime (500);
            furnitures.get(27).getFurniture3DModel().setVisible(false);
            this.person3DModel.setVisible(true);
            
            
        
        // Front of the Kattle
        goals.add(new Int2D(15,37));
        //Front of the Cupboard
        goals.add(new Int2D(11,27));
        //Front of the Mug
        goals.add(new Int2D(11,37));
        //Front of the Fridge
        goals.add(new Int2D(10,37));
        //Front of the Milk
        goals.add(new Int2D(13,37));
        //Front of the fridge again
         goals.add(new Int2D(9,37));
         //Front of the mug again
        goals.add(new Int2D(14,37));
        
        
//        goals.add(new Int2D(14,37));
//        goals.add(new Int2D(10,37));
////        Furniture Ft = (Furniture) ubik.getBuilding().getFloor(floor).getFurnitureHandler().getFurniture(10, 39);
////        Ft.getFurniture3DModel().setColor(Color.GREEN.getRGB());
////        Ft.getFurniture3DModel().setColor(Color.GRAY.getRGB());
          goals.add(PositionTools.getRoom(this,rooms[1]).getCenter());
          
//        goals.add(new Int2D(14,37));
        //goals.add(new Int2D(37,10));
        //goals.add(PositionTools.getRoom(this,rooms[0]).getCenter());
//        goals.add(PositionTools.getRoom(this,rooms[5]).getCenter());
//        this.waitingTime(3000);
//        goals.add(PositionTools.getRoom(this,rooms[6]).getCenter());
//        goals.add(new Int2D(42,38));
//         this.waitingTime(8000);
//         goals.add(new Int2D(35,10));
//          this.waitingTime(5000);
//          goals.add(new Int2D(25,39));
       
         //goals.add(PositionTools.getRoom(this,rooms[10]).getCenter());
         
       // this.stop();
//        goals.add(new Int2D(31,34));
//        goals.add(new Int2D(31,7));
//        goals.add(new Int2D(41,38));
//        goals.add(new Int2D(31,7));
        //goals.add(new Int2D(11,32));
        //goals.add(new Int2D(14,32));
        
        
        
        
     //   goals.add(PositionTools.getRoom(this,rooms[5]).getCenter()); 
       
        
    }

    

    
    
    
    public static void setGlobalGoal(String globalGoal) {
        Worker.globalGoal= globalGoal;
        
    }
}