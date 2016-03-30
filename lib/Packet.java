package lib;

/*
 * The packet class provides packetization functions and class elements. 
 * It has all the necessary functions to parse and create a packet.
 * 
 * */

public class Packet {
	private int type;
	private String data;
	
	public Packet(int type, String data){
		this.type = type;
		this.data = data;
	}
	
	public int getOption(){
		return this.type;
	}
	
	public String getData(){
		return this.data;
	}
	
	public Packet(String payload){
		String[] arr = payload.split("\\|");
		//System.out.println(Arrays.toString(arr));
		this.type = Integer.parseInt(arr[2]);
		this.data = arr[6];
	}
	
	public String toString(){
		return getPayload();
	}
	
	public String getPayload(){
		return "|type|"+type+"|/type||data|"+data+"|/data|";
	}
	
	public static void main(String[] args) {
		//Packet p = new Packet("|option|2|/option||data|1,Intro to Algos,8,mp3;2,Intro to networks,5,mp4;3,Intro to AI,6,mp4|data|");
		//System.out.println(p);
		//String s = ":abc:xyz";
		//System.out.println(Arrays.toString(s.split(":")));
	}
	
}
