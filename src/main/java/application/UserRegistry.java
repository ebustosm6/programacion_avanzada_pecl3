package application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import server.ui.UserControlJFrame;
import application.user.User;
import application.user.YoungUser;
import application.user.ChildUser;

public class UserRegistry implements Serializable{

    private Map<String, User> visitantes = new HashMap<>();
    private Map<String, String> monitoresEnZonaActual = new ConcurrentHashMap<>();
    private Map<String, Integer> usuariosEnZonaActual = new ConcurrentHashMap<>();
    private Map<String, Integer> usuariosEnZonaAcumulado = new ConcurrentHashMap<>();
    private Map<String, ArrayList<String>> usuariosEnZonaActualIds = new ConcurrentHashMap<>();
    private int numeroAdultos = 0;
    private int numeroNinios = 0;
    private UserControlJFrame userControl;


    public UserRegistry(UserControlJFrame userControl) {
        this.userControl = userControl;
    }
    
    public void comprobarDetenerReanudar(){
        try {
            userControl.getL().lock();
            while (userControl.getDetenerReanudar()) {
                try {
                    userControl.getC().await(); // espera a que le manden una señal
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } finally {
            userControl.getL().unlock();
        } 
    }
    
    public Map<String, Integer> getUsuariosEnZonaActual() {
        return usuariosEnZonaActual;
    }

    public void setUsuariosEnZonaActual(Map<String, Integer> usuariosEnZonaActual) {
        this.usuariosEnZonaActual = usuariosEnZonaActual;
    }

    public Map<String, Integer> getUsuariosEnZonaAcumulado() {
        return usuariosEnZonaAcumulado;
    }

    public void setUsuariosEnZonaAcumulado(Map<String, Integer> usuariosEnZonaAcumulado) {
        this.usuariosEnZonaAcumulado = usuariosEnZonaAcumulado;
    }

    public ArrayList<String> getColaParque() {
        return colaParque;
    }

    public void setColaParque(ArrayList<String> colaParque) {
        this.colaParque = colaParque;
    }
    private ArrayList<String> colaParque = new ArrayList<String>();

    public void registrarZonaActividad(String identificatorActividad) {
        this.usuariosEnZonaActual.put(identificatorActividad, 0);
        this.usuariosEnZonaAcumulado.put(identificatorActividad, 0);

    }

    public void registrarZonasActividad(String identificatorActividad, List<String> identificatoresAreas) {
        for (String identificatorArea : identificatoresAreas) {
            this.usuariosEnZonaActualIds.put(identificatorActividad + identificatorArea, new ArrayList<String>());
        }

    }

    public List<String> getIdentificadoresUsuariosEnActividad(String identificatorActividad, String identificatorArea) {
        return this.usuariosEnZonaActualIds.get(identificatorActividad + identificatorArea);
    }

    public void aniadirMonitorEnZona(String identificatorActividad, String identificatorAreaMonitor, String identificatorMonitor) {
        monitoresEnZonaActual.put(identificatorActividad, identificatorMonitor);
        userControl.setDatos(identificatorActividad+identificatorAreaMonitor, identificatorMonitor);
    }

    public void eliminarMonitorDeZona(String identificatorActividad) {
        monitoresEnZonaActual.put(identificatorActividad, "");
    }

    public synchronized void aniadirVisitanteZonaActividad(String identificatorActividad, String identificatorArea, String identificatorUsuario) {
        int cantidadActual = this.usuariosEnZonaActual.get(identificatorActividad) + 1;
        this.usuariosEnZonaActual.put(identificatorActividad, cantidadActual);
        int cantidadAcumulado = this.usuariosEnZonaAcumulado.get(identificatorActividad) + 1;
        this.usuariosEnZonaAcumulado.put(identificatorActividad, cantidadAcumulado);
        ArrayList<String> usuariosEnZonaActualIdsArray = this.usuariosEnZonaActualIds.get(identificatorActividad + identificatorArea);
        if (identificatorActividad.equals("ActividadTobogan")) {
        	// fantasia
        	System.out.println("asdasd");
        }
        usuariosEnZonaActualIdsArray.add(identificatorUsuario);
        usuariosEnZonaActualIdsArray.remove(null);
        this.usuariosEnZonaActualIds.put(identificatorActividad + identificatorArea,usuariosEnZonaActualIdsArray);
        userControl.setDatos(identificatorActividad+identificatorArea,usuariosEnZonaActualIdsArray.toString());
    }

    public synchronized void eliminarVisitanteZonaActividad(String identificatorActividad, String identificatorArea, String identificatorUsuario) {
        int cantidadActual = this.usuariosEnZonaActual.get(identificatorActividad) - 1;
        this.usuariosEnZonaActual.put(identificatorActividad, cantidadActual);
        ArrayList<String> usuariosEnZonaActualIdsArray = this.usuariosEnZonaActualIds.get(identificatorActividad + identificatorArea);
        usuariosEnZonaActualIdsArray.remove(identificatorUsuario);
        this.usuariosEnZonaActualIds.put(identificatorActividad + identificatorArea,usuariosEnZonaActualIdsArray);
        userControl.setDatos(identificatorActividad+identificatorArea,usuariosEnZonaActualIdsArray.toString());
        
    
    
    }

    public User buscarVisitante(String identificator) {
        User visitanteEncontrado = null;
        if (this.visitantes.containsKey(identificator)) {
            visitanteEncontrado = this.visitantes.get(identificator);
        }
        return visitanteEncontrado;
    }
    
    public List<String> buscarVisitanteLista(String identificator) {
        ArrayList<String> resultado = new ArrayList<>();
        User visitante = buscarVisitante(identificator);
        if (visitante != null) {
            resultado.add(visitante.getIdentificator());
        resultado.add(visitante.getCurrentActivity());
        resultado.add(String.valueOf(visitante.getTotalActivitiesDone()));
        }
        return resultado;
    }

    public Map<String, User> getVisitantes() {
        return visitantes;
    }

    public void aniadirVisitante(User visitante) {
        this.visitantes.put(visitante.getIdentificator(), visitante);
        if (visitante instanceof ChildUser|| visitante instanceof YoungUser) {
            numeroNinios++;
        } else {
            numeroAdultos++;
        }
    }
    
    public void eliminarVisitante(User visitante) {
        this.visitantes.put(visitante.getIdentificator(), visitante);
        if (visitante instanceof ChildUser || visitante instanceof YoungUser) {
            numeroNinios--;
        } else {
            numeroAdultos--;
        }
    }

    public int getNumeroAdultos() {
        return numeroAdultos;
    }

    public int getNumeroNinios() {
        return numeroNinios;
    }

    public int getVisitantesActualesEnZona(String identificator) {
        return this.usuariosEnZonaActual.get(identificator);
    }

    public int getVisitantesAcumuladoEnZona(String identificator) {
        return this.usuariosEnZonaAcumulado.get(identificator);
    }

}
