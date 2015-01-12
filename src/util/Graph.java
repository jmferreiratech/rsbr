package util;

import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Graph {
	boolean debug = true;
	static private String[] ports = { "N", "S", "E", "W" };
	ArrayList<Vertice> vertices;
	ArrayList<Aresta> arestas;
	int dimX;
	int dimY;

	public Graph() {
		vertices = new ArrayList<>();
		arestas = new ArrayList<>();
	}

	public Graph(File topology) {
		System.out.println("from file: "+topology.getName());
		vertices = new ArrayList<>();
		arestas = new ArrayList<>();

		try {
			Scanner sc = new Scanner(new FileReader(topology));

			String[] lines = null, columns = null;
			if (sc.hasNextLine())
				lines = sc.nextLine().split("; ");
			if (sc.hasNextLine())
				columns = sc.nextLine().split("; ");

			dimX = lines[0].split(" ").length + 1;
			dimY = lines.length;

			for (int i = 0; i < dimX; i++) {
				for (int j = 0; j < dimY; j++) {
					String vertice = i + "." + j;
					this.addVertice(vertice);

				}
			}

			for (int i = 0; i < lines.length; i++) {
				String[] line = lines[i].split(" ");
				for (int j = 0; j < line.length; j++) {
					if (line[j].charAt(0) == '0') // there is a link
					{
						Vertice starting = this.getVertice(j + "."
								+ (columns.length - i));
						Vertice ending = this.getVertice((j + 1) + "."
								+ (columns.length - i));
						this.addAresta(starting, ending, ports[2]);
						this.addAresta(ending, starting, ports[3]);
					}
				}
			}

			for (int i = 0; i < columns.length; i++) {
				String[] column = columns[i].split(" ");
				for (int j = 0; j < column.length; j++) {
					if (column[j].charAt(0) == '0') // there is a link
					{
						Vertice starting = this.getVertice(j + "."
								+ (columns.length - i));
						Vertice ending = this.getVertice(j + "."
								+ (columns.length - 1 - i));
						this.addAresta(starting, ending, ports[1]);
						this.addAresta(ending, starting, ports[0]);
					}
				}
			}

			sc.close();

		} catch (Exception ex) {
			Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public Graph(int dX,int dY, double perc)
	{
		vertices = new ArrayList<>();
		arestas = new ArrayList<>();
		
		dimX=dX;
		dimY=dY;
		int nArests = (dimX-1)*dimY + dimX*(dimY-1);
		int nFalts = (int)Math.ceil((double)nArests*perc);
		System.out.println("#Arestas: "+nArests);
		System.out.println("#Faults: "+nFalts);
			
		//Adiciona Vertices
		for(int y=0; y<dimY; y++)
			for(int x=0; x<dimX; x++)
				addVertice(x+"."+y);
		
		//Adiciona Arestas
		for(int y=0; y<dimY; y++)
			for(int x=0; x<dimX; x++)
			{
				if(contem(x+"."+(y+1)))
					addAresta(getVertice(x+"."+y), getVertice(x+"."+(y+1)), ports[0]);
				if(contem(x+"."+(y-1)))
					addAresta(getVertice(x+"."+y), getVertice(x+"."+(y-1)), ports[1]);
				if(contem((x+1)+"."+y)) 
					addAresta(getVertice(x+"."+y), getVertice((x+1)+"."+y), ports[2]);	
				if(contem((x-1)+"."+y)) 
					addAresta(getVertice(x+"."+y), getVertice((x-1)+"."+y), ports[3]);	
			}				
		
		//Adiciona Falhas e checa isolamento
		for(int i=0;i<nFalts;i++)
		{
			while(true)
			{
				int idx = (int)(Math.random()*(arestas.size()));
				Aresta toRemove = arestas.get(idx);
				if (debug) System.out.println("Removing: "+toRemove.getOrigem().getNome()+"->"+toRemove.getDestino().getNome());
				removeAresta(toRemove);
				
				if(haveIsolatedCores())
					AddAresta(toRemove);
				else break;
			}
		}
	}
	
	//Checha se existe cores isolados
	public boolean haveIsolatedCores()
	{
		ArrayList<Vertice> alc = new ArrayList<Vertice>();
		//Escolha do 0.0 para ser o core inicial. Garantido a existencia em todas as topologias
		getVertice("0.0").checkIsolation(alc);
		
		//Se lista de alcan�aveis for igual ao total de cores n�o existe isolamento
		if(!(alc.size()==vertices.size())) return true;
		
    	return false;
	}
	
	private void removeAresta(Aresta toRemove)
	{
		//Aresta indo = arestas.get(indx);
		Aresta vindo = toRemove.getDestino().getAresta(toRemove.getOrigem());
		
		toRemove.getOrigem().adj.remove(toRemove);
		vindo.getOrigem().adj.remove(vindo);
		arestas.remove(toRemove); arestas.remove(vindo);
	}
	
	private void AddAresta(Aresta toAdd)
	{
		Aresta vindo = toAdd.getDestino().getAresta(toAdd.getOrigem());
		
		toAdd.getOrigem().adj.add(toAdd);
		vindo.getDestino().adj.add(vindo);
		arestas.add(toAdd); arestas.add(vindo);
	}

	public void setGraph() {
		// Dijkstra
		for (Vertice v : this.vertices) {
			v.setPai(null);
			v.setVisitado(false);
			v.setDistancia(Integer.MAX_VALUE);
		}
	}

	public boolean contem(String vertice) {

		for (int i = 0; i < vertices.size(); i++) {

			if (vertice.equals(vertices.get(i).getNome())) {
				return true;
			}
		}
		return false;
	}

	public ArrayList<Vertice> getVertices() {

		return this.vertices;

	}

	public ArrayList<Aresta> getArestas() {
		return this.arestas;
	}

	public Vertice getVertice(String nomeVertice) {
		Vertice vertice = null;

		for (Vertice v : this.vertices) {
			if (v.getNome().equals(nomeVertice))
				vertice = v;
		}

		if (vertice == null) {
			System.out.println("Vertice: " + nomeVertice + " nao encontrado");
			return null;
		}

		return vertice;
	}

	private void addVertice(String nome) {
		Vertice v = new Vertice(nome);
		vertices.add(v);
	}

	private void addAresta(Vertice origem, Vertice destino, String cor) {
		Aresta e = new Aresta(origem, destino, cor);
		origem.addAdj(e);
		arestas.add(e);
	}

	public String toString() {
		String r = "";
		System.out.println("Graph:");
		for (Vertice u : vertices) {
			r += u.getNome() + " -> ";
			for (Aresta e : u.getAdj()) {
				Vertice v = e.getDestino();
				r += v.getNome() + e.getCor() + ", ";
			}
			r += "\n";
		}
		return r;
	}

	public ArrayList<Vertice> getVertices(String min, String max) {
		ArrayList<Vertice> sws = new ArrayList<Vertice>();
		int xMin = Integer.valueOf(min.substring(0, 1));
		int yMin = Integer.valueOf(min.substring(1, 2));
		int xMax = Integer.valueOf(max.substring(0, 1));
		int yMax = Integer.valueOf(max.substring(1, 2));
		for (int x = xMin; x <= xMax; x++)
			for (int y = yMin; y <= yMax; y++)
				sws.add(this.getVertice(x + "" + y));

		if (sws.size() == 0)
			sws = null;
		return sws;

	}

	/*
	 * private int dimension() {
	 * 
	 * return (int) Math.sqrt((double)this.vertices.size()); }
	 */

	public int dimX() {
		return dimX;
	}

	public int dimY() {
		return dimY;
	}

}
