package factory;

import model.Sala;

public abstract class FabricaSala {
    public abstract Sala criarSala(String numero, int capacidade);
}