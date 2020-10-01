package edu.eci.cvds.sampleprj.dao.mybatis.mappers;

import edu.eci.cvds.sampleprj.dao.PersistenceException;
import edu.eci.cvds.samples.entities.Item;
import edu.eci.cvds.samples.entities.ItemRentado;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Iván Camilo Rincón Saavedra
 * @author Leonardo Galeano
 * @version 10/1/2020
 */
public interface ItemRentadoMapper {
    /**
     * Metodo que consulta todos los item rentados por un cliente
     * @param id, long que representa el identificador del cliente
     * @return List<item>, lista de items rentados por un cliente especifico
     */
    public List<ItemRentado> consultarItemsRentados(@Param("idcli") long id);


    /**
     * Metodo que consulta los items rentados
     * @return List<ItemRentado> lista de los items rentados
     * @throws PersistenceException
     */
    public List<ItemRentado> consultarItemRentados();


}
