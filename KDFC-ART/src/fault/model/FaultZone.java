package fault.model;

import auxiliary.model.Point;

public abstract class FaultZone {
	
	public double theta;
	public abstract Boolean findTarget(Point p);
	

}
