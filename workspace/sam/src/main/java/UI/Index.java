package UI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;


public class Index{

	public static void main(String[] args) {
		
		
		JLabel label1 = new JLabel("Label 1");
		label1.setBounds(50, 50, 50, 50);
		
		JLabel label2 = new JLabel("Label 2");
		label2.setBounds(50, 50, 50, 50);
		
		JButton btn = new JButton("click");
		btn.setBounds(20, 30,50,50);
		
		JFrame frame = new JFrame("MY PROJECT");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		frame.add(label2);
		frame.add(label1);
		frame.setSize(420,420);
		frame.setVisible(true);
	}

}
