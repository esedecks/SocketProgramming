import java.net.*;
import java.io.*;
public class Servidor_ftp {

	public static void main(String[] args) throws Exception{
	ServerSocket ss = new ServerSocket(4000);
	System.out.println("Servicio iniciado, esperando por cliente...");
	
	for(;;){
		System.out.println("Socket esperando cliente\r"); 
		Socket cl = ss.accept();
		System.out.println("Cliente conectado desde:"+cl.getInetAddress()+":"+cl.getPort());


		BufferedOutputStream bos = null; // new BufferedOutputStream(new FileOutputStream("duke1.png"));
		ObjectInputStream ois = new ObjectInputStream(cl.getInputStream()); 
		BufferedInputStream bis = new BufferedInputStream(cl.getInputStream()); 
		int nArchivos =(int) ois.readInt(); 
		System.out.println("nArchivos = "+nArchivos); 
		int contador = 0; 
		while(contador < nArchivos){
			String info =(String) ois.readObject();
			System.out.println("Info = "+info);
			String[] infoTemp = info.split(","); 
			infoTemp[1] = infoTemp[1].replaceAll("(\\r|\\n)", "");
			bos = new BufferedOutputStream(new FileOutputStream(infoTemp[0].toString()));
			//int fin;
			//int tam_bloque=(bis.available()>=1024)? 1024 :bis.available();
			
			System.out.println("tamaño archivo:"+bis.available()+ "bytes..");
			int b_leidos;
			long leidos=0;
		    long completados=0;
			byte[] buf = new byte[1024];
			
            long tam_arch =Long.parseLong(infoTemp[1]);// bis.available();
			while((b_leidos=bis.read(buf,0,buf.length))!= -1){
			    leidos += b_leidos;
				completados = (leidos * 100) / tam_arch;
				System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
				System.out.print("Descargando:"+completados+" %");
				bos.write(buf,0,b_leidos);
				bos.flush();
				
				//tam_bloque=(bis.available()>=1024)? 1024 :bis.available();
			}//while
			completados = leidos = b_leidos = 0 ; 
			contador++; 
			ois.close();
			bos.close();
			bis.close(); 
			cl = ss.accept();//Se debe crear un nuevo socket, por que el que fue cerrado queda inutilizado, 
			                //Debido a que el servidor es quien termina primero de enviar los datos, se quedará bloqueado, 
			                //esperando una nueva conexión
			bos = null;
			ois = new ObjectInputStream(cl.getInputStream());
			bis = new BufferedInputStream(cl.getInputStream()); 
		}//while para controlar numero de archivos		
		System.out.println(); 
	}//for
	
		
	}//main
}
