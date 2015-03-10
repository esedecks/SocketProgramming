import java.net.*;
import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.*; 
import java.awt.HeadlessException;
public class Cliente_ftp{
    /*Variables de instacia*/
    Socket cl ; 
    JFileChooser fileChooser; 
    BufferedOutputStream bos;
	ObjectOutputStream oos; 
	BufferedInputStream bis;
	File[] files ; 
	
    public Cliente_ftp(String ipRemote, int port) throws IOException{
        cl = new Socket(InetAddress.getByName(ipRemote),port);
        bos = null; 
        oos = null; 
        bis = null; 
        files = null; 
    }
    
    public File[] getFiles() throws HeadlessException{
        fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true); 
         int resultado = fileChooser.showOpenDialog(null);
         if(resultado==JFileChooser.APPROVE_OPTION){
            files = fileChooser.getSelectedFiles(); 
            return  files; 
        }         
        return null;
    }   
    public Socket getSocket(){
        return cl;
    }
    
    //función para errores
    public static void error(String msgErr){
        System.out.println(msgErr);
        System.exit(1);
    }
    //Abrir y cerrar flujos
    public void openStreams() throws IOException{
        bos = new BufferedOutputStream(cl.getOutputStream());
	    oos = new ObjectOutputStream(cl.getOutputStream());            
    }
    public void closeStreams() throws IOException{
        bos.close(); 
		oos.close(); 
    }
    public void setTimeOut(int time) throws SocketException{
        cl.setSoTimeout(3000);    
    }
    //Sobrecarga de métodos
    public void say_something(Object obj)throws IOException{
        oos.writeObject(obj	);
		oos.flush();       
    }
    public void say_something(int num) throws IOException{
        oos.writeInt(num);
	    oos.flush(); 
    
    }
    public void say_something(byte[] buf, int begin, int bytesTowrite) throws IOException{
        bos.write(buf,begin,bytesTowrite);
        bos.flush();        
    }
    
	public static void main(String[] args) {
	int port =0, timeOut=0; 
	String hostRemote=""; 
	try{
	    if(args.length != 3)
	    error("Usar : java nombrePrograma  hostRemoto port timeOut"); 
	    hostRemote = args[0];
	    port = Integer.parseInt(args[1]); 
	    timeOut = Integer.parseInt(args[2]);//3000; 
	    if(port <= 1024)
	        error("Puerto : "+port+" invalido"); 
	    if(timeOut <0)
	        error("TimeOut invalido"); 
    }catch(NumberFormatException nfe){
        nfe.printStackTrace(); 
        error("No es valido valor dado desde consola"); 
    }catch(Exception e) {
        e.printStackTrace(); 
        error("Error inesperado");
    }
    
    File[] f = null; 
    Cliente_ftp client = null; 
    
    
    try{
     client = new Cliente_ftp(hostRemote, port);
     f= client.getFiles(); 
     if (f==null){
        error(" Seleccione uno o más archivos "); 
     }
	}catch(IOException ioe){
	    error("No es posible conectarse a la dirección remota "+hostRemote+ " en puerto :"+port+" Servidor desconectado");
	    ioe.printStackTrace(); 
	}catch(HeadlessException he){
	    error("No es posible abrir archivos");
	    he.printStackTrace(); 
	}
	catch(Exception e){
	    error("Ha sucedido algo inesperado");
	    e.printStackTrace(); 
	}
	int nArchivos = f.length;
	System.out.println("Numero de archivos a enviar: " + nArchivos);
	BufferedInputStream bis = null; 
	try{
	    client.openStreams();
	    client.setTimeOut(timeOut);
	    client.say_something(nArchivos); 
	    int contador = 0 ; 
	    while(contador < nArchivos){
	        String nameFile = f[contador].getName().replace(",", "");//eliminamos comas
	        String lenFile = new String(f[contador].length()+"");//Obtenemos tamaño de archivo
	        client.say_something((Object)nameFile+","+lenFile+"\n\r"); 
		    byte[] buf = new byte[1024];
		    bis = new BufferedInputStream(new FileInputStream(f[contador].getPath()));
		    int fin;
		    long tam_arch =(long)f[contador].length();// bis.available();	
		    int tam_bloque=(bis.available()>=1024)? 1024 :bis.available();
		    long completados=0,leidos=0;
		    int b_leidos=0;
		
		while((b_leidos=bis.read(buf,0,buf.length))!=-1){
		    leidos += b_leidos;//tam_bloque;
			completados = (leidos * 100) / tam_arch;
			System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
			System.out.print("Subiendo archivo: "+completados+" %");
			tam_bloque=(bis.available()>=1024)? 1024 :bis.available();
			client.say_something(buf,0,b_leidos);
			
		}//while
		completados = leidos = b_leidos = 0 ; 
		client.closeStreams();
		bis.close();
		client =  new Cliente_ftp(hostRemote, port);
		client.setTimeOut(timeOut); 
		client.openStreams(); 
		bis = null; 
		contador++; 
	}
	client.closeStreams(); 
	}catch(IOException ioe){
	    ioe.printStackTrace(); 
	    error("No es posible comunicarse con el servidor, intentelo más tarde");
	}catch(Exception e){
	    e.printStackTrace();
	    error("Algo ha ido mal ...");
	}
	}//main
	
}//class
