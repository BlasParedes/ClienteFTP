/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PruebaFTP;

/**
 *
 * @author jose.paredes
 */

import java.io.*;    
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.*;
/**
 * Clase que gestiona la conexión a un servidor y la de sus ficheros.
 * @author Fernando Pacheco Sánchez.
 *
 */
public class ClienteFTP {
    // Variables de clase.
    private String dirActual;
    private FTPClient cliente = new FTPClient();

    // Métodos: 
    /**
     * Nos conecta a un servidor mediante usuario y contraseña.
     * @param server Servidor al que nos queremos conectar.
     * @param user Usuario para poder acceder.
     * @param pwd Contraseña para poder acceder.
     * @return True, si la conexión se estableció.<br>
     * False, es caso contrario.
     */
    public boolean conectar(String server, String user, String pwd){     
        try {
            // Conectarse e identificarse.
            cliente.connect(server);
            if(cliente.login(user, pwd)){
                // Entrando a modo pasivo
                cliente.enterLocalPassiveMode();
                // Activar recibir/enviar cualquier tipo de archivo
                cliente.setFileType(FTP.BINARY_FILE_TYPE);

                // Obtener respuesta del servidor y acceder.
                int respuesta = cliente.getReplyCode();
                if (FTPReply.isPositiveCompletion(respuesta) == true) {
                    return true;
                }else{
                    return false;
                }
            }else{
                System.err.println("Usuario o contraseña incorrectos.");                
                return false;
            }


        } catch (IOException e) {
            System.err.println("Host del servidor incorrecto: "+server);
            e.printStackTrace();
            return false;
        }           
    }

    /**
     * Cierra sesión del usuario y se desconecta del servidor.
     * @return True, si se ha desconectado correctamente.<br>
     * False, en caso contrario.
     */
    public boolean desconectar() {
        try {
            // Cerrar sesion y desconectar.
            cliente.logout();
            cliente.disconnect();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cambia de directorio dentro de un servidor.
     * @param dirCarpeta Dirección completa de la carpeta a la cual queremos acceder
     * dentro del servidor.
     * @return True, si ha cambiado de directorio.<br>
     * False, en caso contrario.
     */ 
    public boolean cambiarDirectorio(String dirCarpeta) {        
        try {
            // Ubicarse en directorio: ftp          
            cliente.changeWorkingDirectory(dirCarpeta);
            dirActual =  cliente.printWorkingDirectory();

            if(dirCarpeta.compareTo(dirActual) == 0){
                // Ha cambiado de forma satisfactoria.
                return true;
            }else{
                return false;
            }

        } catch (IOException e) {
            System.err.println("Error al cambiar de directorio.");
            e.printStackTrace();
            return false;
        }       
    }

    /**
     * Descarga un fichero zip del servidor y lo almacena en una ruta específica.
     * @param nomFich Nombre del fichero zip a descargar.
     * @param destino Dirección donde se quiere almacenar el zip.
     * @return True, si la descarga y almacenamiento ha sido correcto.<br>
     * False, en caso contrario.
     */
    public boolean descargaFich(String nomFich, String destino){
        String extZIP = ".ZIP";// El fichero de extensión que tu desees.
        OutputStream os;
        boolean fichDescargado = false;

        // Asegurarse que es ".ZIP".
        if(nomFich.length() < 4){
            return false;
        }  
        try {
            // Descargar el ZIP. 
            os = new BufferedOutputStream(new FileOutputStream(destino +"/"+ nomFich));
            fichDescargado = cliente.retrieveFile(nomFich, os);
            os.close();

        } catch (IOException e) {
            System.err.println("No ha sido posible encontrar la carpeta: "+destino);
            e.printStackTrace();
        }

        return fichDescargado;      
    }

    /**
     * Borra un fichero ZIP de la ubicación actual del servidor.
     * @param nomFich Nombre del fichero ZIP a borrar.
     * @return True, si se ha borrado del servidor.<br>
     * False, en caso contrario.
     */
    public boolean borrarFichero(String nomFich){
        boolean borrado = false;  
        try {
            borrado = cliente.deleteFile(nomFich);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return borrado;
    }

    /**
     * Sube un fichero al servidor en la ubicación actual.
     * @param pathFich Dirección del fichero, incluido el fichero. Ejm: /hola/queHay/pepe.txt
     * @param fich Nombre del fichero y su extensión. Ejm: pepe.txt
     * @return True, si ha subido de forma satisfactoria el fichero.<br>
     * False, en caso contrario.
     */
    public boolean subirFichero(String pathFich, String fich){
        InputStream is;
        boolean fichSubido = false;

        try {           
            // Capturar el fichero de su ruta.
            is = new BufferedInputStream(new FileInputStream(pathFich));

            // Subir el fichero en sí.
            fichSubido = cliente.storeFile(fich, is);           
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return fichSubido;
    }


    // Getters y setters:
    /**
     * Nos dice en que ubicación del servidor estamos actualmente.
     * @return Un String que contiene el path de ubicación actual.
     */
    public String dameDirActual() {
        return dirActual;
    }
    
    public String[] listNames() throws IOException{
        return cliente.listNames();
    }
}
