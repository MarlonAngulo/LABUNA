/**
 * Created by Derian on 12/5/2018.
 */
package com.example.progland.labuna;

public class laboratorios {
    private  int lid;
    private  String nombre;

    public  laboratorios(){

    }

    public  laboratorios(int lid, String nombre){
        this.lid=lid;
        this.setNombre(nombre);
    }


    public int getId() {
        return this.lid;
    }
    public void setId(int id) {
        this.lid = lid;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getNombre() {
        return this.nombre;
    }



    @Override
    public String toString(){
        return nombre;
    }



}