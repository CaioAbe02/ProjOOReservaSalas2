package seed;

import java.util.List;
import java.util.ArrayList;

import model.Pessoa;
import model.Aluno;
import model.Professor;

public class pessoaSeed {
    private List<Pessoa> pSeed = new ArrayList<>();
    
    public List<Pessoa> seed() {
        pSeed.add(new Professor("Ana Moreira"));
        pSeed.add(new Professor("Daniela Oliveira"));
        pSeed.add(new Professor("Didier Oliveros"));
        pSeed.add(new Professor("Fabio Silveira"));
        pSeed.add(new Professor("Luis Pereira"));

        pSeed.add(new Aluno("Matheus Campos"));
        pSeed.add(new Aluno("Murilo Gomes"));

        return pSeed;
    }
}
