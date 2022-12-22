package br.com.turing.machine;

import br.com.turing.machine.domain.*;
import br.com.turing.machine.response.TuringMachineProcessingResponse;
import br.com.turing.machine.response.TuringMachineResponse;
import br.com.turing.machine.validator.TuringMachineValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
public class CreateTuringMachine extends JPanel implements ActionListener {
    private static final int HEIGHT = 20;
    private static final int WIDTH = 20;
    private static final int FONT_SIZE = 20;
    private static final int SLEEP_TIME = 0;
    private static final int QUANTITY_OF_TAPE_CELL = 74;
    public static final String PROCESSOR_EVENT = "Processar";
    private final String arrowImageFilePath = "classpath:images/arrow.png";

    String actualState;

    Integer indexFirstTape = 0;
    Integer indexSecondTape = 0;

    ArrayList<CellTape> firstTape = new ArrayList<>();
    ArrayList<CellTape> secondTape = new ArrayList<>();
    TuringMachine turingMachine;

    ResourceLoader resourceLoader = new DefaultResourceLoader();

    ReadTuringMachineTransitions readTuringMachineTransitions = new ReadTuringMachineTransitions();
    ImageIcon arrowImageIcon = new ImageIcon(new ImageIcon(resourceLoader.getResource(arrowImageFilePath).getURL()).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT));
    JButton processorButton = new JButton("Processar");
    JButton inputButton = new JButton("Enviar");
    JButton skipProcessingButton = new JButton("Pular processamento");
    JTextField userInputWord;
    JLabel arrow;
    JTextField drawActualState;

    TuringMachineResponse turingMachineResponse = new TuringMachineResponse();

    String inputFilePath = "classpath:entrada/a^nb^nc^n.json";

    CreateTuringMachine() throws Exception {
        setLayout(null);

        turingMachine = readTuringMachineTransitions.readFile(inputFilePath);
        turingMachine.setName(inputFilePath.replace("classpath:entrada/", ""));
        turingMachineResponse.setMaquinaTuring(turingMachine);

        actualState = turingMachine.getInitialState();

        drawActualState = new JTextField("", 30);
        drawActualState.setEnabled(false);

        arrow = new JLabel(arrowImageIcon, SwingConstants.CENTER);

        userInputWord = new JTextField("", 30);
        userInputWord.setBounds(100, 500, 150, 100);
        userInputWord.setSize(new Dimension(300, 50));
        userInputWord.setToolTipText("Entre com uma palavra para ser processada.");

        inputButton.setBounds(350, 600, 200, 100);
        inputButton.addActionListener(
                e -> {
                    try {
                        userInputWordSubmitAction();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
        );

        processorButton.setBounds(100, 600, 200, 100);
        processorButton.setEnabled(false);
        processorButton.addActionListener(this);

        skipProcessingButton.setBounds(100, 730, 200, 100);
        skipProcessingButton.setEnabled(false);
        skipProcessingButton.addActionListener(this);

        add(arrow);
        add(userInputWord);
        add(inputButton);
        add(processorButton, BorderLayout.SOUTH);
        add(skipProcessingButton);
        add(drawActualState);
    }

    private void userInputWordSubmitAction() throws IOException {
        TuringMachineValidator turingMachineValidator = new TuringMachineValidator(turingMachine);

        inputButton.setEnabled(false);
        userInputWord.setEnabled(false);

        String initialSymbol = turingMachine.getStartMaker();
        String word = userInputWord.getText();

        turingMachineResponse.setCadeia(word);

        if(!turingMachineValidator.isValidWord(word)) {
            turingMachineResponse.setStatus("INVÁLIDO");
            writeTuringMachineResponseToFile(turingMachineResponse);
            JOptionPane.showMessageDialog(null, "Algum símbolo da cadeia não pertence ao alfabeto.");
            System.exit(0);
        }

        String wordWithInitialSymbol = initialSymbol != null ? initialSymbol.concat(word) : word;

        int quantityOfBlankSymbols = QUANTITY_OF_TAPE_CELL - wordWithInitialSymbol.length();
        int quantityOfBlankSymbolsOnTheLeftSide = quantityOfBlankSymbols / 2;
        int quantityOfBlankSymbolsOnTheRightSide = quantityOfBlankSymbols - quantityOfBlankSymbolsOnTheLeftSide;

        String whiteSymbol = turingMachine.getWhiteSymbol();

        String wordWithBlankSymbolsToMountFirstTape = whiteSymbol.repeat(quantityOfBlankSymbolsOnTheLeftSide).concat(wordWithInitialSymbol.concat(whiteSymbol.repeat(quantityOfBlankSymbolsOnTheRightSide)));
        String wordWithAllBlankSymbolsToMountSecondTape = whiteSymbol.repeat(QUANTITY_OF_TAPE_CELL);

        indexFirstTape = quantityOfBlankSymbolsOnTheLeftSide;
        indexSecondTape = quantityOfBlankSymbolsOnTheLeftSide;

        firstTapeDraw(wordWithBlankSymbolsToMountFirstTape);
        secondTapeDraw(wordWithAllBlankSymbolsToMountSecondTape);
    }

    public void firstTapeDraw(String word) {

        Graphics graphics = getGraphics();

        graphics.setFont(new Font("", Font.PLAIN, FONT_SIZE));

        int axisX = 100;
        int axisY = 100;

        for (int iterator = 0; iterator < word.length(); iterator++) {
            char actualCharacter = word.charAt(iterator);

            graphics.drawRect(axisX, axisY, WIDTH, HEIGHT);

            int drawStringYAxis = axisY + HEIGHT;

            graphics.drawString(String.valueOf(actualCharacter), axisX, drawStringYAxis);

            if (firstTape.size() < word.length()) {
                saveEachCellOnFirstTapeCoordinate(axisX, axisY, actualCharacter, drawStringYAxis);
            }

            axisX = movesOnAxisXForEachCell(axisX);
        }

        CellTape cellTape = firstTape.get(indexFirstTape);

        drawInitialArrowOnTape(graphics, 130, cellTape);

        drawBeginningActualState(100, 300);

        processorButton.setEnabled(true);
        skipProcessingButton.setEnabled(true);
    }

    public void secondTapeDraw(String word) {

        Graphics graphics = getGraphics();

        graphics.setFont(new Font("", Font.PLAIN, FONT_SIZE));

        int axisX = 100;
        int axisY = 200;

        for (int iterator = 0; iterator < word.length(); iterator++) {
            char actualCharacter = word.charAt(iterator);

            graphics.drawRect(axisX, axisY, WIDTH, HEIGHT);

            int drawStringYAxis = axisY + HEIGHT;

            graphics.drawString(String.valueOf(actualCharacter), axisX, drawStringYAxis);

            if (secondTape.size() < word.length()) {
                saveEachCellOnSecondTapeCoordinate(axisX, axisY, actualCharacter, drawStringYAxis);
            }

            axisX = movesOnAxisXForEachCell(axisX);
        }
        CellTape cellTape = secondTape.get(indexSecondTape);

        drawInitialArrowOnTape(graphics, 230, cellTape);

        processorButton.setEnabled(true);
        skipProcessingButton.setEnabled(true);
    }

    private void drawInitialArrowOnTape(Graphics graphics, int yAxis, CellTape cellTape) {
        graphics.drawImage(arrowImageIcon.getImage(), cellTape.getXAxis(), yAxis, null);
    }

    private void saveEachCellOnFirstTapeCoordinate(int axisX, int axisY, char actualCharacter, int drawStringYAxis) {
        firstTape.add(
                CellTape
                        .builder()
                        .xAxis(axisX)
                        .yAxis(axisY)
                        .drawStringYAxis(drawStringYAxis)
                        .symbol(Symbol.builder().character(String.valueOf(actualCharacter)).build())
                        .width(WIDTH)
                        .height(HEIGHT)
                        .build()
        );
    }

    private void saveEachCellOnSecondTapeCoordinate(int axisX, int axisY, char actualCharacter, int drawStringYAxis) {
        secondTape.add(
                CellTape
                        .builder()
                        .xAxis(axisX)
                        .yAxis(axisY)
                        .drawStringYAxis(drawStringYAxis)
                        .symbol(Symbol.builder().character(String.valueOf(actualCharacter)).build())
                        .width(WIDTH)
                        .height(HEIGHT)
                        .build()
        );
    }

    private void drawBeginningActualState(int x, int y) {
        drawActualState.setText("Estado atual: ".concat(actualState));
        drawActualState.setBounds(x, y, 130, 50);
    }

    private static int movesOnAxisXForEachCell(int axisX) {
        axisX = axisX + WIDTH;
        return axisX;
    }

    public void updateTapeDraw(Transition transition) {

        Graphics graphics = getGraphics();
        graphics.setFont(new Font("", Font.PLAIN, FONT_SIZE));

        updateFirstTape(transition, graphics);

        drawActualState.setText("Estado atual: ".concat(actualState));

    }

    private void updateFirstTape(Transition transition, Graphics graphics) {
        boolean isFirstTape = true;
        CellTape firstTapeCellCoordinate = firstTape.get(indexFirstTape);

        clearOldRectTapeAndDrawNewRectWithNewSymbol(transition, graphics, firstTapeCellCoordinate, isFirstTape);

        movesToTheLeftOrTheRightOnTapeBasedOnTransitionDirectionForFirstTape(transition, graphics, firstTapeCellCoordinate, 130);

        updateActualSymbolReadFromTapeInTapeCoordinateList(transition, firstTapeCellCoordinate, isFirstTape);

        updateSecondTape(transition, graphics, firstTapeCellCoordinate);
    }

    private void updateSecondTape(Transition transition, Graphics graphics, CellTape firstTapeCellCoordinate) {
        boolean isFirstTape = false;

        CellTape secondTapeCellCoordinate = secondTape.get(indexSecondTape);

        clearOldRectTapeAndDrawNewRectWithNewSymbol(transition, graphics, secondTapeCellCoordinate, isFirstTape);

        movesToTheLeftOrTheRightOnTapeBasedOnTransitionDirectionForSecondTape(transition, graphics, secondTapeCellCoordinate, 230);

        constructTuringMachineResponse(transition, firstTapeCellCoordinate, secondTapeCellCoordinate);

        actualState = transition.getDestinyState();

        updateActualSymbolReadFromTapeInTapeCoordinateList(transition, secondTapeCellCoordinate, isFirstTape);
    }

    private void constructTuringMachineResponse(Transition transition, CellTape firstTapeCellCoordinate, CellTape secondTapeCellCoordinate) {
        TuringMachineProcessingResponse turingMachineProcessingResponse = TuringMachineProcessingResponse
                .builder()
                .estadoAtual(actualState)
                .escreveSimboloPrimeiraFita(transition.getWriteSymbolFirstTape())
                .escreveSimboloSegundaFita(transition.getWriteSymbolSecondTape())
                .estadoDestino(transition.getDestinyState())
                .direcaoPrimeiraFita(transition.getDirectionFirstTape().name())
                .direcaoSegundaFita(transition.getDirectionSecondTape().name())
                .leSimboloPrimeiraFita(firstTapeCellCoordinate.getSymbol().getCharacter())
                .leSimboloSegundaFita(secondTapeCellCoordinate.getSymbol().getCharacter())
                .build();

        turingMachineResponse.getProcessamento().add(turingMachineProcessingResponse);
    }

    private void movesToTheLeftOrTheRightOnTapeBasedOnTransitionDirectionForFirstTape(Transition transition, Graphics graphics, CellTape cellTapeCoordinate, int yAxis) {
        if(Direction.RIGHT.equals(transition.getDirectionFirstTape())) {
            graphics.clearRect(cellTapeCoordinate.getXAxis(), yAxis, WIDTH, HEIGHT);
            graphics.drawImage(arrowImageIcon.getImage(), cellTapeCoordinate.getXAxis() + WIDTH, yAxis, null);
            indexFirstTape += 1;
        } else if (Direction.LEFT.equals(transition.getDirectionFirstTape())) {
            graphics.clearRect(cellTapeCoordinate.getXAxis(), yAxis, WIDTH, HEIGHT);
            graphics.drawImage(arrowImageIcon.getImage(), cellTapeCoordinate.getXAxis() - WIDTH, yAxis, null);
            indexFirstTape -= 1;
        }
    }

    private void movesToTheLeftOrTheRightOnTapeBasedOnTransitionDirectionForSecondTape(Transition transition, Graphics graphics, CellTape cellTapeCoordinate, int yAxis) {
        if(Direction.RIGHT.equals(transition.getDirectionSecondTape())) {
            graphics.clearRect(cellTapeCoordinate.getXAxis(), yAxis, WIDTH, HEIGHT);
            graphics.drawImage(arrowImageIcon.getImage(), cellTapeCoordinate.getXAxis() + WIDTH, yAxis, null);
            indexSecondTape += 1;
        } else if (Direction.LEFT.equals(transition.getDirectionSecondTape())) {
            graphics.clearRect(cellTapeCoordinate.getXAxis(), yAxis, WIDTH, HEIGHT);
            graphics.drawImage(arrowImageIcon.getImage(), cellTapeCoordinate.getXAxis() - WIDTH, yAxis, null);
            indexSecondTape -= 1;
        }
    }

    private static void clearOldRectTapeAndDrawNewRectWithNewSymbol(Transition transition, Graphics graphics, CellTape cellTapeCoordinate, boolean isFirstTape) {
        graphics.clearRect(cellTapeCoordinate.getXAxis(), cellTapeCoordinate.getYAxis(), WIDTH, HEIGHT);

        graphics.drawRect(cellTapeCoordinate.getXAxis(), cellTapeCoordinate.getYAxis(), WIDTH, HEIGHT);

        if(isFirstTape) {
            graphics.drawString(transition.getWriteSymbolFirstTape(), cellTapeCoordinate.getXAxis(), cellTapeCoordinate.getDrawStringYAxis());
        } else {
            graphics.drawString(transition.getWriteSymbolSecondTape(), cellTapeCoordinate.getXAxis(), cellTapeCoordinate.getDrawStringYAxis());
        }

    }

    private static void updateActualSymbolReadFromTapeInTapeCoordinateList(Transition transition, CellTape cellTapeCoordinate, boolean isFirstTape) {
        if(isFirstTape) {
            cellTapeCoordinate.setSymbol(Symbol.builder().character(transition.getWriteSymbolFirstTape()).build());
        } else {
            cellTapeCoordinate.setSymbol(Symbol.builder().character(transition.getWriteSymbolSecondTape()).build());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(PROCESSOR_EVENT)) {
            Optional<Transition> transition = getTransition();
            try {
                if(transition.isPresent()) {
                    processTuringMachineOneStepPerTime(transition);
                } else {
                    validateTuringMachineAcceptsWord();
                    writeTuringMachineResponseToFile(turingMachineResponse);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            processorButton.setEnabled(false);
            skipProcessingButton.setEnabled(false);

            processTuringMachineStepByStepTillTheEnd();
        }
    }

    private void writeTuringMachineResponseToFile(TuringMachineResponse turingMachineResponse) throws IOException {
        String outputPath = "saida/".concat(LocalDateTime.now().toString()).concat(".json");

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(turingMachineResponse);

        mapper.writeValue(new File(outputPath), json);

        System.out.println("Processamento terminou.\n");

        System.out.println(json);
    }

    private void processTuringMachineOneStepPerTime(Optional<Transition> transition) throws IOException {
        if(transition.isPresent()) {
            updateTapeDraw(transition.get());
        }
    }

    private void processTuringMachineStepByStepTillTheEnd() {
        new Thread(() -> {

            long start = System.currentTimeMillis();

            Optional<Transition> transition = getTransition();

            while (transition.isPresent()) {
                updateTapeDraw(transition.get());
                transition = getTransition();
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            long elapsed = System.currentTimeMillis() - start;
            turingMachineResponse.setExecutionTime((elapsed/ 1000d));
            validateTuringMachineAcceptsWord();
            try {
                writeTuringMachineResponseToFile(turingMachineResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



        }).start();
    }

    private void validateTuringMachineAcceptsWord() {
        if(turingMachine.hasFinalStates()) {
            if(turingMachine.isWordAccepted(actualState)) {
                turingMachineResponse.setStatus("ACEITO");
                JOptionPane.showMessageDialog(null, "A cadeia foi aceita.");
            } else {
                turingMachineResponse.setStatus("REJEITADO");
                JOptionPane.showMessageDialog(null, "A cadeia foi rejeitada.");
            }
        } else {
            turingMachineResponse.setStatus("PROCESSADO");
            JOptionPane.showMessageDialog(null, "O processamento terminou.");
        }
    }

    private Optional<Transition> getTransition() {
        CellTape firstTapeCell = firstTape.get(indexFirstTape);
        CellTape secondTapeCell = secondTape.get(indexSecondTape);

        return turingMachine.findTransitionByActualStateAndReadSymbol(actualState, firstTapeCell.getSymbol().getCharacter(), secondTapeCell.getSymbol().getCharacter());
    }

    public static void main(String[] args) throws Exception {

        JFrame frame = new JFrame("Máquina de Turing");

        frame.add(new CreateTuringMachine());
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1600, 900));

    }
}
