package Personal;

/**
 * Created by DeeJa on 13/3/2018.
 */

public class Titular {
    private String titulo;
    private String descripcion;
    private String imagen;
    private int img;

    public Titular()
    {}
        public Titular(String titulo, String descripcion, int img)
        {
            this.setTitulo(titulo);
            this.setDescripcion(descripcion);
            this.setImg(img);
        }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }




}
