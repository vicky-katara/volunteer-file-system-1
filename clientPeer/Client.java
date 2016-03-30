package clientPeer;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
	static boolean debugFlag=true;

	Client(String serverIP, int portNumber){
		// Connect to the Server and ask for host names available to store files.
	}

	public static void main(String[] args) {
		String[] connectionInfo = new URLReader().getConnectionString().split(":");
		//		new RequestReceiver(requestReceiverPortNumber).start();
		Client c =new Client(connectionInfo[0],Integer.parseInt(connectionInfo[1]));
		main_menu();
	}

	private static void main_menu() {
		Scanner in = new Scanner(System.in);
		String parenPattern = "[(](.*)[)]";
		Pattern open = Pattern.compile("^open"+parenPattern+"$");
		Pattern close = Pattern.compile("^close"+parenPattern+"$");
		Pattern read = Pattern.compile("^read"+parenPattern+"$");
		Pattern write = Pattern.compile("^write"+parenPattern+"$");
		Pattern mount = Pattern.compile("^mount"+parenPattern+"$");
		Matcher m;
		String consoleString = "";

		while (!consoleString.equals("exit")){
			// contains the main menu options
			System.out.println("commands supported: open(),close(),read(),write(),mount(),exit");
			consoleString = in.nextLine();

			m = open.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("open command called with parameter "+ fileName);
				open(fileName);
				continue;
			}
			m = close.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("close command called with parameter "+ m.group(1));
				close(fileName);
				continue;
			}
			m = read.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("read command called with parameter "+ m.group(1));
				read(fileName);
				continue;
			}
			m = write.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("write command called with parameter "+ m.group(1));
				write(fileName);
				continue;
			}
			m = mount.matcher(consoleString);
			if(m.find()) {
				String fileName= m.group(1);
				if (debugFlag) System.out.println("mount command called with parameter "+ m.group(1));
				mount(fileName);
				continue;
			}
			if (consoleString.equals("exit")){
				System.out.println("exiting now.");
				//System.exit(0);
				break;
			}
			//catchall
			System.out.println("command "+consoleString+"not recognized, please try again.");
		
		}

		in.close();
	}

	private static void mount(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private static void write(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private static void read(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private static void close(String fileName) {
		// TODO Auto-generated method stub
		
	}

	private static void open(String fileName) {
		// TODO Auto-generated method stub

	}
}
