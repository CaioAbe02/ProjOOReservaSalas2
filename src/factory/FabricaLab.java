package factory;

import model.Sala;
import model.Laboratorio;

public class FabricaLab extends FabricaSala {
    
    @Override
    public Sala criarSala(String numero, int capacidade) {
        return new Laboratorio(numero, capacidade);
    }
}
