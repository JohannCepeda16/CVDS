package edu.eci.cvds.samples.services.impl;

import edu.eci.cvds.samples.entities.Cliente;
import edu.eci.cvds.samples.entities.Item;
import edu.eci.cvds.samples.entities.ItemRentado;
import edu.eci.cvds.samples.entities.TipoItem;
import edu.eci.cvds.samples.services.ExcepcionServiciosAlquiler;
import edu.eci.cvds.samples.services.ServiciosAlquiler;
import org.mybatis.guice.transactional.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ServiciosAlquilerItemsStub implements ServiciosAlquiler {

    private static final int MULTA_DIARIA=5000;
    private final static long MILLISECONDS_IN_DAY = 24 * 60 * 60 * 1000;

    private final Map<Long,Cliente> clientes;
    private final Map<Integer,Item> itemsDisponibles;
    private final Map<Integer,ItemRentado> itemsrentados;
    private final Map<Integer,TipoItem> tipositems;

    private final Map<Integer,Long> mapaPrestamosPorIdCliente;

    public ServiciosAlquilerItemsStub() {
        clientes = new HashMap<>();
        itemsDisponibles = new HashMap<>();
        itemsrentados = new HashMap<>();
        tipositems = new HashMap<>();
        mapaPrestamosPorIdCliente=new HashMap<>();
        poblar();
    }

    @Override
    public int valorMultaRetrasoxDia(int itemId) throws ExcepcionServiciosAlquiler {
        //consultarItem( itemId).getTarifaxDia();
        return MULTA_DIARIA;
    }

    @Override
    public Cliente consultarCliente(long docu) throws ExcepcionServiciosAlquiler {
        Cliente c=null;
        if(clientes.containsKey(docu)){
            c=clientes.get(docu);
        }
        else{
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.NO_CLIENTE);
        }
        return c;
    }

    @Override
    public List<Cliente> consultarClientes() throws ExcepcionServiciosAlquiler {
        return  new LinkedList<>(clientes.values());
    }

    @Transactional
    @Override
    public void registrarCliente(Cliente p) throws ExcepcionServiciosAlquiler {
        if (!clientes.containsKey(p.getDocumento())) {
            clientes.put(p.getDocumento(), p);
        } else {
            throw new ExcepcionServiciosAlquiler("El cliente con documento "+p+" ya esta registrado.");
        }
    }

    @Transactional
    @Override
    public void vetarCliente(long docu, boolean estado) throws ExcepcionServiciosAlquiler {
        if(clientes.containsKey(docu)){
            Cliente c=clientes.get(docu);
            c.setVetado(estado);
        }
        else{throw new ExcepcionServiciosAlquiler("Error al vetar al cliente con id: "+docu);}
    }

    @Override
    public ItemRentado consultarItemsRentados(int idItem, long idCliente) throws ExcepcionServiciosAlquiler {
        if (itemsrentados.containsKey(idItem)){
            return itemsrentados.get( idItem);
        }
        else{
            consultarItem(idItem);
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.NO_ALQUILERITEM);
        }
    }

    @Override
    public Item consultarItem(int id) throws ExcepcionServiciosAlquiler {
        Item i = null;
        if(itemsDisponibles.containsKey(id)){
            i=itemsDisponibles.get(id);
        }
        else if( !itemsrentados.containsKey(id)){
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.NO_ITEM);
        }
        return i;
    }

    @Override
    public List<Item> consultarItemsDisponibles()  {
        return  new LinkedList<>(itemsDisponibles.values());
    }

    @Transactional
    @Override
    public void registrarItem(Item i) throws ExcepcionServiciosAlquiler {
        if (!itemsDisponibles.containsKey(i.getId())) {
            itemsDisponibles.put(i.getId(), i);
        } else {
            throw new ExcepcionServiciosAlquiler("El item " + i.getId() + " ya esta registrado.");
        }
    }

    @Transactional
    @Override
    public void actualizarTarifaItem(int id, long tarifa) throws ExcepcionServiciosAlquiler {
        if( tarifa <= 0){
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.TARIFA_INVALIDA);
        }
        if (itemsDisponibles.containsKey(id)) {
            Item c = itemsDisponibles.get(id);
            c.setTarifaxDia(tarifa);
            itemsDisponibles.put(id, c);
        } else {
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.NO_ITEM);
        }
    }

    @Override
    public TipoItem consultarTipoItem(int id) throws ExcepcionServiciosAlquiler {
        TipoItem i = null;
        if(tipositems.containsKey(id)){
            i=tipositems.get(id);
        }
        else{
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.NO_TIPOITEM);
        }
        return i;

    }

    @Override
    public List<TipoItem> consultarTiposItem() throws ExcepcionServiciosAlquiler {
        return  new LinkedList<>(tipositems.values());
    }

    @Transactional
    @Override
    public void registrarAlquilerCliente(Date date,long docu, Item item, int numdias) throws ExcepcionServiciosAlquiler {
        if (numdias<0){
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.DIAS_INVALIDOS);
        }
        consultarItem(item.getId());
        consultarCliente(docu);
        LocalDate ld=date.toLocalDate();
        LocalDate ld2=ld.plusDays(numdias);

        ItemRentado ir=new ItemRentado(0,item,date,java.sql.Date.valueOf(ld2));

        if (clientes.containsKey(docu)) {
            Cliente c = clientes.get(docu);
            c.getRentados().add(ir);
            itemsDisponibles.remove(ir.getItem().getId());
            itemsrentados.put(item.getId(), ir);
            mapaPrestamosPorIdCliente.put(item.getId(),docu);
        }
        else {
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.NO_CLIENTE);
        }
    }

    @Override
    public List<ItemRentado> consultarItemsCliente(long idcliente) throws ExcepcionServiciosAlquiler{
        if (clientes.containsKey(idcliente)) {
            Cliente c = clientes.get(idcliente);
            return c.getRentados();
        } else {
            throw new ExcepcionServiciosAlquiler("Cliente no registrado:" + idcliente);
        }

    }

    private Cliente consultarClienteConItem(int iditem) throws ExcepcionServiciosAlquiler{
        if (mapaPrestamosPorIdCliente.containsKey(iditem)){
            long idcli=mapaPrestamosPorIdCliente.get(iditem);
            if (clientes.containsKey(mapaPrestamosPorIdCliente.get(iditem))){
                return clientes.get(idcli);
            }
            else{
                throw new ExcepcionServiciosAlquiler("El cliente "+idcli+" asociado al "
                        + "alquiler del item "+iditem+" no esta registrado.");
            }
        }
        else{
            throw new ExcepcionServiciosAlquiler("El item "+iditem+ " no esta alquilado.");
        }
    }

    @Override
    public long consultarMultaAlquiler(int idItem,Date fechaDevolucion,long idCliente) throws ExcepcionServiciosAlquiler{
        if (!itemsrentados.containsKey(idItem)){
            consultarItem( idItem);
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.NO_ALQUILERITEM);
        }
        else{
            ItemRentado ir=itemsrentados.get(idItem);
            LocalDate fechaInicioRenta = ir.getFechainiciorenta().toLocalDate();
            LocalDate fechaMinimaEntrega=ir.getFechafinrenta().toLocalDate();
            LocalDate fechaEntrega=fechaDevolucion.toLocalDate();
            if(fechaEntrega.isBefore( fechaInicioRenta ) ){
                throw new ExcepcionServiciosAlquiler( ExcepcionServiciosAlquiler.FECHA_LIMITE_INVALIDA );
            }
            else if(fechaEntrega.isBefore( fechaMinimaEntrega )  ){
                return 0;
            }
            long diasRetraso = ChronoUnit.DAYS.between(fechaMinimaEntrega, fechaEntrega);
            return diasRetraso*MULTA_DIARIA;
        }
    }

    @Override
    public long consultarCostoAlquiler(int iditem, int numdias) throws ExcepcionServiciosAlquiler {
        if(numdias < 0 ){
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.DIAS_INVALIDOS);
        }
        if (!itemsDisponibles.containsKey(iditem) && !itemsrentados.containsKey(iditem)) {
            throw new ExcepcionServiciosAlquiler(ExcepcionServiciosAlquiler.NO_ITEM);
        } else {
            return itemsDisponibles.get(iditem).getTarifaxDia()*numdias;
        }

    }

    private void poblar() {

        TipoItem ti1=new TipoItem(1,"Video");
        TipoItem ti2=new TipoItem(2,"Juego");
        TipoItem ti3=new TipoItem(3,"Musica");
        tipositems.put(1,ti1);
        tipositems.put(2,ti2);
        tipositems.put(3,ti3);

        Item i1=new Item(ti1, 1, "Los 4 Fantasticos", "Los 4 Fantásticos  es una película de superhéroes  basada en la serie de cómic homónima de Marvel.", java.sql.Date.valueOf("2005-06-08"), 2000, "DVD", "Ciencia Ficcion");
        Item i2=new Item(ti2, 2, "Halo 3", "Halo 3 es un videojuego de disparos en primera persona desarrollado por Bungie Studios.", java.sql.Date.valueOf("2007-09-08"), 3000, "DVD", "Shooter");
        Item i3=new Item(ti3, 3, "Thriller", "Thriller es una canción interpretada por el cantante estadounidense Michael Jackson, compuesta por Rod Temperton y producida por Quincy Jones.", java.sql.Date.valueOf("1984-01-11"), 2000, "DVD", "Pop");
        Item i4=new Item(ti1, 4, "Los 4 Fantasticos", "Los 4 Fantásticos  es una película de superhéroes  basada en la serie de cómic homónima de Marvel.", java.sql.Date.valueOf("2005-06-08"), 2000, "DVD", "Ciencia Ficcion");
        Item i5=new Item(ti2, 5, "Halo 3", "Halo 3 es un videojuego de disparos en primera persona desarrollado por Bungie Studios.", java.sql.Date.valueOf("2007-09-08"), 3000, "DVD", "Shooter");
        Item i6=new Item(ti3, 6, "Thriller", "Thriller es una canción interpretada por el cantante estadounidense Michael Jackson, compuesta por Rod Temperton y producida por Quincy Jones.", java.sql.Date.valueOf("1984-01-11"), 2000, "DVD", "Pop");
        Item i7=new Item(ti3, 7, "Sniper", "Thriller es una canción interpretada por el cantante estadounidense Michael Jackson, compuesta por Rod Temperton y producida por Quincy Jones.", java.sql.Date.valueOf("1984-01-11"), 2000, "DVD", "Pop");
        Item i8=new Item(ti3, 8, "Gta", "Thriller es una canción interpretada por el cantante estadounidense Michael Jackson, compuesta por Rod Temperton y producida por Quincy Jones.", java.sql.Date.valueOf("1984-01-11"), 2000, "DVD", "Pop");
        Item i9=new Item(ti3, 1001169369, "Gta", "Thriller es una canción interpretada por el cantante estadounidense Michael Jackson, compuesta por Rod Temperton y producida por Quincy Jones.", java.sql.Date.valueOf("1984-01-11"), 2000, "DVD", "Pop");
        //items.put(1, i1);
        //items.put(2, i2);
        //items.put(3, i3);
        itemsDisponibles.put(4, i4);
        itemsDisponibles.put(5, i5);
        itemsDisponibles.put(6, i6);
        itemsDisponibles.put(2,i2);
        itemsDisponibles.put(7,i7);
        itemsDisponibles.put(i9.getId(),i9);

        ItemRentado ir1=new ItemRentado(0,i1, java.sql.Date.valueOf("2017-01-01"), java.sql.Date.valueOf("2017-03-12"));
        ItemRentado ir3=new ItemRentado(0,i3, java.sql.Date.valueOf("2017-01-04"), java.sql.Date.valueOf("2017-04-7"));
        ItemRentado ir4=new ItemRentado(0,i4, java.sql.Date.valueOf("2017-01-07"), java.sql.Date.valueOf("2017-07-12"));
        ItemRentado ir8=new ItemRentado(0,i8, java.sql.Date.valueOf("2017-01-07"), java.sql.Date.valueOf("2017-07-12"));

        ArrayList<ItemRentado> list1 = new ArrayList<>();
        list1.add(ir1);
        ArrayList<ItemRentado> list2 = new ArrayList<>();
        list2.add(ir3);
        ArrayList<ItemRentado> list3 = new ArrayList<>();
        list3.add(ir4);
        ArrayList<ItemRentado> list8 = new ArrayList<>();
        list8.add(ir8);

        itemsrentados.put( i1.getId(),ir1);
        itemsrentados.put( i3.getId(),ir3);
        itemsrentados.put( i4.getId(),ir4);
        itemsrentados.put( i8.getId(),ir8);


        Cliente c1=new Cliente("Oscar Alba", 1026585664, "6788952", "KRA 109#34-C30", "oscar@hotmail.com", false,list1);
        Cliente c2=new Cliente("Carlos Ramirez", 1026585663, "6584562", "KRA 59#27-a22", "carlos@hotmail.com", false,list2);
        Cliente c3=new Cliente("Ricardo Pinto", 1026585669, "4457863", "KRA 103#94-a77", "ricardo@hotmail.com", false,list3);
        Cliente c4 =new Cliente( "Javier López",2160666, "4457863", "KRA 103#94-a77", "ricardo@hotmail.com", false,list8);
        Cliente c5 =new Cliente( "Leonardo",999, "4457863", "KRA 103#94-a77", "ricardo@hotmail.com", false,list8);
        clientes.put(c1.getDocumento(), c1);
        clientes.put(c2.getDocumento(), c2);
        clientes.put(c3.getDocumento(), c3);
        clientes.put(c4.getDocumento(),c4);

        mapaPrestamosPorIdCliente.put(i1.getId(),c1.getDocumento());
        mapaPrestamosPorIdCliente.put(i3.getId(),c2.getDocumento());
        mapaPrestamosPorIdCliente.put(i4.getId(),c3.getDocumento());
        mapaPrestamosPorIdCliente.put(i8.getId(),c4.getDocumento());

    }
}