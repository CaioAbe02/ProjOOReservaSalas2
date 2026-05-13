package seed;

import java.util.List;
import java.util.ArrayList;

import factory.*;
import model.Sala;

public class salaSeed {
    private List<Sala> sSeed = new ArrayList<>();

    FabricaIndividual fIndividual = new FabricaIndividual();
    FabricaGrupo fGrupo = new FabricaGrupo();
    FabricaLab fLab = new FabricaLab();

    public List<Sala> seed() {
        for(int i = 1; i <= 10; i++) {
            sSeed.add(fIndividual.criarSala("Sala de Estudos " + i, 5));
        }

        for(int i = 201; i <= 210; i++) {
            int capacidade;

            if(i % 2 == 0) {
                capacidade = 50;
            } else {
                capacidade = 30;
            }
            
            sSeed.add(fGrupo.criarSala(String.valueOf(i), capacidade));
        }

        for(int i = 301; i <= 310; i++) {
            int capacidade;

            if(i % 2 == 0) {
                capacidade = 50;
            } else {
                capacidade = 30;
            }
            
            sSeed.add(fGrupo.criarSala(String.valueOf(i), capacidade));
        }

        for(int i = 401; i <= 407; i++) {
            int capacidade;

            if(i % 2 == 0) {
                capacidade = 30;
            } else {
                capacidade = 60;
            }
            
            sSeed.add(fLab.criarSala(String.valueOf(i), capacidade));
        }

        return sSeed;
    }
}