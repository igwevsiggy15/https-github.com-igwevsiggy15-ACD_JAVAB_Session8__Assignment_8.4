package Assignment8_4;

import java.util.Scanner;

class BusSeats{
	volatile String[] names;
	volatile int booked;
	
	
	BusSeats(int seats) {
		this.booked = 0;
		this.names = new String[seats];
	}
	
	
	synchronized boolean reserveSeats(String[] passengers) {
		if (passengers.length > 0 && (this.names.length - booked) >= passengers.length) {
			for (String string : passengers) {
				names[booked] = string;
				booked++;
			}
			return true;
		}
		return false;
	}
	
	synchronized int getTotalReserved() {
		return this.booked;
	}
}

class ScanIn{
	private volatile Scanner sc;
	
	public ScanIn() {
		this.sc = new Scanner(System.in);
	}
	
	synchronized String getString(String prompt) {
		if (sc != null) {
			try {
				System.out.print(prompt);
				return sc.nextLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	synchronized int getInt(String prompt) {
		try {
			System.out.println(prompt);
			int num = sc.nextInt();
			sc.nextLine();
			return num;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	synchronized void close() {
		sc.close();
	}
}

class ReservationThread extends Thread{

	int num;
	BusSeats s;
	String[] names;
	
	public ReservationThread(BusSeats s, String[] names, String name) {
		super(name);
		this.s = s;
		this.names = names;
		this.num = names.length;
	}
	
	@Override
	public void run() {
		boolean reserved = false;
		reserved = s.reserveSeats(names);
		System.out.println(this.getName()+ ": Processing seat reservation for: " + num + "seats.");
		if (reserved) {
			String n = "";
			for(String name : names) {
				n += "\n" + name;
			}
			System.out.println(this.getName()+ ": Reservation complete."
					+ "\nNumber of seats: " + num
					+ "\nNames:" + n);
		} else {
			System.out.println(this.getName()+ ": Reservation failed.");
		}
	}
}

public class Reservation {
	public static void main(String args[]) {
		//Bus has 89 seats
		BusSeats bs = new BusSeats(89);
		Thread[] ts = new Thread[30];
		
		//makes 30 threads that reserves 3 seats each  one of the reservations should fail
		//so expected 87 seats reserved.
		for (int i = 0; i < ts.length; i++) {
			ts[i] = new ReservationThread(bs, new String[]{"Mike"+(i+1), "Iggy"+ (i+1), "Say"+ (i+1)}, "Reservation " + (i+1));
			ts[i].start();
		}
		
		for(int i=0; i < ts.length; i++) {
			try {
				ts[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//printing report
		System.out.println("\n\n\nREPORT: Number of Seats reserved = " + bs.booked);
		System.out.println("Names:\n");
		int count = 0;
		for(String name: bs.names) {
			System.out.print(name + "\t");
			count++;
			if (count == 4) {
				System.out.println();
				count =0;
			}
		}
	}
	
}