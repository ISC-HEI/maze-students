package hevs.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
 * <p>This class is a simple interface to draw figures and graphics. It is based on
 * the Swing framework and uses a {@link JFrame} as a drawing billboard.</p>
 * 
 * <p>Several methods are provided to paint things on the screen.</p>
 * 
 * <p>
 * Revision notes :
 * <ul>
 * <li>1.0 (roi) :<br>
 * <ul>
 *  <li>Initial revision, October 5th 2009
 * </ul> 
 * </li>
 * 
 * <li>
 * 1.1 (mui) :<br> 
 * <ul>
 *  <li>Changed displayFrame to DisplayFrame(naming convention) - Added new constructor for setting the window title.
 *  <li>Changed visibility of background as an accessible member {@link #backgroundColor}.
 *  <li>Made the clean function faster by using a clipping mask.
 *  <li>Added {@link #setPixel(int, int, Color)} and {@link #setPixel(int, int, int)} methods. 
 *  <li>Changed behaviour of the init function to display {@link #backgroundColor} as initial color.
 *  <li>Added default constructor behaviour to enable changing window title.
 *  <li>Added threaded pause function ({@link #pause(int)}).
 * </ul>
 * 
 * <li>
 * 1.2 (mui) :<br>
 * <ul>
 *  <li>Changed paint method for flicker-free and smoother behaviour (which also means that <code>repaint</code> calls have 
 *  been replaced by <code>invalidate</code> calls). 
 *  <li>Added rendering hints for nicer display.
 * </ul>
 * 
 * <li>
 * 1.21 (mui) :<br>
 * <ul>
 *  <li>Window is now centered on screen at startup.
 *  <li>Window is now non-resizable.
 * </ul>
 * 
 * <li>
 * 1.3 (mui) :<br>
 * <ul>
 *  <li>Added key manager interface
 *  <li>Changed members visibility
 *  <li>Removed obsolete <code>display()</code> method
 * </ul>
 * 
 * <li>1.4 (roi) :<br>
 * <ul>
 * 	<li>Added {@link fillOval()} method
 * </ul>
 * 
 * <li>1.41 (mui) :<br>
 * <ul>
 *  <li>Changed how constructors handle rendering hints so
 *  	that the quality can be chosen in the constructor instead
 *  	of being hard-coded.  
 *  <li>Added {@link #drawRotatedPicture(int, int, double, double, String)} method
 * </ul>
 *
 * <li>1.42 (mui) :<br>
 * <ul>
 *  <li>Changed fields visibility and added getters/setters  
 * </ul>
 * 
 * <li>2.0 (mui) :<br>
 * <ul>
 *  <li>Major overhaul in drawing for increased speed and stability using
 *  	double buffering.
 *    
 * </ul>
 * 
 * </ul>
 * </p>
 * 
 * @author Pierre-André Mudry <a href='mailto:pierre-andre.mudry&#64;hevs.ch'></a>
 * @author Pierre Roduit (pierre.roduit@hevs.ch)
 * @version 2.00
 * 
 */
public class SimpleGraphics {
	/**
	 * The subclass which create the windows frame
	 */
	protected DisplayFrame display;
	protected int frameWidth;
	protected int frameHeight;
	protected boolean enableRenderingHints = false;
	protected boolean checkBorders = true;
	protected int backgroundColor = Color.white.getRGB();
	
	/**
	 * Constructor
	 * @param width Width of the display window
	 * @param height Height of the display window
	 * @param title Title of the display window
	 * @param high_quality Use high quality rendering (slower)
	 */
	public SimpleGraphics(int width, int height, String title, boolean high_quality){
		enableRenderingHints = high_quality;
		display = new DisplayFrame(width, height, title);			
		setFrameWidth(width);
		setFrameHeight(height);	
		clear();	
	}
	
	/**
	 * @see #SimpleGraphics(int, int, String, boolean)
	 */
	public SimpleGraphics(int width, int height, String title){
		this(width, height, title, false);
	}
	
	/**
	 * @see #SimpleGraphics(int, int, String, boolean)
	 */
	public SimpleGraphics(int width, int height) {
		this(width, height, null, false);
	}

	/**
	 * @see #SimpleGraphics(int, int, String, boolean)
	 */
	public SimpleGraphics(int width, int height, boolean rendering_hints){		
		this(width, height, null, rendering_hints);
	}
	
	/**
	 * Sets a keyboard listener
	 * @param k The KeyListener to listen to
	 */
	public void setKeyManager(KeyListener k){
		display.addKeyListener(k);
	}

	/**
	 * Method which cleans up the display. Everything becomes the background
	 * again.
	 */
	public void clear() {
		display.g2d.clearRect(0, 0, display.imageWidth, display.imageHeight);		
		display.invalidate();
	}

	/**
	 * Method which cleans up the display. Everything becomes the background
	 * again.
	 */
	public void clear(Color c) {
		Color old = display.g2d.getBackground();
		display.g2d.setBackground(c);
		display.g2d.clearRect(0, 0, display.imageWidth, display.imageHeight);
		display.g2d.setBackground(old);		
		display.invalidate();
	}
	
	
	/**
	 * Set the color of the future drawings
	 * 
	 * @param c Selected color for drawing
	 */
	public void setColor(Color c) {		
		display.g2d.setColor(c);
	}	

	/**
	 * Draw the selected pixel with the color selected with setColor.
	 * 
	 * @param x
	 *            X coordinate of the pixel
	 * @param y
	 *            Y coordinate of the pixel
	 */
	public void setPixel(int x, int y) {
		// Test that the pixel to set is in the frame
		if ((x < 0) || (y < 0) || (x >= getFrameWidth()) || (y >= getFrameHeight())){
			if(checkBorders)
				System.out.println("Coordinates out of frame");
		}
		else{
			display.img.setRGB(x, y, display.g2d.getColor().getRGB());
		}
		
		display.invalidate();
	}

	/**
	 * Draws a pixel with a given color. Does not change the current color.
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param c
	 *            Color to use for this pixel (this pixel only, see
	 *            {@link #setColor(Color)}
	 */
	public void setPixel(int x, int y, Color c) {
		Color oldColor = display.g2d.getColor();
		setColor(c);
		setPixel(x, y);		
		setColor(oldColor);
		
		display.invalidate();
	}

	/**
	 * Draws a pixel with a given color. Does not change the current color
	 * 
	 * @param x
	 *            X coordinate
	 * @param y
	 *            Y coordinate
	 * @param c
	 *            Color to use (RGB coded)
	 */
	public void setPixel(int x, int y, int c) {
		setPixel(x, y, new Color(c));
	}

	/**
	 * Clears a pixel's by replacing its current color with the background's
	 * 
	 * @param x
	 *            X coordinate of the pixel to set
	 * @param y
	 *            Y coordinate of the pixel to set
	 */
	public void clearPixel(int x, int y) {
		setPixel(x, y, backgroundColor);
	}

	/**
	 * Draws a string at a given location. Note that the boundaries are not
	 * checked and text may be painted outside the window.
	 * 
	 * @param x
	 *            X coordinate of the text to be drawn
	 * @param y
	 *            Y coordinate of the text to be drawn
	 * @param text
	 *            The text to be drawn
	 */
	public void paintText(int x, int y, String text) {
		display.g2d.drawString(text, x, y);
		display.invalidate();
	}
	
	/**
	 * Methods which shows the buffered image. For most drawing methods, it is
	 * useless, as this method is called in the drawing method itself. However,
	 * for method such as setPixel, it is necessary.
	 */
	public void repaint(){		
		display.myPaint();		
	}
	
	/**
	 * Stops the execution of the program for a certain period
	 * @param delay Number of millisecond to stop execution
	 */
	public void pause(int delay){
		try{
			Thread.sleep(delay);
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	/**
	 * Draw a line from P1 to P2 in the color selected with setColor.
	 * 
	 * @param p1x
	 *            X coordinate of P1
	 * @param p1y
	 *            Y coordinate of P1
	 * @param p2x
	 *            X coordinate of P2
	 * @param p2y
	 *            Y coordinate of P2
	 */
	public void drawLine(int p1x, int p1y, int p2x, int p2y) {
		display.g2d.drawLine(p1x, p1y, p2x, p2y);
		display.invalidate();
	}
	
	public void drawPolygon(Polygon p){
		setColor(Color.gray);
		display.g2d.fill(p);
		display.g2d.drawPolygon(p);
		display.invalidate();
	}
	
	public void drawFilledPolygon(Polygon p, Color c){
		Color oldColor = display.g2d.getColor();
		setColor(c);
		display.g2d.fill(p);
		display.g2d.drawPolygon(p);
		display.invalidate();
		setColor(oldColor);
	}

	/**
	 * Draw an empty rectangle in the color selected with setColor().
	 * 
	 * @param posX
	 *            X coordinate of the top left corner of the rectangle
	 * @param posY
	 *            Y coordinate of the top left corner of the rectangle
	 * @param width
	 *            Width of the rectangle
	 * @param height
	 *            Height of the rectangle
	 */
	public void drawRect(int posX, int posY, int width, int height) {
		display.g2d.drawRect(posX, posY, width, height);
		display.invalidate();
	}
		
	/**
	 * Draws a circle starting from <code>(centerX, centerY)</code>
	 * @param posX X top-left position of the circle
	 * @param posY Y top-left position of the circle
	 * @param diameter Diameter of the drawn circle
	 */
	public void drawCircle(int posX, int posY, int diameter){		
		display.g2d.drawOval(posX, posY, diameter, diameter);
		display.invalidate();
	}
	
	/**
	 * Write a given string at <code>(posX, posY)</code>
	 * @param posX X position of string
	 * @param posY Y position of string
	 * @param str the string to write
	 */
	public void drawString(int posX, int posY, String str)
	{
		display.g2d.drawString(str, posX, posY);
		display.invalidate();
	}
	
	public void drawString(int posX, int posY, String str, Color color, int size)
	{
		Font oldFont = display.g2d.getFont();
		Color oldColor = display.g2d.getColor();
		
		Font font = new Font("SansSerif", Font.PLAIN, size);
		display.g2d.setFont(font);
		display.g2d.setColor(color);
		display.g2d.drawString(str, posX, posY);
		
		display.g2d.setFont(oldFont);
		display.g2d.setColor(oldColor);
		
		display.invalidate();
	}
	
	/**
	 * Draw a centered picture from a file (gif, jpg, png) to <code>(posX, posY)</code>
	 * @param posX X position of the image
	 * @param posY Y position of the image
	 * @param filename path of the image file
	 */
	public void drawPicture(int posX, int posY, SimpleGraphicsBitmap bitmap)
	{		
		display.g2d.drawImage(bitmap.mBitmap,posX-bitmap.getWidth()/2,posY-bitmap.getHeight()/2,null);
		display.invalidate();
	}
	
	/**
	 * Draw a centered picture from a file (gif, jpg, png) to <code>(posX, posY)</code>
	 * @param posX X position of the image
	 * @param posY Y position of the image
	 * @param angle The rotation angle of the image to be drawn
	 * @param imageName path of the image file
	 */
	public void drawTransformedPicture(int posX, int posY, double angle, double scale, String imageName)
	{	
		drawTransformedPicture(posX, posY, angle, scale, new SimpleGraphicsBitmap(imageName));
	} 
	
	/**
	 * Draw a centered picture from a file (gif, jpg, png) to <code>(posX, posY)</code>
	 * @param posX X position of the image
	 * @param posY Y position of the image
	 * @param angle The rotation angle of the image to be drawn
	 * @param bitmap A {@link #SimpleGraphicsBitmap()} bitmap
	 */
	public void drawTransformedPicture(int posX, int posY, double angle, double scale, SimpleGraphicsBitmap bitmap)
	{	
		AffineTransform t = new AffineTransform();		
		
		t.rotate(angle, posX, posY);
		t.translate(posX-bitmap.getWidth()/2, posY-bitmap.getHeight()/2);		
		t.scale(scale, scale);
		display.g2d.drawImage(bitmap.mBitmap, t, null);
		display.invalidate();
	}

	/**
	 * Draw a filled rectangle in the color selected with setColor.
	 * 
	 * @param posX
	 *            X coordinate of the top left corner of the rectangle
	 * @param posY
	 *            Y coordinate of the top left corner of the rectangle
	 * @param width
	 *            Width of the rectangle
	 * @param height
	 *            Height of the rectangle
	 */
	public void drawFillRect(int posX, int posY, int width, int height) {
		display.g2d.fillRect(posX, posY, width, height);
		display.invalidate();
	}

	/**
	 * Getters and setters 
	 */	
	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public void setFrameHeight(int frameHeight) {
		this.frameHeight = frameHeight;
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public Graphics2D getGraphics(){
		return display.g2d;
	}

	/**
	 * Subclass which extends JFrame, to create the windows and keep all the
	 * frame stuff hidden from the end user.
	 */
	protected class DisplayFrame extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * 2D graphic used to draw
		 */
		public Graphics2D g2d;
		
		/**
		 * Buffered image used to draw g2d into and to set pixels.
		 */
		public BufferedImage img;
		
		/**
		 * Color used to draw objects and pixels
		 */		
		private int imageWidth, imageHeight;

		/**
		 * Default constructor
		 * 
		 * @param width
		 *            Number of pixels for the window's width
		 * @param height
		 *            Number of pixels for the window's height
		 * @param title
		 *            Displayed title of the window
		 */
		public DisplayFrame(int width, int height, String title) {
			super(title);
												
			if(title == null){
				this.setTitle("Informatics 1 - SimpleGraphics window");
			}
			
			imageWidth = width;
			imageHeight = height;
			this.setSize(this.imageWidth, this.imageHeight);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
									
			// Prevent resizing
			this.setResizable(false);
			
			// Display the windows at the center of the screen
			this.setLocationRelativeTo(null);
			
			// The frame must be set to visible, if we want to access the
			// corresponding Graphics to draw into.
			this.setVisible(true);
			
			// Fixes the bug with the invalid buffer strategies
			while(!this.isVisible() && !this.isValid())
			{}
			
			// Create a double buffer strategy
			this.createBufferStrategy(2);
			
			this.setBackground(Color.white);
						
			// Create graphics and image
			img = new BufferedImage(this.imageWidth, this.imageHeight, BufferedImage.TYPE_INT_ARGB);			
			g2d = img.createGraphics();
						
			// Set rendering hints for nicer display (if speed allows)
			if (enableRenderingHints) {
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                    RenderingHints.VALUE_ANTIALIAS_ON);		  		   
			    g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
	                    RenderingHints.VALUE_RENDER_QUALITY);			    
			}					
			
			this.setIgnoreRepaint(true);
			
			Font font = new Font("SansSerif", Font.PLAIN, 12);
		    g2d.setFont(font);
		    
			// Set drawing color to black		
			g2d.setColor(Color.black);
			g2d.setBackground(new Color(backgroundColor));
		}

		/**
		 * Override the paint method.
		 */
		public void myPaint() {											
			BufferStrategy bf = this.getBufferStrategy();						
												
			while(bf == null)
				bf = this.getBufferStrategy();
			
			do {
				do {
					Graphics2D g1 = (Graphics2D) bf.getDrawGraphics();
					g1.drawImage(img, 0, 0, this);

					// We don't need that anymore
					g1.dispose();
				} while (bf.contentsRestored());

				// Shows the contents of the backbuffer on the screen.
				bf.show();

				// Tell the system to draw, otherwise it can take a few extra ms
				// until it does
				Toolkit.getDefaultToolkit().sync();

				// Repeat the rendering if the drawing buffer was lost
			} while (bf.contentsLost());
		}					
	}
	
	 
	static {
	    System.setProperty("sun.java2d.transaccel", "True");
	    // System.setProperty("sun.java2d.trace", "timestamp,log,count");
	    // System.setProperty("sun.java2d.opengl", "True");
	    //System.setProperty("sun.java2d.d3d", "True");
	    System.setProperty("sun.java2d.ddforcevram", "True");
	}
}