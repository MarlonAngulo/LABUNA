/**
 * Created by Derian on 12/5/2018.
 */
package com.example.progland.labuna;

//clase para guardar y optener los laboratorios
public class laboratorios {
    private  int lid;//id del lab
    private  String nombre;//nombre del lab

    public  laboratorios(){//constructor sin parametros

    }
    public  laboratorios(int lid, String nombre){//constructor parametrisado
        this.lid=lid;
        this.setNombre(nombre);
    }

//get y set del id
    public int getId() {
        return this.lid;
    }
    public void setId(int id) {
        this.lid = lid;
    }
//get y set del nomre
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getNombre() {
        return this.nombre;
    }


//metodo para devolver el nombre
    @Override
    public String toString(){
        return nombre;
    }



}
