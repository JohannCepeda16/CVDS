package edu.eci.cvds.view;

import edu.eci.cvds.samples.entities.Cliente;
import edu.eci.cvds.samples.services.ExcepcionServiciosAlquiler;
import edu.eci.cvds.samples.services.ServiciosAlquiler;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import java.util.List;

/**
 * @author Iván Camilo Rincón Saavedra
 * @version 10/5/2020
 */
@ManagedBean(name = "alquiler")
@ApplicationScoped
public class AlquilerItemsBean extends BasePageBean {
    @Inject
    private ServiciosAlquiler serviciosAlquiler;
    private List<Cliente>clientes;
    private Cliente selectedCliente;

    public void consultarClientes(){
        try {
            clientes = serviciosAlquiler.consultarClientes();
        } catch (ExcepcionServiciosAlquiler excepcionServiciosAlquiler) {
            excepcionServiciosAlquiler.printStackTrace();
        }

    }

    public void registrarCliente(String nombre, long documento, String telefono, String direccion, String email ){
        try {
            System.out.println(serviciosAlquiler);
            serviciosAlquiler.registrarCliente( new Cliente(nombre,documento,telefono,direccion,email) );
        } catch (ExcepcionServiciosAlquiler excepcionServiciosAlquiler) {
            System.out.println(excepcionServiciosAlquiler.getMessage());
        }
    }

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public Cliente getSelectedCliente() {
        return selectedCliente;
    }

    public void setSelectedCliente(Cliente selectedCliente) {
        this.selectedCliente = selectedCliente;
    }
}
