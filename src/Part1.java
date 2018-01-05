import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Part1 extends JFrame
{
	public static void main(String[] args)
	{
		Part1 window = new Part1();
		window.setVisible(true);
		window.setSize(800, 700);
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);

		Container cont = window.getContentPane();
		cont.setLayout(new BorderLayout());

		//This panel will contain the grid and circle
		GridPanel gridPanel = new GridPanel();
		gridPanel.setPreferredSize(new Dimension(400, 500));
		cont.add(gridPanel, BorderLayout.PAGE_START);

		JLabel jLabel = new JLabel("Click, drag and release Mouse to draw a circle. Click 'Clear' to reset.");
		gridPanel.add(jLabel, BorderLayout.PAGE_START);
		
		// This panel will contain the Clear button
		JPanel buttonPanel = new JPanel();
		JButton button1;
		button1 = new JButton("Clear");
		buttonPanel.add(button1);
		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				// On click, clear everything
				gridPanel.clear();
			}
		});
		cont.add(buttonPanel, BorderLayout.CENTER);
	}
}

class GridPanel extends JPanel
{
	// This class represents the Grid
	private class Grid
	{
		// The 20*20 GridPoint instances
		private Point[][] points;

		public Grid()
		{
			// Initialize the 20*20 GridPoint instances on the window
			
			points = new Point[20][20];
			int startX = 200;
			int startY = 50;
			int d = 20;

			for(int i=0;i<20;i++)
			{
				int x = startX + (d*i);
				for(int j=0;j<20;j++)
				{
					int y = startY + (d*j);

					points[i][j] = new Point(x,y);
				}  
			}
		}  

		// Draw the points on the grid and if circle is present also do the necessary 
		// computation for the extrapolated points
		public void drawGrid(Graphics g, Circle circle) 
		{
			int rad = circle.rad, cx = circle.cx, cy = circle.cy; 
			double maxDist = -1, minDist = 400;
			Point maxPoint = null, minPoint = null;
			double avgDist = 10;

			for(int i=0;i<20;i++)
			{
				for(int j=0;j<20;j++)
				{
					Point p = points[i][j];

					// If circle is present then find GridPoints which lie on the circle
					if(circle.mode != 0)
					{
						// Calculate the distance between the point and the center of the circle
						double dist = Math.hypot(cx-p.x, cy-p.y);
						
						// If the distance is nearly equal to radius, then select it
						if(Math.abs(dist-rad) < avgDist)
						{
							// Selected point hence color it as blue. 
							g.setColor(Color.blue);
							
							// This find the max distance Point
							if(dist > maxDist)
							{
								maxDist = dist;
								maxPoint = p;
							}

							// This find the min distance Point
							if(dist < minDist)
							{
								minDist = dist;
								minPoint = p;
							}
						}
						else
						{
							// Selected point hence color it as grey. 
							g.setColor(Color.gray);
						}
					}
					// draw the point.
					p.draw(g);
				}  
			}

			if(maxPoint == null || minPoint == null)
			{
				return;
			}
			
			// Also draw the min and max circles if present
			g.setColor(Color.red);
			new Circle(cx, cy, (int) maxDist, 2).draw(g);
			g.setColor(Color.red);
			new Circle(cx, cy, (int) minDist, 2).draw(g);
		}

		// This class represents the GridPoints
		private class Point
		{
			// Represents the x and y co-ordinates
			public int x,y;

			public Point(int xx, int yy)
			{
				x = xx;
				y = yy;
			}

			// This function draws the point
			public void draw(Graphics g)
			{
				int r=2;
				g.fillOval(x-r,y-r,2*r,2*r);
			}
		}
	}

	// This class represents the circle if drawn
	private class Circle
	{
		// the center co-ordinates and radius of the circle
		private int cx,cy;
		private int rad;
		
		// This stores the mode of the circle
		// 0 - No Circle drawn
		// 1 - The circle is being drawn
		// 2 - The circle is present
		private int mode=0;

		public Circle()
		{
		}

		public Circle(int x, int y, int r, int m)
		{
			cx=x;
			cy=y;
			rad = r;
			mode  = m;
		}

		// Draw the circle
		public void draw(Graphics g) 
		{
			if(mode == 0)
			{
				return;
			}

			int x = cx-(rad);
			int y = cy-(rad);
			g.drawOval(x,y,2*rad,2*rad);
		}

		public void setXY(int x, int y)
		{
			cx=x;cy=y;
			mode=1;
		}

		// Set readius
		public void setRad(int rx, int ry)
		{
			rad = (int) Math.hypot(cx-rx, cy-ry);
			mode=2;
		}

		// delete the drawn circle
		public void reset()
		{
			cx=0;cy=0;rad=0;
			mode=0;
		}
	}

	private Circle circle = new Circle();

	private Grid grid;

	public GridPanel()
	{
		grid = new Grid();
		setBackground(Color.WHITE);

		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent m)
			{
				// if mouse pressed, the position is the center of the circle
				circle.setXY(m.getX(), m.getY());
				repaint();
			}

			public void mouseReleased(MouseEvent m)
			{
				// if mouse released, the position is the edge of the circle
				circle.setRad(m.getX(), m.getY());
				repaint();
			}
		});

		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent m)
			{
				// if mouse dragged, the position is the current egde of the circle
				circle.setRad(m.getX(), m.getY());
				repaint();
			}
		});

		repaint();
	}

	// Draw the GridPanel Component
	public void paintComponent(Graphics g)
	{ 
		super.paintComponent(g);
		circle.draw(g);
		grid.drawGrid(g, circle);
	}

	// resets everything
	public void clear() 
	{
		circle.reset();
		repaint();
	}
}
