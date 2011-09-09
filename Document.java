/*
 Copyright 2008 Sahab Yazdani
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
    http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
*/

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

import com.jhlabs.image.GaussianFilter;


public class Document {
	private PainterlyStyle painterlyStyle;
	
	
	private final static float[] sobelXMatrix = { 
			-1.0f, 0.0f, 1.0f,
			-2.0f, 0.0f , 2.0f,
			-1.0f, 0.0f, 1.0f };
	private final static float[] sobelYMatrix = {
			-1.0f, -2.0f, -1.0f,
			0f, 0f, 0f,
			1.0f, 2.0f, 1.0f
	};

	private class SobelVector {
		public short x;
		public short y;
	};
	
	private BufferedImage source, target;
	private BufferedImage writtenArea;
	private BufferedImage blurred = null;	
	private SobelVector[] sobelVector = null;
	
	private List<StateChangeListener> _stateChangeListeners = new Vector<StateChangeListener>();
	
	public Document() {
		source = null;
		setPredefinedStyle( PainterlyStyle.IMPRESSIONIST_STYLE );
	}
	
	public void selectSourceFile( String filename ) {
		try {
			source = ImageIO.read( new File( filename ) );
			target = new BufferedImage( source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB );
			writtenArea = new BufferedImage( source.getWidth(), source.getHeight(), BufferedImage.TYPE_BYTE_BINARY );
			
			sobelVector = new SobelVector[source.getWidth() * source.getHeight()];
			for ( int i = 0; i < sobelVector.length; i++ ) {
				sobelVector[i] = new SobelVector();
			}
		} catch ( Exception e ) {
			source = null;
			target = null;
			sobelVector = null;
		}
	}

	public void saveDocument( String filename ) {
		try {
			ImageIO.write( target, "png", new File( filename ) );
		} catch ( Exception e ) {		
		}
	}
	
	/* Painterly Rendering stuff */

	private short doConvolution( int x, int y, BufferedImage source, float[] convolve ) {
		short value = 0;
		for ( int i = 0; i < 3; i++ ) {
			for ( int j = 0; j < 3; j++ ) {
				try {
					int intensity = new Color( source.getRGB( x + i - 1, y + j - 1 ) ).getBlue();
					
					value += intensity * convolve[j*3+i];
				} catch ( Exception e ) {
					// if we catch an exception, then
					// probably we are out of bounds, so just ignore it
				}
			}
		}
		return value;
	}
	
	private void calculateSobelVectors( BufferedImage luma ) {
		int width = luma.getWidth();
		int height = luma.getHeight();
		
		for ( int y = 0; y < height; ++y ) {
			for ( int x = 0; x < width; ++x ) {
				SobelVector v = sobelVector[y*width+x];
				
				v.x = doConvolution( x, y, luma, sobelXMatrix );
				v.y = doConvolution( x, y, luma, sobelYMatrix );
			}
		}
	}

