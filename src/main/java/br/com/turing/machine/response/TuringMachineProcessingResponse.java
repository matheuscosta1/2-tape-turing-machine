package br.com.turing.machine.response;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TuringMachineProcessingResponse implements Serializable {

  private String estadoAtual;
  private String leSimboloPrimeiraFita;
  private String leSimboloSegundaFita;
  private String escreveSimboloPrimeiraFita;
  private String escreveSimboloSegundaFita;
  private String direcaoPrimeiraFita;
  private String direcaoSegundaFita;
  private String estadoDestino;

}
