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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PainterlyFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Document _document = new Document();

	// widgets
	private String[] styleDescriptions = { 
			"Impressionist",
			"Expressionist",
			"Colorist Wash",
			"Pointillist"
	};
	private JComboBox predefinedStyles;
	private JSlider maxBrushSize;
	private JSlider colorOpacity;
	private JSlider minStrokeLength;
	private JSlider maxStrokeLength;
	private JSlider blurFactor;
	private JSlider gridSize;
	private JSlider curvature;
	private JSlider threshold;
	private JSlider hueJitter;
	private JSlider saturationJitter;
	private JSlider valueJitter;
	private JSlider redJitter;
	private JSlider greenJitter;
	private JSlider blueJitter;
	private JCheckBox drawEdges;
	private JSlider edgeThreshold;
	private JButton applyButton;

	public PainterlyFrame() {
		super( "CS798 Painterly Rendering Assignment" );
		
		createLayout();
		
		setupMenu();
		
		this.setSize( new Dimension( 800, 600 ) );
		this.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
	}
	
	private Component createBrushTab() {
		JPanel parametersFrame = new JPanel();
		parametersFrame.setLayout( new GridLayout( 4, 2 ) );
		parametersFrame.setBorder( createEmptyBorder() );
	
		maxBrushSize = new JSlider( 2, 32, _document.getMaximumBrushSize() );
		maxBrushSize.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_document.setMaximumBrushSize( maxBrushSize.getValue() );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Max Brush Size: " ) );
		parametersFrame.add( maxBrushSize );
		
		minStrokeLength = new JSlider( 0, 40, _document.getMinimumStrokeLength() );
		minStrokeLength.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_document.setMinimumStrokeLength( minStrokeLength.getValue() );
				if ( minStrokeLength.getValue() > maxStrokeLength.getValue() ) {
					maxStrokeLength.setValue( minStrokeLength.getValue() );
				}
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Min Stroke Length: " ) );
		parametersFrame.add( minStrokeLength );
		
		maxStrokeLength = new JSlider( 0, 40, _document.getMaximumStrokeLength() );
		maxStrokeLength.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_document.setMaximumStrokeLength( maxStrokeLength.getValue() );
				if ( maxStrokeLength.getValue() < minStrokeLength.getValue() ) {
					minStrokeLength.setValue( maxStrokeLength.getValue() );
				}
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Max Stroke Length: " ) );
		parametersFrame.add( maxStrokeLength );

		curvature = new JSlider( 0, 100, (int)( _document.getCurvatureFilter() * 100.0f ) );
		curvature.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setCurvatureFilter( (float)curvature.getValue() / 100.0f );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Stroke Curvature: " ) );
		parametersFrame.add( curvature );
		
		return parametersFrame;
	}
	
	private Component createStyleTab() {
		JPanel parametersFrame = new JPanel();
		parametersFrame.setLayout( new GridLayout( 5, 2 ) );
		parametersFrame.setBorder( createEmptyBorder() );
		
		blurFactor = new JSlider( 0, 100, (int)( _document.getBlurFactor() * 100.0f ) );
		blurFactor.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setBlurFactor( (float)blurFactor.getValue() / 100.0f );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Blur Factor: " ) );
		parametersFrame.add( blurFactor );
		
		gridSize = new JSlider( 0, 100, (int)( _document.getGridSize() * 100.0f ) );
		gridSize.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setGridSize( (float)gridSize.getValue() / 100.0f );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Grid Size: " ) );
		parametersFrame.add( gridSize );		

		threshold = new JSlider( 100, 3000, (int)( _document.getThreshold() * 10.0f ) );
		threshold.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setThreshold( (float)threshold.getValue() / 10.0f );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Painting Accuracy: " ) );
		parametersFrame.add( threshold );

		drawEdges = new JCheckBox( "Trace Edges", _document.getDrawEdges() );
		drawEdges.addItemListener( new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if ( e.getStateChange() == ItemEvent.SELECTED ) {
					_document.setDrawEdges( true );
				} else {
					_document.setDrawEdges( false );
				}
				edgeThreshold.setEnabled( _document.getDrawEdges() );					
				applyButton.setEnabled( true );
				
			}
		} );
		parametersFrame.add( drawEdges );
		parametersFrame.add( new JLabel() );
		
		edgeThreshold = new JSlider( 0, 100, (int)_document.getEdgeThreshold() );
		edgeThreshold.setEnabled( _document.getDrawEdges() );
		edgeThreshold.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setEdgeThreshold( (float)edgeThreshold.getValue() );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Edge Threshold: " ) );
		parametersFrame.add( edgeThreshold );
		
		return parametersFrame;
	}
	
	private Component createColorTab() {
		JPanel parametersFrame = new JPanel();
		parametersFrame.setLayout( new GridLayout( 7, 2 ) );
		parametersFrame.setBorder( createEmptyBorder() );
		
		colorOpacity = new JSlider( 0, 255, _document.getColorOpacity() );
		colorOpacity.addChangeListener( new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				_document.setColorOpacity( colorOpacity.getValue() );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Color Opacity: " ) );
		parametersFrame.add( colorOpacity );

		hueJitter = new JSlider( 0, 10, (int)( _document.getHueJitter() * 10.0f ) );
		hueJitter.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setHueJitter( (float)hueJitter.getValue() / 10.0f );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Hue Jittering: " ) );
		parametersFrame.add( hueJitter );
		
		saturationJitter = new JSlider( 0, 10, (int)( _document.getSaturationJitter() * 10.0f ) );
		saturationJitter.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setSaturationJitter( (float)saturationJitter.getValue() / 10.0f );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Saturation Jittering: " ) );
		parametersFrame.add( saturationJitter );

		valueJitter = new JSlider( 0, 10, (int)( _document.getValueJitter() * 10.0f ) );
		valueJitter.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setValueJitter( (float)valueJitter.getValue() / 10.0f );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Value Jittering: " ) );
		parametersFrame.add( valueJitter );
		
		redJitter = new JSlider( 0, 10, (int)( _document.getRedJitter() * 10.0f ) );
		redJitter.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setRedJitter( (float)redJitter.getValue() / 10.0f );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Red Jittering: " ) );
		parametersFrame.add( redJitter );
		
		greenJitter = new JSlider( 0, 10, (int)( _document.getGreenJitter() * 10.0f ) );
		greenJitter.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setGreenJitter( (float)greenJitter.getValue() / 10.0f );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Green Jittering: " ) );
		parametersFrame.add( greenJitter );
		
		blueJitter = new JSlider( 0, 10, (int)( _document.getBlueJitter() * 10.0f ) );
		blueJitter.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent e ) {
				_document.setBlueJitter( (float)blueJitter.getValue() / 10.0f );
				applyButton.setEnabled( true );
			}
		} );
		parametersFrame.add( new JLabel( "Blue Jittering: " ) );
		parametersFrame.add( blueJitter );
		
		return parametersFrame;
	}
	
	private Border createEmptyBorder() {
		return BorderFactory.createEmptyBorder(3, 3, 3, 3);
	}
	
	private Component createToolbox() {
		JPanel toolboxFrame = new JPanel();
		toolboxFrame.setLayout( new BorderLayout( ) );
	
		toolboxFrame.setBorder( createEmptyBorder() );
		
		predefinedStyles = new JComboBox();
		predefinedStyles.setEditable(false);
		for ( int i = 0; i < styleDescriptions.length; i++ ) {
			predefinedStyles.addItem( styleDescriptions[i] );
		}
		predefinedStyles.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				switch ( predefinedStyles.getSelectedIndex() ) {
				case 0:
					_document.setPredefinedStyle( PainterlyStyle.IMPRESSIONIST_STYLE );
					break;
				case 1:
					_document.setPredefinedStyle( PainterlyStyle.EXPRESSIONIST_STYLE );
					break;
				case 2:
					_document.setPredefinedStyle( PainterlyStyle.COLORISTWASH_STYLE );
					break;
				case 3:
					_document.setPredefinedStyle( PainterlyStyle.POINTILLIST_STYLE );
					break;
				}
				
				loadToolboxValuesFromDocument();
				applyButton.setEnabled( true );
			}
		} );
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout( new GridLayout( 1, 2 ) );
		northPanel.setBorder( createEmptyBorder() );
		northPanel.add( new JLabel( "Predefined Styles: " ) );
		northPanel.add( predefinedStyles );
		toolboxFrame.add( northPanel, BorderLayout.NORTH );

		applyButton = new JButton( "Apply Parameters" );
		applyButton.setBorder( BorderFactory.createCompoundBorder( createEmptyBorder(),  applyButton.getBorder() ) );
		applyButton.setEnabled ( false );
		applyButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
				_document.doPainterly();
				setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
				
				applyButton.setEnabled( false );
			}
		} );
		toolboxFrame.add( applyButton, BorderLayout.SOUTH );

		JTabbedPane tabbedPane = PainterlyFrame.createTabbedPane( JTabbedPane.LEFT );
		PainterlyFrame.addTab( tabbedPane, "Brush Properties", createBrushTab() );
		PainterlyFrame.addTab( tabbedPane, "Painting Style Properties", createStyleTab() );
		PainterlyFrame.addTab( tabbedPane, "Color Properties", createColorTab() );
		
		toolboxFrame.add( tabbedPane, BorderLayout.CENTER );
		
		return toolboxFrame;
	}
	
	private void loadToolboxValuesFromDocument() {
		maxBrushSize.setValue( _document.getMaximumBrushSize() );
		colorOpacity.setValue( _document.getColorOpacity() );
		minStrokeLength.setValue( _document.getMinimumStrokeLength() );
		maxStrokeLength.setValue( _document.getMaximumStrokeLength() );
		blurFactor.setValue( (int)( _document.getBlurFactor() * 100.0f ) );
		gridSize.setValue( (int)( _document.getGridSize() * 100.0f ) );
		curvature.setValue( (int)( _document.getCurvatureFilter() * 100.0f ) );
		threshold.setValue( (int)( _document.getThreshold() * 10.0f ) );
		hueJitter.setValue( (int)( _document.getHueJitter() * 10.0f ) );
		saturationJitter.setValue( (int)( _document.getSaturationJitter() * 10.0f ) );
		valueJitter.setValue( (int)( _document.getValueJitter() * 10.0f ) );
		redJitter.setValue( (int)( _document.getRedJitter() * 10.0f ) );
		greenJitter.setValue( (int)( _document.getGreenJitter() * 10.0f ) );
		blueJitter.setValue( (int)( _document.getBlueJitter() * 10.0f ) );
		drawEdges.setSelected( _document.getDrawEdges() );
		edgeThreshold.setValue( (int)_document.getEdgeThreshold() );
	}

	private void createLayout() {
		this.getContentPane().add( createToolbox(), BorderLayout.WEST );
		this.getContentPane().add( new JScrollPane( new PainterlyPanel( _document ) ), BorderLayout.CENTER );
		
		this.getContentPane().validate();
	}
	
	private void setupMenu() {
		JMenuBar menu = new JMenuBar();
		
		JMenu imageMenu = new JMenu( "Image" );
		
		JMenuItem selectSourceMI = new JMenuItem( "Select Source..." );
		selectSourceMI.addActionListener( onSelectSourceClick );
		imageMenu.add( selectSourceMI );
		
		JMenuItem saveMI = new JMenuItem( "Save..." );
		saveMI.addActionListener( onSaveClick );
		imageMenu.add( saveMI );
		
		JMenuItem exitMI = new JMenuItem( "Exit" );
		exitMI.addActionListener( onExitClick );
		imageMenu.add( exitMI );
		
		menu.add( imageMenu );
		
		setJMenuBar( menu );
	}

	public static JTabbedPane createTabbedPane(int tabPlacement){ 
	    switch(tabPlacement){ 
	        case JTabbedPane.LEFT: 
	        case JTabbedPane.RIGHT: 
	            Object textIconGap = UIManager.get("TabbedPane.textIconGap"); 
	            Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets"); 
	            UIManager.put("TabbedPane.textIconGap", new Integer(1)); 
	            UIManager.put("TabbedPane.tabInsets", new Insets(tabInsets.left, tabInsets.top, tabInsets.right, tabInsets.bottom)); 
	            JTabbedPane tabPane = new JTabbedPane(tabPlacement); 
	            UIManager.put("TabbedPane.textIconGap", textIconGap); 
	            UIManager.put("TabbedPane.tabInsets", tabInsets); 
	            return tabPane; 
	        default: 
	            return new JTabbedPane(tabPlacement); 
	    } 
	} 
	
	public static void addTab(JTabbedPane tabPane, String text, Component comp){ 
	    int tabPlacement = tabPane.getTabPlacement(); 
	    switch(tabPlacement){ 
	        case JTabbedPane.LEFT: 
	        case JTabbedPane.RIGHT: 
	            tabPane.addTab(null, new VerticalTextIcon(text, tabPlacement==JTabbedPane.RIGHT), comp); 
	            return; 
	        default: 
	            tabPane.addTab(text, null, comp); 
	    } 
	} 	
	private ActionListener onSelectSourceClick = new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			JFileChooser chooser = new JFileChooser();
			
			int result = chooser.showOpenDialog( PainterlyFrame.this );
			if ( result == JFileChooser.APPROVE_OPTION ) {
				_document.selectSourceFile( chooser.getSelectedFile().getAbsolutePath() );
				applyButton.setEnabled( true );
				applyButton.doClick();
			} else {
				return;
			}
		}
	};

	private ActionListener onSaveClick = new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			JFileChooser chooser = new JFileChooser();
			
			int result = chooser.showSaveDialog( PainterlyFrame.this );
			if ( result == JFileChooser.APPROVE_OPTION ) {
				_document.saveDocument( chooser.getSelectedFile().getAbsolutePath() );
			} else {
				return;
			}
		}
	};

	private ActionListener onExitClick = new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
			System.exit( 0 );
		}
	};
	
}
