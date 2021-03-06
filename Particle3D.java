import java.util.Scanner;
import java.io.*;
/**
 * A class for particle in 3D space, complete with constructors
 * setters and getters
 *
 * @author M. Kirsz
 * @author R. Pruciak
 * @version "02/2016"
 *
 */
public class Particle3D  {

    // Private properties
    private String label;
    private double mass;
    private Vector3D position;
    private Vector3D velocity;

    /* 
     * Constructors
     *
     */

    /** Default constructor. Constructs a new Particle3D. 
     */   
    public Particle3D() {
        this.setMass(0.0);
        this.setPosition(new Vector3D(0.0,0.0,0.0));
        this.setVelocity(new Vector3D(0.0,0.0,0.0));
	this.setLabel("null");
    }
    
    /** Explicit constructor. Constructs a new Particle3D with
     * explicitly given position, velocity, and mass.
     *
     * @param l a String that represents particle label.
     * @param m a double that defines the mass.
     * @param p a Vector3D that defines the position.
     * @param v a Vector3D that defines the velocity.
     */
    public Particle3D(String l, double m, Vector3D p, Vector3D v) {
	this.setLabel(l);
        this.setMass(m);
        this.setPosition(p);
        this.setVelocity(v);
    }
    
    /*
     * Setters and getters
     *
     */ 

    /** Set the label of a Particle3D.
     *
     * @param l a String representing the label.
     */
    public void setLabel(String l) { this.label = l; }

    /** Set the mass of a Particle3D.
     *
     * @param m a double representing the mass.
     */
    public void setMass(double m)     { this.mass = m; }

    /** Set the position of a Particle3D
     *
     * @param p a Vector3D representing the position.
     */
    public void setPosition(Vector3D p) {
	this.position = new Vector3D(p);
    }

    /** Set the velocity of a Particle3D
     *
     * @param v a Vector3D representing the velocity.
     */
    public void setVelocity(Vector3D v) {
	this.velocity = new Vector3D(v);
    }
    
    /** Get the label of a Particle3D.
     *
     * @return a String representing the label.
     */
    public String getLabel() { return label; }
   
    /** Get the position of a Particle3D.
     *
     * @return a Vector3D representing the position.
     */
    public Vector3D getPosition() { return position; }

    /** Get the velocity of a Particle3D.
     *
     * @return a Vector3D representing the velocity.
     */
    public Vector3D getVelocity() { return velocity; }

    /** Get the mass of a Particle3D.
     *
     * @return a double representing the mass.
     */
    public double getMass() { return mass; }

    /* 
     * toString Method
     */
    
    /** Returns String representation of a Particle3D.
     * 
     * @return the string representation of a Particle3D
     */
    public String toString() {
        return this.getLabel() + " " + this.getPosition();
    } 

    /*
     * Instance Methods
     */

    /** Time integration support: evolve the velocity
     * according to dv = F * dt / m
     *
     * @param dt a double that is the timestep
     * @param force Vector3D that is the current force on the particle.
     */
    public void leapVelocity(double dt, Vector3D force) {
	velocity = Vector3D.vecAdd(velocity,force.scalMul(dt/mass));
    }

    /** Time integration support: evolve the position
     * according to dx = v * dt.
     *
     * @param dt a double that is the timestep.
     */
    public void leapPosition(double dt) {
        position = Vector3D.vecAdd(position,velocity.scalMul(dt));
    }
    
    /** Time integration support: evolve the position
     * according to dx = v * dt + 0.5 * a * dt^2.
     *
     * @param dt a double that is the timestep.
     * @param force a Vector3D that is the current force.
     */
    public void leapPosition(double dt, Vector3D force) {
	// position = position + velocity * dt + 0.5 * force/mass * dt*dt;
	position = Vector3D.vecAdd(position,velocity.scalMul(dt));
	position = Vector3D.vecAdd(position,force.scalMul(0.5/mass*dt*dt));
    } 
    
    /*
     * Static Methods
     *
     */

    /** separation vector between two Particle3D
     *
     * @param p1 first particle
     * @param p2 second particle
     * @return Vector3D representing separation from p1 to p2
     */
    public static Vector3D pSep(Particle3D p1, Particle3D p2) {
	return Vector3D.vecSub(p2.getPosition(),p1.getPosition());
    }

    /** Unit vector between two Particle3D
     *
     * @param p1 first particle
     * @param p2 second particle
     * @return unit Vector3D pointing from p1 to p2
     */
    public static Vector3D unitVec(Particle3D p1, Particle3D p2) {
	double r = pSep(p1,p2).mag();
	if (r==0) { return new Vector3D(); }
	else {
	    return pSep(p1,p2).scalDiv(r);
	}
    }

    /** Magnitude of force between two Particle3D
     *
     * @param p1 first particle
     * @param p2 second particle
     * @return a double that is a magnitude of force between two particles
     */
    public static double magForce(Particle3D p1, Particle3D p2) {
	double r = pSep(p1,p2).magSq();
	if  (r == 0) { return 0.0; }
	else {
	    return Nbody.G * p1.getMass() * p2.getMass() / r;
	}
    }

