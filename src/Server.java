import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server extends Thread {

	static int maxPlayers = 2;
	static ServerSocket srvs = null;
	static Socket sock;
	static BufferedReader bir[] = new BufferedReader[maxPlayers];
	static PrintWriter pw[] = new PrintWriter[maxPlayers];
	static int count = 0;
	static int temp, x, y, x1, y1;
	static String playerList = "";
	static String playerStatus = "";
	static String input = "";
	static String welcome = "";
	static Boolean first = true;
	static Point koord;
	static LinkedList<Point> punkter[];

	public static void main(String[] args) {

		try {
			srvs = new ServerSocket(26626);
		} catch (IOException ex) {
			System.out.println("Error creating server socket");
			System.exit(0);
		}

		Socket[] clients = new Socket[maxPlayers];
		playerList = "";
		first = true;

		try {

			// Repeat until 2th players joined the game
			while (count < maxPlayers) {
				System.out.println("Calling for Player " + (count + 1));

				clients[count] = srvs.accept();

				// Create communication to the new client
				pw[count] = new PrintWriter(clients[count].getOutputStream());
				bir[count] = new BufferedReader(new InputStreamReader(
						clients[count].getInputStream()));

				// Send status til klienterne
				if (first == true) {
					pw[count].println("Connected as player " + (count + 1)
							+ "\nPlease wait");
					pw[count].flush();
					System.out
							.println("Player " + (count + 1) + " connected\n");
					first = false;

				} else {
					pw[count].println("Connected as player " + (count + 1)
							+ "\nPlease wait");
					pw[count].flush();
					System.out
							.println("Player " + (count + 1) + " connected\n");

				}
				count++;

			}

			if (count == maxPlayers) {
				for (int i = 0; i < count; i++) {
					pw[i].println("All players connected. Starting game!");
					pw[i].flush();
				}
			}

			while (true) {

				String s = bir[0].readLine();
				System.out.println("Player 1:");
				System.out.println(s);

				String s1 = bir[1].readLine();
				System.out.println("Player 2:");
				System.out.println(s1);

				if (s != null) {
					Scanner sc = new Scanner(s);
					x = sc.nextInt();
					y = sc.nextInt();

				} else {
					pw[0].close();
					bir[0].close();
					clients[0].close();
					pw[1].close();
					bir[1].close();
					clients[1].close();
				}

				if (s1 != null) {
					Scanner sc = new Scanner(s1);
					x1 = sc.nextInt();
					y1 = sc.nextInt();

				} else {
					pw[1].close();
					bir[1].close();
					clients[1].close();
					pw[0].close();
					bir[0].close();
					clients[0].close();
				}

				pw[0].println(Integer.toString(x) + " " + Integer.toString(y));
				pw[0].println(Integer.toString(x1) + " " + Integer.toString(y1));
				pw[0].flush();

				pw[1].println(Integer.toString(x1) + " " + Integer.toString(y1));
				pw[1].println(Integer.toString(x) + " " + Integer.toString(y));
				pw[1].flush();

			}

		} catch (IOException ex) {
			System.out.println("Error accepting from socket");
			System.exit(0);
		}
	}
}
