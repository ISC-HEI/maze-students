package hevs.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * An implementation of the {@link CommonBitmap} interface that is intended to
 * be used on an operating systems that provides the {@link java.awt.BufferedImage} classes
 * 
 * @version 1.0, April 2010
 * @author <a href='mailto:pandre.mudry&#64;hevs.ch'> Pierre-André Mudry</a>
 */
public class SimpleGraphicsBitmap {
	protected BufferedImage mBitmap;
	
	public SimpleGraphicsBitmap(String imageName)
	{			
		try {			
			mBitmap = ImageIO.read(SimpleGraphicsBitmap.class.getResource(imageName));
		} catch (Exception e) {
			System.out.println("Could not find image " + imageName + ", exiting !");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public int getWidth()
	{
		return mBitmap.getWidth();
	}
	
	public int getHeight()
	{
		return mBitmap.getHeight();
	}
	
	public BufferedImage getImage(){		
		return this.mBitmap;
	}	
}
