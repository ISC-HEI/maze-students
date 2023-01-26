package hevs.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

interface Renderable {
	public void render(Graphics2D g);
}

/**
 * A graphics display that uses double buffering and does not bug
 * 
 * @author Pierre-Andre Mudry
 * @version 2.0
 */
public abstract class ImageGraphicsMultiBuffer implements Renderable {
	private static final long serialVersionUID = 6832022057915586803L;

	protected static Color[] COLORS = new Color[] { Color.red, Color.blue, Color.green, Color.white, Color.black,
			Color.yellow, Color.gray, Color.cyan, Color.pink, Color.lightGray, Color.magenta, Color.orange,
			Color.darkGray };

	protected int fWidth, fHeight;

	protected JFrame mainFrame;
	protected BufferStrategy bufferStrategy = null;
	protected static final int numBuffers = 2;

	// BufferedImage backgroundBitmap = null;

	public ImageGraphicsMultiBuffer(String title, int width, int height) {
		this(title, width, height, true);
	}

	public ImageGraphicsMultiBuffer(String title, int width, int height, boolean hasDecoration) {
		// Shall we try a different look for the window ?
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e1) {
		}

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();

		try {
			// loadImage(backGroundFilePath);
			GraphicsConfiguration gc = device.getDefaultConfiguration();
			mainFrame = new JFrame(title, gc);
			mainFrame.setResizable(false);
			mainFrame.setIgnoreRepaint(true);
			mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			mainFrame.getContentPane().setPreferredSize(new Dimension(width, height));
			mainFrame.setUndecorated(!hasDecoration);
			mainFrame.pack();

			// Get the size of the screen
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

			// Determine the new location of the window
			fWidth = mainFrame.getSize().width;
			fHeight = mainFrame.getSize().height;
			int x = (dim.width - fWidth) / 2;
			int y = (dim.height - fHeight) / 2;

			// Move the window
			mainFrame.setLocation(x, y);
			mainFrame.setVisible(true);
			mainFrame.createBufferStrategy(numBuffers);

			while (bufferStrategy == null)
				bufferStrategy = mainFrame.getBufferStrategy();

			// Do the rendering
			class RenderThread extends SwingWorker<String, Object> {
				@Override
				public String doInBackground() {
					try {
						long lastLoopTime = System.currentTimeMillis();

						while (true) {
							long delta = System.currentTimeMillis() - lastLoopTime;
							lastLoopTime = System.currentTimeMillis();

							render();

							/**
							 * FIXME constant time rendering, not ideal. Should
							 * take into account the refresh rate
							 */
							Thread.sleep(10);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return "Nothing !";
				}

				@Override
				protected void done() {
				}
			}

			// Launch the rendering thread
			new RenderThread().execute();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Quite self-explanatory
	 */
	public void destroy() {

		// this will make sure WindowListener.windowClosing() et al. will be
		// called.
		WindowEvent wev = new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);

		// this will hide and dispose the frame, so that the application quits
		// by itself if there is nothing else around.
		mainFrame.setVisible(false);
		mainFrame.dispose();	
	}

	private void render() {
		Graphics2D g = null;

		try {
			g = (Graphics2D) bufferStrategy.getDrawGraphics();

			// Enable antialiasing for shapes
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			// Antialias for text
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

			g.setBackground(Color.white);
			g.clearRect(0, 0, fWidth, fHeight);

			// Call the interface method
			render(g);

			bufferStrategy.show();

		} finally {
			g.dispose();
		}

		// Shows the contents of the backbuffer on the screen.
		bufferStrategy.show();

		Toolkit.getDefaultToolkit().sync();
	}

	private void checkPerformance() {
		Rectangle bounds = mainFrame.getBounds();

		for (float lag = 200.0f; lag > 0.00000006f; lag = lag / 1.33f) {
			for (int i = 0; i < numBuffers; i++) {
				Graphics g = bufferStrategy.getDrawGraphics();

				if (!bufferStrategy.contentsLost()) {
					g.setColor(COLORS[i]);
					g.fillRect(0, 0, bounds.width, bounds.height);
					bufferStrategy.show();
					g.dispose();
				}
				try {
					Thread.sleep((int) lag);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public static void main(String args[]) {
		new ImageGraphicsMultiBuffer("Hello", 320, 320) {
			int i = 1;
			int direction = 1;

			@Override
			public void render(Graphics2D g) {
				g.fillRect(10 + i, 10 + i, 100 + i, 100 + i);

				i += direction;

				if (i > 100 || i == 0) {
					direction *= -1;
				}
			}
		};

	}
}