package factory;

import model.Sala;
import model.SalaIndividual;

public class FabricaIndividual extends FabricaSala {
    
    @Override
    public Sala criarSala(String numero, int capacidade) {
        return new SalaIndividual(numero, capacidade);
    }
}