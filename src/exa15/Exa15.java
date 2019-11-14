
package exa15;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
public class Exa15 {
public static Connection conexion=null;

    public static Connection getConexion() throws SQLException  {
        //SE realiza la conexion en esta parte del codigo
        String usuario = "hr";
        String password = "hr";
        String host = "localhost"; 
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;
        
           
            conexion = DriverManager.getConnection(ulrjdbc);
            return conexion;
        }

     
     public static void closeConexion() throws SQLException {
      conexion.close();
      }
     
     public static void crearXML() throws IOException, XMLStreamException, ClassNotFoundException, SQLException{
         
         //Fichero XML en blanco
         File fichero = new File("/home/oracle/Desktop/Tablas Exame");
         FileWriter ficheroW = new FileWriter(fichero);
         
         //FileOutputStream: permite la lectura de archivos XML
         XMLOutputFactory fichOut = XMLOutputFactory.newInstance();
         
         //permite la escritura en archivos XML
         XMLStreamWriter fichStream = fichOut.createXMLStreamWriter (ficheroW);
         
         
         //Primeras escrituras en nuestro archivo
         fichStream.writeStartDocument("1.0");
         fichStream.writeStartElement("Platos");
         
         //Lectura del fichero serializado
         FileInputStream fichLeer = new FileInputStream ("/home/oracle/Desktop/TABLAS EXAME/platoss");
         ObjectInputStream fichOIS = new ObjectInputStream(fichLeer);
         
         Platos obj = new Platos();
         
         //Bucle while para que siga leyendo el archivo platoss
         //Hasta que dea nulo
         while ((obj = (Platos) fichOIS.readObject()) != null) {
            int peso = 0;
            int grasa = 0;
            String codigo = "";
            int grasaTotal = 0;
            
            PreparedStatement pst1 = conexion.prepareStatement("select PESO,CODC from composicion where CODP = ?");

            pst1.setString(1, obj.getCodigop());

            ResultSet rs1 = pst1.executeQuery();

            while (rs1.next()) {

                peso = rs1.getInt(1);
                codigo = rs1.getString(2);

                
                PreparedStatement pst2 = conexion.prepareStatement("select GRAXA from componentes where CODC = ?");

                pst2.setString(1, codigo);

                ResultSet rs2 = pst2.executeQuery();

                //cada codc SOLO tiene una grasa
                rs2.next();
                grasa = rs2.getInt(1);
                //calculo de la garasa del plato
                grasaTotal += (peso * grasa) / 100;
            }
                
                fichStream.writeStartElement("Plato:");
                fichStream.writeAttribute("codigo", obj.getCodigop());
                fichStream.writeStartElement("Nombre");
                fichStream.writeCharacters(obj.getNomep());
                fichStream.writeEndElement();
                fichStream.writeStartElement("Grasa");
                fichStream.writeCharacters(Integer.toString(grasaTotal));
                fichStream.writeEndElement();
                fichStream.writeEndElement();
                
         }
         fichStream.writeEndElement();
         fichStream.writeEndDocument();
         fichOIS.close();
         fichLeer.close();
         
         
         fichStream.close();
         ficheroW.close();
                


       
            
     }
    public static void main(String[] args) throws FileNotFoundException, IOException, SQLException, ClassNotFoundException, XMLStreamException{
        Exa15.getConexion();
        Exa15.crearXML();
        Exa15.closeConexion();
    }
    
}
