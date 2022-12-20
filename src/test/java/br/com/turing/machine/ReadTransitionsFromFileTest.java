package br.com.turing.machine;

import br.com.turing.machine.domain.TuringMachine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ReadTransitionsFromFileTest {
    @Test
    void shouldReadFile() throws Exception {
        ReadTransitionsFromFile transitions = new ReadTransitionsFromFile();

        String inputFilePath = "classpath:transicoes/soma-binarios-versao-2";
        TuringMachine turingMachine = transitions.readFile(inputFilePath);
        assertNotNull(turingMachine);
    }
}