package edu.eci.cvds.samples.services.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import edu.eci.cvds.sampleprj.dao.*;

import edu.eci.cvds.samples.entities.Cliente;
import edu.eci.cvds.samples.entities.Item;
import edu.eci.cvds.samples.entities.ItemRentado;
import edu.eci.cvds.samples.entities.TipoItem;
import edu.eci.cvds.samples.services.ExcepcionServiciosAlquiler;
import edu.eci.cvds.samples.services.ServiciosAlquiler;
import org.mybatis.guice.transactional.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Singleton
public class ServiciosAlquilerImpl implements ServiciosAlquiler {

    @Inject
    private ItemDAO itemDAO;

    @Inject
    private ClienteDAO clienteDAO;

    @Inject
    private ItemRentadoDAO itemRentadoDAO;

    @Inject
    private TipoItemDAO tipoItemDAO;

    @Override
    public int valorMultaRetrasoxDia(int itemId) throws ExcepcionServiciosAlquiler{
        try{
            Optional<Item> item = Optional.ofNullable(itemDAO.load( itemId ) );
            if( !item.isPresent()){
                throw new ExcepcionServiciosAlquiler("El item con id:" +itemId+ " no existe en la base de datos." );
            }
            return (int)item.get().getTarifaxDia();
        }
        catch (PersistenceException ex){
            throw new ExcepcionServiciosAlquiler("Error al consultar al consultar el valor de la multa del item con id: " +itemId ,ex);
        }

    }

    @Override
    public Item consultarItem(int id) throws ExcepcionServiciosAlquiler {
        try {
            return itemDAO.load(id);
        } catch (PersistenceException ex) {
            throw new ExcepcionServiciosAlquiler("Error al consultar el item "+id,ex);
        }
    }

    @Override
    public Cliente consultarCliente(long docu) throws ExcepcionServiciosAlquiler {
        try{
            return clienteDAO.load(docu);
        }
        catch(PersistenceException ex){
            throw new ExcepcionServiciosAlquiler("Cliente no encontrado.",ex);
        }
    }

    @Override
    public List<ItemRentado> consultarItemsCliente(long idcliente) throws ExcepcionServiciosAlquiler {
        try{
            return itemRentadoDAO.consultarItemsRentados(idcliente);
        }
        catch (PersistenceException ex){
            throw new ExcepcionServiciosAlquiler("Error al consultar items cliente.",ex);
        }
    }

    @Override
    public List<Cliente> consultarClientes() throws ExcepcionServiciosAlquiler {
        try{
            return clienteDAO.consultarClientes();
        }
        catch (PersistenceException ex){
            throw new ExcepcionServiciosAlquiler("Error al consultar clientes.",ex);
        }
    }

    @Override
    public List<Item> consultarItemsDisponibles() throws ExcepcionServiciosAlquiler{
        try{
            return itemDAO.load();
        }
        catch (PersistenceException ex){
            throw new ExcepcionServiciosAlquiler("Error al consultar Items disponibles.",ex);
        }
    }

    @Override
    public long consultarMultaAlquiler(int iditem, Date fechaDevolucion) throws ExcepcionServiciosAlquiler {
        throw new ExcepcionServiciosAlquiler("Sin id de un cliente no se puede calcular la multa.");
    }

    @Override
    public TipoItem consultarTipoItem(int id) throws ExcepcionServiciosAlquiler {
        try {
            return tipoItemDAO.load(id);
        } catch (PersistenceException ex) {
            throw new ExcepcionServiciosAlquiler("Error al consultar Tipo Item con id " + id, ex);
        }
    }

    @Override
    public List<TipoItem> consultarTiposItem() throws ExcepcionServiciosAlquiler {
        try {
            return tipoItemDAO.loadTiposItems();
        } catch (PersistenceException ex) {
            throw new ExcepcionServiciosAlquiler("Error al consultar Tipo Items.", ex);
        }
    }

    @Transactional
    @Override
    public void registrarAlquilerCliente(Date date, long docu, Item item, int numdias) throws ExcepcionServiciosAlquiler {
        try {
            clienteDAO.agregarItemRentado(docu,item.getId(),date, Date.valueOf(date.toLocalDate().plusDays(numdias)));
        } catch (PersistenceException ex) {
            throw new ExcepcionServiciosAlquiler("Error al registrar Alquiler a cliente.", ex);
        }
    }

    @Transactional
    @Override
    public void registrarCliente(Cliente c) throws ExcepcionServiciosAlquiler {
        try {
            clienteDAO.save(c);
        }
        catch (PersistenceException ex) {
            throw new ExcepcionServiciosAlquiler("Error al agregar cliente.", ex);
        }
    }

    @Override
    public long consultarCostoAlquiler(int iditem, int numdias) throws ExcepcionServiciosAlquiler {
        try{
            Item cons = itemDAO.load(iditem);
            return cons.getTarifaxDia() * numdias;
        }
        catch(PersistenceException ex){
            throw new ExcepcionServiciosAlquiler("Error al consultar costo de alquiler de x dias.",ex);
        }
    }

    @Transactional
    @Override
    public void actualizarTarifaItem(int id, long tarifa) throws ExcepcionServiciosAlquiler {
        try{
            itemDAO.actualizarTarifa(id,tarifa);
        }
        catch(PersistenceException ex){
            throw new ExcepcionServiciosAlquiler("Error al cambiar tarifa del item con id " + id,ex);
        }
    }

    @Transactional
    @Override
    public void registrarItem(Item i) throws ExcepcionServiciosAlquiler {
       try{
           itemDAO.save(i);
       }
       catch (PersistenceException ex){
           throw new ExcepcionServiciosAlquiler("Error al registrar item.",ex);
       }
    }

    @Transactional
    @Override
    public void vetarCliente(long docu, boolean estado) throws ExcepcionServiciosAlquiler {
        try{
            clienteDAO.vetar(docu,estado);
        }
        catch(PersistenceException ex){
            throw new ExcepcionServiciosAlquiler("Error al vetar",ex);
        }
    }

}