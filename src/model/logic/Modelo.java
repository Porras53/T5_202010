package model.logic;

import java.awt.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

import model.data_structures.*;

/**
 * Definicion del modelo del mundo
 *
 */
public class Modelo {
	/**
	 * Atributos del modelo del mundo
	 */
	
	/**
	 * Cola de lista encadenada.
	 */
	
	private HashLinearProbing datosCola2;
	
	private HashSeparateChaining datosCola3;

	private static Comparable[] aux;

	/**
	 * Constructor del modelo del mundo con capacidad predefinida
	 */
	public Modelo()
	{
		
		datosCola2 = new HashLinearProbing();
		datosCola3=new HashSeparateChaining();
	}

	/**
	 * Carga el archivo .JSON en una lista enlazada.
	 * @throws FileNotFoundException. Si no encuentra el archivo.
	 */
	
	public void cargarCola() throws FileNotFoundException
	{
		//Definir mejor la entrada para el lector de json
		
		long inicio = System.currentTimeMillis();
		long inicio2 = System.nanoTime();
		String dir= "./data/Comparendos_DEI_2018_Bogot�_D.C.geojson";
		File archivo= new File(dir);
		JsonReader reader= new JsonReader( new InputStreamReader(new FileInputStream(archivo)));
		JsonObject gsonObj0= JsonParser.parseReader(reader).getAsJsonObject();

		JsonArray comparendos=gsonObj0.get("features").getAsJsonArray();
		int i=0;
		

		
		System.out.println("El tama�o inicial del linear hash : "+datosCola2.getNodos().length);
		System.out.println("El tama�o inicial del separate hash : "+datosCola3.getNodosSet().length);
		while(i<comparendos.size())
		{
			JsonElement obj= comparendos.get(i);
			JsonObject gsonObj= obj.getAsJsonObject();

			JsonObject gsonObjpropiedades=gsonObj.get("properties").getAsJsonObject();
			int objid= gsonObjpropiedades.get("OBJECTID").getAsInt();
			String fecha= gsonObjpropiedades.get("FECHA_HORA").getAsString();
			String mediodeteccion = "";
			String clasevehiculo=gsonObjpropiedades.get("CLASE_VEHICULO").getAsString();
			String tiposervi=gsonObjpropiedades.get("TIPO_SERVICIO").getAsString();
			String infraccion=gsonObjpropiedades.get("INFRACCION").getAsString();
			String desinfraccion=gsonObjpropiedades.get("DES_INFRACCION").getAsString();
			String localidad=gsonObjpropiedades.get("LOCALIDAD").getAsString();
			String municipio = "";

			JsonObject gsonObjgeometria=gsonObj.get("geometry").getAsJsonObject();

			JsonArray gsonArrcoordenadas= gsonObjgeometria.get("coordinates").getAsJsonArray();
			double longitud= gsonArrcoordenadas.get(0).getAsDouble();
			double latitud= gsonArrcoordenadas.get(1).getAsDouble();

			Comparendo agregar=new Comparendo(objid, fecha,mediodeteccion,clasevehiculo, tiposervi, infraccion, desinfraccion, localidad, municipio ,longitud,latitud);
			datosCola2.put(agregar.getLlave(), agregar);
			datosCola3.putInSet(agregar.getLlave(), agregar);
			i++;
		}
		long fin2 = System.nanoTime();
		long fin = System.currentTimeMillis();
		
		System.out.println("El tama�o final del linear hash : "+datosCola2.getNodos().length);
		System.out.println("El tama�o final del separate hash : "+datosCola3.getNodosSet().length);
		System.out.println((fin2-inicio2)/1.0e9 +" segundos, de la carga de datos normal.");
		
		
	}
	
