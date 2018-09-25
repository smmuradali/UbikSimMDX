/*
* 
* 
* This file is part of EscapeSim. EscapeSim is a UbikSim library. 
* 
* EscapeSim has been developed by members of the research Group on 
* Intelligent Systems [GSI] (Grupo de Sistemas Inteligentes), 
* acknowledged group by the  Technical University of Madrid [UPM] 
* (Universidad Politécnica de Madrid) 
* 
* Authors:
* Mercedes Garijo
* Geovanny Poveda
* Emilio Serrano
* 
* 
* Contact: 
* http://www.gsi.dit.upm.es/;
* 
* 
* 
* EscapeSim, as UbikSim, is free software: 
* you can redistribute it and/or modify it under the terms of the GNU 
* General Public License as published by the Free Software Foundation, 
* either version 3 of the License, or (at your option) any later version. 
*
* 
* EscapeSim is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with VoteSim. If not, see <http://www.gnu.org/licenses/>
 */

package sim.app.ubik.behaviors.testbehaviors;

import java.util.ArrayList;
import sim.app.ubik.behaviors.Automaton;
import sim.app.ubik.behaviors.DoNothing;
import sim.app.ubik.behaviors.Move;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.behaviors.escape.Disappear;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.people.Person;
import sim.app.ubik.people.TestPerson;
import sim.engine.SimState;
import ubiksimdist.EscapeSim;
import sim.app.ubik.Ubik;

public class MoveAllRoomsAutomaton extends Automaton{
 
    protected String rooms[];
    
    /**
     * Person whose behaviour is modelled in this automaton
     */
    protected TestPerson p =  (TestPerson) personImplementingAutomaton; 
    /**
     * Object with the simulation
     */
    protected Ubik sim=  p.getUbik();
    
    
 
    
    

    
	
    /**
     * Constructor. Since it is the main automaton, no priority is needed (there are not other automata at the same level)
     * @param personImplementingAutomaton 
     */
   public MoveAllRoomsAutomaton(Person personImplementingAutomaton) {
        super(personImplementingAutomaton);
 
    }
   
   /**
    * Default behaviour if pending transitions are empty: do nothing one step.
    * @param simState
    * @return 
    */
    @Override
    public Automaton getDefaultState(SimState simState) {
        return new DoNothing(p,0,1,"doNothing");
    }
    
    public void getAllRooms(){
        int i = 0;
        ArrayList<Room> listRooms = PositionTools.getRooms(personImplementingAutomaton);
        rooms = new String[listRooms.size()];
        for(Room r : listRooms){
            String roomName = r.getName();
            rooms[i] = roomName;
            i++;
        }
    }
     
      /**
       * This fills the pending transition list.
       * If the agent is not going to exit, plan to go to the closest stairs (priority of 10) and plan to disappear
       * 
       * Besides, since this method is called in each step, if the person is close to fire, the counter in the monitor agent is incremented
       * @param simState
       * @return 
       */
    @Override
    public ArrayList<Automaton> createNewTransitions(SimState simState) {
      ArrayList<Room> ro= PositionTools.getRooms(personImplementingAutomaton);
      ArrayList<Automaton> r=null;
      if(!this.isTransitionPlanned("moveAllRooms") ){
        r=new ArrayList<Automaton>();
        for (int n = 0; n < ro.size(); n++){
            Room hab = ro.get(n);
            r.add(new Move(p, 10, -1, "moveAllRooms", hab));
           
        }
         r.add(new Disappear(p, "disappear", null));
             
      } 
            
      return r;
    }

}
