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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class PainterlyPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Document _document;
	
	public PainterlyPanel( Document doc ) {
		super( null );
		
		this.setLayout( null );

		_document = doc;
		_document.addStateChangeListener( new StateChangeListener() {
			public void stateChanged( ) {
				setSize( getPreferredSize() );
				repaint();
			}
		} );
	}
	
	public void paintComponent( Graphics g ) {
		Graphics2D gX = (Graphics2D)g;
		
		super.paintComponent( g );
		
		gX.setBackground( Color.LIGHT_GRAY );
		gX.clearRect( 0, 0, this.getWidth(), this.getHeight() );
		if ( _document.getTargetImage() != null ) {
			gX.drawImage( _document.getTargetImage(), 
					0, 0, 
					_document.getTargetImage().getWidth(), _document.getTargetImage().getHeight(), 
					null );
		} else {
			gX.setColor( Color.white );
			gX.fillRect( 0, 0, 640, 480 );
		}
	}
	
	public Dimension getPreferredSize() {
		if ( _document.getTargetImage() != null ) {
			return new Dimension( _document.getTargetImage().getWidth(),
					_document.getTargetImage().getHeight() );
		} else {
			return new Dimension( 640, 480 );
		}
	}
}
