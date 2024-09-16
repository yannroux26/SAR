package dev;

public class Channel {
	public int read(byte[] bytes, int offset, int length) {return 0;}

	public int write(byte[] bytes, int offset, int length) {
		System.out.print(bytes);
		return 0;}

	public void disconnect() {}

	public boolean disconnected() {return true;}
}