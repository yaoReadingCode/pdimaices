/*
 * Created on Jun 9, 2005
 * @author Rafael Santos (rafael.santos@lac.inpe.br)
 * 
 * Part of the Java Advanced Imaging Stuff site
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI)
 * 
 * STATUS: Complete, but will probably add some features in the future.
 * 
 * Redistribution and usage conditions must be done under the
 * Creative Commons license:
 * English: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.en
 * Portuguese: http://creativecommons.org/licenses/by-nc-sa/2.0/br/deed.pt
 * More information on design and applications are on the projects' page
 * (http://www.lac.inpe.br/~rafael.santos/Java/JAI).
 */
package algorithms.kmeans;

/**
 * This class represents the information about a cluster. Instances of it will
 * be returned by a clustering task.
 */
public class KMeansClusterDescriptor
  {
  // The cluster description fields: a centroid and the number of assigned points or pixels.
  private float[] centroid;
  private int numberOfAssignedPoints;
  
 /**
  * Returns the centroid of this cluster as a array of doubles.
  * @return the cluster's centroid.
  */ 
  public float[] getCentroid()
    {
    return centroid;
    }
  
 /**
  * Set the centroid values of this cluster.
  * @param centroid the array with the centroid values.
  */
  public void setCentroid(float[] centroid)
    {
    this.centroid = centroid;
    }
  
 /**
  * Get the number of points with are assigned to this cluster.
  * @return the assignment count.
  */
  public int getNumberOfAssignedPoints()
    {
    return numberOfAssignedPoints;
    }
  
 /**
  * Set the number of points with are assigned to this cluster.
  * @param numberOfAssignedPoints the new assignment count.
  */
  public void setNumberOfAssignedPoints(int numberOfAssignedPoints)
    {
    this.numberOfAssignedPoints = numberOfAssignedPoints;
    }
  
 /**
  * Returns the information about this cluster as a String.
  */
  public String toString()
    {
    String r = "Centroid: [";
    for(int d=0;d<centroid.length;d++) r += centroid[d]+" ";
    r +=" ] count: "+numberOfAssignedPoints;
    return r;
    }
  
  }