    /** Force Vector3D between two Particle3D
     *
     * @param p1 first particle
     * @param p2 second particle
     * @return a Vector3D representing force on p1 due to p2
     */
    public static Vector3D vecForce(Particle3D p1, Particle3D p2) {
	return unitVec(p1,p2).scalMul(magForce(p1,p2));
    }

    /** The kinetic energy of a Particle3D,
     * calculated as 1/2*m*v^2.
     *
     * @param p a Particle3D
     * @return a double that is the kinetic energy.
     */
    public static double kEnergy(Particle3D p) { 
	return 0.5*p.getMass()*p.getVelocity().mag()*p.getVelocity().mag(); 
    }

    /** The potential energy of two Particle3D
     * calculated as -G*m1*m2/r
     *
     * @param p1 a Particle3D representing first particle
     * @param p2 a Particle3D representing second particle 
     * @return potential energy of p1 and p2
     */
    public static double potEnergy(Particle3D p1, Particle3D p2) {
	return -Nbody.G * p1.getMass() * p2.getMass() / pSep(p1,p2).mag();  }

    /** Total energy of two Particle3D
     * as a sum of kinetic and potential energies
     *
     * @param p1 a Particle3D representing first particle
     * @param p2 a particle3D representing second particle
     * @return double representing total energy of p1 and p2
     */
    public static double totEnergy(Particle3D p1, Particle3D p2) {
	return kEnergy(p1) + kEnergy(p2) + potEnergy(p1, p2); 
    }

    /** A method to read a particle from a file
     *
     * @param scan a Scanner attached to the input file
     * @return Particle3D p
     */    
    public static Particle3D pScanner(Scanner scan) {	
	Particle3D p = new Particle3D();
	p.setLabel(scan.next());
	p.setMass(scan.nextDouble());	
	p.setPosition(new Vector3D
		      (scan.nextDouble(),scan.nextDouble(),scan.nextDouble()));
	p.setVelocity(new Vector3D
		      (scan.nextDouble(),scan.nextDouble(),scan.nextDouble()));
	return p;
    }

    /** Time integration support: evolve the position of all particles
     * in an array according to dx = v * dt.
     *
     * @param dt a double representing the size of a time step
     * @param p a Particle3D array containing all particles in a system
     * @param f a Vector3D array of forces at n-th step
     * @return String formatted for vmd
     */
    public static void leapPosition(double dt, Particle3D[] p, Vector3D[] f) {	 
	for (int i=0; i < p.length; i++) {	    
	    p[i].leapPosition(dt,f[i]);
	}
    }

     /** Time integration support: evolve the velocity for all particles
     * in an array according to dv = F * dt / m
     *
     * @param dt a double representing the size of a time step
     * @param p a Particle3D array containing all particles in a system
     * @param currentForce a Vector3D array of forces at n-th step
     * @param newForce a Vector3D array of forces at n-th+1 step
     * @return String formatted for vmd
     */
    public static void leapVelocity(double dt, Particle3D[] p,
				    Vector3D[] currentForce, Vector3D[] newForce) {
	for (int i=0; i < p.length; i++) {
	    p[i].leapVelocity(dt,Vector3D.vecAdd(currentForce[i],newForce[i]).scalDiv(2));
	}
    }

    /** Updates forces array based on current position
     * of particles in a system
     *
     * @param p a Particle3D array containing all particles in a system
     * @param f a Vector3D array of forces
     * @return String formatted for vmd
     */
    public static void updateForce(Particle3D[] p, Vector3D[] f) {
	for (int i=0; i < p.length; i++) {
	    for (int j=0; j < p.length; j++) {		  
		if (i != j) {
		    f[i] = Vector3D.vecAdd(f[i], vecForce(p[i], p[j]));
		}
	    }
	}  
    }
 
    /** Method formats output suitable for vmd
     * Returns a string which contains positions of all particles
     * in a sytem at particular step
     *
     * @param p a Particle3D array containing all particles in a system
     * @param stepNumber an int representing stepNumber
     * @return String formatted for vmd
     */
    public static String vmd(Particle3D[] p, int stepNumber) {
	String s1;
      	String s2 = ""; // so compiler doesn't complain
	s1 =	String.format("%d\nPoint = %d\n", p.length, stepNumber);
	for (int j=0; j < p.length; j++) {
	    s2 = s2 + String.format("%10s\n", p[j]);
	}   
	return s1+s2;
    }

    /** Total energy of the system of N particles
     * as a sum of kinetic and potential energies
     *
     * @param p1 a Particle3D array containing all particles in a system
     * @return double representing total energy of all particles
     */
    public static double sysEnergy(Particle3D[] p) {
	double energy = 0;
	for (int i=0; i<p.length; i++) {
	    for (int j=i+1; j<p.length; j++) {
		energy += potEnergy(p[i],p[j]);
	    }
	    energy += kEnergy(p[i]);
	}
	return energy;
    }
}
