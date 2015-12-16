package nettyServer;

import java.util.Scanner;

public class LaunchClass {
	
	public static void main(String[] args) {
		LaunchClass launch = new LaunchClass();
		launch.printMainCommands();
		System.out.println("Choose command:");
		Scanner scan = new Scanner(System.in);
		while(!scan.hasNextInt()) {
			if(scan.hasNextInt()) {
				int command = scan.nextInt();
				System.out.println(command);
				break;
			}
		}
		scan.close();
	}
	
	private void printMainCommands() {
		System.out.println("<------Netty Server App----->");
		System.out.println("|                           |");
		System.out.println("| To start server press \"1\".|");
		System.out.println("| To exit press \"0\".        |");
		System.out.println("|                           |");
		System.out.println("<--------------------------->\n");
	}
}
