package com.faculdade.sensor;

/** Colega: temperatura em °C (alerta a partir de 80, crítico a partir de 90). */
public class SensorTemperatura extends SensorMonitorado {

    public SensorTemperatura(String nome) {
        super(nome, 80.0, 90.0);
    }

    @Override
    protected Evento eventoPara(Nivel nivel) {
        return switch (nivel) {
            case NORMAL -> Evento.TEMPERATURA_NORMALIZADA;
            case ALERTA -> Evento.TEMPERATURA_ALTA;
            case CRITICO -> Evento.TEMPERATURA_CRITICA;
        };
    }
}
