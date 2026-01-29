package org.example.Modelos;

import jakarta.persistence.*;

@Entity
@Table(name="motivo_fuera_servicio")
public class MotivoFueraDeServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name="cambio_estado_id")
    private CambioEstado cambioEstado;

    @ManyToOne(optional=false)
    @JoinColumn(name="motivo_tipo_id")
    private MotivoTipo motivoTipo;

    private String comentario;


    public MotivoFueraDeServicio() {
    }

    public MotivoFueraDeServicio(String comentario, MotivoTipo motivoTipo) {
        this.comentario = comentario;
        this.motivoTipo = motivoTipo;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public MotivoTipo getMotivoTipo() {
        return motivoTipo;
    }

    public void setMotivoTipo(MotivoTipo motivoTipo) {
        this.motivoTipo = motivoTipo;
    }

    //Ver new()

}
