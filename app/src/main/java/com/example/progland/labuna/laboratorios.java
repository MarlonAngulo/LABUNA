/**
 * Created by Derian on 12/5/2018.
 */
package com.example.progland.labuna;

public class laboratorios {
    private  int id;
    private  String nombre;

    public  laboratorios(){

    }

    public  laboratorios(int id, String nombre){
        this.id=id;
        this.setNombre(nombre);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    @Override
    public String toString(){
        return nombre;
    }
}
