package br.com.turing.machine.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transition implements Serializable  {

  @JsonProperty("leSimboloPrimeiraFita")
  private String symbolReadFirstTape;

  @JsonProperty("leSimboloSegundaFita")
  private String symbolReadSecondTape;

  @JsonProperty("escrevePrimeiraFita")
  private String writeSymbolFirstTape;

  @JsonProperty("escreveSegundaFita")
  private String writeSymbolSecondTape;

  @JsonProperty("direcaoPrimeiraFita")
  private Direction directionFirstTape;

  @JsonProperty("direcaoSegundaFita")
  private Direction directionSecondTape;

  @JsonProperty("estadoOrigem")
  private String originState;

  @JsonProperty("estadoDestino")
  private String destinyState;

}
