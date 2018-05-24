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
    private String tipo = " ";

    private int IdReservas = 0;
    private String NombreReservas = "";
    private static VariablesGlobales instance = null;

    protected VariablesGlobales() {}
    public static VariablesGlobales getInstance() {
        if(instance == null) {instance = new VariablesGlobales(); }
        return instance;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public int getIdReservas() {
        return IdReservas;
    }

    public void setIdReservas(int idReservas) {
        IdReservas = idReservas;
    }

    public String getNombreReservas() {
        return NombreReservas;
    }

    public void setNombreReservas(String nombreReservas) {
        NombreReservas = nombreReservas;
    }
}// fin de la clase de variables globales
