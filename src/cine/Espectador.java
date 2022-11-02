package cine;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Espectador {

		UUID id;
		protected ArrayList<Entrada> entradas;
		
		public Espectador(UUID id, ArrayList<Entrada> entradas) {
			super();
			this.id = UUID.randomUUID ();
			this.setEntradas(new ArrayList<Entrada>(entradas));
		}

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}

		public ArrayList<Entrada> getEntradas() {
			return entradas;
		}

		public void setEntradas(ArrayList<Entrada> entradas) {
			this.entradas = entradas;
		}

		@Override
		public String toString() {
			return "Espectador [id=" + id + ", entradas=" + entradas + "]";
		}

		@Override
		public int hashCode() {
			return Objects.hash(entradas, id);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Espectador other = (Espectador) obj;
			return Objects.equals(entradas, other.entradas) && Objects.equals(id, other.id);
		}
		
		
		
		
}