package com.jhlabs.map.proj;

import com.jhlabs.Point2D;
import com.jhlabs.map.*;

public class SwissObliqueMercatorProjection extends CylindricalProjection
{
	private double K, c, hlf_e, kR, cosp0, sinp0;
	private static final int NITER = 6;

public void initialize()
{
    super.initialize();
    
	double cp, phip0, sp;

	hlf_e = 0.5 * e;
	cp = Math.cos(projectionLatitude);
	cp *= cp;
	c = Math.sqrt(1 + es * cp * cp * rone_es);
	sp = Math.sin(projectionLatitude);
	sinp0 = sp / c;
	phip0 = Math.asin(sinp0);
	cosp0 = Math.cos(phip0);
	sp *= e;
	K = Math.log(Math.tan(MapMath.QUARTERPI + 0.5 * phip0)) - c * (
		Math.log(Math.tan(MapMath.QUARTERPI + 0.5 * projectionLatitude)) - hlf_e *
		Math.log((1. + sp) / (1. - sp)));
	kR = scaleFactor * Math.sqrt(one_es) / (1. - sp * sp);
}

public Point2D.Double project(double lam, double phi, Point2D.Double xy)
{
	double phip, lamp, phipp, lampp, sp, cp;

	sp = e * Math.sin(phi);
	phip = 2.* Math.atan( Math.exp( c * (
			Math.log(Math.tan(MapMath.QUARTERPI + 0.5 * phi)) - hlf_e * Math.log((1. + sp)/(1. - sp)))
		+ K)) - MapMath.HALFPI;
	lamp = c * lam;
	cp = Math.cos(phip);
	phipp = Math.asin(cosp0 * Math.sin(phip) - sinp0 * cp * Math.cos(lamp));
	lampp = Math.asin(cp * Math.sin(lamp) / Math.cos(phipp));
	xy.x = kR * lampp;
	xy.y = kR * Math.log(Math.tan(MapMath.QUARTERPI + 0.5 * phipp));
	return xy;
}

public Point2D.Double projectInverse(double x, double y, Point2D.Double lp)
{
	double phip, lamp, phipp, lampp, cp, esp, con, delp;
	int i;

	phipp = 2. * (Math.atan(Math.exp(y / kR)) - MapMath.QUARTERPI);
	lampp = x / kR;
	cp = Math.cos(phipp);
	phip = Math.asin(cosp0 * Math.sin(phipp) + sinp0 * cp * Math.cos(lampp));
	lamp = Math.asin(cp * Math.sin(lampp) / Math.cos(phip));
	con = (K - Math.log(Math.tan(MapMath.QUARTERPI + 0.5 * phip)))/c;
	for (i = NITER; i > 0; i--)
	{
		esp = e * Math.sin(phip);
		delp = (con + Math.log(Math.tan(MapMath.QUARTERPI + 0.5 * phip)) - hlf_e *
				Math.log((1. + esp)/(1. - esp)) ) *
			(1. - esp * esp) * Math.cos(phip) * rone_es;
		phip -= delp;
		if (Math.abs(delp) < EPS10)
			break;
	}
	// TODO error was set in C on this condition
	if (i == 0) { }
	
	lp.y = phip;
	lp.x = lamp / c;

	return lp;
}

public boolean hasInverse() {
        return true;
    }

    public String toString() {
    	// For CH1903
        return "Swiss Oblique Mercator";
    }

}
