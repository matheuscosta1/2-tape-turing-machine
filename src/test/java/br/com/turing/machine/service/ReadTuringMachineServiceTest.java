package br.com.turing.machine.service;

import br.com.turing.machine.ReadTuringMachineTransitions;
import br.com.turing.machine.domain.TuringMachine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReadTuringMachineServiceTest {

    @Test
    void shouldReadFile() throws Exception {
        ReadTuringMachineTransitions turingMachineService = new ReadTuringMachineTransitions();

        String inputFilePath = "classpath:entrada/2-tape-wcw.json";
        TuringMachine turingMachineRequest = turingMachineService.readFile(inputFilePath);
        Assertions.assertNotNull(turingMachineRequest);
    }
}