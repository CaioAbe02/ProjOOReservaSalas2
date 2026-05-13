package factory;

import model.Sala;
import model.SalaGrupo;

public class FabricaGrupo extends FabricaSala {

    @Override
    public Sala criarSala(String numero, int capacidade) {
        return new SalaGrupo(numero, capacidade);
    }
}