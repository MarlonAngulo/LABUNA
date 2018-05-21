package com.example.progland.labuna;

/**
 * Created by DeeJa on 21/5/2018.
 */
// Se debe crear en el paquete com.example...
// Forma de uso:
// VariablesGlobales vg = VariablesGlobales.getInstance(); vg.setMitexto("Hola");    int i = vg.getMivalor();
public class VariablesGlobales {
    private String mitexto=" ";
    private int mivalor=0;
    private double minum=3.14;

    private static VariablesGlobales instance = null;

    protected VariablesGlobales() {}
    public static VariablesGlobales getInstance() {
        if(instance == null) {instance = new VariablesGlobales(); }
        return instance;
    }


    public String getMitexto() {
        return mitexto;
    }

    public void setMitexto(String mitexto) {
        this.mitexto = mitexto;
    }

    public int getMivalor() {
        return mivalor;
    }

    public void setMivalor(int mivalor) {
        this.mivalor = mivalor;
    }

    public double getMinum() {
        return minum;
    }

    public void setMinum(double minum) {
        this.minum = minum;
    }
}// fin de la clase de variables globales
