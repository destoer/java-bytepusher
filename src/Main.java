import java.awt.*;
import javax.swing.*;
import java.awt.image.*;


class Main extends JPanel
{
	static public BytePusher vm;

	static Main panel = new Main();
	BufferedImage img =  new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
	void draw()
	{
		repaint();
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		// render our imagae from the palette
		
		for(int x = 0; x < 256; x++)
		{
			for(int y = 0; y < 256; y++)
			{
				int unsigned = vm.mem[(vm.mem[5] << 16)+(y << 8) + x] & 0xff;
				img.setRGB(x,y,vm.pal[unsigned]);
			}
		}

		
		g.drawImage(img,0,0,256,256,null);
	} 

	public static void main(String[] args) throws Exception
	{
		if(args.length != 1)
		{
			System.out.printf("usage: <program name> <file to open>\n");
			return;
		}

		vm = new BytePusher(args[0]);

		var frame = new JFrame("Bytepusher");
		frame.setSize(256,285);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.setPreferredSize(new Dimension(256,256));
		frame.add(panel);

		// probably a less jank way to do this
		final int fps = 60; 
		final int screen_ticks_per_frame = 1000 / fps;
		long next_time;

		while(true)
		{
			next_time = System.currentTimeMillis() + screen_ticks_per_frame;

			vm.run();
			panel.draw();


			final long now = System.currentTimeMillis();

			if(now <= next_time)
			{
				Thread.sleep(next_time-now);
			}
		}
	}
}
