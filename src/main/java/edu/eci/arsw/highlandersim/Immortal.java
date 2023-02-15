package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

    private ImmortalUpdateReportCallback updateCallback = null;

    private int health;

    private int defaultDamageValue;

    private final List<Immortal> immortalsPopulation;

    private final String name;

    private final Random r = new Random(System.currentTimeMillis());

    private boolean paused = false;

    private int id;

    private boolean alive = true;


    public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue, ImmortalUpdateReportCallback ucb, int id) {
        super(name);
        this.updateCallback = ucb;
        this.name = name;
        this.immortalsPopulation = immortalsPopulation;
        this.health = health;
        this.defaultDamageValue = defaultDamageValue;
        this.id = id;
    }

    public void run() {

        while (alive) {
            Immortal im;

            int myIndex = immortalsPopulation.indexOf(this);

            int nextFighterIndex = r.nextInt(immortalsPopulation.size());

            //avoid self-fight
            if (nextFighterIndex == myIndex) {
                nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            }

            im = immortalsPopulation.get(nextFighterIndex);

            //synchronized (immortalsPopulation) {
            this.fight(im);
            //}

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (paused) {
                synchronized (immortalsPopulation) {
                    try {
                        immortalsPopulation.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }

        }

    }

    public void fight(Immortal i2) {
        Immortal i1 = this;
        if (i1.getIde() < i2.getIde()) {
            Immortal temp = i1;
            i1 = i2;
            i2 = temp;
        }

        synchronized (i1) {
            synchronized (i2) {
                if (i2.getHealth() > 0) {
                    i2.changeHealth(i2.getHealth() - defaultDamageValue);
                    this.health += defaultDamageValue;
                    updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
                } else {
                    i2.setAlive(false);
                    updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
                }
            }
        }


    }

    public void changeHealth(int v) {
        health = v;
    }

    public int getHealth() {
        return health;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getIde() {
        return id;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    @Override
    public String toString() {

        return name + "[" + health + "]";
    }

}
