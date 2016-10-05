// Cary McEwan, Brandon James, Hung Tran
// COP 4600

package cop4600assigment1;

import java.util.*; 
import java.io.*;

// Process class with variables read in from process.in
// originalBurst and finishedTime used for calculating wait and turnaround time
public class Process {
    
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
