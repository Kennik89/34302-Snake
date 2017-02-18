import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

import javax.swing.*;

public class SnakeMainClient extends JFrame {

	// Version UID
	private static final long serialVersionUID = 1L;

	private static final long FRAME_TIME = 1000L / 50L;

	// Minimum længde
	private static final int MIN_SNAKE_LENGTH = 5;

	// Maks antal retninger
	private static final int MAX_DIRECTIONS = 3;

	// Initialisering af boardet
	private Board board;

	// Serverstuff
	static Socket MySocket;
	static BufferedReader bir;
	static PrintWriter pw;
	static Scanner console;
	static String x, x1;

	// Initialisering af sidepanel til score og chat og lækkerhed
	// private SidePanel side;

	private Random random;

	// Clock til gamelogikken
	private Clock logicTimer;

	// Gamestates
	private boolean isNewGame;
	private boolean isGameOver;

	// punkter for snake.
	private LinkedList<Point> snake;

	// punkter for retningen.
	private LinkedList<Direction> directions;

	private Point head, body, head1, body1;

	private SnakeMainClient() {
		super("Snake Client");
		// Set a BorderLayout on the main window
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		this.board = new Board(this);
		add(board, BorderLayout.CENTER);

		// KeyListener til at reagere på tasterne
		addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {

				switch (e.getKeyCode()) {

				case KeyEvent.VK_UP:
					if (!isGameOver) {
						if (directions.size() < MAX_DIRECTIONS) {
							Direction last = directions.peekLast();
							if (last != Direction.South
									&& last != Direction.North) {
								directions.addLast(Direction.North);
							}
						}
					}
					break;

				case KeyEvent.VK_LEFT:
					if (!isGameOver) {
						if (directions.size() < MAX_DIRECTIONS) {
							Direction last = directions.peekLast();
							if (last != Direction.East
									&& last != Direction.West) {
								directions.addLast(Direction.West);
							}
						}
					}
					break;

				case KeyEvent.VK_RIGHT:
					if (!isGameOver) {
						if (directions.size() < MAX_DIRECTIONS) {
							Direction last = directions.peekLast();
							if (last != Direction.West
									&& last != Direction.East) {
								directions.addLast(Direction.East);
							}
						}
					}
					break;

				case KeyEvent.VK_DOWN:
					if (!isGameOver) {
						if (directions.size() < MAX_DIRECTIONS) {
							Direction last = directions.peekLast();
							if (last != Direction.North
									&& last != Direction.South) {
								directions.addLast(Direction.South);
							}
						}
					}
					break;

				case KeyEvent.VK_ENTER:
					if (isNewGame || isGameOver) {
						resetGame();
					}
					break;

				}
			}
		});

		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void startGame() {

		// Først initierer vi alt vi skal bruge
		this.random = new Random();
		this.snake = new LinkedList<Point>();
		this.directions = new LinkedList<Direction>();
		this.logicTimer = new Clock(10.0f);
		this.isNewGame = true;

		// vi sætter timeren til pause med vilje
		logicTimer.setPaused(true);

		// Vi laver et loop til at holde øje med spillet og opdatere det
		while (true) {
			// Først skal vi have frame starttiden
			long start = System.nanoTime();

			// Så opdatere vi logicTimeren
			logicTimer.update();

			// Hvis en runde er gået i logicTimeren så skal spillet opdateres
			if (logicTimer.hasElapsedCycle()) {
				updateGame();
			}

			board.repaint();
			// side.repaint();

			long delta = (System.nanoTime() - start) / 1000000L;
			if (delta < FRAME_TIME) {
				try {
					Thread.sleep(FRAME_TIME - delta);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void updateGame() {

		TileType collision = updateSnake();

		try {
			x1 = bir.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (x1 != null) {

			Scanner sc = new Scanner(x1);
			int x = sc.nextInt();
			int y = sc.nextInt();

			// Opretter punktet
			body1 = new Point(x, y);

			// Printer punktet
			System.out.println("Player 2:" + body1);
		}

		board.setTile(body1, TileType.Body);

		if (collision == TileType.Body) {
			isGameOver = true;
			logicTimer.setPaused(true);
		} else if (collision == TileType.Head) {
			isGameOver = true;
			logicTimer.setPaused(true);
		} else if (collision == TileType.Space) {
			spawnSpace();
		}
	}

	public TileType updateSnake() {
		Direction direction = directions.peekFirst();

		Point head = new Point(snake.peekFirst());
		switch (direction) {
		case North:
			head.y--;
			break;

		case South:
			head.y++;
			break;

		case West:
			head.x--;
			break;

		case East:
			head.x++;
			break;

		case Retning:
			break;
		}

		if (head.x < 0 || head.x >= Board.COL_COUNT || head.y < 0
				|| head.y >= Board.ROW_COUNT) {
			return TileType.Body;
		}

		TileType old = board.getTile(head.x, head.y);

		if (old == TileType.Space && snake.size() > MIN_SNAKE_LENGTH) {
			Point tail = snake.removeFirst();
			board.setTile(tail, null);
			old = board.getTile(head.x, head.y);
		}

		// Opretter Point fra vores array til at sende til serveren
		final Point koordinat = snake.peekFirst();
		int xkord = koordinat.x;
		int ykord = koordinat.y;

		// Sender punktet
		pw.println(Integer.toString(xkord) + " " + Integer.toString(ykord));
		pw.flush();

		// Læser punktet fra serveren igen

		try {
			x = bir.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Opretter de to koordinater hvis der er modtaget noget
		if (x != null) {

			Scanner sc = new Scanner(x);
			int x = sc.nextInt();
			int y = sc.nextInt();

			// Opretter punktet
			body = new Point(x, y);

			// Printer punktet
			System.out.println("Player 1:" + body);

		}

		if (old != TileType.Body) {
			board.setTile(body, TileType.Body);
			snake.push(head);
			board.setTile(head, TileType.Head);
			if (directions.size() > 1) {
				directions.poll();
			}
		}
		return old;
	}

	private void resetGame() {

		// Reset NewGame og GameOver
		this.isNewGame = false;
		this.isGameOver = false;

		try {

			int indexx = random.nextInt(60) + 10;
			int indexy = random.nextInt(60) + 10;

			pw.println(Integer.toString(indexx) + " "
					+ Integer.toString(indexy));
			pw.flush();

			String headkord = bir.readLine();

			String headkord1 = bir.readLine();

			System.out.println("RECIEVED FROM PLAYER1:" + headkord);
			System.out.println("RECIEVED FROM PLAYER2:" + headkord1);

			if (headkord != null) {
				Scanner sc = new Scanner(headkord);
				int x = sc.nextInt();
				int y = sc.nextInt();

				head = new Point(x, y);

			}
			if (headkord1 != null) {
				Scanner sc = new Scanner(headkord1);
				int x = sc.nextInt();
				int y = sc.nextInt();

				head1 = new Point(x, y);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Genstart slangen og tilføj Head til vores LinkedList
		snake.clear();
		snake.add(head);

		// Ryd boardet og tilføj Head
		board.clearBoard();
		board.setTile(head, TileType.Head);
		board.setTile(head1, TileType.Head);

		// Ryd retning og vælg tilfældig retning at starte i
		int retnum = random.nextInt(4) + 1;
		Direction Retning = Direction.Retning;
		if (retnum == 1) {
			Retning = Direction.North;
		} else if (retnum == 2) {
			Retning = Direction.East;
		} else if (retnum == 3) {
			Retning = Direction.South;
		} else if (retnum == 4) {
			Retning = Direction.West;
		}
		directions.clear();
		directions.add(Retning);

		// Genstart logicTimeren
		logicTimer.reset();

		for (int i = 0; i < 30 || i > 30; i++) {
			spawnSpace();

		}

	}

	private void spawnSpace() {

		int index = random.nextInt(Board.COL_COUNT * Board.ROW_COUNT
				- snake.size());

		int freeFound = -1;
		for (int x = 0; x < Board.COL_COUNT; x++) {
			for (int y = 0; y < Board.ROW_COUNT; y++) {
				TileType type1 = board.getTile(x, y);
				if (type1 == null || type1 == TileType.Space) {
					if (++freeFound == index) {
						board.setTile(x, y, TileType.Space);
						break;
					}
				}
			}
		}
	}

	public boolean isNewGame() {
		return isNewGame;
	}

	public boolean isGameOver() {
		return isGameOver;
	}

	public Direction getDirection() {
		return directions.peek();
	}

	public static void main(String[] args) {

		try {

			// Connecting to server
			MySocket = new Socket("localhost", 26626);

			// Create input and output
			bir = new BufferedReader(new InputStreamReader(
					MySocket.getInputStream()));
			pw = new PrintWriter(MySocket.getOutputStream());

			// Read two outputs from the server
			System.out.println(bir.readLine()); // Connected as player #
			System.out.println(bir.readLine()); // Please wait

			// Holder her indtil maxPlayers er connected
			System.out.println(bir.readLine()); // All players connected.
												// Starting game!

			SnakeMainClient snake = new SnakeMainClient();
			snake.startGame();

		} catch (Exception e1) {
			JOptionPane.showMessageDialog(null,
					"Kunne ikke fÂ forbindelse til serveren!", "ERROR",
					JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}

	}

}