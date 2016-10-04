/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculate;

import ThreadManagement.EdgeGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import jsf31kochfractalfx.JSF31KochFractalFX;
import timeutil.TimeStamp;

/**
 *
 * @author Stefan
 */
public class KochManager
{
    private JSF31KochFractalFX application;
    //private KochFractal kochFractal;
    private KochFractal kochFractal1 = new KochFractal();
    private KochFractal kochFractal2 = new KochFractal();
    private KochFractal kochFractal3 = new KochFractal();
    private TimeStamp timeStampCalc;// = new TimeStamp();
    private TimeStamp timeStampDraw = new TimeStamp();
    private Object stefan;
    private int next;
    
    private ArrayList<Edge> edges = new ArrayList<>();
     
    public KochManager(JSF31KochFractalFX application, Object stefan)
    {
        this.application = application;
        this.stefan = stefan;
    }
    
    public void addEdges(Edge edge) 
    {
        synchronized(stefan)
        {
            edges.add(edge);  
        }
    }
    public JSF31KochFractalFX getApplication()
    {
        return application;
    }
    
    public void changeLevel(int nxt)
    {
        kochFractal1.setLevel(nxt);
        kochFractal2.setLevel(nxt);
        kochFractal3.setLevel(nxt);
        this.next = nxt;
        
        
        edges.clear();
        //kochFractal.generateLeftEdge();
        //kochFractal.generateBottomEdge();
        //kochFractal.generateRightEdge();
        Thread thread1 = new Thread(new EdgeGenerator(0, this, kochFractal1/*.getLevel()*/, stefan));
        Thread thread2 = new Thread(new EdgeGenerator(1, this, kochFractal2/*.getLevel()*/, stefan));
        Thread thread3 = new Thread(new EdgeGenerator(2, this, kochFractal3/*.getLevel()*/, stefan));
        
        timeStampCalc = new TimeStamp();
        timeStampCalc.setBegin("" + (next - 1));
        thread1.start();
        thread2.start();
        thread3.start();
        
        //application.setTextCalc(timeStampCalc.toString());
        application.setTextNrEdges("" + kochFractal1.getNrOfEdges());
    }
    
    public synchronized void drawEdges() 
    {
        timeStampDraw.init();
        timeStampDraw.setBegin("" + (kochFractal1.getLevel()-1));
        application.clearKochPanel();
        
        for(Edge edge : edges)
        {
            application.drawEdge(edge);
        }
        timeStampDraw.setEnd("" + (kochFractal1.getLevel()));
        application.setTextDraw(timeStampDraw.toString());
    }
    
    public List<Edge> getEdges()
    {
        return edges;
    }

    public void setDone() {
        timeStampCalc.setEnd("level " + (next));
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                application.setTextCalc(timeStampCalc.toString());
            }
        });
        
    }

}
