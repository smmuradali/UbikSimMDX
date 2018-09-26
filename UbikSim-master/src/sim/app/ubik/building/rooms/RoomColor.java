/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sim.app.ubik.building.rooms;

/**
 *
 * @author SA2305
 */
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import annas.graph.DefaultArc;
import annas.graph.Graph;

import sim.app.ubik.graph.Node;
import sim.app.ubik.Ubik;
import ubik3d.model.Home;
import ubik3d.model.Room;

public class RoomColor {
    protected Ubik ubik;
    protected List<Room> rooms;
    protected Color color;
    protected Room room3DModel;
    protected int floor;
    protected String name;
    public RoomColor(int floor, Ubik ubik, Room model){
        this.floor=floor;
        this.ubik=ubik;
        this.name=model.getName();
        this.room3DModel=model;
        
    }
    
    public Room getroom3DModel(){
        return room3DModel;
    }
    
}