	private void renderEdges() {
		if ( !getDrawEdges() ) {
			return;
		}
		
		{
			Graphics2D g = writtenArea.createGraphics();
			g.setBackground( Color.black );
			g.clearRect( 0, 0, writtenArea.getWidth(), writtenArea.getHeight() );
		}
				
		BufferedImage sourceTmp = source;
		
		BufferedImage blackSource = new BufferedImage( source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = blackSource.createGraphics();
		g.setBackground( Color.black );
		g.clearRect( 0, 0, blackSource.getWidth(), blackSource.getHeight() );
		source = blackSource;
		
		float maxGradMag = Float.MIN_VALUE;
		
		for ( int x = 0; x < source.getWidth(); x++ ) {
			for ( int y = 0; y < source.getHeight(); y++ ) {
				float gradmag = getGradientMagnitude( new Point2D.Float( x, y ) );
				if ( gradmag > maxGradMag ) maxGradMag = gradmag;
			}
		}
		
		for ( int x = 0; x < source.getWidth(); x++ ) {
			for ( int y = 0; y < source.getHeight(); y++ ) {
				float gradmag = getGradientMagnitude( new Point2D.Float( x, y ) );
				
				if ( gradmag < maxGradMag * getEdgeThreshold()/100.0f ) {
					SobelVector sV = sobelVector[y*source.getWidth()+x]; 
					sV.x = 0;
					sV.y = 0;
				}
			}
		}
		
		doDrawDot = false;
		paintLayer( 2.0f );
		doDrawDot = true;
		
		source = sourceTmp;
	}
	
	public void doPainterly() {
		try {
			{
				Graphics2D g = target.createGraphics();
				g.setBackground( Color.white );
				g.clearRect( 0, 0, target.getWidth(), target.getHeight() );
			}
			
			{
				Graphics2D g = writtenArea.createGraphics();
				g.setBackground( Color.black );
				g.clearRect( 0, 0, writtenArea.getWidth(), writtenArea.getHeight() );
			}
			
			for ( int i = getMaximumBrushSize(); i > 1; i/=2 ) {
				BufferedImageOp gaussian = new GaussianFilter( (float)i * getBlurFactor() );
				
				blurred = gaussian.filter( source, null );
				
				BufferedImage luma = new BufferedImage( blurred.getWidth(), blurred.getHeight(), BufferedImage.TYPE_BYTE_GRAY );
				luma.createGraphics().drawImage( blurred, 0, 0, null );
				
				calculateSobelVectors( luma );
				
				paintLayer( (float)i );
			}

			renderEdges();
			
			fireStateChangeEvent();
		} catch ( Exception e ) {
			Graphics2D g = target.createGraphics();
			g.setBackground( new Color( 255, 255, 255 ) );
			g.clearRect( 0, 0, target.getWidth(), target.getHeight() );
		}
	}
	
	private void paintLayer( float brushSize ) {
		int grid = Math.round( getGridSize() * brushSize );
		int gridsqr = grid * grid;
		
		Vector<Point2D.Float> strokeList = new Vector<Point2D.Float>();
		
		for ( int x = 0; x < source.getWidth(); x += grid ) {
			for ( int y = 0; y < source.getHeight(); y+= grid ) {
				float areaError = 0.0f;
				float worstError = Float.MIN_VALUE;
				int wEX = x, wEY = y;
				
				for ( int eY = y - grid/2; eY < y + grid/2; eY++ ) {
					for ( int eX = x - grid/2; eX < x + grid/2; eX++  ) {
						int jitteredX = eX + (int)((Math.random() - 0.5) * grid );
						int jitteredY = eY + (int)((Math.random() - 0.5) * grid );
		
						if ( jitteredX < 0 ) { jitteredX = 0; }
						if ( jitteredX >= source.getWidth() ) { jitteredX = source.getWidth() - 1; }
						
						if ( jitteredY < 0 ) { jitteredY = 0; }
						if ( jitteredY >= source.getHeight() ) { jitteredY = source.getHeight() - 1; }
						
						float difference = getDifference( jitteredX, jitteredY );
						
						if ( difference > worstError ) {
							worstError = difference;
							wEX = jitteredX;
							wEY = jitteredY;
						}
						
						areaError += difference;
					}
				}
				
				areaError /= (float)gridsqr;
				
				if ( areaError > getThreshold() ) {
					strokeList.add( new Point2D.Float( (float)wEX, (float)wEY ) );
				}
			}
		}
		
		while ( !strokeList.isEmpty() ) {
			Point2D.Float obj = strokeList.remove( (int)Math.floor( Math.random() * strokeList.size() ) );
			makeStroke( brushSize, obj );
		}
	}

	float clamp( float v, float l, float h ) {
		if ( v >= h ) { v = h; }
		if ( v <= l ) { v = l; }
		
		return v;
	}
	int clamp( int v, int l, int h ) {
		if ( v >= h ) { v = h; }
		if ( v <= l ) { v = l; }
		
		return v;
	}
	
	Color createStrokeColor( int x, int y ) {
		Color temp = new Color( source.getRGB( x, y ), true );
		
		if ( doDrawDot ) {
			Color baseStrokeColor = new Color( temp.getRed(), temp.getGreen(), temp.getBlue() );
	
			float[] hsbvals = new float[3];
			Color.RGBtoHSB(baseStrokeColor.getRed(), baseStrokeColor.getGreen(), baseStrokeColor.getBlue(), hsbvals);
	
			hsbvals[0] += ( Math.random() - 0.5f ) * getHueJitter();
			hsbvals[1] += ( Math.random() - 0.5f ) * getSaturationJitter();
			hsbvals[2] += ( Math.random() - 0.5f ) * getValueJitter();
			
			Color jitteredHSB = new Color( Color.HSBtoRGB( 
					clamp( hsbvals[0], 0.0f, 1.0f ),
					clamp( hsbvals[1], 0.0f, 1.0f ),
					clamp( hsbvals[2], 0.0f, 1.0f ) ) );
			Color jitteredRGB = new Color( 
					clamp( jitteredHSB.getRed() + (int)(( Math.random() - 0.5f ) * getRedJitter()), 0, 255 ),
					clamp( jitteredHSB.getGreen() + (int)(( Math.random() - 0.5f ) * getGreenJitter()), 0, 255 ),
					clamp( jitteredHSB.getBlue() + (int)(( Math.random() - 0.5f ) * getBlueJitter()), 0, 255 ),
					getColorOpacity() );
			
			return jitteredRGB;
		} else {
			return temp;
		}
	}
	
	private boolean doDrawDot = true;
	
	void renderStroke( BufferedImage t, Vector<Point2D.Float> path, Color strokeColor, float brushSize ) {
		Point2D.Float initial = path.elementAt( 0 );
		
		Graphics2D g = t.createGraphics();

		g.getRenderingHints().put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		g.setColor( strokeColor );

//		float halfBrush = brushSize / 2;
		float halfBrush = 0;
		if ( path.size() > 1 ) {
			GeneralPath gp = new GeneralPath();

			gp.moveTo( (int)( initial.x - halfBrush ), (int)( initial.y - halfBrush ) );
			for ( int i = 1; i < path.size() - 1; i++ ) {
				Point2D.Float p1 = path.elementAt( i - 1 );
				Point2D.Float p2 = path.elementAt( i );
				Point2D.Float p3 = path.elementAt( i + 1 );
				
				gp.curveTo( p1.x - halfBrush, p1.y - halfBrush,
						p2.x - halfBrush, p2.y - halfBrush,
						p3.x - halfBrush, p3.y - halfBrush);
			}
			gp.lineTo( path.elementAt( path.size() - 1 ).x - halfBrush, path.elementAt( path.size() - 1 ).y - halfBrush );
			
			g.setStroke( new BasicStroke( brushSize ) );
			g.draw( gp );
		} else {
			if ( doDrawDot ) {
				g.fillOval( (int)( initial.x - halfBrush ), (int)( initial.y - halfBrush ), 
						(int)brushSize, (int)brushSize );
			}
		}
	}
	
	void makeStroke( float brushSize, Point2D.Float initial ) {
		Vector<Point2D.Float> path = new Vector<Point2D.Float>();
		
		Point2D.Float current = initial;
		Point2D.Float lastDelta = new Point2D.Float( 0.0f, 0.0f );
		Color strokeColor = createStrokeColor( (int)current.getX(), (int)current.getY() );

		path.add( (Point2D.Float)current.clone() );
		
		for ( int i = 1; i < getMaximumStrokeLength(); i++ ) {
			if ( i > getMinimumStrokeLength() && 
					getDifference( (int)current.x, (int)current.y ) < getDifference( new Color( source.getRGB( (int)current.getX(), (int)current.getY() ), true ), strokeColor ) ) {
				break;
			}
			
			if ( getGradientMagnitude( current ) == 0.0f ) {
				break;
			}
			
			Point2D.Float gradient = getGradientDirection( current );
			Point2D.Float delta = new Point2D.Float( (float)-gradient.getY(), (float)gradient.getX() );
			
			if ( lastDelta.getX() * delta.getX() + lastDelta.getY() * delta.getY() < 0 ) {
				delta.x = -delta.x;
				delta.y = -delta.y;
			}
			
			float deltaMag = (float)Math.sqrt( delta.x * delta.x + delta.y * delta.y );
			
			delta.x = getCurvatureFilter() * delta.x + ( 1 - getCurvatureFilter() ) * lastDelta.x;
			delta.x = delta.x / deltaMag;
			
			delta.y = getCurvatureFilter() * delta.y + ( 1 - getCurvatureFilter() ) * lastDelta.y;
			delta.y = delta.y / deltaMag;

			current.x += brushSize * delta.x;
			current.y += brushSize * delta.y;
			
			// brush went off canvas, so just stop it
			if ( current.x < 0.0f || current.x >= source.getWidth() ||
					current.y < 0.0f || current.y >= source.getHeight() ) {
				break;
			}
			
			lastDelta = delta;	

			path.add( (Point2D.Float)current.clone() );
		}
		
		renderStroke( target, path, strokeColor, brushSize );
		renderStroke( writtenArea, path, Color.white, brushSize );
	}
	
	private float getDifference( Color sC, Color tC ) {
		int dR = ( sC.getRed() - tC.getRed() );
		int dB = ( sC.getBlue() - tC.getBlue() );
		int dG = ( sC.getGreen() - tC.getGreen() );
	
		return (float)Math.sqrt( ( double )( dR * dR + dB * dB + dG * dG ) );
	}
	
	private float getDifference( int eX, int eY ) {
		float difference;
		
		Color sC = new Color( source.getRGB( eX, eY ), true );
		Color tC = new Color( target.getRGB( eX, eY ), true );
		Color wC = new Color( writtenArea.getRGB( eX, eY ) );
		
		if ( wC.equals( Color.black ) ) {
			difference = (float)Integer.MAX_VALUE;
		} else {
			difference = getDifference( sC, tC );
		}
		
		return difference;
	}
	
	private SobelVector getGradientVector( Point2D.Float pos ) {
		return sobelVector[(int)pos.y * source.getWidth() + (int)pos.x];
	}
	
	private float getGradientMagnitude( Point2D.Float pos ) {
		SobelVector sv = getGradientVector( pos );
		
		return (float)Math.sqrt( sv.x * sv.x + sv.y * sv.y );
	}
	
	private Point2D.Float getGradientDirection( Point2D.Float pos ) {
		SobelVector sv = getGradientVector( pos );
		
		Point2D.Float gradient = new Point2D.Float( sv.x, sv.y );
		
		gradient.x /= getGradientMagnitude( pos );
		gradient.y /= getGradientMagnitude( pos );
		
		return gradient;
	}
	
	/* Properties */
	
	public BufferedImage getTargetImage() {
		return target;		
	}

	public void setPredefinedStyle( PainterlyStyle newStyle ) {
		painterlyStyle = new PainterlyStyle( newStyle );
	}
	
	public void setMaximumBrushSize( int newValue ) {
		painterlyStyle.setMaximumBrushSize( newValue );		
	}
	public int getMaximumBrushSize() {
		return painterlyStyle.getMaximumBrushSize();
	}
	
	public void setColorOpacity( int newValue ) {
		painterlyStyle.setColorOpacity( newValue );
	}
	public int getColorOpacity() {
		return painterlyStyle.getColorOpacity();
	}
	
	public void setBlurFactor( float newValue ) {	
		painterlyStyle.setBlurFactor( newValue );
	}
	public float getBlurFactor() {
		return painterlyStyle.getBlurFactor();
	}
	
	public void setGridSize( float newValue ) {
		painterlyStyle.setGridSize( newValue );
	}
	public float getGridSize() {
		return painterlyStyle.getGridSize();
	}
	
	public void setCurvatureFilter( float newValue ) {
		painterlyStyle.setCurvatureFilter( newValue );
	}
	public float getCurvatureFilter() {
		return painterlyStyle.getCurvatureFilter();
	}
	
	public void setThreshold( float newValue ) {
		painterlyStyle.setThreshold( newValue );
	}
	public float getThreshold() {
		return painterlyStyle.getThreshold();
	}
	
	public void setMinimumStrokeLength( int newValue ) {
		painterlyStyle.setMinimumStrokeLength( newValue );
	}
	public int getMinimumStrokeLength() {
		return painterlyStyle.getMinimumStrokeLength();
	}
	
	public void setMaximumStrokeLength( int newValue ) {
		painterlyStyle.setMaximumStrokeLength( newValue );
	}
	public int getMaximumStrokeLength() {
		return painterlyStyle.getMaximumStrokeLength();
	}

	public void setHueJitter( float newValue ) {
		painterlyStyle.setHueJitter( newValue );
	}
	public float getHueJitter() {
		return painterlyStyle.getHueJitter();
	}	
	
	public void setSaturationJitter( float newValue ) {
		painterlyStyle.setSaturationJitter( newValue );
	}
	public float getSaturationJitter() {
		return painterlyStyle.getSaturationJitter();
	}
	
	public void setValueJitter( float newValue ) {
		painterlyStyle.setValueJitter( newValue );
	}
	public float getValueJitter() {
		return painterlyStyle.getValueJitter();
	}
	
	public void setRedJitter( float newValue ) {
		painterlyStyle.setValueJitter( newValue );
	}
	public float getRedJitter() {
		return painterlyStyle.getRedJitter();
	}
	
	public void setGreenJitter( float newValue ) {
		painterlyStyle.setGreenJitter( newValue );
	}
	public float getGreenJitter() {
		return painterlyStyle.getGreenJitter();
	}
	
	public void setBlueJitter( float newValue ) {
		painterlyStyle.setBlueJitter( newValue );
	}
	public float getBlueJitter() {
		return painterlyStyle.getBlueJitter();
	}
	
	public void setDrawEdges( boolean newValue ) {
		painterlyStyle.setDrawEdges( newValue );
	}
	public boolean getDrawEdges() {
		return painterlyStyle.getDrawEdges( );
	}
	
	public void setEdgeThreshold( float newValue ) {
		painterlyStyle.setEdgeThreshold( newValue );
	}
	public float getEdgeThreshold() {
		return painterlyStyle.getEdgeThreshold();
	}
	
	/* Event Listeners */
	
	public void addStateChangeListener( StateChangeListener l ) {
		_stateChangeListeners.add( l );
	}
	
	protected void fireStateChangeEvent( ) {
		for ( Iterator<StateChangeListener> it = _stateChangeListeners.iterator(); it.hasNext(); ) {
			it.next().stateChanged( );
		}
	}
}
