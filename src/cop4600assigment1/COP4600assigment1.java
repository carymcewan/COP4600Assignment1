// Cary McEwan, Brandon James, Hung Tran
// COP 4600
package cop4600assigment1;

/**
 *
 * @author Carrito
 */

import java.util.*; 
import java.io.*;

public class COP4600assigment1 {

    static CpuScheduler scheduler;
    
    // Function for reading in processes.in file and creating scheduler based on values
    public static void instantiateScheduler() throws FileNotFoundException {
        File file = new File("processes.in.txt");
        Scanner sc = new Scanner(file);
        sc.next();
        // schedulerProcessCount represents the amount of processes that will be run
        int schedulerProcessCount = Integer.parseInt(sc.next());
        sc.nextLine();
        sc.next();
        
        // schedulerRunFor represents the total time the scheduler will run for
        int schedulerRunFor = Integer.parseInt(sc.next());
        sc.nextLine();
        sc.next();
        
        // schedulerType represents the type of scheduler we will be using. Options are sjf, rr, fcfs
        String schedulerType = sc.next();
        sc.nextLine();
        sc.next();
        
        // schedulerQuantum represents the quantum value only needed for Round Robin
        int schedulerQuantum = Integer.parseInt(sc.next());

        // Instantiate scheduler with the values read in from process.in
        scheduler = new CpuScheduler(schedulerProcessCount,schedulerRunFor,schedulerType,schedulerQuantum);
        sc.nextLine();
        int i = 0;
        
        // Add each process to the schduler
        while(i < schedulerProcessCount) {
            sc.next();
            sc.next();
            // name represents the Process name
            String name = sc.next();
            sc.next();
            
            // arrival represents when the Proccess will arrive
            int arrival = Integer.parseInt(sc.next());
            sc.next();
            
            // burst represents the total burst of that Process
            int burst = Integer.parseInt(sc.next());
            
            // Add the process to the scheduler's set of processes
            scheduler.add(new Process(name,arrival,burst));
            i++;
        }
        sc.close();
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // Must be run to read file and instantiate the scheduler
        instantiateScheduler();
        
        // Function for running scheduler based on instantiated values.
        scheduler.run();
    } 
}
