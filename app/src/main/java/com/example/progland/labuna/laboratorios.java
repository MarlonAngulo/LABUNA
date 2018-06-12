/**
 * Created by Derian on 12/5/2018.
 */
package com.example.progland.labuna;


/**
 * clase para guardar y optener los laboratorios
 */
public class laboratorios {
    private  int lid;//id del lab
    private  String nombre;//nombre del lab

    /**
     * constructor sin parametros
     */
    public  laboratorios(){

    }

    /**
     * Constructor parametrisado
     * @param lid Parametro tipo Int
     * @param nombre Parametro tipo String
     */
    public  laboratorios(int lid, String nombre){
        this.lid=lid;
        this.setNombre(nombre);
    }

    /**
     * Funcion get Id, extrae ids guardados en la clase.
     * @return
     */
    public int getId() {
        return this.lid;
    }

    /**
     * Metodo SetNombre, aplica o guarda un id
     * @param id Parametro tipo int
     */
    public void setId(int id) {
        this.lid = lid;
    }

    /**
     * Metodo SetNombre, aplica o guarda un nombre
     * @param nombre Parametro tipo String
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Funcion getNombre, extrae Nombres guardados en la clase.
     * @return
     */
    public String getNombre() {
        return this.nombre;
    }

     /**
     * Funcion  para devolver el nombre
     * @return
     */
    @Override
    public String toString(){
        return nombre;
    }



}
