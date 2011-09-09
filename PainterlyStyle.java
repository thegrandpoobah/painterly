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

public class PainterlyStyle {
	public final static PainterlyStyle IMPRESSIONIST_STYLE = new PainterlyStyle( 0.5f, 1.0f, 1.0f, 100.0f, 16, 4, 8, 255, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, false, 200.0f );
	public final static PainterlyStyle EXPRESSIONIST_STYLE = new PainterlyStyle( 0.5f, 1.0f, 0.25f, 50.0f, 16, 10, 8, 192, 0.0f, 0.0f, 0.7f, 0.0f, 0.0f, 0.0f, false, 200.0f );
	public final static PainterlyStyle COLORISTWASH_STYLE = new PainterlyStyle( 0.5f, 1.0f, 1.0f, 200.0f, 16, 4, 8, 128, 0.0f, 0.0f, 0.0f, 0.3f, 0.3f, 0.3f, false, 200.0f );
	public final static PainterlyStyle POINTILLIST_STYLE = new PainterlyStyle( 0.5f, 0.5f, 1.0f, 100.0f, 0, 0, 4, 255, 0.3f, 0.0f, 0.99f, 0.0f, 0.0f, 0.0f, false, 200.0f );
	
	private float fSigma; // blur factor
	private float fG; // grid size
	private float fC; // curvature filter
	private float threshold; // threshold
	private int maxStrokeLength; // minimum stroke length
	private int minStrokeLength;  // maximum stroke length
	private int maxBrushSize; // the biggest brush used
	private int colorOpacity;
	
	// colour jittering
	private float hJitter;
	private float sJitter;
	private float vJitter;
	
	private float rJitter;
	private float gJitter;
	private float bJitter;
	
	// drawing edges
	private boolean drawEdges;
	private float edgeThreshold;

	public PainterlyStyle( float fSigma, float fG, float fC, float threshold, 
			int maxStrokeLength, int minStrokeLength, int maxBrushSize, int colorOpacity,
			float hJitter, float sJitter, float vJitter,
			float rJitter, float gJitter, float bJitter,
			boolean drawEdges, float edgeThreshold ) {
		this.fSigma = fSigma;
		this.fG = fG;
		this.fC = fC;
		this.threshold = threshold;
		this.maxStrokeLength = maxStrokeLength;
		this.minStrokeLength = minStrokeLength;
		this.maxBrushSize = maxBrushSize;
		this.colorOpacity = colorOpacity;
		this.hJitter = hJitter;
		this.sJitter = sJitter;
		this.vJitter = vJitter;
		this.rJitter = rJitter;
		this.gJitter = gJitter;
		this.bJitter = bJitter;
		this.drawEdges = drawEdges;
		this.edgeThreshold = edgeThreshold;
	}
	
	public PainterlyStyle( PainterlyStyle source ) {
		this.fSigma = source.getBlurFactor();
		this.fG = source.getGridSize();
		this.fC = source.getCurvatureFilter();
		this.threshold = source.getThreshold();
		this.maxStrokeLength = source.getMaximumStrokeLength();
		this.minStrokeLength = source.getMinimumStrokeLength();
		this.maxBrushSize = source.getMaximumBrushSize();
		this.colorOpacity = source.getColorOpacity();
		this.hJitter = source.getHueJitter();
		this.sJitter = source.getSaturationJitter();
		this.vJitter = source.getValueJitter();
		this.rJitter = source.getRedJitter();
		this.gJitter = source.getGreenJitter();
		this.bJitter = source.getBlueJitter();
	}

	public void setMaximumBrushSize( int newValue ) {
		maxBrushSize = newValue;		
	}
	public int getMaximumBrushSize() {
		return maxBrushSize;
	}
	
	public void setColorOpacity( int newValue ) {
		colorOpacity = newValue;
	}
	public int getColorOpacity() {
		return colorOpacity;
	}
	
	public void setBlurFactor( float newValue ) {	
		fSigma = newValue;
	}
	public float getBlurFactor() {
		return fSigma;
	}
	
	public void setGridSize( float newValue ) {
		fG = newValue;
	}
	public float getGridSize() {
		return fG;
	}
	
	public void setCurvatureFilter( float newValue ) {
		fC = newValue;
	}
	public float getCurvatureFilter() {
		return fC;
	}
	
	public void setThreshold( float newValue ) {
		threshold = newValue;
	}
	public float getThreshold() {
		return threshold;
	}
	
	public void setMinimumStrokeLength( int newValue ) {
		minStrokeLength = newValue;
	}
	public int getMinimumStrokeLength() {
		return minStrokeLength;
	}
	
	public void setMaximumStrokeLength( int newValue ) {
		maxStrokeLength = newValue;
	}
	public int getMaximumStrokeLength() {
		return maxStrokeLength;
	}

	public void setHueJitter( float newValue ) {
		hJitter = newValue;
	}
	public float getHueJitter() {
		return hJitter;
	}	
	
	public void setSaturationJitter( float newValue ) {
		sJitter = newValue;
	}
	public float getSaturationJitter() {
		return sJitter;
	}
	
	public void setValueJitter( float newValue ) {
		vJitter = newValue;
	}
	public float getValueJitter() {
		return vJitter;
	}
	
	public void setRedJitter( float newValue ) {
		rJitter = newValue;
	}
	public float getRedJitter() {
		return rJitter;
	}
	
	public void setGreenJitter( float newValue ) {
		gJitter = newValue;
	}
	public float getGreenJitter() {
		return gJitter;
	}
	
	public void setBlueJitter( float newValue ) {
		bJitter = newValue;
	}
	public float getBlueJitter() {
		return bJitter;
	}
	
	public void setDrawEdges( boolean newValue ) {
		drawEdges = newValue;
	}
	public boolean getDrawEdges() {
		return drawEdges;
	}
	
	public void setEdgeThreshold( float newValue ) {
		edgeThreshold = newValue;
	}
	public float getEdgeThreshold() {
		return edgeThreshold;
	}
};
