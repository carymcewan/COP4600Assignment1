// Cary McEwan, Brandon James, Hung Tran
// COP 4600
package cop4600assigment1;

/**
 *
 * @author Carrito
 */

import java.util.*; 
import java.io.*;

class Process {
    
    String name;
    int arrival;
    int burst;
    int originalBurst;
    int finishedTime;
    
    // Constructor for instantiating variables in Process class
    Process(String name, int arrival, int burst) {
        this.name = name;
        this.arrival = arrival;
        this.burst = burst;
        this.originalBurst = burst;
    }
    
}

class CpuScheduler {
    
    String newLine = System.getProperty("line.separator");
    int processCount;
    int runFor;
    String schedulerType;
    int quantum;
    FileWriter fileWriter;
    BufferedWriter bufferedWriter;
    // HashSet used for simply keeping track of all Processes
    HashSet<Process> processes = new HashSet<Process>();
    
    // Constructor used for instantiating CpuSheduler variables
    CpuScheduler(int processCount, int runFor, String schedulerType, int quantum) {
        this.processCount = processCount;
        this.runFor = runFor;
        this.schedulerType = schedulerType;
        this.quantum = quantum;
        
         try {
            // Assume default encoding.
            this.fileWriter = new FileWriter("processes.out.txt");

            // Always wrap FileWriter in BufferedWriter.
            this.bufferedWriter = new BufferedWriter(fileWriter);
        }
        catch(IOException ex) {
            System.out.println(
                "Error writing to file '"
                + "processes.out.txt" + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }
    
    // Public function used to add a Process to the scheduler's HashSet processes
    public void add(Process thisProcess) {
        processes.add(thisProcess);
    }
    
    // Public function used for testing that correct values have been read in from the processes.in file
    public void print() {
        for (Process e : processes) {
            System.out.println("Process Name: "+e.name+" Arrival Time: "+e.arrival+" Burst: "+e.burst);
        }
    }
    
    // Public function that decides which type of scheduler function will be used and then calls that private function
    // Also prints out the wait and turnaround times for each process.
    public void run() throws IOException {
       bufferedWriter.write(processCount + ((processCount == 1)? " process" : " processes" +newLine));
       //System.out.println(processCount + ((processCount == 1)? " process" : " processes")); 
       if(null != schedulerType)
            switch (schedulerType) {
            case "fcfs":
                bufferedWriter.write("Using First Come First Serve" +newLine);
                bufferedWriter.write(newLine);
                FCFS();
                break;
            case "rr":
                bufferedWriter.write("Using Round-Robin"+newLine);
                bufferedWriter.write("Quantum "+quantum+newLine);
                bufferedWriter.write(newLine);
                RR();
                break;
            case "sjf":
                bufferedWriter.write("Using Shortest Job First"+newLine);
                bufferedWriter.write(newLine);
                SJF();
                break;
        }
       bufferedWriter.newLine();
        for (Process p: processes)
            bufferedWriter.write(p.name+" wait "+(p.finishedTime-p.arrival-p.originalBurst)+ " turnaround "+(p.finishedTime-p.arrival)+newLine);
        bufferedWriter.close();
    }
    
    // First Come First Serve Function
    private void FCFS() throws IOException {
        // Create a queue full of processes. 
        // currentlyRunning is a boolean used to let us know if a process is currently running or not.
        // runningProcess is a temp variable that represents the process that is currently running.
        Queue<Process> q = new ArrayDeque<Process>();
        boolean currentlyRunning = false;
        Process runningProcess = new Process("TEMP",500,0);
        
        // For loop that executes for the total time the processor is supposed to run for
        for (int currentTime = 0; currentTime <= runFor ; currentTime++) {
            
            // Checks each process in our processes HashSet to see if it is currently the process' arrival time. If so, add it to the queue
            // and indicate that it has arrived.
            for (Process p : processes) {
                if (p.arrival == currentTime) {
                    q.add(p);
                    bufferedWriter.write("Time "+currentTime+": "+p.name+" arrived"+newLine);
                }
            }
            
            // If the current runningProcess' burst is 0, this means that it is finished running.
            // Therefore, indicate that it has finished and save its finishedTime for later calculations.
            // Set currentlyRunning to false so that a new Process can be selected from the queue.
            if (runningProcess.burst == 0) {
                if(runningProcess.name != "TEMP") {
                    bufferedWriter.write("Time "+currentTime+": "+runningProcess.name+" finished"+newLine);
                    runningProcess.finishedTime = currentTime;
                }     
                currentlyRunning = false;
            }
            
            // If a process is not currentlyRunning and our queue is not empty, we need to select a new process from the queue.
            // Inidicate that the new process has been selected and set currentlyRunning to true so that a new process will not be
            // selected until this process has finished.
            // If a process is not currentlyRunning and the queue is empty, this means that the process should be idle for this current time unit.
            if (!currentlyRunning) {
                if (!q.isEmpty()) {
                   runningProcess = q.remove();
                   bufferedWriter.write("Time "+currentTime+": "+runningProcess.name+" selected (burst "+runningProcess.burst+")"+newLine);
                   currentlyRunning = true; 
                }
                else
                    if (currentTime != runFor)
                        bufferedWriter.write("Time "+currentTime+": Idle"+newLine);
            }
            
            // Decrement the remaining burst for the current runningProcess.
            runningProcess.burst--;
        }
        bufferedWriter.write("Finished at time "+runFor+newLine);
    }
    
    // Round Robin Function
    private void RR() throws IOException {
        // Create a queue full of processes. 
        // currentlyRunning is a boolean used to let us know if a process is currently running or not.
        // runningProcess is a temp variable that represents the process that is currently running.
        // int change is a variable that will indicate when to switch. When change equals our quantum value, we will reset the change value to 0
        // and switch to the next process in the queue.
        Queue<Process> q = new ArrayDeque<Process>();
        Process runningProcess = new Process("TEMP",500,500);
        int change = 0;
        boolean currentlyRunning = false;
        
        // For loop that executes for the total time the processor is supposed to run for
        for (int currentTime = 0; currentTime <= runFor ; currentTime++) {
            
            // Checks each process in our processes HashSet to see if it is currently the process' arrival time. If so, add it to the queue
            // and indicate that it has arrived.
            for (Process p : processes) {
                if (p.arrival == currentTime) {
                    q.add(p);
                    bufferedWriter.write("Time "+currentTime+": "+p.name+" arrived"+newLine);
                }
            }
            
            // If the current runningProcess' burst is 0 meaning that it is finished running, or the change value is equal to the quantum, this means
            // we need to switch processes.
            // If it is finished, meaning runningProcess.burst == 0, indicate that it has finished and save its finishedTime for later calculations.
            // If not, this means that we have hit the quantum so reset the change value to 0 and readd this process to the queue so it can run again.
            // Either way, set currentlyRunning to false so that a new Process can be selected from the queue.
            if (runningProcess.burst == 0 || change == quantum) {
                if(runningProcess.name != "TEMP" && runningProcess.burst == 0) {
                    bufferedWriter.write("Time "+currentTime+": "+runningProcess.name+" finished"+newLine);
                    runningProcess.finishedTime = currentTime;
                    runningProcess.burst = -1;
                }
                else {
                    q.add(runningProcess);
                }
                change = 0;
                currentlyRunning = false;
            }
            
            // If a process is not currentlyRunning and our queue is not empty, we need to select a new process from the queue.
            // Inidicate that the new process has been selected and set currentlyRunning to true so that a new process will not be
            // selected until this process has finished.
            // If a process is not currentlyRunning and the queue is empty, this means that the process should be idle for this current time unit.
            if (!currentlyRunning) {
                if (!q.isEmpty()) {
                   runningProcess = q.remove();
                   bufferedWriter.write("Time "+currentTime+": "+runningProcess.name+" selected (burst "+runningProcess.burst+")"+newLine);
                   currentlyRunning = true; 
                }
                else
                    if (currentTime != runFor)
                        bufferedWriter.write("Time "+currentTime+": Idle"+newLine);
            }
            
            // Decrement the current process' remaining burst and increase the change value.
            if (currentlyRunning) {
                runningProcess.burst--;
                change++;
            }
        }
        bufferedWriter.write("Finished at time "+runFor+newLine);
    }
    
    // Shortest Job First Function
    private void SJF() throws IOException {
        // Create a queue full of processes. 
        // currentlyRunning is a boolean used to let us know if a process is currently running or not.
        // runningProcess is a temp variable that represents the process that is currently running.
        // int change is a variable that will indicate when to switch. When change equals our quantum value, we will reset the change value to 0
        // and switch to the next process in the queue.
        HashSet<Process> arrivedProcesses = new HashSet<Process>();
        Process runningProcess = new Process("TEMP",500,500);
        boolean currentlyRunning = false;
        String lastProcess = "";
        
        // For loop that executes for the total time the processor is supposed to run for
        for (int currentTime = 0; currentTime <= runFor ; currentTime++) {
            
            // Checks each process in our processes HashSet to see if it is currently the process' arrival time. If so, add it to the queue
            // and indicate that it has arrived.
            for (Process p : processes) {
                if (p.arrival == currentTime) {
                    arrivedProcesses.add(p);
                    bufferedWriter.write("Time "+currentTime+": "+p.name+" arrived"+newLine);
                }
            }

            if (!arrivedProcesses.isEmpty()) {
                            
                if (runningProcess.burst == 0) {
                    if(runningProcess.name != "TEMP") {
                        bufferedWriter.write("Time "+currentTime+": "+runningProcess.name+" finished"+newLine);
                        runningProcess.finishedTime = currentTime;
                        arrivedProcesses.remove(runningProcess);
                    }
                    currentlyRunning = false;
                }
                int minBurst = 1000000;
                for (Process p : arrivedProcesses) {
                    if (p.burst < minBurst) {
                        minBurst = p.burst;
                        runningProcess = p;
                    }
                }
//                if (!lastProcess.equals(runningProcess.name))
                bufferedWriter.write("Time "+currentTime+": "+runningProcess.name+" selected (burst "+runningProcess.burst+")"+newLine);


                runningProcess.burst--;
//                lastProcess = runningProcess.name;
            }
            else if (currentTime != runFor)
                bufferedWriter.write("Time "+currentTime+": Idle"+newLine);
        }
        bufferedWriter.write("Finished at time "+runFor+newLine);
    }
    
}

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
