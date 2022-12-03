package br.com.turing.machine;

import br.com.turing.machine.domain.Direction;
import br.com.turing.machine.domain.Tape;
import br.com.turing.machine.domain.Transition;
import br.com.turing.machine.domain.TuringMachine;
import br.com.turing.machine.exception.TuringMachineException;
import br.com.turing.machine.validator.TuringMachineValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
public class ReadTransitionsFromFile {

    ResourceLoader resourceLoader = new DefaultResourceLoader();

    public TuringMachine readFile(String inputFilePath) throws Exception {
        List<Transition> transitions = new ArrayList<>();

        TuringMachine turingMachine = new TuringMachine();
        Resource resource = resourceLoader.getResource(inputFilePath);

        try {

            File myObj = new File(resource.getURI().getPath());
            Scanner myReader = new Scanner(myObj);

            while (myReader.hasNextLine()) {

                //delta(q, simb-lido1, sim-lido2) = (p, sim-escrito1, simb-escrito2, Dir1, Dir2)
                //q0,a,B=q0,a,a,RIGHT,RIGHT

                String data = myReader.nextLine();
                String symbolReadFromTape1AndTape2AndOriginState = data.split("=")[0];
                String destinyStateWriteSymbolAndDirectionFromTape1AndTape2 = data.split("=")[1];

                transitions.add(Transition.builder()
                        .originState(symbolReadFromTape1AndTape2AndOriginState.split(",")[0])
                        .symbolReadFirstTape(symbolReadFromTape1AndTape2AndOriginState.split(",")[1])
                        .symbolReadSecondTape(symbolReadFromTape1AndTape2AndOriginState.split(",")[2])
                        .destinyState(destinyStateWriteSymbolAndDirectionFromTape1AndTape2.split(",")[0])
                        .writeSymbolFirstTape(destinyStateWriteSymbolAndDirectionFromTape1AndTape2.split(",")[1])
                        .writeSymbolSecondTape(destinyStateWriteSymbolAndDirectionFromTape1AndTape2.split(",")[2])
                        .directionFirstTape(Direction.valueOf(destinyStateWriteSymbolAndDirectionFromTape1AndTape2.split(",")[3]))
                        .directionSecondTape(Direction.valueOf(destinyStateWriteSymbolAndDirectionFromTape1AndTape2.split(",")[4]))
                        .build());

            }

            ObjectMapper mapper = new ObjectMapper();

            turingMachine.setTransitions(transitions);

            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(turingMachine);

            System.out.println(json);

            myReader.close();
            return turingMachine;
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return null;
    }
}
