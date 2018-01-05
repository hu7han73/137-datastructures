package onethreeseven.datastructures;

import onethreeseven.jclimod.CLIProgram;

/**
 * Entry point for running the commands of solely this module.
 * @author Luke Bermingham
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("You are running the data-structures module, type lc for the relevant commands.");

        CLIProgram program = new CLIProgram();
        program.startListeningForInput();


    }

}
