package edu.eci.cvds.sampleprj.dao.mybatis;

import edu.eci.cvds.sampleprj.dao.ClienteDAO;
import edu.eci.cvds.sampleprj.dao.PersistenceException;
import edu.eci.cvds.sampleprj.dao.mybatis.mappers.ClienteMapper;
import edu.eci.cvds.samples.entities.Cliente;
import edu.eci.cvds.samples.entities.Item;
import edu.eci.cvds.samples.entities.ItemRentado;
import edu.eci.cvds.samples.services.ExcepcionServiciosAlquiler;

import javax.inject.Inject;
import java.sql.Date;
import java.util.List;

/**
 * @author Iván Camilo Rincón Saavedra
 * @author Leonardo Galeano
 * @version 10/1/2020
 */
public class MyBATISClienteDAO implements ClienteDAO {
    @Inject
    private ClienteMapper ClienteMapper;

    @Override
    public Cliente load(long id) throws PersistenceException {
        try{
            return ClienteMapper.consultarCliente(id);
        }
        catch (org.apache.ibatis.exceptions.PersistenceException e){
            throw new PersistenceException("Error al consultar cliente con id " + id,e);
        }
    }

    @Override
    public void save(Cliente cli) throws PersistenceException {
        try{
            ClienteMapper.agregarCliente(cli);
        }
        catch(org.apache.ibatis.exceptions.PersistenceException e){
            throw new PersistenceException("Error al agregar cliente.",e);
        }
    }

    @Override
    public void agregarItemRentado(long docu, int id, Date fechaini, Date fechafin) throws PersistenceException {
        try{
            ClienteMapper.agregarItemRentadoACliente(docu,id,fechaini,fechafin);
        }
        catch(org.apache.ibatis.exceptions.PersistenceException e){
            throw new PersistenceException("Error al agregar item rentado con id " + id + " al cliente con documento " + docu +".",e);
        }
    }

    @Override
    public List<Cliente> consultarClientes() throws PersistenceException {
        try{
            return ClienteMapper.consultarClientes();
        }
        catch(org.apache.ibatis.exceptions.PersistenceException e){
            throw new PersistenceException("Error al consultar.",e);
        }
    }

    @Override
    public List<Item> consultarItemsRentados(long id) throws PersistenceException {
        try{
            return ClienteMapper.consultarItemsRentados(id);
        }
        catch(org.apache.ibatis.exceptions.PersistenceException e){
            throw new PersistenceException("Error al consultar.",e);
        }
    }
}