	public ListaDoblementeEncadenada requerimiento1Linear(Date fe, String clasevehicu,String infra) 
	{
		ListaDoblementeEncadenada retorno=new ListaDoblementeEncadenada();
		KeyComparendo nuevo= new KeyComparendo(fe,clasevehicu,infra);
		NodoHash[] nodos=datosCola2.getNodos();
		for(int i=0;i<nodos.length;i++) 
		{
			if(nodos[i]!=null) 
			{
				if((nodos[i].darE()).equals(nuevo)) 
				{
					retorno.insertarFinal(((Comparendo)nodos[i].darv()));
				}
			}
		}
		
		Comparable[] copia= copiar(retorno);
		retorno= new ListaDoblementeEncadenada();
		shellSortMenoraMayor(copia);
		pegar(copia, retorno);
		
		return retorno;
	}
	
	public ListaDoblementeEncadenada requerimiento1Separate(Date fe, String clasevehicu,String infra) 
	{
		ListaDoblementeEncadenada retorno=new ListaDoblementeEncadenada();
		KeyComparendo nuevo= new KeyComparendo(fe,clasevehicu,infra);
		NodoHash[] nodos=datosCola3.getNodosSet();
		for(int i=0;i<nodos.length;i++) 
		{
			if(nodos[i]!=null) 
			{
				if((nodos[i].darE()).equals(nuevo)) 
				{
					retorno=(ListaDoblementeEncadenada)nodos[i].darv();
					
				}
			}
		}
		Comparable[] copia= copiar(retorno);
		retorno= new ListaDoblementeEncadenada();
		shellSortMenoraMayor(copia);
		pegar(copia, retorno);
		
		return retorno;
	}
	
	
	public void requerimiento3() 
	{
		ListaDoblementeEncadenada nuevo= new ListaDoblementeEncadenada();
		ListaDoblementeEncadenada nuevo1= new ListaDoblementeEncadenada();
		NodoHash[] nodos=datosCola2.getNodos();
		NodoHash[] nodos1=datosCola3.getNodosSet();
		for(int i=0; i<nodos.length;i++) 
		{
			if(nodos[i]!=null) 
			{
				nuevo.insertarFinal(nodos[i].darE());
			}
			
		}
		
		for(int i=0; i<nodos1.length;i++) 
		{
			if(nodos1[i]!=null) 
			{
				nuevo1.insertarFinal(nodos1[i].darE());
			}
		}
		
		Comparable[] copia= copiar(nuevo);
		Comparable[] copia1= copiar(nuevo);
		Comparable[] keyexistentes= new Comparable[8000];
		Comparable[] keyexistentes1= new Comparable[8000];
		
		for(int i=0;i<8000;i++) 
		{
			int valorEntero = (int) Math.floor(Math.random()*(copia.length));
			keyexistentes[i]=copia[valorEntero];
		}
		
		for(int i=0;i<8000;i++) 
		{
			int valorEntero = (int) Math.floor(Math.random()*(copia1.length));
			keyexistentes1[i]=copia1[valorEntero];
		}
		
		double sumadetodo=0.0;
		double mayor=0.0;
		double menor=100000.0;
		
		
		
		
		for(int i=0;i<keyexistentes.length;i++) 
		{
			long inicio2 = System.nanoTime();
			datosCola2.get(keyexistentes[i]);
			long fin2 = System.nanoTime();
			double tiempo=(fin2-inicio2)/1.0e9;
			sumadetodo+=tiempo;
			if(tiempo>mayor) 
			{
				mayor=tiempo;
			}
			if(tiempo<menor) 
			{
				menor= tiempo;
			}
			
			
		}
		
		double sumadetodo1=0.0;
		double mayor1=0.0;
		double menor1=100000.0;
		
		for(int i=0;i<keyexistentes1.length;i++) 
		{
			
			long inicio3 = System.nanoTime();
			datosCola3.getSet(keyexistentes1[i]);
			long fin3 = System.nanoTime();
			double tiempo2=(fin3-inicio3)/1.0e9;
			sumadetodo1+=tiempo2;
			if(tiempo2<menor1) 
			{
				menor1=tiempo2;
				System.out.println(menor1);
			}
			
			if(tiempo2>mayor1) 
			{
				mayor1=tiempo2;
			}
			
		}
		
		System.out.println("El tiempo mayor del get con llaves existentes en linear hash fue de: "+mayor+" segundos.");
		System.out.println("El tiempo menor del get con llaves existentes en linear hash fue de: "+menor+" segundos.");
		
		System.out.println("El tiempo mayor del get con llaves existentes en separate hash fue de: "+mayor1+" segundos.");
		System.out.println("El tiempo menor del get con llaves existentes en separate hash fue de: "+menor1+" segundos.");
		
		
		SimpleDateFormat objSDF= new SimpleDateFormat("yyyy/MM/dd");
		Date nuevafecha=null;
		try {
			
			nuevafecha=objSDF.parse("2019/02/15");
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		KeyComparendo noexiste= new KeyComparendo(nuevafecha,"Motocicleta","C02");
		
 		for(int i=0;i<2000;i++) 
 		{
 			long inicio2 = System.nanoTime();
			datosCola2.get(noexiste);
			long fin2 = System.nanoTime();
			double tiempo=(fin2-inicio2)/1.0e9;
			sumadetodo+=tiempo;
			if(tiempo>mayor) 
			{
				mayor=tiempo;
			}
			if(tiempo<menor) 
			{
				menor= tiempo;
			}
 		}
 		
 		for(int i=0;i<2000;i++)
 		{
 			long inicio3 = System.nanoTime();
			datosCola3.getSet(noexiste);
			long fin3 = System.nanoTime();
			double tiempo2=(fin3-inicio3)/1.0e9;
			sumadetodo1+=tiempo2;
			
			
			if(tiempo2>mayor1) 
			{
				mayor1=tiempo2;
			}
			
 		}
 		
 		
 		double promedioget=sumadetodo/10000.0;
 		double promedioget1=sumadetodo1/10000.0; 
 		
 		System.out.println("El tiempo promedio de los 10000 get aleatorios en linear hash fue de: "+promedioget+" segundos.");
 		System.out.println("El tiempo mayor del get en linear hash fue de: "+mayor+" segundos.");
		System.out.println("El tiempo menor del get en linear hash fue de: "+menor+" segundos.");
		
		System.out.println("El tiempo promedio de los 10000 get aleatorios  en separate hash fue de: "+promedioget1+" segundos.");
 		System.out.println("El tiempo mayor del get en separate hash fue de: "+mayor1+" segundos.");
		System.out.println("El tiempo menor del get en separate hash fue de: "+menor1+" segundos.");
		
	}
	
	
	
	private static void shuffle(Comparable[] a)
	{
		Random r= new Random();
		for(int i= a.length-1;i>0;i--)
		{
			int index= r.nextInt(i+1);
			Comparable a2= a[index];
			a[index]=a[i];
			a[i]=a2;
		}
	}

	public static Comparable[] getAux() {
		return aux;
	}

	

	public HashLinearProbing getDatosCola2() {
		return datosCola2;
	}

	public HashSeparateChaining getDatosCola3() {
		return datosCola3;
	}
	
	public Comparable[] copiar(ListaDoblementeEncadenada datos)
	{
		int i=0;
		Node puntero=datos.darCabeza2();
		Comparable[] arreglo= new Comparable[datos.darLongitud()];
		while(i<datos.darLongitud())
		{
			arreglo[i]= puntero.darE();
			puntero=puntero.darSiguiente();
			i++;
		}
		return arreglo;
		
	}
	
	public void pegar(Comparable[] copia, ListaDoblementeEncadenada nuevo)
	{
		int i=0;
		while(i<copia.length)
		{
			nuevo.insertarFinal(copia[i]);
			i++;
		}
	}
	
	public void shellSortMenoraMayor(Comparable datos[])
	{
		
		int N=datos.length;
		int h=1;
		while(h<N/3)
			h=3*h+1;
		while(h>=1){
			for(int i=h;i<N;i++)
			{
					for(int j=i;j>=h && less(datos[j], datos[j-h]);j-=h)
					{
						exch(datos,j,j-h);
					}
			}
			h=h/3;
		}
		
	}
	
	private boolean less(Comparable v,Comparable w)
	{
		return v.compareTo(w) < 0;
	}
	
	private void exch(Comparable[] datos,int i, int j)
	{
		Comparable t=datos[i];
		datos[i]=datos[j];
		datos[j]=t;
	}








}

